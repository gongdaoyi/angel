package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AngelApplication {

    public static void main(String[] args) {

        SpringApplication.run(AngelApplication.class, args);
        System.out.println("AngelApplication 启动成功");
    }

}
