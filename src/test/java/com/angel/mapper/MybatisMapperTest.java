package com.angel.mapper;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * MybatisMapper 纯JUnit单元测试（使用Mock）
 *
 * @author Angel Development Team
 * @version 1.0.0
 */
@Log4j2
@ExtendWith(MockitoExtension.class)
@DisplayName("MybatisMapper 单元测试")
class MybatisMapperTest {

    @Mock
    private MybatisMapper mybatisMapper;

    @BeforeEach
    void setUp() {
        log.info("=== 开始执行 MybatisMapper 单元测试 ===");
    }

    @Test
    @DisplayName("应该成功查询国籍字典 - 子项值为'VEN'（委内瑞拉）")
    void dictTest() {
        // Given - 设置 Mock 返回值
        JSONObject mock = new JSONObject();
        mock.put("dict_prompt", "委内瑞拉");
        mock.put("subentry", "VEN");
        when(mybatisMapper.qryNationalityDict(anyString())).thenReturn(mock);

        // When
        JSONObject result = mybatisMapper.qryNationalityDict("VEN");

        // Then
        assertNotNull(result, "查询结果不应为null");
        assertEquals("委内瑞拉", result.getString("dict_prompt"));
        assertEquals("VEN", result.getString("subentry"));

        verify(mybatisMapper, times(1)).qryNationalityDict("VEN");
        log.info("✓ 测试通过：成功查询到VEN");
    }

}