package com.ruoyi.crm.wechatmp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMassTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassPreviewRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassQuotaVo;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassSendRequest;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMassTaskMapper;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;

@ExtendWith(MockitoExtension.class)
class WechatMpMassServiceImplTest
{
    @Mock
    private CrmWechatMpMassTaskMapper massTaskMapper;

    @Mock
    private CrmWechatMpConfigMapper configMapper;

    @Mock
    private WechatMpTokenService tokenService;

    @Mock
    private WechatMpTencentApiClient tencentApiClient;

    @InjectMocks
    private WechatMpMassServiceImpl service;

    @Test
    void validate_rejectsTagWithoutTagId()
    {
        stubConfig();
        WechatMpMassSendRequest req = baseSend("all");
        req.setAudienceType("tag");
        req.setTagId(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.send(req));
        assertTrue(ex.getMessage().contains("MP_MASS_INVALID_PARAM"));
        assertTrue(ex.getMessage().contains("tagId"));
    }

    @Test
    void validate_rejectsIsToAllWithTag()
    {
        stubConfig();
        WechatMpMassSendRequest req = baseSend("tag");
        req.setTagId(101L);
        req.setIsToAll(true);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.send(req));
        assertTrue(ex.getMessage().contains("isToAll=true"));
    }

    @Test
    void validate_rejectsEmptyText()
    {
        stubConfig();
        WechatMpMassSendRequest req = baseSend("all");
        req.getContent().put("text", "");

        ServiceException ex = assertThrows(ServiceException.class, () -> service.send(req));
        assertTrue(ex.getMessage().contains("content.text"));
    }

    @Test
    void validate_rejectsMpnewsWithoutMediaId()
    {
        stubConfig();
        WechatMpMassSendRequest req = baseSend("all");
        req.setMsgType("mpnews");
        JSONObject content = new JSONObject();
        req.setContent(content);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.send(req));
        assertTrue(ex.getMessage().contains("content.mediaId"));
    }

    @Test
    void send_success_setsSendingAndMsgId()
    {
        stubConfig();
        when(tokenService.getAccessToken("wx1")).thenReturn("tok");
        JSONObject wx = new JSONObject();
        wx.put("errcode", 0);
        wx.put("msg_id", 3147483650L);
        wx.put("msg_data_id", 2247483650L);
        when(tencentApiClient.massSendAll(eq("tok"), any())).thenReturn(wx);
        when(massTaskMapper.insert(any())).thenAnswer(inv -> {
            CrmWechatMpMassTask t = inv.getArgument(0);
            t.setId(12L);
            return 1;
        });

        WechatMpMassSendRequest req = baseSend("all");
        CrmWechatMpMassTask task = service.send(req);

        assertEquals("sending", task.getStatus());
        assertEquals("3147483650", task.getMsgId());
        assertEquals("2247483650", task.getMsgDataId());
        assertTrue(Boolean.TRUE.equals(task.getIsToAll()));
        verify(massTaskMapper, times(1)).update(any());
    }

    @Test
    void send_noQuota_marksFailed()
    {
        stubConfig();
        when(tokenService.getAccessToken("wx1")).thenReturn("tok");
        JSONObject wx = new JSONObject();
        wx.put("errcode", 45028);
        wx.put("errmsg", "has no masssend quota");
        when(tencentApiClient.massSendAll(eq("tok"), any())).thenReturn(wx);
        when(massTaskMapper.insert(any())).thenAnswer(inv -> {
            CrmWechatMpMassTask t = inv.getArgument(0);
            t.setId(1L);
            return 1;
        });

        WechatMpMassSendRequest req = baseSend("all");
        ServiceException ex = assertThrows(ServiceException.class, () -> service.send(req));
        assertTrue(ex.getMessage().startsWith("MP_MASS_NO_QUOTA"));

        ArgumentCaptor<CrmWechatMpMassTask> cap = ArgumentCaptor.forClass(CrmWechatMpMassTask.class);
        verify(massTaskMapper).update(cap.capture());
        assertEquals("failed", cap.getValue().getStatus());
        assertEquals(Integer.valueOf(45028), cap.getValue().getErrorCode());
    }

    @Test
    void send_unauthorized_marksFailed()
    {
        stubConfig();
        when(tokenService.getAccessToken("wx1")).thenReturn("tok");
        JSONObject wx = new JSONObject();
        wx.put("errcode", 48001);
        wx.put("errmsg", "api unauthorized");
        when(tencentApiClient.massSendAll(eq("tok"), any())).thenReturn(wx);
        when(massTaskMapper.insert(any())).thenAnswer(inv -> {
            CrmWechatMpMassTask t = inv.getArgument(0);
            t.setId(1L);
            return 1;
        });

        ServiceException ex = assertThrows(ServiceException.class, () -> service.send(baseSend("all")));
        assertTrue(ex.getMessage().startsWith("MP_MASS_UNAUTHORIZED"));
    }

    @Test
    void send_clientMsgId_returnsExisting()
    {
        stubConfig();
        CrmWechatMpMassTask existing = new CrmWechatMpMassTask();
        existing.setId(99L);
        existing.setClientMsgId("crm-mass-001");
        existing.setStatus("sending");
        when(massTaskMapper.selectByClientMsgId("wx1", "crm-mass-001")).thenReturn(existing);

        WechatMpMassSendRequest req = baseSend("all");
        req.setClientMsgId("crm-mass-001");
        CrmWechatMpMassTask out = service.send(req);

        assertEquals(99L, out.getId());
        verify(tencentApiClient, never()).massSendAll(any(), any());
        verify(massTaskMapper, never()).insert(any());
    }

    @Test
    void preview_requiresTouserOrTowxname()
    {
        stubConfig();
        WechatMpMassPreviewRequest req = new WechatMpMassPreviewRequest();
        req.setAppId("wx1");
        req.setMsgType("text");
        JSONObject content = new JSONObject();
        content.put("text", "hi");
        req.setContent(content);

        ServiceException ex = assertThrows(ServiceException.class, () -> service.preview(req));
        assertTrue(ex.getMessage().contains("touser or towxname"));
    }

    @Test
    void quota_serviceAccount_estimatesRemain()
    {
        CrmWechatMpConfig cfg = new CrmWechatMpConfig();
        cfg.setAppId("wx1");
        cfg.setAccountType("service");
        when(configMapper.selectByAppId("wx1")).thenReturn(cfg);
        when(massTaskMapper.countIsToAllInPeriod(eq("wx1"), any(Date.class), any(Date.class))).thenReturn(1);

        WechatMpMassQuotaVo vo = service.getQuota("wx1");
        assertEquals("service", vo.getAccountType());
        assertEquals("month", vo.getPeriod());
        assertEquals(Integer.valueOf(1), vo.getIsToAllUsedEstimate());
        assertEquals(Integer.valueOf(4), vo.getIsToAllLimitEstimate());
        assertEquals(Integer.valueOf(3), vo.getRemainEstimate());
    }

    @Test
    void quota_unknownAccount_nullEstimates()
    {
        CrmWechatMpConfig cfg = new CrmWechatMpConfig();
        cfg.setAppId("wx1");
        cfg.setAccountType(null);
        when(configMapper.selectByAppId("wx1")).thenReturn(cfg);

        WechatMpMassQuotaVo vo = service.getQuota("wx1");
        assertNull(vo.getRemainEstimate());
        assertNull(vo.getPeriod());
    }

    @Test
    void applyMassSendJobFinish_updatesCountsIdempotent()
    {
        CrmWechatMpMassTask task = new CrmWechatMpMassTask();
        task.setId(5L);
        task.setStatus("sending");
        task.setMsgId("1001");
        when(massTaskMapper.selectByMsgId("wx1", "1001")).thenReturn(task);

        service.applyMassSendJobFinish("wx1", "1001", "send success", 100, 2, 98, 0);
        assertEquals("finished", task.getStatus());
        assertEquals(Integer.valueOf(98), task.getSentCount());

        service.applyMassSendJobFinish("wx1", "1001", "send success", 100, 2, 98, 0);
        verify(massTaskMapper, times(2)).update(any());
        assertEquals("finished", task.getStatus());
    }

    @Test
    void applyMassSendJobFinish_missingTask_noThrow()
    {
        when(massTaskMapper.selectByMsgId("wx1", "missing")).thenReturn(null);
        service.applyMassSendJobFinish("wx1", "missing", "send success", 1, 0, 1, 0);
        verify(massTaskMapper, never()).update(any());
    }

    @Test
    void applyMassSendJobFinish_failedStatus()
    {
        CrmWechatMpMassTask task = new CrmWechatMpMassTask();
        task.setId(5L);
        task.setStatus("sending");
        when(massTaskMapper.selectByMsgId("wx1", "1001")).thenReturn(task);

        service.applyMassSendJobFinish("wx1", "1001", "err(10001)", 10, 0, 0, 10);
        assertEquals("failed", task.getStatus());
    }

    @Test
    void isWxMassFailed_detectsFail()
    {
        assertTrue(WechatMpMassServiceImpl.isWxMassFailed("send fail"));
        assertFalse(WechatMpMassServiceImpl.isWxMassFailed("send success"));
    }

    @Test
    void send_tagAudience_forcesIsToAllFalse()
    {
        stubConfig();
        when(tokenService.getAccessToken("wx1")).thenReturn("tok");
        JSONObject wx = new JSONObject();
        wx.put("errcode", 0);
        wx.put("msg_id", "99");
        when(tencentApiClient.massSendAll(eq("tok"), any())).thenReturn(wx);
        when(massTaskMapper.insert(any())).thenAnswer(inv -> {
            CrmWechatMpMassTask t = inv.getArgument(0);
            t.setId(3L);
            return 1;
        });

        WechatMpMassSendRequest req = baseSend("tag");
        req.setMsgType("mpnews");
        JSONObject content = new JSONObject();
        content.put("mediaId", "MEDIA_ID");
        req.setContent(content);
        req.setTagId(101L);
        req.setIsToAll(null);

        CrmWechatMpMassTask task = service.send(req);
        assertFalse(Boolean.TRUE.equals(task.getIsToAll()));

        ArgumentCaptor<JSONObject> bodyCap = ArgumentCaptor.forClass(JSONObject.class);
        verify(tencentApiClient).massSendAll(eq("tok"), bodyCap.capture());
        JSONObject filter = bodyCap.getValue().getJSONObject("filter");
        assertFalse(filter.getBooleanValue("is_to_all"));
        assertEquals(101L, filter.getLongValue("tag_id"));
    }

    private void stubConfig()
    {
        CrmWechatMpConfig cfg = new CrmWechatMpConfig();
        cfg.setAppId("wx1");
        cfg.setAccountType("service");
        when(configMapper.selectByAppId("wx1")).thenReturn(cfg);
    }

    private WechatMpMassSendRequest baseSend(String audienceType)
    {
        WechatMpMassSendRequest req = new WechatMpMassSendRequest();
        req.setAppId("wx1");
        req.setMsgType("text");
        JSONObject content = new JSONObject();
        content.put("text", "hello mass");
        req.setContent(content);
        req.setAudienceType(audienceType);
        return req;
    }
}
