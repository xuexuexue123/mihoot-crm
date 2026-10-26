package com.ruoyi.crm.wechatmp.service.impl;

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
import com.ruoyi.crm.wechatmp.dto.WechatMpQrcodeCreateRequest;

@ExtendWith(MockitoExtension.class)
class WechatMpQrcodeServiceImplEventAppIdTest
{
    @Mock
    private CrmEventMapper crmEventMapper;

    @InjectMocks
    private WechatMpQrcodeServiceImpl qrcodeService;

    @Test
    void createRejectsAppIdMismatchWithBoundEvent()
    {
        CrmEvent event = new CrmEvent();
        event.setEventId("evt-1");
        event.setWechatMpAppId("wx-bound");
        when(crmEventMapper.selectCrmEventByEventId("evt-1")).thenReturn(event);

        WechatMpQrcodeCreateRequest req = new WechatMpQrcodeCreateRequest();
        req.setAppId("wx-other");
        req.setSceneStr("scene-1");
        req.setName("推广码");
        req.setCrmEventId("evt-1");

        ServiceException ex = assertThrows(ServiceException.class, () -> qrcodeService.create(req));

        assertEquals("wechat_mp_app_id_mismatch", ex.getMessage());
    }
}
