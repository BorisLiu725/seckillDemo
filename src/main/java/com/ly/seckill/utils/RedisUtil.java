package com.ly.seckill.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by BorisLiu on 2019/11/24
 */
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Boolean setNx(String key, String value, Long timeout){
        Boolean ret = stringRedisTemplate.opsForValue().setIfAbsent(key,value);
        if (Objects.nonNull(ret)){
            stringRedisTemplate.expire(key,timeout,TimeUnit.SECONDS);
        }
        return ret;
    }

    public StringRedisTemplate getStringRedisTemplate() {
        return stringRedisTemplate;
    }


    public void setList(String key, List<String> tokenLists) {
        stringRedisTemplate.opsForList().leftPushAll(key,tokenLists);
    }
}
