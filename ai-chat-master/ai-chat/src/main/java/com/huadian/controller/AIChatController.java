package com.huadian.controller;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.huadian.chat.ChatSession;
import com.huadian.chat.ChatSessionService;
import com.huadian.chat.RedisChatMemory;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


/**
 * @author xtwang
 * @des
 * @date 2025/2/6 上午10:47
 */
@RestController
@RequestMapping("/api/chat")
@OpenAPIDefinition(
        info = @Info(title = "Chat API", version = "1.0",
                     description = "API for chat operations")
)
@Tag(name = "AI聊天", description = "集成机器人角色设定，会话存储，聊天记录持久化")
//@RequiredArgsConstructor

public class AIChatController {
    private final ChatClient chatClient;
    private final RedisChatMemory redisChatMemory;
    private final ChatSessionService chatSessionService;

    @Autowired
    public AIChatController(@Qualifier("chatClientOpenAI") ChatClient chatClient,
                            RedisChatMemory redisChatMemory,
                            ChatSessionService chatSessionService){
         this.chatClient = chatClient;
         this.redisChatMemory = redisChatMemory;
         this.chatSessionService = chatSessionService;
    }
    @Operation(summary = "流式回答聊天")
    @GetMapping(value = "/ai/generateStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> generateStream(
            @RequestParam(value = "message", defaultValue = "讲个笑话") String message,
            @RequestParam String sessionId,
            @RequestParam String userId,
            @RequestParam String time
    )
    {
        Assert.notNull(message, "message不能为空");
        Assert.notNull(userId, "userId不能为空");

        // 默认生成一个会话
        if (StrUtil.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            ChatSession chatSession = new ChatSession()
                    .setSessionId(sessionId)
                    .setSessionName(
                            message.length() >= 15 ? message.substring(0, 15) : message
                    );
            chatSessionService.saveSession(chatSession, userId);
        }

        UserMessage userMessage = UserMessage
                .builder()
                .text(message)
                .metadata(
                        Map.of(
                                "createdTime",time,
                                "id",UUID.randomUUID().toString()
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
    public ResponseEntity<List<Message>> getMessages(@RequestParam String sessionId) {
        Assert.notNull(sessionId, "sessionId不能为空");
        return ResponseEntity.ok(redisChatMemory.get(sessionId));
    }

    @Operation(summary = "获取会话列表")
    @GetMapping("/ai/sessions")
    public ResponseEntity<List<ChatSession>> getSessions(@RequestParam String userId) {
        Assert.notNull(userId, "userId不能为空");
        return ResponseEntity.ok(chatSessionService.getSessions(userId));
    }


    @Operation(summary = "普通聊天")
    @GetMapping(value = "/ai/generate")
    public ResponseEntity<String> generate(@RequestParam(value = "message", defaultValue = "讲个笑话") String message,
                                           @RequestParam String sessionId,
                                           @RequestParam String userId) {
        Assert.notNull(message, "message不能为空");
        Assert.notNull(userId, "userId不能为空");
        // 默认生成一个会话
        if (StrUtil.isBlank(sessionId)) {
            sessionId = UUID.randomUUID().toString();
            ChatSession chatSession = new ChatSession().setSessionId(sessionId).setSessionName(message.length() >= 15 ? message.substring(0, 15) : message);
            chatSessionService.saveSession(chatSession, userId);
        }
        String finalSessionId = sessionId;
        return ResponseEntity.ok(
                chatClient
                    .prompt()
                    .user(message)
                    .advisors(advisorSpec ->
                            advisorSpec
                                    .param(CONVERSATION_ID, finalSessionId)
                                    .param(TOP_K, 100)
                    ).call()
                    .content()
        );
    }
}