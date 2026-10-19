package com.ruoyi.crm.service.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 认定潜客答卷计分（BANT / CHAMP / MEDDIC），与 CRM 抽屉字段名及分值一致。
 */
public final class LeadQualifySurveyScoreSupport
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final List<String> BANT_FIELDS = Collections.unmodifiableList(
            Arrays.asList("budget", "authority", "need", "timeline"));
    private static final List<String> CHAMP_FIELDS = Collections.unmodifiableList(
            Arrays.asList("challenges", "authority_champ", "money", "prioritization"));
    private static final List<String> MEDDIC_FIELDS = Collections.unmodifiableList(
            Arrays.asList("metrics", "economicBuyer", "decisionCriteria", "decisionProcess",
                    "identifyPain", "champion"));

    private LeadQualifySurveyScoreSupport()
    {
    }

    /**
     * 将答案对象与 surveyScores 合并为可写入 {@code crm_lead.survey_json} 的 JSON 字符串。
     */
    public static String mergeAnswersWithScores(JsonNode answers)
    {
        ObjectNode root;
        if (answers == null || answers.isNull() || !answers.isObject())
        {
            root = OBJECT_MAPPER.createObjectNode();
        }
        else
        {
            root = answers.deepCopy();
        }
        ObjectNode scores = OBJECT_MAPPER.createObjectNode();
        int bant = sumGroup(root, BANT_FIELDS);
        int champ = sumGroup(root, CHAMP_FIELDS);
        int meddic = sumGroup(root, MEDDIC_FIELDS);
        scores.put("BANT", bant);
        scores.put("CHAMP", champ);
        scores.put("MEDDIC", meddic);
        scores.put("total", bant + champ + meddic);
        root.set("surveyScores", scores);
        try
        {
            return OBJECT_MAPPER.writeValueAsString(root);
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Failed to serialize survey answers", ex);
        }
    }

    public static String mergeAnswersWithScores(Map<String, ?> answers)
    {
        JsonNode node = answers == null ? null : OBJECT_MAPPER.valueToTree(answers);
        return mergeAnswersWithScores(node);
    }

    static int scoreValue(JsonNode value)
    {
        if (value == null || value.isNull())
        {
            return 0;
        }
        String text = value.isTextual() ? value.asText() : value.asText();
        if (text == null)
        {
            return 0;
        }
        switch (text.trim())
        {
            case "Yes":
            case "High":
                return 2;
            case "Partially":
            case "Medium":
                return 1;
            case "No":
            case "Low":
            default:
                return 0;
        }
    }

    private static int sumGroup(JsonNode answers, List<String> fields)
    {
        int sum = 0;
        for (String field : fields)
        {
            sum += scoreValue(answers.get(field));
        }
        return sum;
    }
}
