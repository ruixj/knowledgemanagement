package com.huadian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author xtwang
 * @des
 */
@SpringBootApplication
@EnableAsync
public class AIKnowledgeManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(AIKnowledgeManageApplication.class, args);
    }
}