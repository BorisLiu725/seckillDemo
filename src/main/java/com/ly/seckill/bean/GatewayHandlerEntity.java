package com.ly.seckill.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class GatewayHandlerEntity implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;
    /**
     * handler名称
     */
    private String handlerName;
    /**
     * handler主键id
     */
    private String handlerId;
    /**
     * 上一个handler
     */
    private String prevHandlerId;
    /**
     * 下一个handler
     */
    private String nextHandlerId;
}
