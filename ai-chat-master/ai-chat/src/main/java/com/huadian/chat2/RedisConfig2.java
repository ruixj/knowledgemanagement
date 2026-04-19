package com.huadian.chat2;


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
public class RedisConfig2 {

    @Value("${redis.key.prefix.session}")
    private String sessionKeyPrefix;

    @Value("${redis.key.prefix.user-sessions}")
    private String userSessionsKeyPrefix;

    @Value("${redis.key.prefix.messages}")
    private String messagesKeyPrefix;

    @Bean("msgRedisTemplate2")
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

    @Bean
    public RedisKeyPrefix redisKeyPrefix() {
        return new RedisKeyPrefix(sessionKeyPrefix, userSessionsKeyPrefix, messagesKeyPrefix);
    }

    public static class RedisKeyPrefix {
        private final String session;
        private final String userSessions;
        private final String messages;

        public RedisKeyPrefix(String session, String userSessions, String messages) {
            this.session = session;
            this.userSessions = userSessions;
            this.messages = messages;
        }

        public String getSession() {
            return session;
        }

        public String getUserSessions() {
            return userSessions;
        }

        public String getMessages() {
            return messages;
        }
    }
}