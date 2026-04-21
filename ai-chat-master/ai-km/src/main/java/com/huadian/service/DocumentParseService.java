package com.huadian.service;

import com.huadian.dao.SysKMMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.FileSystemResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.huadian.rag.etl.splitter.ChineseTokenTextSplitter;

@Service
public class DocumentParseService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParseService.class);

    @Autowired
    private SysKMMapper kmMapper;

    @Autowired
    @Qualifier("pgVectorStore")
    private VectorStore vectorStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * 异步解析文件：读取 → 分片 → Embedding → 写入向量库 → 更新状态
     */
    @Async
    public void parseAsync(Long fileId) {
        Map<String, Object> fileInfo = kmMapper.getFileById(fileId);
        if (fileInfo == null) {
            log.warn("parseAsync: file not found, id={}", fileId);
            return;
        }

        String storagePath = (String) fileInfo.get("storage_path");
        Object kbIdObj = fileInfo.get("knowledge_base_id");
        String knowledgeBaseId = kbIdObj != null ? kbIdObj.toString() : "";

        // 构建文件绝对路径
        ApplicationHome home = new ApplicationHome(getClass());
        File jarDir = home.getSource();
        String rootDir = jarDir != null ? jarDir.getParentFile().toString() : System.getProperty("user.dir");
        String absolutePath = rootDir + storagePath;

        log.info("Parsing file id={}, path={}", fileId, absolutePath);

        try {
            // 1. 标记为 processing
            Map<String, Object> processingParams = new HashMap<>();
            processingParams.put("id", fileId);
            processingParams.put("parsing_status", "processing");
            processingParams.put("chunk_count", 0);
            kmMapper.updateParseResult(processingParams);

            // 2. 读取文档（Tika 支持 PDF/Word/Excel/HTML/TXT 等）
            FileSystemResource resource = new FileSystemResource(absolutePath);
            TikaDocumentReader reader = new TikaDocumentReader(resource);
            List<Document> rawDocs = reader.get();
            log.info("Read {} raw documents from file id={}", rawDocs.size(), fileId);

            // 3. 分片
            ChineseTokenTextSplitter splitter = new ChineseTokenTextSplitter();
            List<Document> chunks = splitter.apply(rawDocs);
            log.info("Split into {} chunks for file id={}", chunks.size(), fileId);

            // 4. 为每个分片添加元数据
            String fileName = fileInfo.getOrDefault("original_name", "").toString();
            for (Document chunk : chunks) {
                chunk.getMetadata().put("file_id", fileId.toString());
                chunk.getMetadata().put("knowledge_base_id", knowledgeBaseId);
                chunk.getMetadata().put("file_name", fileName);
            }

            // 5. 使用 EmbeddingModel 批量生成向量
            List<String> texts = chunks.stream()
                    .map(Document::getText)
                    .collect(Collectors.toList());
            log.info("Generating embeddings for {} chunks via EmbeddingModel...", texts.size());
            List<float[]> embeddings = embeddingModel.embed(texts);
            log.info("Embedding done, dimension={}", embeddings.isEmpty() ? 0 : embeddings.get(0).length);

            // 6. 将向量写回 Document，再批量存入向量库
            List<Document> embeddedChunks = new ArrayList<>();
            for (int i = 0; i < chunks.size(); i++) {
                Document original = chunks.get(i);
                Document withEmbedding = Document.builder()
                        .id(original.getId())
                        .text(original.getText())
                        .metadata(original.getMetadata())
                        .embedding(embeddings.get(i))
                        .build();
                embeddedChunks.add(withEmbedding);
            }
            vectorStore.add(embeddedChunks);
            log.info("Saved {} chunks to vector store for file id={}", embeddedChunks.size(), fileId);

            // 7. 更新状态为 done，记录 chunk 数量
            Map<String, Object> doneParams = new HashMap<>();
            doneParams.put("id", fileId);
            doneParams.put("parsing_status", "done");
            doneParams.put("chunk_count", embeddedChunks.size());
            kmMapper.updateParseResult(doneParams);

            log.info("Parsing done, file id={}, chunks={}", fileId, embeddedChunks.size());

        } catch (Exception e) {
            log.error("Parsing failed, file id={}", fileId, e);
            Map<String, Object> errParams = new HashMap<>();
            errParams.put("id", fileId);
            errParams.put("parsing_status", "error");
            errParams.put("chunk_count", 0);
            kmMapper.updateParseResult(errParams);
        }
    }
}
