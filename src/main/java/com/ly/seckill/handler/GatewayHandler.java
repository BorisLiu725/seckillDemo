package com.ly.seckill.handler;

import java.util.Objects;

/**
 * Created by BorisLiu on 2019/11/25
 */
public abstract class GatewayHandler {

    private GatewayHandler nextGatewayHandler;

    public abstract void service();


    public void setNextGatewayHandler(GatewayHandler nextGatewayHandler) {
        this.nextGatewayHandler = nextGatewayHandler;
    }

    //指向下一关
    protected void nextService(){
        if (Objects.nonNull(nextGatewayHandler)){
            nextGatewayHandler.service();
        }
    }

}
