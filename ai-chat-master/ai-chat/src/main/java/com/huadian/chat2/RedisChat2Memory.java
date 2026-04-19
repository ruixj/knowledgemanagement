package com.huadian.chat2;

import com.huadian.chat2.ChatMessage;
import com.huadian.chat2.RedisConfig2;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author xtwang
 * @des RedisChatMemory
 */
@Component
@RequiredArgsConstructor
public class RedisChat2Memory implements ChatMemory {

    @Qualifier("msgRedisTemplate2")
    private final  RedisTemplate<String, Object> redisTemplate;
    private final RedisConfig2.RedisKeyPrefix keyPrefix;
    private final  int lastN = 10;

    @Override
    public void add(String conversationId, List<Message> messages) {
        //String sessionKey = keyPrefix.getSession() + conversationId;

        String messagesKey = keyPrefix.getMessages() + conversationId;
        try {
            for (Message message : messages) {
                ChatMessage chatMessage = new ChatMessage();
                MessageType messageType = message.getMessageType();
                String type = messageType.getValue();
                switch (type) {
                    case "user" ->  {
                        Map<String, Object> metadata = message.getMetadata();
                        String createdTime = (String) metadata.get("createdTime");
                        chatMessage.setId(UUID.randomUUID().toString());
                        chatMessage.setContent(message.getText());
                        chatMessage.setRole(type);
                        chatMessage.setCreatedAt(createdTime);
                        chatMessage.setSessionId(conversationId);

                    }
                    case "assistant" -> {

                        chatMessage.setId(UUID.randomUUID().toString());
                        chatMessage.setContent(message.getText());
                        chatMessage.setRole(type);

                        chatMessage.setSessionId(conversationId);
                    }
                    case "system" -> {
                        Map<String, Object> metadata = message.getMetadata();
                        chatMessage.setId(UUID.randomUUID().toString());
                        chatMessage.setContent(message.getText());
                        chatMessage.setRole(type);
                        chatMessage.setSessionId(conversationId);
                    }
                    default -> throw new IllegalArgumentException("Unknown message type: " + type);
                }
                redisTemplate.opsForList().rightPush(messagesKey, chatMessage);
            }
            // 使用List存储消息，新的消息添加到尾部
            //redisTemplate.opsForList().rightPushAll(messagesKey, messages);

            // 设置消息列表的过期时间
            //redisTemplate.expire(messagesKey, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);

            //log.debug("保存消息成功: session={}, message={}", message.getSessionId(), message.getId());
        } catch (Exception e) {
            //log.error("保存消息失败: session={}", message.getSessionId(), e);
            throw new RuntimeException("保存消息失败", e);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        String messagesKey = keyPrefix.getMessages() + conversationId;
        // 从 Redis 获取最新的 lastN 条消息
        List<Object>  Messages = redisTemplate.opsForList().range(messagesKey, -lastN, -1);
        if (Messages != null) {
            List<Message> messages = new ArrayList<>();
            for (Object message : Messages) {
                ChatMessage chatMessage = (ChatMessage)message;
                String type = chatMessage.getRole();
                switch (type) {
                    case "user" -> messages.add(new UserMessage(chatMessage.getContent()));
                    case "assistant" -> messages.add(new AssistantMessage(chatMessage.getContent()));
                    case "system" -> messages.add(new SystemMessage(chatMessage.getContent()));
                    default -> throw new IllegalArgumentException("Unknown message type: " + type);
                }
            }
            return messages;

        }

        return List.of();
    }

    @Override
    public void clear(String conversationId) {
        String messagesKey = keyPrefix.getMessages() + conversationId;
        redisTemplate.delete(messagesKey);
        //redisTemplate.delete(REDIS_KEY_PREFIX + conversationId);
    }

}