package com.qdw.task;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot Starter
 *
 * @author Frank Zhang
 */
@SpringBootApplication(scanBasePackages = {"com.qdw.task", "com.alibaba.cola"})
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
