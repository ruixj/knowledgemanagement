package com.huadian.chat2;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String sessionId;
    private String content;
    private String role;
    private String createdAt;

    public ChatMessage() {
        LocalDateTime localDateTime = LocalDateTime.now();

        // 定义日期时间格式
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //DateTimeFormatter.ISO_OFFSET_DATE
        // 使用formatter格式化LocalDateTime
        String formattedDateTime = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        this.createdAt = formattedDateTime;
    }

    public enum MessageType {
        USER, AI
    }
}