package com.ruoyi.crm.mobile.push;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmMobilePushDevice;
import com.ruoyi.crm.domain.dto.MobileDeviceRegisterRequest;
import com.ruoyi.crm.mapper.CrmMobilePushDeviceMapper;

@ExtendWith(MockitoExtension.class)
class MobilePushDeviceServiceTest
{
    @Mock
    private CrmMobilePushDeviceMapper deviceMapper;

    @InjectMocks
    private MobilePushDeviceService service;

    @Test
    void register_rejectsInvalidPlatform()
    {
        MobileDeviceRegisterRequest body = new MobileDeviceRegisterRequest();
        body.setPlatform("windows");
        body.setPushToken("tok");
        assertThrows(ServiceException.class, () -> service.register("t1", 1L, body));
    }

    @Test
    void register_upsertsDevice()
    {
        MobileDeviceRegisterRequest body = new MobileDeviceRegisterRequest();
        body.setPlatform("ios");
        body.setPushToken("abc");
        body.setAppVersion("1.0.0");
        service.register("t1", 42L, body);
        verify(deviceMapper).upsert(any(CrmMobilePushDevice.class));
    }

    @Test
    void unregister_withoutToken_deletesAll()
    {
        service.unregister("t1", 9L, null);
        verify(deviceMapper).deleteAllByUser("t1", 9L);
        verify(deviceMapper, never()).deleteByToken(eq("t1"), eq(9L), any());
    }
}
