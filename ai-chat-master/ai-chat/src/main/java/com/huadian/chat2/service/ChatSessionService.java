package com.huadian.chat2.service;


import com.huadian.chat2.ChatMessage;
import com.huadian.chat2.ChatSession;
import com.huadian.chat2.repository.RedisChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("chatSessionService2")
public class ChatSessionService {

    @Autowired
    private RedisChatRepository redisChatRepository;

    @Autowired
    private AiChatService aiChatService;


    public List<ChatSession> getUserSessions(String userId) {
        return redisChatRepository.findSessionsByUserId(userId);
    }

    public ChatSession getSessionWithMessages(String sessionId, String userId) {
        ChatSession session = redisChatRepository.findSessionById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此会话");
        }

        // 加载会话的消息
        List<ChatMessage> messages = redisChatRepository.findMessagesBySessionId(sessionId);
        session.setMessages(messages);

        return session;
    }

    public ChatSession createSession(String userId, String firstMessage) {
        ChatSession session = new ChatSession();
        session.setId(redisChatRepository.generateSessionId());
        session.setUserId(userId);

        // 使用AI生成会话标题
        String title = aiChatService.generateSessionTitle(firstMessage);
        session.setTitle(title);

        // 保存会话
        redisChatRepository.saveSession(session);

//        // 保存第一条用户消息
//        ChatMessage userMessage = new ChatMessage();
//        userMessage.setId(redisChatRepository.generateMessageId());
//        userMessage.setSessionId(session.getId());
//
//        userMessage.setContent(firstMessage);
//        userMessage.setType(ChatMessage.MessageType.USER);
//        redisChatRepository.saveMessage(userMessage);
//
//        // 更新会话的消息列表
//        session.getMessages().add(userMessage);

        redisChatRepository.saveSession(session);

        log.info("创建新会话: {}", session.getId());
        return session;
    }



    public ChatMessage addMessageToSession(String sessionId, String content,
                                           ChatMessage.MessageType type, String userId) {
        ChatSession session = redisChatRepository.findSessionById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此会话");
        }

        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setId(redisChatRepository.generateMessageId());
        message.setSessionId(sessionId);
        message.setContent(content);
        //message.setType(type);

        // 保存消息
        redisChatRepository.saveMessage(message);

        // 更新会话时间戳
        session.updateTimestamp();
        redisChatRepository.saveSession(session);

        log.debug("添加消息到会话: session={}, type={}", sessionId, type);
        return message;
    }

    public List<ChatMessage> getConversationHistory(String sessionId) {
        List<ChatMessage> messages = redisChatRepository.findMessagesBySessionId(sessionId);

        return messages ;
    }

    public ChatSession updateSessionTitle(String sessionId, String newTitle, String userId) {
        ChatSession session = redisChatRepository.findSessionById(sessionId)
                .orElseThrow(() -> new RuntimeException("会话不存在"));

        if (!session.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此会话");
        }

        session.setTitle(newTitle);
        session.updateTimestamp();
        redisChatRepository.saveSession(session);

        return session;
    }

    public void deleteSession(String sessionId, String userId) {
        Optional<ChatSession> session = redisChatRepository.findSessionById(sessionId)
                ;

        if (!session.isPresent()){
            return;
        }

        if (!session.get().getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此会话");
        }


        redisChatRepository.deleteSession(sessionId, userId);

        log.info("删除会话: {}", sessionId);
    }

    public long getSessionMessageCount(String sessionId) {
        return redisChatRepository.getMessageCount(sessionId);
    }
}