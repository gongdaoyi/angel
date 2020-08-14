package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AngelApplication {

    public static void main(String[] args) {

        SpringApplication.run(AngelApplication.class, args);
        System.out.println("AngelApplication 启动成功");
    }

}
