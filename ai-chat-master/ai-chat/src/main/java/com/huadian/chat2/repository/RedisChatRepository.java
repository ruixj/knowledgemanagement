package com.huadian.chat2.repository;


import com.huadian.chat2.ChatMessage;
import com.huadian.chat2.ChatSession;

import com.huadian.chat2.RedisConfig2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Repository("redisRository")
public class RedisChatRepository  {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisConfig2.RedisKeyPrefix keyPrefix;

    // 会话数据过期时间：30天
    private static final long SESSION_EXPIRE_DAYS = 30;
    // 用户会话列表过期时间：60天
    private static final long USER_SESSIONS_EXPIRE_DAYS = 60;

    @Autowired
    public RedisChatRepository(RedisTemplate<String, Object> redisTemplate,
                               RedisConfig2.RedisKeyPrefix keyPrefix) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
    }

    // 会话相关操作
    public void saveSession(ChatSession session) {
        String sessionKey = keyPrefix.getSession() + session.getId();
        String userSessionsKey = keyPrefix.getUserSessions() + session.getUserId();

        try {
            // 保存会话详情
            redisTemplate.opsForValue().set(sessionKey, session, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);

            // 将会话ID添加到用户的会话列表中
            redisTemplate.opsForZSet().add(userSessionsKey, session.getId(),
                    session.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toEpochSecond());
            redisTemplate.expire(userSessionsKey, USER_SESSIONS_EXPIRE_DAYS, TimeUnit.DAYS);

            log.debug("保存会话成功: {}", sessionKey);
        } catch (Exception e) {
            log.error("保存会话失败: {}", sessionKey, e);
            throw new RuntimeException("保存会话失败", e);
        }
    }

    public Optional<ChatSession> findSessionById(String sessionId) {
        String sessionKey = keyPrefix.getSession() + sessionId;
        try {
            ChatSession session = (ChatSession) redisTemplate.opsForValue().get(sessionKey);
            if (session != null) {
                // 延长会话过期时间
                redisTemplate.expire(sessionKey, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);
            }
            return Optional.ofNullable(session);
        } catch (Exception e) {
            log.error("查找会话失败: {}", sessionKey, e);
            return Optional.empty();
        }
    }

    public List<ChatSession> findSessionsByUserId(String userId) {
        String userSessionsKey = keyPrefix.getUserSessions() + userId;
        try {
            // 获取用户的所有会话ID（按更新时间倒序）
            Set<Object> sessionIds = redisTemplate.opsForZSet()
                    .reverseRange(userSessionsKey, 0, -1);

            if (sessionIds == null || sessionIds.isEmpty()) {
                return new ArrayList<>();
            }

            // 批量获取会话详情
            List<ChatSession> sessions = sessionIds.stream()
                    .map(sessionId -> findSessionById((String) sessionId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());

            //延长用户会话列表的过期时间
            redisTemplate.expire(userSessionsKey, USER_SESSIONS_EXPIRE_DAYS, TimeUnit.DAYS);

            return sessions;
        } catch (Exception e) {
            log.error("查找用户会话列表失败: {}", userSessionsKey, e);
            return new ArrayList<>();
        }
    }

    public void deleteSession(String sessionId, String userId) {
        String sessionKey = keyPrefix.getSession() + sessionId;
        String userSessionsKey = keyPrefix.getUserSessions() + userId;
        String messagesKey = keyPrefix.getMessages() + sessionId;

        try {
            // 删除会话详情
            redisTemplate.delete(sessionKey);

            // 从用户会话列表中移除
            redisTemplate.opsForZSet().remove(userSessionsKey, sessionId);

            // 删除会话的所有消息
            redisTemplate.delete(messagesKey);

            log.debug("删除会话成功: {}", sessionKey);
        } catch (Exception e) {
            log.error("删除会话失败: {}", sessionKey, e);
            throw new RuntimeException("删除会话失败", e);
        }
    }

    // 消息相关操作
    public void saveMessage(ChatMessage message) {
        String messagesKey = keyPrefix.getMessages() + message.getSessionId();
        try {
            // 使用List存储消息，新的消息添加到尾部
            redisTemplate.opsForList().rightPush(messagesKey, message);

            // 设置消息列表的过期时间
            redisTemplate.expire(messagesKey, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);

            log.debug("保存消息成功: session={}, message={}", message.getSessionId(), message.getId());
        } catch (Exception e) {
            log.error("保存消息失败: session={}", message.getSessionId(), e);
            throw new RuntimeException("保存消息失败", e);
        }
    }




    public List<ChatMessage> findMessagesBySessionId(String sessionId) {
        String messagesKey = keyPrefix.getMessages() + sessionId;
        try {
            List<Object> messageObjects = redisTemplate.opsForList().range(messagesKey, 0, -1);
            if (messageObjects == null) {
                return new ArrayList<>();
            }

            // 转换消息对象
            List<ChatMessage> messages = messageObjects.stream()
                    .map(obj -> (ChatMessage) obj)
                    .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                    .collect(Collectors.toList());

            // 延长消息列表的过期时间
            redisTemplate.expire(messagesKey, SESSION_EXPIRE_DAYS, TimeUnit.DAYS);

            return messages;
        } catch (Exception e) {
            log.error("查找会话消息失败: {}", messagesKey, e);
            return new ArrayList<>();
        }
    }

    public long getMessageCount(String sessionId) {
        String messagesKey = keyPrefix.getMessages() + sessionId;
        try {
            Long count = redisTemplate.opsForList().size(messagesKey);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("获取消息数量失败: {}", messagesKey, e);
            return 0;
        }
    }

    // ID生成
    public static String generateSessionId() {
        return "session_" + UUID.randomUUID().toString();
    }

    public static String generateMessageId() {
        return "msg_" + UUID.randomUUID().toString();
    }

}