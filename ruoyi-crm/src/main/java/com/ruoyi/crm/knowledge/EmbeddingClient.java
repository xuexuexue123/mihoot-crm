package com.ruoyi.crm.knowledge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.WCredential;
import com.ruoyi.crm.mapper.WCredentialMapper;

/**
 * 知识库 Embedding 客户端（默认阿里百炼 DashScope qwen3.7-text-embedding，1024 维）。
 *
 * 独立于 chat 路径；密钥解析顺序：租户 w_credential(QWEN) → 全局 bailian.api.key。
 * 硬约束：入库与查询必须同 provider + model + dimensions；dimensions 与 DDL vector(1024) 绑定。
 */
@Component
public class EmbeddingClient
{
    private static final Logger log = LoggerFactory.getLogger(EmbeddingClient.class);

    /** 单请求最大条数：qwen3.7-text-embedding 上限 20、text-embedding-v3/v4 上限 10，取保守值 */
    public static final int MAX_BATCH_SIZE = 10;

    /** 与 crm_kb_chunk.embedding vector(1024) 绑定，禁止运行时变更 */
    public static final int REQUIRED_DIMENSIONS = 1024;

    private static final String BAILIAN_EMBEDDINGS_URL =
            "https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings";

    @Value("${crm.knowledge.embedding.provider:bailian}")
    private String provider;

    @Value("${crm.knowledge.embedding.model:qwen3.7-text-embedding}")
    private String model;

    @Value("${crm.knowledge.embedding.dimensions:1024}")
    private int dimensions;

    @Value("${bailian.api.key:}")
    private String globalBailianApiKey;

    @Autowired
    private WCredentialMapper wCredentialMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    private boolean configValid;

    public EmbeddingClient()
    {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(60_000);
        this.restTemplate = new RestTemplate(factory);
    }

    @PostConstruct
    void validateConfig()
    {
        configValid = dimensions == REQUIRED_DIMENSIONS;
        if (!configValid)
        {
            log.error("knowledge embedding dimensions={} 与 DDL vector({}) 不一致，知识库写入/检索路径已禁用；"
                    + "变更维度需 DDL 迁移 + 全量 reindex", dimensions, REQUIRED_DIMENSIONS);
        }
    }

    public String getModel()
    {
        return model;
    }

    public int getDimensions()
    {
        return dimensions;
    }

    /** 单条 embed（retrieve 用），返回 pgvector 字面量 "[v1,v2,...]" */
    public String embedOne(String text)
    {
        List<String> result = embedBatch(Collections.singletonList(text));
        return result.get(0);
    }

    /** 批量 embed（ingest 用）；内部按 MAX_BATCH_SIZE 分批 */
    public List<String> embedBatch(List<String> texts)
    {
        requireUsable();
        if (texts == null || texts.isEmpty())
        {
            return Collections.emptyList();
        }
        String apiKey = resolveApiKey();
        List<String> vectors = new ArrayList<>(texts.size());
        for (int from = 0; from < texts.size(); from += MAX_BATCH_SIZE)
        {
            int to = Math.min(from + MAX_BATCH_SIZE, texts.size());
            vectors.addAll(callEmbeddings(apiKey, texts.subList(from, to)));
        }
        return vectors;
    }

    private void requireUsable()
    {
        if (!configValid)
        {
            throw new ServiceException("知识库 embedding 配置无效：dimensions 必须为 " + REQUIRED_DIMENSIONS
                    + "（与 vector(" + REQUIRED_DIMENSIONS + ") DDL 绑定）", HttpStatus.ERROR);
        }
        if (!"bailian".equalsIgnoreCase(provider))
        {
            throw new ServiceException("暂不支持的 embedding provider: " + provider, HttpStatus.ERROR);
        }
    }

