package com.angel;

import com.angel.utils.sync.AsynExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 异步执行器配置类
 *
 * <p>在Spring容器启动时初始化异步任务执行器的线程池</p>
 * <p>
 * 配置参数：
 * <ul>
 *   <li>nbop.aysn.size：线程池大小，默认值为10</li>
 * </ul>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@Configuration
public class AsyncExecutorConfig {

    /**
     * 线程池大小配置键
     */
    private static final String THREAD_POOL_SIZE_KEY = "nbop.aysn.size";

    /**
     * 默认线程池大小
     */
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;

    /**
     * 线程池大小（从配置文件注入）
     */
    @Value("${" + THREAD_POOL_SIZE_KEY + ":" + DEFAULT_THREAD_POOL_SIZE + "}")
    private int asyncSize;

    /**
     * 初始化异步执行器
     *
     * <p>在Bean初始化完成后调用，初始化全局异步任务线程池</p>
     */
    @PostConstruct
    public void init() {
        log.info("开始初始化异步执行器，线程池大小: {}", asyncSize);
        AsynExecutor.init(asyncSize);
    }

}