package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.EmailAnalysisResultDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisSentimentDto;
import com.ruoyi.crm.domain.dto.EmailAnalysisUpsertDto;

public class EmailAnalysisValidatorTest
{
    private static final String VALID_BODY_HASH =
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Test
    public void validUpsertPasses()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        assertDoesNotThrow(() -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    @Test
    public void missingMessageExternalIdFails()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        dto.setMessageExternalId("");
        assertThrows(ServiceException.class, () -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    @Test
    public void invalidBodyHashFails()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        dto.setBodyHash("not-a-hash");
        assertThrows(ServiceException.class, () -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    @Test
    public void invalidUrgencyFails()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        dto.getAnalysis().setUrgency("Critical");
        assertThrows(ServiceException.class, () -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    @Test
    public void invalidSentimentLabelFails()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        dto.getAnalysis().getSentiment().setLabel("Angry");
        assertThrows(ServiceException.class, () -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    @Test
    public void invalidSentimentScoreFails()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        dto.getAnalysis().getSentiment().setScore(101);
        assertThrows(ServiceException.class, () -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    @Test
    public void missingAnalysisFails()
    {
        EmailAnalysisUpsertDto dto = buildValidDto();
        dto.setAnalysis(null);
        assertThrows(ServiceException.class, () -> EmailAnalysisValidator.validateUpsertDto(dto));
    }

    private EmailAnalysisUpsertDto buildValidDto()
    {
        EmailAnalysisSentimentDto sentiment = new EmailAnalysisSentimentDto();
        sentiment.setLabel("Neutral");
        sentiment.setScore(50);
        sentiment.setDescription("Neutral tone");

        EmailAnalysisResultDto analysis = new EmailAnalysisResultDto();
        analysis.setExecutiveSummary("Summary");
        analysis.setCategory("Support");
        analysis.setUrgency("Medium");
        analysis.setSentiment(sentiment);
        analysis.setDetectedIntent("Request help");
        analysis.setSuggestedAction("Reply");

        EmailAnalysisUpsertDto dto = new EmailAnalysisUpsertDto();
        dto.setMessageExternalId("msg-123");
        dto.setLocale("en");
        dto.setBodyHash(VALID_BODY_HASH);
        dto.setAnalysis(analysis);
        return dto;
    }
}
