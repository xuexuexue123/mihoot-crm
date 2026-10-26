package com.ruoyi.crm.enums;

import com.ruoyi.common.utils.StringUtils;

/**
 * LLM 提供商标识（契约 §3）
 */
public enum AiModelProvider
{
    QWEN("qwen", "QWEN"),
    DEEPSEEK("deepseek", "DEEPSEEK"),
    OPENAI("openai", "OPENAI"),
    GEMINI("gemini", "GEMINI"),
    ANTHROPIC("anthropic", "ANTHROPIC");

    private final String key;
    private final String credentialType;

    AiModelProvider(String key, String credentialType)
    {
        this.key = key;
        this.credentialType = credentialType;
    }

    public String getKey()
    {
        return key;
    }

    public String getCredentialType()
    {
        return credentialType;
    }

    public static AiModelProvider fromKey(String key)
    {
        if (StringUtils.isBlank(key))
        {
            return null;
        }
        String normalized = key.trim().toLowerCase();
        for (AiModelProvider provider : values())
        {
            if (provider.key.equals(normalized))
            {
                return provider;
            }
        }
        return null;
    }
}
