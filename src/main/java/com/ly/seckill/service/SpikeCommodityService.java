package com.ly.seckill.service;

import com.ly.seckill.commons.BaseResponse;

/**
 * Created by BorisLiu on 2019/11/24
 */
public interface SpikeCommodityService {
    /**
     * 用户秒杀接口
     * */
    public BaseResponse spike(Integer phone, Long seckillId);
    /**
     * 给订单的id 生成对应的令牌
     * */
    public BaseResponse<String> addSpikeToken(Long seckillId,Long tokenNums);
    /**
     * 秒杀哦mq实现方式
     * */
    public BaseResponse<String> spikeNext(Integer phone, Long seckillId);
}
