package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.MultipartConfigElement;

@MapperScan("com.angel.mapper")
@SpringBootApplication
@EnableScheduling
public class AngelApplication {

    public static void main(String[] args) {

        SpringApplication.run(AngelApplication.class, args);
        System.out.println("AngelApplication 启动成功");
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxRequestSize("2GB");
        factory.setMaxFileSize("2GB");
        return factory.createMultipartConfig();
    }

}
