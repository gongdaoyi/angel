package com.angel.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/host")
public class HostController {

    @ApiOperation("本地测试接口")
    @RequestMapping("/test")
    public void test(String clientId) {
        log.info(clientId);
    }

}
