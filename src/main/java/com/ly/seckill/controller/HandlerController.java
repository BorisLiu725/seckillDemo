package com.ly.seckill.controller;

import com.ly.seckill.handler.GatewayHandler;
import com.ly.seckill.service.GatewayHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandlerController {
    @Autowired
    private GatewayHandlerService gatewayHandlerService;

    @RequestMapping("/client")
    public String client() {
        GatewayHandler firstGatewayHandler = gatewayHandlerService.getFirstGatewayHandler();
        firstGatewayHandler.service();
        return "success";
    }
}
