package com.ruoyi.crm.service.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.WCredential;
import com.ruoyi.crm.domain.dto.AiModelConfigUpsertDto;
import com.ruoyi.crm.enums.AiModelProvider;
import com.ruoyi.crm.mapper.WCredentialMapper;

/**
 * AI 模型配置校验（契约 §4）
 */
public final class AiModelConfigValidator
{
    private static final Set<String> TRANSLATION_LANGUAGE_CODES = new HashSet<>();

    static
    {
        // 完整 BCP-47
        TRANSLATION_LANGUAGE_CODES.add("zh-CN");
        TRANSLATION_LANGUAGE_CODES.add("zh-TW");
        TRANSLATION_LANGUAGE_CODES.add("en-US");
        TRANSLATION_LANGUAGE_CODES.add("en-GB");
        TRANSLATION_LANGUAGE_CODES.add("ja-JP");
        TRANSLATION_LANGUAGE_CODES.add("ko-KR");
        TRANSLATION_LANGUAGE_CODES.add("fr-FR");
        TRANSLATION_LANGUAGE_CODES.add("de-DE");
        TRANSLATION_LANGUAGE_CODES.add("es-ES");
        TRANSLATION_LANGUAGE_CODES.add("vi-VN");
        TRANSLATION_LANGUAGE_CODES.add("th-TH");
        TRANSLATION_LANGUAGE_CODES.add("id-ID");
        TRANSLATION_LANGUAGE_CODES.add("pt-BR");
        TRANSLATION_LANGUAGE_CODES.add("ru-RU");
        TRANSLATION_LANGUAGE_CODES.add("ar-SA");
        TRANSLATION_LANGUAGE_CODES.add("it-IT");
        // ISO 639-1 短码（Next.js catalog 兼容）
        TRANSLATION_LANGUAGE_CODES.add("zh");
        TRANSLATION_LANGUAGE_CODES.add("en");
        TRANSLATION_LANGUAGE_CODES.add("ja");
        TRANSLATION_LANGUAGE_CODES.add("ko");
        TRANSLATION_LANGUAGE_CODES.add("fr");
        TRANSLATION_LANGUAGE_CODES.add("de");
        TRANSLATION_LANGUAGE_CODES.add("es");
        TRANSLATION_LANGUAGE_CODES.add("vi");
        TRANSLATION_LANGUAGE_CODES.add("th");
        TRANSLATION_LANGUAGE_CODES.add("id");
        TRANSLATION_LANGUAGE_CODES.add("pt");
        TRANSLATION_LANGUAGE_CODES.add("ru");
        TRANSLATION_LANGUAGE_CODES.add("ar");
        TRANSLATION_LANGUAGE_CODES.add("it");
    }

    private AiModelConfigValidator()
    {
    }

    public static void validateUpsertDto(AiModelConfigUpsertDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        validateProvider(dto.getProvider());
        validateCredentialId(dto.getCredentialId());
        validateModel(dto.getModel());
        validateTemperature(dto.getTemperature());
        validateTranslationLanguage(dto.getDefaultTranslationLanguage());
    }

    public static void validateCredentialForProvider(AiModelConfigUpsertDto dto, WCredentialMapper wCredentialMapper)
    {
        AiModelProvider provider = AiModelProvider.fromKey(dto.getProvider());
        WCredential credential = wCredentialMapper.selectWCredentialById(dto.getCredentialId().trim());
        if (credential == null)
        {
            throw new ServiceException("凭证不存在或无效", HttpStatus.BAD_REQUEST);
        }
        String expectedType = provider.getCredentialType();
        if (!expectedType.equalsIgnoreCase(StringUtils.trim(credential.getType())))
        {
            throw new ServiceException("凭证类型与 provider 不匹配，期望 " + expectedType, HttpStatus.BAD_REQUEST);
        }
    }

    public static BigDecimal normalizeTemperature(BigDecimal temperature)
    {
        return temperature.setScale(2, RoundingMode.HALF_UP);
    }

    private static void validateProvider(String provider)
    {
        if (AiModelProvider.fromKey(provider) == null)
        {
            throw new ServiceException("provider 必须为 qwen、deepseek、openai、gemini 或 anthropic", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateCredentialId(String credentialId)
    {
        if (StringUtils.isBlank(credentialId))
        {
            throw new ServiceException("credentialId 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateModel(String model)
    {
        if (StringUtils.isBlank(model))
        {
            throw new ServiceException("model 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateTemperature(BigDecimal temperature)
    {
        if (temperature == null)
        {
            throw new ServiceException("temperature 不能为空", HttpStatus.BAD_REQUEST);
        }
        BigDecimal normalized = temperature.setScale(2, RoundingMode.HALF_UP);
        if (normalized.compareTo(BigDecimal.ZERO) < 0 || normalized.compareTo(BigDecimal.ONE) > 0)
        {
            throw new ServiceException("temperature 必须在 0.0 到 1.0 之间", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateTranslationLanguage(String language)
    {
        if (StringUtils.isBlank(language) || !TRANSLATION_LANGUAGE_CODES.contains(language.trim()))
        {
            throw new ServiceException("defaultTranslationLanguage 不在允许的语言列表内", HttpStatus.BAD_REQUEST);
        }
    }
}
