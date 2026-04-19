package com.huadian.config;

// MultipleChatModelConfig.java

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier; // 用于注入特定名称的 Bean
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 这是一个 Spring 配置类，用于根据 YAML 配置创建多个 OpenAiChatModel 和 ChatClient 实例。
 */
@Data
//@Configuration
public class MultipleChatModelConfig {

    /**
     * 定义一个内部类，用于映射 YAML 中单个 OpenAI 模型配置的属性。
     */
    @Data
    public static class OpenAiConfigProperties {
        private String name; // 模型的唯一名称
        private String apiKey; // API Key
        private String baseUrl; // Base URL
        private Map<String, Object> options; // 模型选项，如 model, temperature 等

    }

    /**
     * 定义一个内部类，用于绑定 `app.ai` 前缀下的所有自定义 AI 配置。
     * `openai-configs` 将被映射到一个 `OpenAiConfigProperties` 对象的列表。
     */
    @ConfigurationProperties(prefix = "app.ai")
    public static class AppAiProperties {
        private List<OpenAiConfigProperties> openaiConfigs;

        // Getters 和 Setters
        public List<OpenAiConfigProperties> getOpenaiConfigs() { return openaiConfigs; }
        public void setOpenaiConfigs(List<OpenAiConfigProperties> openaiConfigs) { this.openaiConfigs = openaiConfigs; }
    }

    /**
     * 将 AppAiProperties 注册为 Spring Bean，以便 Spring Boot 可以绑定 YAML 配置。
     * @return 绑定了配置的 AppAiProperties 实例
     */
    @Bean
    public AppAiProperties appAiProperties() {
        return new AppAiProperties();
    }

    /**
     * 根据 `AppAiProperties` 中定义的配置列表，动态创建并注册多个 ChatClient Bean。
     * 这些 ChatClient 将被存储在一个 Map 中，键是模型名称，值是对应的 ChatClient 实例。
     *
     * @param appAiProperties 包含所有自定义模型配置的属性对象
     * @return 一个 Map，其中包含所有命名 ChatClient 实例
     */
    @Bean
    public Map<String, ChatClient> namedChatClients(AppAiProperties appAiProperties) {
        // 如果没有配置，则返回一个空 Map
        if (appAiProperties.getOpenaiConfigs() == null) {
            System.out.println("未找到 app.ai.openai-configs 配置。");
            return Map.of();
        }

        System.out.println("正在创建自定义 ChatClient 实例...");
        return appAiProperties.getOpenaiConfigs().stream()
                .collect(Collectors.toMap(
                        OpenAiConfigProperties::getName, // 使用配置中定义的 'name' 作为 Map 的键
                        config -> {
                            System.out.println("  - 创建模型: " + config.getName() + " (Base URL: " + config.getBaseUrl() + ", Model: " + config.getOptions().get("model") + ")");

                            // 1. 创建 OpenAiApi 实例
                            OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(config.baseUrl).apiKey(config.apiKey).build();

                            // 2. 创建 OpenAiChatOptions 实例
                            // 从 Map 中提取选项，并转换为 OpenAiChatOptions 对象
                            OpenAiChatOptions.Builder optionsBuilder = OpenAiChatOptions.builder();
                            if (config.getOptions() != null) {
                                if (config.getOptions().containsKey("model")) {
                                    optionsBuilder.model((String) config.getOptions().get("model"));
                                }
                                if (config.getOptions().containsKey("temperature")) {
                                    Object temp = config.getOptions().get("temperature");
                                    if (temp instanceof Number) {
                                        optionsBuilder.temperature(((Number) temp).doubleValue());
                                    }
                                }
                                if (config.getOptions().containsKey("topP")) {
                                    Object topP = config.getOptions().get("topP");
                                    if (topP instanceof Number) {
                                        optionsBuilder.topP(((Number) topP).doubleValue());
                                    }
                                }
                                // 如果有其他 ChatOptions，可以在这里继续添加映射
                            }
                            OpenAiChatOptions chatOptions = optionsBuilder.build();

                            // 3. 创建 OpenAiChatModel 实例
                            OpenAiChatModel chatModel =   OpenAiChatModel
                                    .builder()
                                    .openAiApi(openAiApi).defaultOptions(chatOptions).build();


                            // 4. 创建并返回 ChatClient 实例
                            return ChatClient.builder(chatModel)
                                    .defaultSystem("您是一个由 " + config.getName() + " 提供支持的有用助手。")
                                    .build();
                        }
                ));
    }
}
