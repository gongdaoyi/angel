package com.angel.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/host")
public class JSONController {

    @Value("#{${nbop.stockright.check.plus:{}}}")
    private JSONObject rightCheckPlusInfo;

    // 使用FastJson的parseObject方法，并为null时返回空JSONObject
    @Value("#{T(com.alibaba.fastjson.JSONObject).parseObject('${nbop.stockright.check.plus:{} }') ?: new com.alibaba.fastjson.JSONObject()}")
    private JSONObject rightCheckPlusInfo_V2;

    @PostMapping("/testJSON")
    public void testJSON() {
        System.out.println("错误注入：" + rightCheckPlusInfo);
        System.out.println("正确注入：" + rightCheckPlusInfo_V2);
    }

}
