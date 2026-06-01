package com.angel.controller;

import com.angel.service.IDictService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/dict")
public class DictController {

    private final IDictService mybatisService;

    public DictController(IDictService mybatisService) {
        this.mybatisService = mybatisService;
    }

    @PostMapping("/generateDict")
    public void qryNationalityDict() {

        mybatisService.generateDict();
    }

}
