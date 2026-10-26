package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmAiModelConfig;
import com.ruoyi.crm.domain.WCredential;
import com.ruoyi.crm.domain.dto.ServiceSummaryDeltaItem;
import com.ruoyi.crm.domain.vo.ContactServiceSummaryVo;
import com.ruoyi.crm.enums.AiModelProvider;
import com.ruoyi.crm.mapper.CrmAiModelConfigMapper;
import com.ruoyi.crm.mapper.WCredentialMapper;
import com.ruoyi.crm.service.IOpenAiChatService;

/**
 * 租户感知 LLM 客户端。优先解密 w_credential（Cryptr / 明文），失败再回退全局 OpenAiChatService。
 */
@Component
public class ContactServiceSummaryLlmClient
{
    private static final Logger log = LoggerFactory.getLogger(ContactServiceSummaryLlmClient.class);

    private static final String SYSTEM_PROMPT = String.join("\n",
            "你是诊所 CRM 内部服务记忆撰写员。",
            "只基于「旧摘要 + 增量」改写五段；不编造。不确定写「未确认」。",
            "不下医疗诊断结论；价格/预约以增量中的明确表述为准。",
            "locked_fields 中的字段必须原样保留，可在其它段反映新信息。",
            "输出必须为 JSON 对象，字段：currentNeed, confirmedFacts, commitmentsTodos, risksSentiment,",
            "lastTouch（对象：at ISO8601 或空, channel, summary）。不要输出 markdown 代码块。");

    @Autowired
    private CrmAiModelConfigMapper aiModelConfigMapper;

    @Autowired
    private WCredentialMapper wCredentialMapper;

    @Autowired
    private CryptrCompatDecryptor cryptrCompatDecryptor;

    @Autowired(required = false)
    private IOpenAiChatService openAiChatService;

    @Value("${bailian.api.key:}")
    private String globalBailianApiKey;

    @Value("${openai.api.key:}")
    private String globalOpenAiApiKey;

