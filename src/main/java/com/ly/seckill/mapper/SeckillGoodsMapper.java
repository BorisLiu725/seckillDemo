package com.ly.seckill.mapper;

import com.ly.seckill.bean.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * Created by BorisLiu on 2019/11/24
 */
@Mapper
public interface SeckillGoodsMapper {

    @Select("select * from seckill_goods where seckill_id = #{seckillId}")
    SeckillGoods getSeckillGoodsById(@Param("seckillId")Long seckillId);


    @Update("update seckill_goods set inventory = inventory-1,version=version+1 where inventory > 0 and seckill_id=#{seckillId} and version = #{version}")
    int optimisticVersionSeckill(@Param("seckillId")Long seckillId,@Param("version")Long version);


    @Update("update seckill_goods set inventory = inventory-1 where inventory > 0")
    int traditionSeckill(@Param("seckillId")Long seckillId);

}
