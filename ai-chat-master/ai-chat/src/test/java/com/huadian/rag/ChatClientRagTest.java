package com.huadian.rag;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import java.util.List;

/**
 * Spring AI RAG功能集成测试类
 * 演示如何结合DashScope大模型和向量存储实现检索增强生成
 * 核心功能包括：基础检索增强、查询重写、多语言翻译增强等
 */
@SpringBootTest
public class ChatClientRagTest {

    private ChatClient chatClient;

    /**
     * 初始化测试数据
     * 向向量存储中添加航班预订和取消政策的文档
     */
    //@BeforeEach
    public void init(@Autowired DashScopeChatModel chatModel,
                     @Autowired VectorStore vectorStore) {
        // 构建测试文档
        Document doc = Document.builder()
                .text("""
                        预订航班:
                        - 通过我们的网站或移动应用程序预订。
                        - 预订时需要全额付款。
                        - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生25的费用。
                        """)
                .build();
        Document doc2 = Document.builder()
                .text("""
                        取消预订:
                        - 最晚在航班起飞前48小时取消。
                        - 取消费用：经济舱75美元，豪华经济舱50美元，商务舱25美元。
                        - 退款将在7个工作日内处理。
                        """)
                .build();

        // 将文档向量化并存储
        vectorStore.add(List.of(doc, doc2));
    }

    /**
     * 基础RAG测试
     * 使用QuestionAnswerAdvisor实现简单的检索增强
     */
    //@Test
    public void testRag(@Autowired DashScopeChatModel dashScopeChatModel,
                        @Autowired VectorStore vectorStore) {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build()) // 添加日志记录
                .build();

        String content = chatClient.prompt()
                .user("退票需要多少费用？")
                .advisors(
                        // 组合使用日志顾问和检索增强顾问
                        SimpleLoggerAdvisor.builder().build(),
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5) // 返回最相关的5个文档片段
                                        .similarityThreshold(0.6) // 相似度阈值0.6
                                        .build())
                                .build()
                )
                .call()
                .content();

        System.out.println("RAG回答结果: " + content);
    }

    /**
     * 带过滤条件的RAG测试
     * 演示如何通过元数据过滤检索结果
     */
    //@Test
    public void testRag2(@Autowired DashScopeChatModel dashScopeChatModel,
                         @Autowired VectorStore vectorStore) {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();

        String content = chatClient.prompt()
                .user("退票需要多少费用？")
                .advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)
                                        .similarityThreshold(0.1) // 更宽松的相似度阈值
                                        // 可添加元数据过滤：.filterExpression("category == 'cancellation'")
                                        .build())
                                .build()
                )
                .call()
                .content();

        System.out.println("带过滤的RAG回答: " + content);
    }

    /**
     * 高级RAG流程测试
     * 使用RetrievalAugmentationAdvisor实现多阶段增强
     */
    //@Test
    public void testRag3(@Autowired VectorStore vectorStore,
                         @Autowired DashScopeChatModel dashScopeChatModel) {
        chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();

        // 构建完整的检索增强流程
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.0) // 接受所有相似度结果
                        .vectorStore(vectorStore)
                        .build())
                // 上下文增强配置
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(false) // 禁止无上下文回答
                        .emptyContextPromptTemplate(new PromptTemplate("未找到相关知识，请重新提问"))
                        .build())
                // 查询重写转换器（去除情绪化内容）
                .queryTransformers(RewriteQueryTransformer.builder()
                        .chatClientBuilder(ChatClient.builder(dashScopeChatModel))
                        .targetSearchSystem("航空票务助手")
                        .build())
                // 查询翻译转换器（支持多语言）
                .queryTransformers(TranslationQueryTransformer.builder()
                        .chatClientBuilder(ChatClient.builder(dashScopeChatModel))
                        .targetLanguage("english")
                        .build())
                // 文档后处理器（可添加日志或过滤）
                .documentPostProcessors((query, documents) -> {
                    System.out.println("优化后查询: " + query.text());
                    System.out.println("检索到文档数: " + documents.size());
                    return documents;
                })
                .build();

        // 测试包含情绪化表达的查询
        String answer = chatClient.prompt()
                .advisors(retrievalAugmentationAdvisor)
                .user("我今天心情不好，不想去玩了，能不能告诉我退票需要多少钱？")
                .call()
                .content();

        System.out.println("高级RAG回答: " + answer);
    }

    /**
     * 测试配置类
     * 初始化内存型向量存储
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public VectorStore vectorStore(DashScopeEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }
}