    @Value("${deepseek.api.key:}")
    private String globalDeepSeekApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public static class LlmResult
    {
        private ContactServiceSummaryVo.SummarySections sections;
        private String model;

        public ContactServiceSummaryVo.SummarySections getSections() { return sections; }
        public void setSections(ContactServiceSummaryVo.SummarySections sections) { this.sections = sections; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
    }

    public LlmResult rewrite(String tenantId, ContactServiceSummaryVo.SummarySections previous,
            List<String> lockedFields, List<ServiceSummaryDeltaItem> deltas, boolean bootstrap)
    {
        String userPrompt = buildUserPrompt(previous, lockedFields, deltas, bootstrap);
        CrmAiModelConfig config = StringUtils.isNotEmpty(tenantId)
                ? aiModelConfigMapper.selectEntityByTenantIdAny(tenantId) : null;
        String modelName = config != null ? config.getModel() : "default";
        String raw;
        if (config != null && StringUtils.isNotEmpty(config.getDelFlag()) && !"0".equals(config.getDelFlag()))
        {
            config = null;
        }
        try
        {
            if (config != null)
            {
                raw = chatWithTenantConfig(config, userPrompt);
            }
            else
            {
                raw = chatWithGlobalFallback(AiModelProvider.QWEN, "qwen-turbo", userPrompt);
                modelName = "bailian-fallback";
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("LLM call failed: " + e.getMessage(), e);
        }
        ContactServiceSummaryVo.SummarySections sections = parseSections(raw);
        applyLocks(previous, sections, lockedFields);
        LlmResult result = new LlmResult();
        result.setSections(sections);
        result.setModel(modelName);
        return result;
    }

    private String chatWithTenantConfig(CrmAiModelConfig config, String userPrompt) throws Exception
    {
        AiModelProvider provider = AiModelProvider.fromKey(config.getProvider());
        WCredential credential = wCredentialMapper.selectWCredentialById(config.getCredentialId());
        String apiKey = resolveApiKey(credential != null ? credential.getValue() : null);
        if (StringUtils.isEmpty(apiKey))
        {
            log.warn("Tenant credential unavailable; falling back to global API key for provider={}",
                    config.getProvider());
            return chatWithGlobalFallback(provider, config.getModel(), userPrompt);
        }
        return chatCompletions(provider, config.getModel(),
                config.getTemperature() != null ? config.getTemperature().doubleValue() : 0.2d,
                apiKey, userPrompt);
    }

    private String chatWithGlobalFallback(AiModelProvider provider, String model, String userPrompt)
            throws Exception
    {
        String apiKey = null;
        AiModelProvider useProvider = provider != null ? provider : AiModelProvider.QWEN;
        if (useProvider == AiModelProvider.OPENAI)
        {
            apiKey = globalOpenAiApiKey;
        }
        else if (useProvider == AiModelProvider.DEEPSEEK)
        {
            apiKey = globalDeepSeekApiKey;
        }
        else
        {
            apiKey = globalBailianApiKey;
            useProvider = AiModelProvider.QWEN;
            if (StringUtils.isEmpty(model))
            {
                model = "qwen-turbo";
            }
        }
        if (StringUtils.isEmpty(apiKey))
        {
            if (openAiChatService != null)
            {
                if (useProvider == AiModelProvider.DEEPSEEK)
                {
                    return openAiChatService.chatWithDeepSeek(SYSTEM_PROMPT + "\n\n" + userPrompt);
                }
                if (useProvider == AiModelProvider.OPENAI)
                {
                    return openAiChatService.chatWithOpenAI(SYSTEM_PROMPT + "\n\n" + userPrompt);
                }
                return openAiChatService.chatWithBailian(SYSTEM_PROMPT + "\n\n" + userPrompt);
            }
            throw new IllegalStateException(
                    "AI credential unavailable: set tenant AI model credential (ENCRYPTION_KEY must match sevencrm) "
                            + "or configure bailian/openai/deepseek.api.key");
        }
        return chatCompletions(useProvider, model, 0.2d, apiKey, userPrompt);
    }

    private String chatCompletions(AiModelProvider provider, String model, double temperature,
            String apiKey, String userPrompt)
    {
        String url = resolveBaseUrl(provider) + "/chat/completions";
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("temperature", temperature);
        body.put("messages", Arrays.asList(
                mapOf("role", "system", "content", SYSTEM_PROMPT),
                mapOf("role", "user", "content", userPrompt)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        try
        {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(body, headers), JsonNode.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
            {
                throw new IllegalStateException("LLM HTTP " + response.getStatusCode());
            }
            return response.getBody().path("choices").path(0).path("message").path("content").asText();
        }
        catch (HttpStatusCodeException e)
        {
            String detail = e.getResponseBodyAsString();
            if (detail != null && detail.length() > 300)
            {
                detail = detail.substring(0, 300);
            }
            throw new IllegalStateException("LLM HTTP " + e.getRawStatusCode()
                    + (StringUtils.isNotEmpty(detail) ? (": " + detail) : ": " + e.getStatusText())
                    + " (check tenant AI credential / ENCRYPTION_KEY / provider API key)", e);
        }
    }

    String resolveApiKey(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            return null;
        }
        String trimmed = raw.trim();

        // Cryptr hex（Next 写入）
        if (CryptrCompatDecryptor.looksLikeCryptrHex(trimmed))
        {
            String decrypted = cryptrCompatDecryptor.tryDecrypt(trimmed);
            if (StringUtils.isNotEmpty(decrypted))
            {
                return normalizePlainKey(decrypted.trim());
            }
            if (!cryptrCompatDecryptor.isConfigured())
            {
                log.warn("w_credential looks Cryptr-encrypted but crm.credential.encryption-key / ENCRYPTION_KEY is empty");
            }
            else
            {
                log.warn("Failed to decrypt w_credential with ENCRYPTION_KEY (key mismatch?)");
            }
            return null;
        }

        return normalizePlainKey(trimmed);
    }

    private static String normalizePlainKey(String trimmed)
    {
        if (trimmed.startsWith("{"))
        {
            try
            {
                JSONObject obj = JSON.parseObject(trimmed);
                String key = obj.getString("apiKey");
                if (StringUtils.isEmpty(key))
                {
                    key = obj.getString("api_key");
                }
                return StringUtils.isNotEmpty(key) ? key.trim() : null;
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return trimmed;
    }

    private static String resolveBaseUrl(AiModelProvider provider)
    {
        if (provider == null)
        {
            return "https://dashscope.aliyuncs.com/compatible-mode/v1";
        }
        switch (provider)
        {
            case OPENAI:
                return "https://api.openai.com/v1";
            case DEEPSEEK:
                return "https://api.deepseek.com/v1";
            case QWEN:
            default:
                return "https://dashscope.aliyuncs.com/compatible-mode/v1";
        }
    }

    private static String buildUserPrompt(ContactServiceSummaryVo.SummarySections previous,
            List<String> lockedFields, List<ServiceSummaryDeltaItem> deltas, boolean bootstrap)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(bootstrap ? "模式: bootstrap\n" : "模式: incremental\n");
        sb.append("locked_fields: ").append(JSON.toJSONString(
                lockedFields != null ? lockedFields : Collections.emptyList())).append("\n");
        sb.append("旧摘要: ").append(previous != null ? JSON.toJSONString(previous) : "{}").append("\n");
        sb.append("增量事件:\n");
        if (deltas == null || deltas.isEmpty())
        {
            sb.append("(无)\n");
        }
        else
        {
            for (ServiceSummaryDeltaItem d : deltas)
            {
                sb.append("- [").append(d.getChannel()).append("] ")
                        .append(d.getDirection()).append(" / ").append(d.getActor())
                        .append(" @ ").append(d.getAt())
                        .append(" : ").append(d.getText()).append("\n");
            }
        }
        return sb.toString();
    }

    static ContactServiceSummaryVo.SummarySections parseSections(String raw)
    {
        String json = raw == null ? "{}" : raw.trim();
        if (json.startsWith("```"))
        {
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}');
            if (start >= 0 && end > start)
            {
                json = json.substring(start, end + 1);
            }
        }
        JSONObject obj = JSON.parseObject(json);
        ContactServiceSummaryVo.SummarySections sections = new ContactServiceSummaryVo.SummarySections();
        if (obj == null)
        {
            return sections;
        }
        sections.setCurrentNeed(obj.getString("currentNeed"));
        sections.setConfirmedFacts(obj.getString("confirmedFacts"));
        sections.setCommitmentsTodos(obj.getString("commitmentsTodos"));
        sections.setRisksSentiment(obj.getString("risksSentiment"));
        JSONObject lt = obj.getJSONObject("lastTouch");
        if (lt != null)
        {
            ContactServiceSummaryVo.LastTouch lastTouch = new ContactServiceSummaryVo.LastTouch();
            lastTouch.setChannel(lt.getString("channel"));
            lastTouch.setSummary(lt.getString("summary"));
            sections.setLastTouch(lastTouch);
        }
        return sections;
    }

    public static void applyLocks(ContactServiceSummaryVo.SummarySections previous,
            ContactServiceSummaryVo.SummarySections next, List<String> lockedFields)
    {
        if (previous == null || lockedFields == null || lockedFields.isEmpty() || next == null)
        {
            return;
        }
        for (String field : lockedFields)
        {
            if ("currentNeed".equals(field))
            {
                next.setCurrentNeed(previous.getCurrentNeed());
            }
            else if ("confirmedFacts".equals(field))
            {
                next.setConfirmedFacts(previous.getConfirmedFacts());
            }
            else if ("commitmentsTodos".equals(field))
            {
                next.setCommitmentsTodos(previous.getCommitmentsTodos());
            }
            else if ("risksSentiment".equals(field))
            {
                next.setRisksSentiment(previous.getRisksSentiment());
            }
        }
    }

    private static Map<String, String> mapOf(String k1, String v1, String k2, String v2)
    {
        Map<String, String> m = new HashMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }
}
