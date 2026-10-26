package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.SemanticProfileSaveRequest;

public class SemanticProfileValidatorTest
{
    private static final String VALID_PROFILE_JSON = "{"
            + "\"entities\":{\"lead\":{\"enabled\":true,\"aliases\":[\"预约\"],"
            + "\"definition\":\"尚未转化为正式患者的意向记录。\",\"agentTool\":\"search_inquiry\"}},"
            + "\"fields\":[{\"id\":\"f-opty-treatmentItemCode\",\"entityKey\":\"opportunity\","
            + "\"fieldKey\":\"treatmentItemCode\",\"meaning\":\"治疗项目编码。\",\"enabled\":true}],"
            + "\"relationships\":[{\"id\":\"r-account-contact\",\"from\":\"account\",\"to\":\"contact\","
            + "\"type\":\"one_to_many\",\"note\":\"一个家庭账户下可有多个患者联系人。\",\"enabled\":true}],"
            + "\"businessRules\":[{\"id\":\"br-opty-closed-won\","
            + "\"rule\":\"opportunity 进入赢单阶段时必须设置 closedWonBasis。\",\"enabled\":true}]"
            + "}";

    @Test
    public void validDentalProfilePasses()
    {
        SemanticProfileSaveRequest request = buildRequest("dental", "1", VALID_PROFILE_JSON);
        assertDoesNotThrow(() -> SemanticProfileValidator.validateSaveRequest(request));
    }

    @Test
    public void unknownEntityKeyFails()
    {
        String json = "{\"entities\":{\"unknownEntity\":{\"enabled\":true}}}";
        SemanticProfileSaveRequest request = buildRequest("generic", "1", json);
        assertThrows(ServiceException.class, () -> SemanticProfileValidator.validateSaveRequest(request));
    }

    @Test
    public void tooManyFieldsFails()
    {
        StringBuilder fields = new StringBuilder("[");
        for (int i = 0; i < 51; i++)
        {
            if (i > 0)
            {
                fields.append(',');
            }
            fields.append("{\"entityKey\":\"lead\",\"fieldKey\":\"f").append(i).append("\",\"meaning\":\"m\"}");
        }
        fields.append(']');
        String json = "{\"fields\":" + fields + "}";
        SemanticProfileSaveRequest request = buildRequest("generic", "1", json);
        assertThrows(ServiceException.class, () -> SemanticProfileValidator.validateSaveRequest(request));
    }

    @Test
    public void ruleTooLongFails()
    {
        String longRule = repeat('a', 501);
        String json = "{\"businessRules\":[{\"rule\":\"" + longRule + "\"}]}";
        SemanticProfileSaveRequest request = buildRequest("generic", "1", json);
        assertThrows(ServiceException.class, () -> SemanticProfileValidator.validateSaveRequest(request));
    }

    @Test
    public void invalidJsonFails()
    {
        SemanticProfileSaveRequest request = buildRequest("generic", "1", "{not-json");
        assertThrows(ServiceException.class, () -> SemanticProfileValidator.validateSaveRequest(request));
    }

    @Test
    public void profileJsonTooLargeFails()
    {
        String json = "{\"businessRules\":[{\"rule\":\"" + repeat('a', 256 * 1024) + "\"}]}";
        SemanticProfileSaveRequest request = buildRequest("generic", "1", json);
        assertThrows(ServiceException.class, () -> SemanticProfileValidator.validateSaveRequest(request));
    }

    @Test
    public void invalidIndustryKeyFails()
    {
        SemanticProfileSaveRequest request = buildRequest("invalid", "1", VALID_PROFILE_JSON);
        assertThrows(ServiceException.class, () -> SemanticProfileValidator.validateSaveRequest(request));
    }

    private SemanticProfileSaveRequest buildRequest(String industryKey, String enabled, String profileJson)
    {
        SemanticProfileSaveRequest request = new SemanticProfileSaveRequest();
        request.setIndustryKey(industryKey);
        request.setEnabled(enabled);
        request.setProfileVersion(1);
        request.setProfileJson(profileJson);
        return request;
    }

    private String repeat(char ch, int count)
    {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++)
        {
            builder.append(ch);
        }
        return builder.toString();
    }
}
