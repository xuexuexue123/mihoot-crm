package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class LeadQualifySurveyScoreSupportTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void mergeAnswersWithScores_fullYesHigh() throws Exception
    {
        Map<String, String> answers = new HashMap<>();
        answers.put("budget", "Yes");
        answers.put("authority", "High");
        answers.put("need", "Yes");
        answers.put("timeline", "High");
        answers.put("challenges", "Yes");
        answers.put("authority_champ", "Yes");
        answers.put("money", "Yes");
        answers.put("prioritization", "Yes");
        answers.put("metrics", "Yes");
        answers.put("economicBuyer", "Yes");
        answers.put("decisionCriteria", "Yes");
        answers.put("decisionProcess", "Yes");
        answers.put("identifyPain", "Yes");
        answers.put("champion", "Yes");

        JsonNode root = MAPPER.readTree(LeadQualifySurveyScoreSupport.mergeAnswersWithScores(answers));
        assertEquals(8, root.path("surveyScores").path("BANT").asInt());
        assertEquals(8, root.path("surveyScores").path("CHAMP").asInt());
        assertEquals(12, root.path("surveyScores").path("MEDDIC").asInt());
        assertEquals(28, root.path("surveyScores").path("total").asInt());
        assertEquals("Yes", root.path("budget").asText());
    }

    @Test
    void mergeAnswersWithScores_mixedAndMissing() throws Exception
    {
        Map<String, String> answers = new HashMap<>();
        answers.put("budget", "Yes");
        answers.put("authority", "Partially");
        answers.put("need", "No");
        answers.put("challenges", "Medium");
        answers.put("money", "Low");
        answers.put("metrics", "unknown");

        JsonNode root = MAPPER.readTree(LeadQualifySurveyScoreSupport.mergeAnswersWithScores(answers));
        // BANT: 2 + 1 + 0 + 0(missing timeline) = 3
        assertEquals(3, root.path("surveyScores").path("BANT").asInt());
        // CHAMP: 1 + 0 + 0 + 0 = 1
        assertEquals(1, root.path("surveyScores").path("CHAMP").asInt());
        // MEDDIC: all 0
        assertEquals(0, root.path("surveyScores").path("MEDDIC").asInt());
        assertEquals(4, root.path("surveyScores").path("total").asInt());
    }

    @Test
    void mergeAnswersWithScores_emptyAnswersStillHasScores() throws Exception
    {
        String json = LeadQualifySurveyScoreSupport.mergeAnswersWithScores((Map<String, ?>) null);
        JsonNode root = MAPPER.readTree(json);
        assertTrue(root.has("surveyScores"));
        assertEquals(0, root.path("surveyScores").path("total").asInt());
    }

    @Test
    void scoreValue_mapping()
    {
        assertEquals(2, LeadQualifySurveyScoreSupport.scoreValue(MAPPER.getNodeFactory().textNode("Yes")));
        assertEquals(2, LeadQualifySurveyScoreSupport.scoreValue(MAPPER.getNodeFactory().textNode("High")));
        assertEquals(1, LeadQualifySurveyScoreSupport.scoreValue(MAPPER.getNodeFactory().textNode("Partially")));
        assertEquals(1, LeadQualifySurveyScoreSupport.scoreValue(MAPPER.getNodeFactory().textNode("Medium")));
        assertEquals(0, LeadQualifySurveyScoreSupport.scoreValue(MAPPER.getNodeFactory().textNode("No")));
        assertEquals(0, LeadQualifySurveyScoreSupport.scoreValue(MAPPER.getNodeFactory().textNode("Low")));
        assertEquals(0, LeadQualifySurveyScoreSupport.scoreValue(null));
    }
}
