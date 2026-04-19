package com.huadian.rag.etl.embedding;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * 嵌入向量(Embedding)测试类
 * 用于测试不同AI模型的文本嵌入(Embedding)能力
 */
@SpringBootTest
public class EmbaddingTest {

    /**
     * 测试Ollama本地模型的嵌入能力
     * @param ollamaEmbeddingModel 自动注入的Ollama嵌入模型实例
     *                            使用本地运行的Ollama服务生成文本向量
     */
    @Test
    public void testEmbadding(@Autowired OllamaEmbeddingModel ollamaEmbeddingModel) {
        // 对文本"我叫小小"生成嵌入向量
        float[] embedded = ollamaEmbeddingModel.embed("我叫小小");

        // 输出向量维度和内容
        System.out.println("向量维度长度: " + embedded.length);  // 打印向量维度数
        System.out.println("向量内容: " + Arrays.toString(embedded));  // 打印向量具体数值
    }

    /**
     * 测试阿里云百炼模型的嵌入能力
     * @param embeddingModel 自动注入的DashScope嵌入模型实例
     *                       使用阿里云百炼平台生成文本向量
     */
    @Test
    public void testAliEmbadding(@Autowired DashScopeEmbeddingModel embeddingModel) {
        // 对文本"我叫小小"生成嵌入向量
        float[] embedded = embeddingModel.embed("我叫小小");

        // 输出向量维度和内容
        System.out.println("向量维度长度: " + embedded.length);  // 打印向量维度数
        System.out.println("向量内容: " + Arrays.toString(embedded));  // 打印向量具体数值
    }
}