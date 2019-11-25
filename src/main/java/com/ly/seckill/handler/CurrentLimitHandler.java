package com.ly.seckill.handler;

import com.ly.seckill.handler.GatewayHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by BorisLiu on 2019/11/25
 */
@Component
@Slf4j
public class CurrentLimitHandler extends GatewayHandler{
    @Override
    public void service() {
        log.info("==》第一关.CurrentLimitHandler");
        super.nextService();
    }

}
