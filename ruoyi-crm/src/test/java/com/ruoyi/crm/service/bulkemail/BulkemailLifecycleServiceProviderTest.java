package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.dto.BulkemailCreateRequest;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.mapper.CrmCampaignContactsMapper;
import com.ruoyi.crm.service.ICrmBulkemailService;

@ExtendWith(MockitoExtension.class)
class BulkemailLifecycleServiceProviderTest
{
    @Mock
    private ICrmBulkemailService crmBulkemailService;

    @Mock
    private CrmBulkemailMapper crmBulkemailMapper;

    @Mock
    private CrmCampaignContactsMapper crmCampaignContactsMapper;

    @Mock
    private BulkemailSendWorker bulkemailSendWorker;

    @Mock
    private IBulkemailProviderConfigService providerConfigService;

    @InjectMocks
    private BulkemailLifecycleService service;

    @Test
    void sendNow_withoutProviderThrows409()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-1");
        campaign.setStatus(BulkemailLifecycleConstants.STATUS_DRAFT);
        when(crmBulkemailMapper.selectCrmBulkemailByBulkId("bulk-1")).thenReturn(campaign);
        when(crmCampaignContactsMapper.countValidEmailByCampaignId("bulk-1")).thenReturn(1);
        doThrow(new ServiceException(BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED, HttpStatus.CONFLICT))
                .when(providerConfigService).requireActive();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendNow("bulk-1", "tenant-a", "admin"));
        assertEquals(BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED, ex.getMessage());
        verify(bulkemailSendWorker, never()).sendAsync(any(), any());
    }

    @Test
    void create_immediate_withoutProviderThrows409()
    {
        BulkemailCreateRequest request = new BulkemailCreateRequest();
        request.setBulkId("bulk-2");
        request.setSendMode("immediate");
        when(crmBulkemailService.insertCrmBulkemail(any())).thenReturn(1);
        doThrow(new ServiceException(BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED, HttpStatus.CONFLICT))
                .when(providerConfigService).requireActive();

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.create(request, "tenant-a", "admin"));
        assertEquals(BulkemailProviderConfigServiceImpl.PROVIDER_NOT_CONFIGURED, ex.getMessage());
        verify(bulkemailSendWorker, never()).sendAsync(any(), any());
    }

    @Test
    void sendNow_withProviderEnqueuesWorker()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-3");
        campaign.setStatus(BulkemailLifecycleConstants.STATUS_DRAFT);
        when(crmBulkemailMapper.selectCrmBulkemailByBulkId("bulk-3")).thenReturn(campaign);
        when(crmCampaignContactsMapper.countValidEmailByCampaignId("bulk-3")).thenReturn(1);
        when(crmBulkemailMapper.tryTransitionStatus(eq("bulk-3"), anyList(),
                eq(BulkemailLifecycleConstants.STATUS_SENDING), any(), isNull(), isNull())).thenReturn(1);

        service.sendNow("bulk-3", "tenant-a", "admin");

        verify(providerConfigService).requireActive();
        verify(bulkemailSendWorker).sendAsync("bulk-3", "tenant-a");
    }
}
