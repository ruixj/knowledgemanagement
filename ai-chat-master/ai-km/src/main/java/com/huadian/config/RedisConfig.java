package com.huadian.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.key.prefix.logins}")
    private String loginsKeyPrefix;



    @Bean
    public RedisKeyPrefix redisKeyPrefix() {
        return new RedisKeyPrefix(loginsKeyPrefix );
    }

    public static class RedisKeyPrefix {
        public String getLoginsKeyPrefix() {
            return loginsKeyPrefix;
        }

        public void setLoginsKeyPrefix(String loginsKeyPrefix) {
            this.loginsKeyPrefix = loginsKeyPrefix;
        }

        private String loginsKeyPrefix;

        public RedisKeyPrefix(String loginsPrefix   ) {
            this.loginsKeyPrefix = loginsPrefix;
        }



    }


    @Bean("redisTemplateObj")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建ObjectMapper并配置
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 使用GenericJackson2JsonRedisSerializer来序列化和反序列化value
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        // 设置key和value的序列化规则
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}