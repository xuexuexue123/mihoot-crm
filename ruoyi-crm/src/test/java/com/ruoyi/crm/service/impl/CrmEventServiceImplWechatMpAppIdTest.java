package com.ruoyi.crm.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.service.EventWechatMpAppIdResolver;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;
import com.ruoyi.ems.service.ISysReTemplateService;

@ExtendWith(MockitoExtension.class)
class CrmEventServiceImplWechatMpAppIdTest
{
    @Mock
    private CrmEventMapper crmEventMapper;

    @Mock
    private ISysReTemplateService sysReTemplateService;

    @Mock
    private IWechatMpMessagePresetService wechatMpMessagePresetService;

    @Mock
    private CrmWechatMpConfigMapper wechatMpConfigMapper;

    @Mock
    private EventWechatMpAppIdResolver eventWechatMpAppIdResolver;

    @InjectMocks
    private CrmEventServiceImpl crmEventService;

    @Test
    void updateRejectsInvalidAppId()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setWechatMpAppId("wx-missing");
        when(wechatMpConfigMapper.selectByAppId("wx-missing")).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> crmEventService.updateCrmEvent(event));

        assertEquals("wechat_mp_app_id_invalid", ex.getMessage());
    }

    @Test
    void updateRejectsDisconnectedAppId()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setWechatMpAppId("wx-off");
        CrmWechatMpConfig cfg = new CrmWechatMpConfig();
        cfg.setAppId("wx-off");
        cfg.setConnected(false);
        cfg.setSecretEnc("enc");
        when(wechatMpConfigMapper.selectByAppId("wx-off")).thenReturn(cfg);

        ServiceException ex = assertThrows(ServiceException.class, () -> crmEventService.updateCrmEvent(event));

        assertEquals("wechat_mp_app_id_not_connected", ex.getMessage());
    }

    @Test
    void updateMpAutomationRequiresResolvedAppIdMatchingPreset()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setMpRegistrationAutomationEnabled(true);
        event.setMpRegistrationPresetId(12L);
        CrmWechatMpMessagePreset preset = new CrmWechatMpMessagePreset();
        preset.setId(12L);
        preset.setStatus("0");
        preset.setAppId("wx-preset");
        when(wechatMpMessagePresetService.getById(12L)).thenReturn(preset);
        when(eventWechatMpAppIdResolver.resolve(event)).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class, () -> crmEventService.updateCrmEvent(event));

        assertEquals("请先配置活动默认公众号或创建推广码", ex.getMessage());
    }
}