    /**
     * 密钥解析：租户 w_credential(type=QWEN，优先主凭证) → 全局 bailian.api.key。
     *
     * 注意：Next 侧写入的凭证是其自有加密（hex），Java 无法解密；
     * 只有 AESUtil 可解密、或明文形如 sk- 的值才会被采用，否则回退全局 key。
     */
    private String resolveApiKey()
    {
        try
        {
            WCredential query = new WCredential();
            query.setType("QWEN");
            List<WCredential> rows = wCredentialMapper.selectWCredentialList(query);
            if (rows != null && !rows.isEmpty())
            {
                rows.sort((a, b) -> Long.compare(
                        b.getIsPrimary() == null ? 0 : b.getIsPrimary(),
                        a.getIsPrimary() == null ? 0 : a.getIsPrimary()));
                for (WCredential row : rows)
                {
                    String usable = extractUsableKey(row.getValue());
                    if (usable != null)
                    {
                        return usable;
                    }
                }
                log.debug("tenant QWEN credentials present but not decryptable by Java, fallback to global key");
            }
        }
        catch (Exception e)
        {
            log.debug("resolve tenant QWEN credential failed, fallback to global key: {}", e.getMessage());
        }
        if (StringUtils.isNotBlank(globalBailianApiKey))
        {
            return globalBailianApiKey;
        }
        throw new ServiceException("知识库向量模型未配置：缺少百炼 API Key（租户 QWEN 凭证或全局 bailian.api.key）",
                HttpStatus.BAD_REQUEST);
    }

    private static String extractUsableKey(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("sk-"))
        {
            return trimmed;
        }
        try
        {
            String plain = AESUtil.decrypt(trimmed);
            if (StringUtils.isNotBlank(plain) && plain.trim().startsWith("sk-"))
            {
                return plain.trim();
            }
        }
        catch (Exception ignored)
        {
            // 非 AESUtil 加密（如 Next 侧密文），跳过
        }
        return null;
    }

    private List<String> callEmbeddings(String apiKey, List<String> batch)
    {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", model);
        body.put("dimensions", dimensions);
        body.put("encoding_format", "float");
        ArrayNode input = body.putArray("input");
        for (String text : batch)
        {
            input.add(text == null ? "" : text);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        ResponseEntity<JsonNode> response;
        try
        {
            response = restTemplate.exchange(BAILIAN_EMBEDDINGS_URL, HttpMethod.POST,
                    new HttpEntity<>(body, headers), JsonNode.class);
        }
        catch (Exception e)
        {
            throw new ServiceException("百炼 embedding 调用失败: " + e.getMessage(), HttpStatus.ERROR);
        }
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
        {
            throw new ServiceException("百炼 embedding 调用失败: HTTP " + response.getStatusCodeValue(),
                    HttpStatus.ERROR);
        }

        JsonNode data = response.getBody().path("data");
        if (!data.isArray() || data.size() != batch.size())
        {
            throw new ServiceException("百炼 embedding 返回数量异常: expect " + batch.size()
                    + ", got " + data.size(), HttpStatus.ERROR);
        }

        String[] ordered = new String[batch.size()];
        for (JsonNode item : data)
        {
            int index = item.path("index").asInt(-1);
            JsonNode vec = item.path("embedding");
            if (index < 0 || index >= batch.size() || !vec.isArray())
            {
                throw new ServiceException("百炼 embedding 返回格式异常", HttpStatus.ERROR);
            }
            if (vec.size() != dimensions)
            {
                throw new ServiceException("百炼 embedding 维度异常: expect " + dimensions
                        + ", got " + vec.size(), HttpStatus.ERROR);
            }
            ordered[index] = toVectorLiteral(vec);
        }
        List<String> result = new ArrayList<>(batch.size());
        for (String literal : ordered)
        {
            if (literal == null)
            {
                throw new ServiceException("百炼 embedding 返回缺少部分向量", HttpStatus.ERROR);
            }
            result.add(literal);
        }
        return result;
    }

    private static String toVectorLiteral(JsonNode vec)
    {
        StringBuilder sb = new StringBuilder(vec.size() * 12);
        sb.append('[');
        for (int i = 0; i < vec.size(); i++)
        {
            if (i > 0)
            {
                sb.append(',');
            }
            sb.append((float) vec.get(i).asDouble());
        }
        sb.append(']');
        return sb.toString();
    }
}
