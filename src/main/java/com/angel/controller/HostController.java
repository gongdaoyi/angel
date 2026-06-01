package com.angel.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主机信息控制器
 *
 * <p>提供主机相关的基础接口</p>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@RestController
@RequestMapping("/host")
public class HostController {

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public String health() {
        return "OK";
    }

}
