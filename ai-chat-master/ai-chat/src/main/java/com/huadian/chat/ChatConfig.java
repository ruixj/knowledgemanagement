package com.huadian.chat;


import com.huadian.chat2.RedisChat2Memory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;

import org.springframework.ai.openai.OpenAiChatModel;
//import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xtwang
 * @des AI聊天配置
 * @date 2025/2/11 上午9:39
 */
@Configuration
public class ChatConfig {
//    @Bean(name = "chatClientOllama")
//    public ChatClient chatClient(OllamaChatModel ollamaChatModel,
//                                 RedisChatMemory redisChatMemory,
//                                 MilvusVectorStore milvusVectorStore) {
//        return ChatClient.builder(ollamaChatModel)
//                .defaultSystem("你是一个RAG知识库问答机器人，致力于解决用户提出的问题，并给出详细的解决方案")
//                .defaultAdvisors(
//                        MessageChatMemoryAdvisor.builder(redisChatMemory).build(),
//                        QuestionAnswerAdvisor.builder( milvusVectorStore).build()
//                )
//                .build();
//
//    }
    @Bean(name = "chatClientOpenAI")
    public ChatClient chatClientOpenAI(OpenAiChatModel openAiChatModel,
                                       RedisChatMemory redisChatMemory,
                                       @Qualifier("pgVectorStore") VectorStore vectorStore) {
        String systemPrompt = """
            你是一个专业的党建AI助手，专门回答关于中国共产党历史、理论、政策、党务工作等方面的问题。
            请用专业、准确、积极的语言回答用户的问题，体现党建工作的严肃性和专业性。
            
            回答要求：
            1. 内容准确，符合党的理论和政策
            2. 语言规范，体现党建工作的特点
            3. 积极正面，弘扬主旋律
            4. 如果用户的问题超出党建范围，请礼貌地引导到相关主题
            """;
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(redisChatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .build();
    }

    @Bean(name = "chatClientOpenAI2")
    public ChatClient chatClientOpenAI2(OpenAiChatModel openAiChatModel,
                                        RedisChat2Memory redisChatMemory,
                                        @Qualifier("pgVectorStore") VectorStore vectorStore) {
        String systemPrompt = """
            你是一个专业的党建AI助手，专门回答关于中国共产党历史、理论、政策、党务工作等方面的问题。
            请用专业、准确、积极的语言回答用户的问题，体现党建工作的严肃性和专业性。
            
            回答要求：
            1. 内容准确，符合党的理论和政策
            2. 语言规范，体现党建工作的特点
            3. 积极正面，弘扬主旋律
            4. 如果用户的问题超出党建范围，请礼貌地引导到相关主题
            """;
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(redisChatMemory).build(),
                        QuestionAnswerAdvisor.builder(vectorStore).build()
                )
                .build();
    }

//    @Bean(name="windowChatMemory")
//    public ChatMemory chatMemory(@Qualifier("redisRository") ChatMemoryRepository redisRository){
//        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
//                .maxMessages(10)
//                .chatMemoryRepository(redisRository)
//                .build();
//        return memory;
//    }

}
