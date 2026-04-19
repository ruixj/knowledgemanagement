package com.huadian.controller;


import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.huadian.chat2.ChatMessage;
import com.huadian.chat2.ChatSession;
import com.huadian.chat2.RedisChat2Memory;
import com.huadian.chat2.service.ChatSessionService;
import com.huadian.messages.ErrorConstant;
import com.huadian.util.ErrorUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Slf4j
@RestController
@RequestMapping("/api/chat2")
public class AIChatController2 {

    @Autowired
    private ChatSessionService chatSessionService;
    private final RedisChat2Memory redisChatMemory;
    private final ChatClient chatClient;

    @Autowired
    public AIChatController2(@Qualifier("chatClientOpenAI2") ChatClient chatClient,
                            RedisChat2Memory redisChatMemory,
                             @Qualifier("chatSessionService2")ChatSessionService chatSessionService){
        this.chatClient = chatClient;
        this.redisChatMemory = redisChatMemory;
        this.chatSessionService = chatSessionService;
    }
    // 获取用户的所有会话列表
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getUserSessions(
            @RequestParam String userId) {
        log.info("获取用户 {} 的会话列表", userId);
        List<ChatSession> sessions = chatSessionService.getUserSessions(userId);
        return ResponseEntity.ok(sessions);
    }

    // 获取特定会话的详细信息（包含消息）
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ChatSession> getSession(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") String userId) {
        log.info("获取用户 {} 的会话 {}", userId, sessionId);
        ChatSession session = chatSessionService.getSessionWithMessages(sessionId, userId);
        return ResponseEntity.ok(session);
    }

    // 创建新会话
    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-User-Id") String userId) {
        log.info("用户 {} 创建新会话，消息: {}", userId, request.getMessage());
        ChatSession session = chatSessionService.createSession(userId, request.getMessage());
        return ResponseEntity.ok(session);
    }

    // 发送消息
//    @PostMapping("/sessions/{sessionId}/messages")
//    public ResponseEntity<ChatMessage> sendMessage(
//            @PathVariable String sessionId,
//            @Valid @RequestBody SendMessageRequest request,
//            @RequestHeader("X-User-Id") String userId) {
//        log.info("用户 {} 在会话 {} 发送消息: {}", userId, sessionId, request.getMessage());
//
//        // 保存用户消息
//        ChatMessage userMessage = chatSessionService.addMessageToSession(
//                sessionId, request.getMessage(), ChatMessage.MessageType.USER, userId);
//
//        // 获取对话历史
//        String history = chatSessionService.getConversationHistory(sessionId);
//
//        // 生成AI回复
//        String aiResponse = chatSessionService.generateResponse(request.getMessage(), history);
//
//        // 保存AI回复
//        ChatMessage aiMessage = chatSessionService.addMessageToSession(
//                sessionId, aiResponse, ChatMessage.MessageType.AI, userId);
//
//        return ResponseEntity.ok(aiMessage);
//    }

    // 删除会话
    @DeleteMapping("/sessions/{sessionId}")
    public ModelMap deleteSession(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") String userId) {
        log.info("用户 {} 删除会话 {}", userId, sessionId);
        chatSessionService.deleteSession(sessionId, userId);

        ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, "会话删除成功");
        return ret;
    }

    @PutMapping("/sessions/{sessionId}/title")
    public ModelMap updateSessionTitle(
            @PathVariable String sessionId,
            @RequestBody  Map map ,
            @RequestHeader("X-User-Id") String userId) {
        String title = (String) map.get("title");
        log.info("用户 {} 更新会话 {} 的标题为: {}", userId, sessionId, title );
        ChatSession session = chatSessionService.updateSessionTitle(sessionId, title, userId);
        ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, session);
        return ret;
    }

    @Operation(summary = "流式回答聊天")
    @PostMapping (value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(
            @RequestBody Map map
    )
    {
        String  message = (String) map.get("message");
        String  userId = (String) map.get("userId");
        String  sessionId = (String) map.get("sessionId");
        String  time      = (String) map.get("time");
        Assert.notNull(message, "message不能为空");
        Assert.notNull(userId, "userId不能为空");

        // 默认生成一个会话
        if (StrUtil.isBlank(sessionId)) {
            ChatSession session = chatSessionService.createSession(userId, message);
            sessionId = session.getId();
        }

        UserMessage userMessage = UserMessage
                .builder()
                .text(message)
                .metadata(
                        Map.of(
                                "createdTime",time
                        )
                ).build();
        Prompt prompt = new Prompt(userMessage);

        String finalSessionId = sessionId;
        return chatClient
                .prompt(prompt)
                //.user(message)
                .advisors(advisorSpec ->
                        advisorSpec
                                .param(CONVERSATION_ID, finalSessionId)
                                .param(TOP_K, 100)
                )
                .stream()
                .chatResponse();
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/ai/messages")
    public ModelMap getMessages(@RequestParam String sessionId) {
        Assert.notNull(sessionId, "sessionId不能为空");
        List<ChatMessage> conversationHistory = chatSessionService.getConversationHistory(sessionId);
        ModelMap ret = ErrorUtil.retErrMsg(ErrorConstant.SUCCESS, conversationHistory);
        return ret;
    }


    // 请求对象定义
    public static class CreateSessionRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class SendMessageRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}