package com.huadian.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xtwang
 * @des 聊天会话
 * @date 2025/2/11 下午3:03
 */
@Data
@Accessors(chain = true)
public class ChatSession {

    @Schema(description = "会话id")
    private String sessionId;

    @Schema(description = "会话名称")
    private String sessionName;

    @Schema(description = "上次更新时间")
    private String lastTime;
}
