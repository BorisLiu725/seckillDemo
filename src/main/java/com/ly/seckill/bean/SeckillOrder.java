package com.ly.seckill.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by BorisLiu on 2019/11/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrder {

    private String orderId;
    private Long seckillId;
    private Integer userPhone;
    private Integer state;
    private Date createTime;

}
