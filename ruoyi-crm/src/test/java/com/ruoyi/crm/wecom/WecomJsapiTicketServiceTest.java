package com.ruoyi.crm.wecom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomJsapiAgentSignatureDto;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WecomJsapiTicketServiceTest
{
    @Mock
    private WecomTencentApiClient apiClient;

    @Mock
    private WecomAccessTokenService accessTokenService;

    @Mock
    private CrmWecomConfigMapper wecomConfigMapper;

    @InjectMocks
    private WecomJsapiTicketService jsapiTicketService;

    private CrmWecomConfig config;

    @BeforeEach
    void setUp()
    {
        config = new CrmWecomConfig();
        config.setCorpId("wwcorp");
        config.setAgentId("1000002");
        when(wecomConfigMapper.selectFirst()).thenReturn(config);
        when(accessTokenService.getAgentAccessToken(config)).thenReturn("access-token");
    }

    @Test
    void validateUrl_rejectsHash()
    {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> WecomJsapiTicketService.validateUrl("https://example.com/wecom/sidebar#tab"));
        assertEquals(400, ex.getCode());
    }

    @Test
    void sha1Hex_matchesWecomAlgorithm()
    {
        String raw = "jsapi_ticket=ticket&noncestr=abc&timestamp=123&url=https://example.com";
        String signature = WecomJsapiTicketService.sha1Hex(raw);
        assertEquals(40, signature.length());
        assertEquals(signature, WecomJsapiTicketService.sha1Hex(raw));
    }

    @Test
    void buildAgentSignature_returnsSignedPayload()
    {
        JSONObject ticketRes = new JSONObject();
        ticketRes.put("errcode", 0);
        ticketRes.put("ticket", "agent-ticket");
        when(apiClient.getAgentConfigTicket("access-token")).thenReturn(ticketRes);

        WecomJsapiAgentSignatureDto dto = jsapiTicketService.buildAgentSignature("https://tenant.example.com/wecom/sidebar");

        assertEquals("wwcorp", dto.getCorpId());
        assertEquals("1000002", dto.getAgentId());
        assertEquals(16, dto.getNonceStr().length());
        assertEquals(40, dto.getSignature().length());
    }

    @Test
    void buildAgentSignature_retriesAfterInvalidToken()
    {
        JSONObject invalid = new JSONObject();
        invalid.put("errcode", WecomTencentApiClient.ERRCODE_INVALID_ACCESS_TOKEN);
        invalid.put("errmsg", "invalid token");

        JSONObject ok = new JSONObject();
        ok.put("errcode", 0);
        ok.put("ticket", "agent-ticket");

        when(apiClient.getAgentConfigTicket("access-token")).thenReturn(invalid);
        when(apiClient.getAgentConfigTicket("fresh-token")).thenReturn(ok);
        when(accessTokenService.getAgentAccessToken(config))
                .thenReturn("access-token")
                .thenReturn("fresh-token");

        WecomJsapiAgentSignatureDto dto = jsapiTicketService.buildAgentSignature("https://tenant.example.com/wecom/sidebar");

        assertEquals(40, dto.getSignature().length());
        verify(accessTokenService, times(2)).getAgentAccessToken(config);
        verify(accessTokenService).invalidate("wwcorp");
        verify(apiClient).getAgentConfigTicket("fresh-token");
    }
}
