package com.angel.controller;

import com.alibaba.fastjson.JSONObject;
import com.angel.service.IMybatisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/mybatis")
public class MybatisController {


    @Autowired
    IMybatisService mybatisService;

    @PostMapping("/qryNationalityDict")
    public JSONObject qryNationalityDict(@RequestParam String subentry) {

        return mybatisService.qryNationalityDict(subentry);
    }

}
