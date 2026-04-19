package com.huadian.config;

import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.mysqlds")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create()
                //.type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "pgDatasource")
    @ConfigurationProperties(prefix = "spring.datasource.pgds")
    public DataSource postgresqlDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
}
