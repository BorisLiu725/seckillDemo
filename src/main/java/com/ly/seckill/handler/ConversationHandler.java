package com.ly.seckill.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConversationHandler extends GatewayHandler {
    @Override
    public void service() {
        log.info("第三关 用户的会话信息拦截.......");
        super.nextService();
    }
}
