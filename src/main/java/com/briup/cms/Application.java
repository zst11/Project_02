package com.briup.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.briup.cms.dao"})
@EnableScheduling  //开启定时器任务
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
