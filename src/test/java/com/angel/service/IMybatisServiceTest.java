package com.angel.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
@SpringBootTest
@DisplayName("IMybatisService 集成测试")
class IMybatisServiceTest {

    @Autowired
    private IMybatisService mybatisService;

    @BeforeEach
    void setUp() {
        log.info("=== 开始执行 IMybatisService 集成测试 ===");
    }

    @Test
    @DisplayName("查询国籍字典 - VEN")
    void qryNationalityDict_withVEN() {
        JSONObject result = mybatisService.qryNationalityDict("VEN");
        assertNotNull(result);
        assertEquals("VEN", result.getString("subentry"));
        log.info("查询结果: {}", result);
    }
}
