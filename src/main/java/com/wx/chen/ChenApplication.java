package com.wx.chen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.wx.chen.mapper")
public class ChenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChenApplication.class, args);
    }
}
