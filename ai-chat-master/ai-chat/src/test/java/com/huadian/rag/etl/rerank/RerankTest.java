package com.huadian.rag.etl.rerank;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;

import com.huadian.rag.etl.splitter.ChineseTokenTextSplitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import java.util.List;

/**
 * 检索重排序（Rerank）功能测试类
 * 演示如何结合DashScope的嵌入模型、重排序模型和聊天模型实现高质量检索增强生成（RAG）
 */
//@SpringBootTest
public class RerankTest {

    /**
     * 初始化测试数据
     * 1. 读取文本文件
     * 2. 使用中文分词器分割文档
     * 3. 将分割后的文档向量化并存储
     */
    //@BeforeEach
    public void init(@Autowired VectorStore vectorStore,
                     @Value("classpath:rag/terms-of-service.txt") Resource resource) {
        // 读取文本文件（如服务条款文档）
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename", resource.getFilename());
        List<Document> documents = textReader.read();

        // 使用自定义中文分词器分割文档（优化中文处理）
        ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter(
                80,   // 每个分块最大token数
                10,   // 分块重叠token数
                5,    // 最小分块token数
                10000,// 最大输入长度
                true  // 是否启用详细日志
        );
        List<Document> chunkedDocuments = splitter.apply(documents);

        // 向量化并存储文档（自动调用DashScope嵌入模型）
        vectorStore.add(chunkedDocuments);
    }

    /**
     * 测试检索重排序流程
     * 1. 通过向量存储检索相关文档
     * 2. 使用DashScope重排序模型优化结果
     * 3. 生成最终回答
     */
    //@Test
    public void testRerank(@Autowired VectorStore vectorStore,
                           @Autowired DashScopeRerankModel dashScopeRerankModel,
                           @Autowired DashScopeChatModel dashScopeChatModel) {

        // 构建ChatClient（使用DashScope聊天模型）
        ChatClient chatClient = ChatClient.builder(dashScopeChatModel).build();

        // 创建检索重排序顾问（核心组件）
        RetrievalRerankAdvisor retrievalRerankAdvisor = new RetrievalRerankAdvisor(
                vectorStore,
                dashScopeRerankModel,  // 使用gte-rerank-v2模型
                SearchRequest.builder()
                        .topK(200)  // 初步检索200个候选文档
                        .build()
        );

        // 执行RAG流程（带重排序优化）
        String content = chatClient.prompt()
                .user("退费费用？")  // 用户查询
                .advisors(retrievalRerankAdvisor)  // 注入重排序逻辑
                .call()
                .content();

        System.out.println("最终回答: " + content);
    }

    /**
     * 测试配置类
     * 初始化基于Ollama的向量存储（可替换为DashScopeEmbeddingModel）
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public VectorStore vectorStore(OllamaEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }
}