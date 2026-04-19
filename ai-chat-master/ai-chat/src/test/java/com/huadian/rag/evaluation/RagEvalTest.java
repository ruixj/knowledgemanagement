package com.huadian.rag.evaluation;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * RAG（检索增强生成）效果评估测试类
 * 演示如何评估AI回答与用户问题及检索内容的相关性
 */
//@SpringBootTest
public class RagEvalTest {

    /**
     * RAG流程相关性评估测试
     * 完整流程：文档检索 -> 生成回答 -> 相关性评估
     */
    //@Test
    public void testRag(@Autowired VectorStore vectorStore,
                        @Autowired DashScopeChatModel dashScopeChatModel) {

        // 构建测试知识库文档（航班预订相关条款）
        List<Document> documents = List.of(
                new Document("""
                        1. 预订航班
                        - 通过我们的网站或移动应用程序预订。
                        - 预订时需要全额付款。
                        - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生25的费用。
                        """),
                new Document("""
                        2. 更改预订
                        - 允许在航班起飞前24小时更改。
                        - 通过在线更改或联系我们的支持人员。
                        - 改签费：经济舱50，豪华经济舱30，商务舱免费。
                        """),
                new Document("""
                        3. 取消预订
                        - 最晚在航班起飞前48小时取消。
                        - 取消费用：经济舱75美元，豪华经济舱50美元，商务舱25美元。
                        - 退款将在7个工作日内处理。
                        """));

        // 将文档存入向量数据库
        vectorStore.add(documents);

        // 构建RAG增强顾问（负责检索相关文档）
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor =
                RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .build())
                        .build();

        // 测试查询（与文档内容无关的问题）
        String query = "我叫什么名字";

        // 执行RAG流程
        ChatResponse chatResponse = ChatClient.builder(dashScopeChatModel)
                .build()
                .prompt(query)
                .advisors(retrievalAugmentationAdvisor)
                .call()
                .chatResponse();

        /**
         * 构建评估请求参数：
         * 1. 原始用户问题
         * 2. RAG流程检索到的上下文文档
         * 3. AI生成的最终回答
         */
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                query,
                chatResponse.getMetadata().get(RetrievalAugmentationAdvisor.DOCUMENT_CONTEXT),
                chatResponse.getResult().getOutput().getText()
        );

        // 使用DashScope模型作为评估器
        RelevancyEvaluator evaluator = new RelevancyEvaluator(
                ChatClient.builder(dashScopeChatModel)
        );

        // 执行相关性评估
        EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);

        // 输出评估结果和AI回答
        System.out.println("=== 评估结果 ===");
        System.out.println(evaluationResponse);
        System.out.println("\n=== AI回答 ===");
        System.out.println(chatResponse.getResult().getOutput().getText());
    }

    /**
     * 测试配置类
     * 初始化基于Ollama的向量存储
     */
    @TestConfiguration
    static class TestConfig {
        @Bean
        public VectorStore vectorStore(OllamaEmbeddingModel embeddingModel) {
            return SimpleVectorStore.builder(embeddingModel).build();
        }
    }
}