package com.ly.seckill.controller;

import com.ly.seckill.commons.BaseResponse;
import com.ly.seckill.service.SpikeCommodityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by BorisLiu on 2019/11/24
 */
@RequestMapping("/seckill")
@Controller
public class SpikeController {

    @Autowired
    private SpikeCommodityService spikeCommodityService;

    @RequestMapping("/spike/{phone}/{seckillId}")
    @ResponseBody
    public BaseResponse<String> spike(@PathVariable("phone") Integer phone,@PathVariable("seckillId") Long seckillId){
       return spikeCommodityService.spike(phone, seckillId);
    }
    /**
     * 秒杀接口
     * */
    @RequestMapping("/spikeNext/{phone}/{seckillId}")
    @ResponseBody
    public BaseResponse<String> spikeNext(@PathVariable("phone") Integer phone,@PathVariable("seckillId") Long seckillId){
        return spikeCommodityService.spikeNext(phone, seckillId);
    }
    /**
     * 生成token，给某个商品生成多少个令牌
     * */
    @RequestMapping("/spikeToken/{seckillId}/{tokenNums}")
    @ResponseBody
    public BaseResponse<String> addSpikeToken(@PathVariable("seckillId") Long seckillId,@PathVariable("tokenNums")Long tokenNums){
        return spikeCommodityService.addSpikeToken(seckillId,tokenNums);
    }
    /**
     * 查询秒杀是否成功
     * 只需要根据用户手机号去查询订单表就ok
     * */

}
