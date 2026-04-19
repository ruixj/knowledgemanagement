package com.huadian;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author xtwang
 * @des
 */
@SpringBootApplication
//@MapperScan("com.huadian.dao")
public class AIChatApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AIChatApplication.class);
    }
    public static void main(String[] args) {
        SpringApplication.run(AIChatApplication.class, args);
    }
}