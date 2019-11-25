package com.ly.seckill.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BlacklistHandler extends GatewayHandler {
    @Override
    public void service() {
        log.info("第二关 黑名单拦截.......");
        nextService();
    }

}
