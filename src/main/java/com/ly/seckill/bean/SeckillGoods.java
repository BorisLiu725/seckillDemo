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
public class SeckillGoods {

    private Long seckillId;
    private String goodsName;
    private Integer inventory;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Long version;

}
