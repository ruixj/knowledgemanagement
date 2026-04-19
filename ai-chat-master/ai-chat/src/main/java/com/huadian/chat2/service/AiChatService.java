package com.huadian.chat2.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class AiChatService {

    private final ChatClient chatClient;

    @Autowired
    public AiChatService(@Qualifier("chatClientOpenAI2") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

//    public String generateResponse(String userMessage, String conversationHistory) {
//        String systemPrompt = """
//            你是一个专业的党建AI助手，专门回答关于中国共产党历史、理论、政策、党务工作等方面的问题。
//            请用专业、准确、积极的语言回答用户的问题，体现党建工作的严肃性和专业性。
//
//            回答要求：
//            1. 内容准确，符合党的理论和政策
//            2. 语言规范，体现党建工作的特点
//            3. 积极正面，弘扬主旋律
//            4. 如果用户的问题超出党建范围，请礼貌地引导到相关主题
//
//            以下是对话历史：
//            %s
//
//            用户问题：%s
//            """.formatted(conversationHistory, userMessage);
//
//        ChatResponse response = chatClient.prompt()
//                .system(systemPrompt)
//                .user(userMessage)
//                .options(OpenAiChatOptions.builder()
//                        .temperature(0.7)
//                        .maxTokens(1000)
//                        .build())
//                .call()
//                .chatResponse();
//
//        return response.getResult().getOutput().getContent();
//    }

    public String generateSessionTitle(String firstMessage) {
//        String prompt = """
//            根据用户的第一个消息生成一个简洁的会话标题（不超过15个字）。
//            用户消息：%s
//            只需返回标题内容，不需要其他说明。
//            """.formatted(firstMessage);
//
//        return chatClient.prompt()
//                .user(prompt)
//                .call()
//                .content();

        String title = firstMessage.length() >= 15 ? firstMessage.substring(0, 15) : firstMessage;
        return title;
    }
}