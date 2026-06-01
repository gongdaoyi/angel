package com.angel;

import com.angel.finder.MultiIdEndpointFinder;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Angel Application - Spring Boot主启动类
 *
 * @author Angel Development Team
 * @version 1.0.0
 */
@Log4j2
@MapperScan("com.angel.mapper")
@SpringBootApplication
@EnableScheduling
@EnableSwagger2
public class AngelApplication implements CommandLineRunner {

    private final ApplicationContext applicationContext;

    public AngelApplication(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication.run(AngelApplication.class, args);
        log.info("Angel Application started successfully");
    }

    @Override
    public void run(String... args) {
        MultiIdEndpointFinder finder = new MultiIdEndpointFinder(applicationContext);
        List<Map<String, Object>> endpoints = finder.findEndpointsWithTargetParams();

        log.info("=== 接口清单 ===");
        endpoints.forEach(endpoint -> log.info("接口: {}", endpoint));
        log.info("=== 总共 {} 个接口 ===", endpoints.size());
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.angel.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(new BasicAuth("basicAuth"));
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("/swagger.*|/api-docs.*"))
                        .build()
        );
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");
        return Collections.singletonList(new SecurityReference("basicAuth", new AuthorizationScope[]{scope}));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Angel Application API")
                .description("RESTful API for Angel Application")
                .termsOfServiceUrl("http://localhost:8080")
                .version("1.0.0")
                .build();
    }

}
