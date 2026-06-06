package com.typhoon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.typhoon.mapper")
public class TyphoonBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TyphoonBackendApplication.class, args);
    }
}