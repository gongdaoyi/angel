package com.angel.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


/**
 * redis 限流
 */
@Component
public class RedisUtils {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    /**
     * 能拿到令牌表示未到上限
     */
    public void getKey() {
        Object limit = redisTemplate.opsForList().leftPop("limit");

        if (ObjectUtils.isEmpty(limit)) {
            System.out.println("当前桶中无令牌");
            return;
        }

        System.out.println(limit);
    }
}
