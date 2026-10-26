package com.ruoyi.crm.service.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.crm.service.IOpenAiChatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class OpenAiChatService implements IOpenAiChatService {

    @Value("${openai.api.key}")
    private String openAiApiKey;
    @Value("${openai.api.model:gpt-3.5-turbo}")
    private String openAiModel;

    @Value("${deepseek.api.key}")
    private String deepSeekApiKey;
    @Value("${deepseek.api.model:deepseek-chat}")
    private String deepSeekModel;

    @Value("${bailian.api.key}")
    private String bailianApiKey;
    @Value("${bailian.api.model:qwen-turbo}")
    private String bailianModel;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String BAILIAN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String chatWithOpenAI(String userPrompt) {
        // 准备请求结构
        ChatMessage system = new ChatMessage("system", "你是一个专业的智能助理。");
        ChatMessage user = new ChatMessage("user", userPrompt);

        ChatRequest request = new ChatRequest(openAiModel, Arrays.asList(system, user));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<ChatRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                OPENAI_API_URL,
                HttpMethod.POST,
                httpEntity,
                JsonNode.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody()
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } else {
            throw new RuntimeException("OpenAI API 调用失败: " + response.getStatusCode());
        }
    }

    public String chatWithDeepSeek(String userPrompt) {
        // 准备请求结构
        ChatMessage system = new ChatMessage("system", "你是一个专业的智能助理。");
        ChatMessage user = new ChatMessage("user", userPrompt);

        ChatRequest request = new ChatRequest(deepSeekModel, Arrays.asList(system, user));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepSeekApiKey);

        HttpEntity<ChatRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                DEEPSEEK_API_URL,
                HttpMethod.POST,
                httpEntity,
                JsonNode.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody()
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } else {
            throw new RuntimeException("DeepSeek API 调用失败: " + response.getStatusCode());
        }
    }

    public String chatWithBailian(String userPrompt) {
        // 准备请求结构
        ChatMessage system = new ChatMessage("system", "你是一个专业的智能助理。");
        ChatMessage user = new ChatMessage("user", userPrompt);

        ChatRequest request = new ChatRequest(bailianModel, Arrays.asList(system, user));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(bailianApiKey);

        HttpEntity<ChatRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                BAILIAN_API_URL,
                HttpMethod.POST,
                httpEntity,
                JsonNode.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody()
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } else {
            throw new RuntimeException("Bailian API 调用失败: " + response.getStatusCode());
        }
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChatMessage {
        public String role;
        public String content;

        public ChatMessage() {}
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }


    // 内部类：ChatRequest
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ChatRequest {
        public String model;

        public List<ChatMessage> messages;

        @JsonProperty("temperature")
        public Double temperature = 0.7;

        public ChatRequest(String model, List<ChatMessage> messages) {
            this.model = model;
            this.messages = messages;
        }
    }
}
