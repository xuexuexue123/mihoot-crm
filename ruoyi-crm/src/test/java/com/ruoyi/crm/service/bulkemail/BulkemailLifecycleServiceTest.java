package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;

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
class BulkemailLifecycleServiceTest
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
    void create_draft_doesNotEnqueueWorker()
    {
        BulkemailCreateRequest request = new BulkemailCreateRequest();
        request.setBulkId("bulk-1");
        request.setSendMode("draft");
        request.setSubject("Test");
        when(crmBulkemailService.insertCrmBulkemail(any())).thenReturn(1);

        int rows = service.create(request, "tenant-a", "admin");

        assertEquals(1, rows);
        assertEquals(BulkemailLifecycleConstants.STATUS_DRAFT, request.getStatus());
        verify(bulkemailSendWorker, never()).sendAsync(any(), any());
    }

    @Test
    void create_immediate_enqueuesWorker()
    {
        BulkemailCreateRequest request = new BulkemailCreateRequest();
        request.setBulkId("bulk-2");
        request.setSendMode("immediate");
        when(crmBulkemailService.insertCrmBulkemail(any())).thenReturn(1);

        service.create(request, "tenant-a", "admin");

        assertEquals(BulkemailLifecycleConstants.STATUS_SENDING, request.getStatus());
        verify(bulkemailSendWorker).sendAsync("bulk-2", "tenant-a");
    }

    @Test
    void create_legacyActive_mapsImmediate()
    {
        BulkemailCreateRequest request = new BulkemailCreateRequest();
        request.setBulkId("bulk-3");
        request.setStatus("active");
        when(crmBulkemailService.insertCrmBulkemail(any())).thenReturn(1);

        service.create(request, "tenant-a", "admin");

        verify(bulkemailSendWorker).sendAsync("bulk-3", "tenant-a");
    }

    @Test
    void create_missingSendModeThrows()
    {
        BulkemailCreateRequest request = new BulkemailCreateRequest();
        request.setBulkId("bulk-4");

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.create(request, "tenant-a", "admin"));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getCode());
    }

    @Test
    void sendNow_conflictWhenNotDraftOrScheduled()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-5");
        campaign.setStatus(BulkemailLifecycleConstants.STATUS_SENT);
        when(crmBulkemailMapper.selectCrmBulkemailByBulkId("bulk-5")).thenReturn(campaign);
        when(crmCampaignContactsMapper.countValidEmailByCampaignId("bulk-5")).thenReturn(1);
        when(crmBulkemailMapper.tryTransitionStatus(eq("bulk-5"), anyList(),
                eq(BulkemailLifecycleConstants.STATUS_SENDING), any(), isNull(), isNull())).thenReturn(0);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.sendNow("bulk-5", "tenant-a", "admin"));
        assertEquals(HttpStatus.CONFLICT, ex.getCode());
    }

    @Test
    void sendNow_successEnqueuesWorker()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-6");
        campaign.setStatus(BulkemailLifecycleConstants.STATUS_DRAFT);
        when(crmBulkemailMapper.selectCrmBulkemailByBulkId("bulk-6")).thenReturn(campaign);
        when(crmCampaignContactsMapper.countValidEmailByCampaignId("bulk-6")).thenReturn(2);
        when(crmBulkemailMapper.tryTransitionStatus(eq("bulk-6"), anyList(),
                eq(BulkemailLifecycleConstants.STATUS_SENDING), any(), isNull(), isNull())).thenReturn(1);

        service.sendNow("bulk-6", "tenant-a", "admin");

        verify(bulkemailSendWorker).sendAsync("bulk-6", "tenant-a");
    }

    @Test
    void schedule_requiresFutureTime()
    {
        Date past = new Date(System.currentTimeMillis() - 60_000L);
        assertThrows(ServiceException.class,
                () -> service.schedule("bulk-7", past, "admin"));
    }

    @Test
    void getNormalized_mapsActiveToSent()
    {
        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-8");
        campaign.setStatus(BulkemailLifecycleConstants.STATUS_ACTIVE_LEGACY);
        when(crmBulkemailService.selectCrmBulkemailByBulkId("bulk-8")).thenReturn(campaign);

        CrmBulkemail result = service.getNormalized("bulk-8");

        assertEquals(BulkemailLifecycleConstants.STATUS_SENT, result.getStatus());
    }
}
