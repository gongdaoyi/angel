package com.angel.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * 使用redis实现分布式锁 Created by wanglvyh
 * <p>
 * 可以以Component Spring bean的方式，也可以程序控制实例化的方式。
 */
public class RedisLock {

    private static Logger logger = LoggerFactory.getLogger(RedisLock.class);

    private RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX_KEY = "REDIS_LOCK:";

    public RedisLock(RedisTemplate<String, String> redisTemplate) {
        super();
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取锁，如果成功获取则返回true
     *
     * @param lockName
     * @return
     */
    public boolean acquireLock(String lockName) {
        try {
            logger.info("尝试获取lock:" + lockName);
            String redisKey = PREFIX_KEY + lockName;
            long expire = 15;// 单位:秒
            long timeout = 5000;// 单位:毫秒
            long redisValue = System.currentTimeMillis() + timeout + 1;
            logger.info("redisValue:" + redisValue);
            // 通过SETNX试图获取一个lock
            if (setNX(redisKey, String.valueOf(redisValue), expire)) {// SETNX成功，则成功获取一个锁
                logger.info("获取lock成功:" + lockName);
                return true;
            } else {// SETNX失败，说明锁仍然被其他对象保持，检查其是否已经超时
                String oldValueString = (String) get(redisKey);
                if (oldValueString == null)
                    return false;
                Long oldValue = Long.valueOf(oldValueString);
                logger.info("当前lock时间值:" + oldValue);
                // 超时
                if (oldValue < System.currentTimeMillis()) {
                    logger.info("当前lock timeout了");
                    String getValue = getAndSet(redisKey, String.valueOf(redisValue));
                    logger.info("lock getAndSet后的新值:" + getValue);
                    // 获取锁成功
                    if (getValue != null && !getValue.equals(oldValue.toString())) {
                        logger.info("获取lock成功:" + lockName);
                        return true;
                    } else {// 已被其他进程捷足先登了
                        logger.info("获取lock失败:" + lockName);
                        return false;
                    }
                } else {// 未超时，则直接返回失败
                    logger.info("获取lock失败:" + lockName);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("获取锁异常");
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 释放锁
     *
     * @param lockName
     * @throws Exception
     */
    public void releaseLock(String lockName) {
        String redisKey = PREFIX_KEY + lockName;
        redisTemplate.delete(redisKey);
        logger.info("释放lock:" + lockName);
    }

    public void releaseLockWithoutException(String lockName) {
        String redisKey = PREFIX_KEY + lockName;
        redisTemplate.delete(redisKey);
        logger.info("释放lock:" + lockName);
    }

    private boolean setNX(final String key, final String value, final long expire) {

        return Objects.requireNonNull(redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            byte[] keyBytes = Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key));

            boolean locked = Objects.requireNonNull(connection.setNX(keyBytes, Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(value))));
            if (locked) {
                connection.expire(keyBytes, expire);
            }
            return locked;
        }));
    }

    private String getAndSet(final String key, final String value) {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            byte[] result = connection.getSet(Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)),
                    Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(value)));
            if (result != null) {
                return new String(result);
            }
            return null;
        });
    }

    private Object get(final String key) {
        return redisTemplate.execute((RedisCallback<Object>) connection -> {
            byte[] bs = connection.get(Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)));
            return redisTemplate.getStringSerializer().deserialize(bs);
        });
    }

}
