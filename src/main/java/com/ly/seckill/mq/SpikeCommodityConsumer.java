package com.ly.seckill.mq;

import com.alibaba.fastjson.JSONObject;
import com.ly.seckill.bean.SeckillGoods;
import com.ly.seckill.bean.SeckillOrder;
import com.ly.seckill.commons.BaseResponse;
import com.ly.seckill.mapper.SeckillGoodsMapper;
import com.ly.seckill.mapper.SeckillOrderMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by BorisLiu on 2019/11/25
 */
@Component
@Slf4j
public class SpikeCommodityConsumer{

    @Autowired
    public SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    public SeckillOrderMapper seckillOrderMapper;


    @RabbitListener(queues = "modify_inventory_queue")
    @Transactional
    public void process(Message message, @Headers Map<String,Object> headers, Channel channel) throws IOException {
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(),"UTF-8");
        log.info("==>messageId:{},msg:{}",messageId,message);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        //1.获取秒杀id
        Long seckillId = jsonObject.getLong("seckillId");
        SeckillGoods seckillGoods = seckillGoodsMapper.getSeckillGoodsById(seckillId);
        if (Objects.isNull(seckillGoods)){
            log.warn("sekillId:{},商品不存在！",seckillId);
            return;
        }
        //2.更新库存
        Long version = seckillGoods.getVersion();
        int res = seckillGoodsMapper.optimisticVersionSeckill(seckillId, version);
        if (!DaoResult(res)){
            log.info(">>>seckillId:{}修改库存失败>>>optimisticVersionSeckill返回值为:{}秒杀失败!",seckillId,res);
            return;
        }
        //3.添加秒杀订单
        //4.秒杀成功，生成订单mq异步方式
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setSeckillId(seckillId);

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        seckillOrder.setOrderId(uuid);
        seckillOrder.setState(-1);
        seckillOrder.setCreateTime(new Date());
        Integer phone = jsonObject.getInteger("phone");
        seckillOrder.setUserPhone(phone);
        int res1 = seckillOrderMapper.insertOrder(seckillOrder);
        if (res1<=0){
            return;
        }
        log.info(">>>seckillId:{}修改库存成功>>>optimisticVersionSeckill返回值为:{}秒杀成!",seckillId,res);

    }

    public Boolean DaoResult(int result){
        return result > 0 ? true : false;
    }
}
