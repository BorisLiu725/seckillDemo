package com.ly.seckill.service;

import com.ly.seckill.bean.GatewayHandlerEntity;
import com.ly.seckill.handler.GatewayHandler;
import com.ly.seckill.mapper.GatewayHandlerMapper;
import com.ly.seckill.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class GatewayHandlerService {
    @Autowired
    private GatewayHandlerMapper gatewayHandlerMapper;
    //防止每次进来都要查询数据库，减少与数据库的交互
    private GatewayHandler firstGatewayHandler;

    public GatewayHandler getFirstGatewayHandler(){
        if(!Objects.isNull(firstGatewayHandler)){
            return firstGatewayHandler;
        }
        //1、获取第一个实体
        GatewayHandlerEntity firstGatewayHandlerEntity = gatewayHandlerMapper.getFirstGatewayHandler();
        if(Objects.isNull(firstGatewayHandlerEntity)){
            return null;
        }
        //2、第一个实体的handlerId
        String firstHandlerId = firstGatewayHandlerEntity.getHandlerId();
        //3、下一个实体的handlerId
        String nextHandlerId = firstGatewayHandlerEntity.getNextHandlerId();
        //4、获取第一个的bean对象
        GatewayHandler firstGatewayHandler = SpringUtils.getBean(firstHandlerId, GatewayHandler.class);
        GatewayHandler tempGatewayHandler = firstGatewayHandler;
        while (!StringUtils.isEmpty(nextHandlerId)){
            //5、查找下一个handler
            GatewayHandler nextGatewayHandler = SpringUtils.getBean(nextHandlerId, GatewayHandler.class);
            //6、查找下一个entity
            GatewayHandlerEntity nextGatewayHandlerEntity = gatewayHandlerMapper.getByHandler(nextHandlerId);
            if(Objects.isNull(nextGatewayHandlerEntity)){
                break;
            }
            //循环给nextHandlerId赋值
            nextHandlerId = nextGatewayHandlerEntity.getNextHandlerId();
            tempGatewayHandler.setNextGatewayHandler(nextGatewayHandler);
            tempGatewayHandler = nextGatewayHandler;
        }
        //赋值
        this.firstGatewayHandler = firstGatewayHandler;
        return firstGatewayHandler;
    }
}
