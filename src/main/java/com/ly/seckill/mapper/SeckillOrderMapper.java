package com.ly.seckill.mapper;

import com.ly.seckill.bean.SeckillOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by BorisLiu on 2019/11/24
 */
@Mapper
public interface SeckillOrderMapper {

    @Insert("insert into seckill_order values(#{orderId},#{seckillId},#{userPhone},#{state},#{createTime}) ")
    public int insertOrder(SeckillOrder seckillOrder);


}
