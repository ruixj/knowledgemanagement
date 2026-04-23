SELECT ldbz_dw2.zw,ldbz_dw2.dzzid,sys_organ.dzzmc, sys_organ.dzzjc,ldbz_dw.zw
FROM ldbz_dw2
LEFT JOIN ldbz_dw ON ldbz_dw2.dzzid = ldbz_dw.dzzid
JOIN sys_organ ON sys_organ.dzzid = ldbz_dw2.dzzid;



-- 知识库表
CREATE TABLE sys_ai_knowledge_bases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by varchar(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    file_count int DEFAULT 0 ,

    INDEX idx_name (name),
    INDEX idx_created_by (created_by ),
    INDEX idx_created_at (created_at ),
    INDEX idx_updated_at (updated_at ) ,
    INDEX idx_name_created_at (created_by,name,created_at desc),
    INDEX idx_name_updated_at (created_by,name,updated_at desc)
);

-- 文件表
CREATE TABLE sys_ai_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL, -- 存储的文件名filetranfiletran
    original_name VARCHAR(255) NOT NULL, -- 原始文件名
    file_type VARCHAR(50), -- 文件类型：PDF、DOCX等
    mime_type VARCHAR(100), -- MIME类型
    size BIGINT NOT NULL, -- 文件大小（字节）
    storage_path VARCHAR(500) NOT NULL, -- 文件存储路径
    hash VARCHAR(64), -- 文件哈希值，用于去重
    status ENUM('uploading', 'processing', 'active', 'error', 'deleted') DEFAULT 'uploading',
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    uploaded_by varchar(255) NOT NULL,
    knowledge_base_id BIGINT NOT NULL,

    INDEX idx_knowledge_base (knowledge_base_id),
    INDEX idx_uploaded_by (uploaded_by),
    INDEX idx_status (status),
    INDEX idx_hash (hash)
);

-- 迁移：为 sys_ai_files 增加解析相关字段
ALTER TABLE sys_ai_files ADD COLUMN chunk_count INT DEFAULT 0 COMMENT '文件切片数量';
ALTER TABLE sys_ai_files ADD COLUMN parsing_status ENUM('pending','processing','done','error') DEFAULT 'pending' COMMENT '解析状态';
ALTER TABLE sys_ai_files ADD COLUMN is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用';

-- 文件元数据表（用于存储文件扩展信息）
CREATE TABLE sys_ai_file_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_id BIGINT NOT NULL,
    meta_key VARCHAR(100) NOT NULL,
    meta_value TEXT,
    INDEX idx_file_id (file_id),
    INDEX idx_meta_key (meta_key),
    UNIQUE KEY unique_file_meta (file_id, meta_key)
);