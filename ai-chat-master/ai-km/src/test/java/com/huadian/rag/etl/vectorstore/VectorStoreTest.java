package com.huadian.rag.etl.vectorstore;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 向量存储测试类
 * 演示如何使用VectorStore进行文档的向量化存储和语义搜索
 */
@SpringBootTest
public class VectorStoreTest {

    /**
     * 测试向量存储和语义搜索功能
     * @param vectorStore 自动注入的向量存储实例
     *                   支持文档的向量化存储和相似度搜索
     */
    @Test
    public void testVectorStore(@Autowired VectorStore vectorStore) {
        // 创建第一个文档：关于航班预订的条款
        Document doc = Document.builder()
                .text("""
          预订航班:
          - 通过我们的网站或移动应用程序预订。
          - 预订时需要全额付款。
          - 确保个人信息（姓名、ID 等）的准确性，因为更正可能会产生 25 的费用。
          """)
                .build();

        // 创建第二个文档：关于取消预订的条款
        Document doc2 = Document.builder()
                .text("""
          取消预订:
          - 最晚在航班起飞前 48 小时取消。
          - 取消费用：经济舱 75 美元，豪华经济舱 50 美元，商务舱 25 美元。
          - 退款将在 7 个工作日内处理。
          """)
                .build();

        // 将文档添加到向量存储中（会自动进行文本向量化）
        vectorStore.add(List.of(doc, doc2));

        // 构建搜索请求
        SearchRequest searchRequest = SearchRequest.builder()
                .query("退票")  // 搜索关键词
                .topK(2)       // 返回最相似的2个结果
                .similarityThreshold(0.5)  // 相似度阈值，过滤低分结果
                .build();

        // 执行相似度搜索
        List<Document> documents = vectorStore.similaritySearch(searchRequest);

        // 打印搜索结果
        for (Document document : documents) {
            System.out.println("匹配文档内容: \n" + document.getText());
            System.out.println("相似度得分: " + document.getScore());
            System.out.println("----------------------------------");
        }
    }
}
