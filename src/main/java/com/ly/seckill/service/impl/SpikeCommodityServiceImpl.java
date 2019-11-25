package com.ly.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ly.seckill.bean.SeckillGoods;
import com.ly.seckill.bean.SeckillOrder;
import com.ly.seckill.commons.BaseResponse;
import com.ly.seckill.mapper.SeckillGoodsMapper;
import com.ly.seckill.mapper.SeckillOrderMapper;
import com.ly.seckill.mq.SpikeCommodityProducer;
import com.ly.seckill.service.SpikeCommodityService;
import com.ly.seckill.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by BorisLiu on 2019/11/24
 */
@Service
@Slf4j
public class SpikeCommodityServiceImpl implements SpikeCommodityService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String tokenPrefix = "seckill_";

    @Autowired
    public SpikeCommodityProducer spikeCommodityProducer;

//    Logger logger = LoggerFactory.getLogger(SpikeCommodityServiceImpl.class);

    @Override
    @Transactional
    public BaseResponse<String> spike(Integer phone, Long seckillId) {
        //1.验证参数
        if (StringUtils.isEmpty(phone)){
            return new BaseResponse<String>(400,"手机号不能为空");
        }
        if (StringUtils.isEmpty(seckillId)){
            return new BaseResponse<String>(400,"商品库存id不能为空");
        }
        SeckillGoods seckillGoods = seckillGoodsMapper.getSeckillGoodsById(seckillId);
        if (Objects.isNull(seckillGoods)){
            return new BaseResponse<String>(400,"无该商品!");
        }
        //2.用户访问频率限制
        Boolean flag = redisUtil.setNx(phone+"", seckillId + "", 10L);
        if (!flag){
            log.info("访问次数过多，10s后再试!==phone:{}====seckillId:{}",phone,seckillId);
            return new BaseResponse<String>(400,"访问次数过多，10s后再试!");
        }

        //3.修改数据库对应的库存
        int res = seckillGoodsMapper.optimisticVersionSeckill(seckillId, seckillGoods.getVersion());
        //int res = seckillGoodsMapper.traditionSeckill(seckillId);

        if (res <= 0){
            log.info("===> 修改库存失败==phone:{}====seckillId:{}",phone,seckillId);
            return  new BaseResponse<String>(400,"亲，请稍后再试。。。");
        }
        //4.秒杀成功，生成订单mq异步方式
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setSeckillId(seckillId);

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        seckillOrder.setOrderId(uuid);
        seckillOrder.setState(-1);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setUserPhone(phone);
        res = seckillOrderMapper.insertOrder(seckillOrder);
        if (res<=0){

            return  new BaseResponse<String>(400,"亲，请稍后再试。。。");
        }
        log.info("===> 修改库存成功==phone:{}====seckillId:{}",phone,seckillId);
        return new BaseResponse<String>(200,"秒杀成功！");
    }

    @Override
    public BaseResponse<String> addSpikeToken(Long seckillId, Long tokenNums) {
        //1.验证参数
        if (StringUtils.isEmpty(tokenNums)){
            return new BaseResponse<String>(400,"token数量不能为空");
        }
        if (StringUtils.isEmpty(seckillId)){
            return new BaseResponse<String>(400,"商品库存id不能为空");
        }
        SeckillGoods seckillGoods = seckillGoodsMapper.getSeckillGoodsById(seckillId);
        if (Objects.isNull(seckillGoods)){
            return new BaseResponse<String>(400,"无该商品!");
        }
        //多线程异步生成令牌
        log.info("===>"+Thread.currentThread().getName()+"调用了生成令牌的方法===");
        createSeckillToken(seckillId,tokenNums);
        return new BaseResponse<String>(400,"令牌正在生成中");
    }

    @Async
    public void createSeckillToken(Long seckillId, Long tokenNums) {
        log.info("===>"+Thread.currentThread().getName()+"异步生成令牌===");
       List<String> tokenLists =  getListToken(tokenPrefix,seckillId,tokenNums);
       redisUtil.setList(seckillId+"",tokenLists);
    }

    private List<String> getListToken(String keyPrefix,Long seckillId, Long tokenNums) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tokenNums; i++) {
            String token = UUID.randomUUID().toString().replaceAll("-","");
            list.add(token);
        }
        return list;
    }


    public String getListKeyToken(Long seckillId){
        return redisUtil.getStringRedisTemplate().opsForList().leftPop(seckillId+"");
    }


    @Transactional
    public BaseResponse<String> spikeNext(Integer phone, Long seckillId){
        //1.验证参数
        if (StringUtils.isEmpty(phone)){
            return new BaseResponse<String>(400,"手机号不能为空");
        }
        if (StringUtils.isEmpty(seckillId)){
            return new BaseResponse<String>(400,"商品库存id不能为空");
        }
        SeckillGoods seckillGoods = seckillGoodsMapper.getSeckillGoodsById(seckillId);
        if (Objects.isNull(seckillGoods)){
            return new BaseResponse<String>(200,"无该商品!");
        }
        //2.从redis中获取对应的秒杀token，采用令牌桶的方式,抢到令牌的人可以去修改数据库
        String token = getListKeyToken(seckillId);
        if (StringUtils.isEmpty(token)){
            log.info("===>亲，该商品已经售空，下次再试哦!");
            return new BaseResponse<String>(200,"亲，该商品已经售空，下次再试哦!");
        }
        //3.获取到秒杀token之后异步放入mq中实现商品库存的修改
        sendSekillMsg(seckillId,phone);
        return new BaseResponse<String>(200,"正在排队中..!");
    }

    /**
     * 异步将消息放入mq中实现修改商品的库存
     * */
    @Async
     public void sendSekillMsg(Long seckillId, Integer phone) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seckillId",seckillId);
        jsonObject.put("phone",phone);
        spikeCommodityProducer.send(jsonObject);
    }



}
