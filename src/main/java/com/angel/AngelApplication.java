package com.angel;

import com.angel.finder.MultiIdEndpointFinder;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.unit.DataSize;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;
import java.util.List;
import java.util.Map;

/**
 * Angel Application - Spring Boot主启动类
 *
 * <p>提供媒体转换、语音识别、OCR识别等功能的Web应用</p>
 * <p>
 * 主要功能包括：
 * <ul>
 *   <li>媒体转换（视频、音频转码）</li>
 *   <li>语音转文字（集成讯飞API）</li>
 *   <li>OCR文字识别</li>
 *   <li>PDF处理（合并、拆分）</li>
 *   <li>Excel处理</li>
 * </ul>
 *
 * @author Angel Development Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Log4j2
@MapperScan("com.angel.mapper")
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class AngelApplication implements CommandLineRunner {

    /**
     * 文件上传最大大小：2GB
     */
    private static final long MAX_FILE_SIZE_GB = 2;
    /**
     * 请求最大大小：2GB
     */
    private static final long MAX_REQUEST_SIZE_GB = 2;
    /**
     * Spring应用上下文
     */
    private final ApplicationContext applicationContext;

    /**
     * 构造函数
     *
     * @param applicationContext Spring应用上下文
     */
    public AngelApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 应用主入口方法
     *
     * <p>启动Spring Boot应用，并执行相关初始化操作</p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AngelApplication.class, args);
        log.info("Angel Application started successfully");
    }

    /**
     * Spring Boot启动后执行的方法
     *
     * <p>扫描所有Controller中包含特定参数的接口端点，并输出清单</p>
     *
     * @param args 命令行参数
     * @throws Exception 执行过程中的异常
     */
    @Override
    public void run(String... args) throws Exception {
        // 扫描所有包含指定参数的接口端点
        MultiIdEndpointFinder finder = new MultiIdEndpointFinder(applicationContext);
        List<Map<String, Object>> endpoints = finder.findEndpointsWithTargetParams();

        log.info("=== 带有目标参数的接口 ===");
        endpoints.forEach(endpoint -> log.info("接口: {}", endpoint));
        log.info("=== 总共 {} 个接口 ===", endpoints.size());
    }

    /**
     * 配置文件上传限制
     *
     * <p>支持最大2GB的文件上传</p>
     *
     * @return 多部分配置元素
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize(DataSize.ofGigabytes(MAX_REQUEST_SIZE_GB));
        factory.setMaxFileSize(DataSize.ofGigabytes(MAX_FILE_SIZE_GB));
        return factory.createMultipartConfig();
    }

    /**
     * 配置Swagger API文档
     *
     * <p>访问地址：http://localhost:8080/swagger-ui.html#/</p>
     *
     * @return Swagger Docket配置
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.angel.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 配置Swagger API信息
     *
     * @return API信息对象
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Angel Application API Documentation")
                .description("RESTful API documentation for Angel Application")
                .termsOfServiceUrl("http://localhost:8080")
                .version("1.0.0")
                .build();
    }

}
