package com.angel;

import com.angel.utils.BladeJavaTimeModule;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Jackson JSON序列化配置类
 *
 * <p>配置全局的ObjectMapper，统一日期格式、时区、序列化行为等</p>
 * <p>
 * 配置项包括：
 * <ul>
 *   <li>日期格式：yyyy-MM-dd HH:mm:ss</li>
 *   <li>时区：系统默认时区</li>
 *   <li>地区：中国</li>
 *   <li>失败容忍：忽略未知属性、空Bean不报错</li>
 *   <li>单引号支持：允许使用单引号</li>
 *   <li>特殊字符支持：允许控制字符和反斜杠转义</li>
 * </ul>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@Configuration
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class JacksonConfiguration {

    /**
     * 默认日期时间格式
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 配置全局ObjectMapper Bean
     *
     * <p>使用@Primary注解确保此配置优先于Spring Boot默认配置</p>
     *
     * @param builder Jackson2ObjectMapperBuilder构建器
     * @return 配置好的ObjectMapper实例
     */
    @Primary
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        log.debug("开始配置Jackson ObjectMapper");

        // 设置默认日期格式
        builder.simpleDateFormat(DATE_TIME_FORMAT);

        // 创建ObjectMapper
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 设置地区为中国
        objectMapper.setLocale(Locale.CHINA);
        log.debug("设置地区: {}", Locale.CHINA);

        // 去掉默认的时间戳格式，使用日期格式化
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 设置为中国时区
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        objectMapper.setTimeZone(timeZone);
        log.debug("设置时区: {}", timeZone.getID());

        // 序列化时，日期的统一格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.CHINA);
        objectMapper.setDateFormat(dateFormat);
        log.debug("设置日期格式: {}", DATE_TIME_FORMAT);

        // 序列化处理：允许未转义的控制字符和反斜杠
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        objectMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);

        // 注册所有可用模块
        objectMapper.findAndRegisterModules();

        // 失败容忍配置
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.debug("配置失败容忍模式");

        // 单引号处理：允许JSON中使用单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        // 反序列化时，属性不存在的兼容处理
        objectMapper.getDeserializationConfig()
                .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 自定义日期格式化模块
        objectMapper.registerModule(new BladeJavaTimeModule());
        objectMapper.findAndRegisterModules();

        log.info("Jackson ObjectMapper配置完成");
        return objectMapper;
    }

}

