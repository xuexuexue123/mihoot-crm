package com.ruoyi.crm.service.support;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.EmailAnalysisResultDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisSentimentDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisUpsertDto;

/**
 * 邮件 AI 分析校验（契约 §4）
 */
public final class EmailAnalysisValidator
{
    private static final int MAX_ANALYSIS_JSON_BYTES = 512 * 1024;

    private static final Pattern BODY_HASH_PATTERN = Pattern.compile("^[a-f0-9]{64}$");

    private static final Set<String> URGENCY_VALUES = new HashSet<>();

    private static final Set<String> SENTIMENT_LABELS = new HashSet<>();

    static
    {
        URGENCY_VALUES.add("Low");
        URGENCY_VALUES.add("Medium");
        URGENCY_VALUES.add("High");

        SENTIMENT_LABELS.add("Positive");
        SENTIMENT_LABELS.add("Neutral");
        SENTIMENT_LABELS.add("Negative");
    }

    private EmailAnalysisValidator()
    {
    }

    public static void validateUpsertDto(EmailAnalysisUpsertDto dto)
    {
        if (dto == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        validateMessageExternalId(dto.getMessageExternalId());
        validateLocale(dto.getLocale());
        validateBodyHash(dto.getBodyHash());
        validateAnalysis(dto.getAnalysis());
    }

    public static String serializeAnalysisJson(EmailAnalysisResultDto analysis)
    {
        String json = JSON.toJSONString(analysis);
        if (json.getBytes(StandardCharsets.UTF_8).length > MAX_ANALYSIS_JSON_BYTES)
        {
            throw new ServiceException("analysis_json 序列化后不能超过 512KB", HttpStatus.BAD_REQUEST);
        }
        return json;
    }

    private static void validateMessageExternalId(String messageExternalId)
    {
        if (StringUtils.isBlank(messageExternalId))
        {
            throw new ServiceException("messageExternalId 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateLocale(String locale)
    {
        if (StringUtils.isBlank(locale))
        {
            throw new ServiceException("locale 不能为空", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateBodyHash(String bodyHash)
    {
        if (StringUtils.isBlank(bodyHash))
        {
            throw new ServiceException("bodyHash 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!BODY_HASH_PATTERN.matcher(bodyHash.trim()).matches())
        {
            throw new ServiceException("bodyHash 必须为 64 位小写 hex", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateAnalysis(EmailAnalysisResultDto analysis)
    {
        if (analysis == null)
        {
            throw new ServiceException("analysis 不能为空", HttpStatus.BAD_REQUEST);
        }
        validateUrgency(analysis.getUrgency());
        validateSentiment(analysis.getSentiment());
        serializeAnalysisJson(analysis);
    }

    private static void validateUrgency(String urgency)
    {
        if (StringUtils.isBlank(urgency) || !URGENCY_VALUES.contains(urgency.trim()))
        {
            throw new ServiceException("analysis.urgency 必须为 Low、Medium 或 High", HttpStatus.BAD_REQUEST);
        }
    }

    private static void validateSentiment(EmailAnalysisSentimentDto sentiment)
    {
        if (sentiment == null)
        {
            throw new ServiceException("analysis.sentiment 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(sentiment.getLabel()) || !SENTIMENT_LABELS.contains(sentiment.getLabel().trim()))
        {
            throw new ServiceException("analysis.sentiment.label 必须为 Positive、Neutral 或 Negative", HttpStatus.BAD_REQUEST);
        }
        Integer score = sentiment.getScore();
        if (score == null || score < 0 || score > 100)
        {
            throw new ServiceException("analysis.sentiment.score 必须在 0 到 100 之间", HttpStatus.BAD_REQUEST);
        }
    }
}
