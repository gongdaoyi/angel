package com.angel.config;

import com.angel.sync.AsynExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class AsyncExecutorConfig {

    @Value("${nbop.aysn.size:10}")
    private int asynSize;

    @PostConstruct
    public void init() {
        AsynExecutor.init(asynSize);
    }

}