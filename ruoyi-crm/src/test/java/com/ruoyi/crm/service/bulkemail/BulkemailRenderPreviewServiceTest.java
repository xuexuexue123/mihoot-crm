package com.ruoyi.crm.service.bulkemail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.dto.BulkemailRenderPreviewRequest;
import com.ruoyi.crm.domain.dto.BulkemailRenderResult;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;

@ExtendWith(MockitoExtension.class)
class BulkemailRenderPreviewServiceTest
{
    @Mock
    private CrmContactMapper crmContactMapper;

    @Mock
    private CrmBulkemailMapper crmBulkemailMapper;

    @Mock
    private BulkemailRenderService renderService;

    @InjectMocks
    private BulkemailRenderPreviewService service;

    @Test
    void preview_contactNotFound_returns404()
    {
        BulkemailRenderPreviewRequest request = new BulkemailRenderPreviewRequest();
        request.setContactId("missing");

        when(crmContactMapper.selectCrmContactByContactId("missing")).thenReturn(null);

        ServiceException ex = assertThrows(ServiceException.class,
                () -> service.preview(request, "tenant-a"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getCode());
    }

    @Test
    void preview_usesSameRenderServiceAsWorker()
    {
        CrmContact contact = new CrmContact();
        contact.setContactId("c-1");
        contact.setName("张三");
        contact.setWorkEmail("zhang@example.com");

        CrmBulkemail campaign = new CrmBulkemail();
        campaign.setBulkId("bulk-1");
        campaign.setSubject("Hello {{contactName}}");
        campaign.setEmailContent("<p>Hi {{contactName}}</p>");
        campaign.setMetadata("{\"personalizationEnabled\":true}");

        BulkemailRenderPreviewRequest request = new BulkemailRenderPreviewRequest();
        request.setBulkId("bulk-1");
        request.setContactId("c-1");

        when(crmContactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);
        when(crmBulkemailMapper.selectCrmBulkemailByBulkId("bulk-1")).thenReturn(campaign);

        BulkemailRenderContext ctx = new BulkemailRenderContext(campaign, null, contact, "tenant-a", true);
        when(renderService.buildContext(eq(campaign), any(), eq(contact), eq("tenant-a"), eq(true))).thenReturn(ctx);
        when(renderService.render(eq("Hello {{contactName}}"), eq("<p>Hi {{contactName}}</p>"), eq(ctx)))
                .thenReturn(new BulkemailRenderResult("Hello 张三", "<p>Hi 张三</p>"));

        BulkemailRenderResult result = service.preview(request, "tenant-a");

        assertEquals("Hello 张三", result.getSubject());
        assertEquals("<p>Hi 张三</p>", result.getEmailContent());
        verify(renderService).render(eq("Hello {{contactName}}"), eq("<p>Hi {{contactName}}</p>"), eq(ctx));
    }

    @Test
    void preview_withoutBulkId_usesRequestBody()
    {
        CrmContact contact = new CrmContact();
        contact.setContactId("c-1");
        contact.setName("张三");

        BulkemailRenderPreviewRequest request = new BulkemailRenderPreviewRequest();
        request.setContactId("c-1");
        request.setSubject("Hi {{contactName}}");
        request.setEmailContent("<p>{{contactName}}</p>");
        request.setPersonalizationEnabled(true);

        when(crmContactMapper.selectCrmContactByContactId("c-1")).thenReturn(contact);

        BulkemailRenderContext ctx = new BulkemailRenderContext(null, null, contact, "tenant-a", true);
        when(renderService.buildContext(isNull(), any(), eq(contact), eq("tenant-a"), eq(true))).thenReturn(ctx);
        when(renderService.render(eq("Hi {{contactName}}"), eq("<p>{{contactName}}</p>"), eq(ctx)))
                .thenReturn(new BulkemailRenderResult("Hi 张三", "<p>张三</p>"));

        BulkemailRenderResult result = service.preview(request, "tenant-a");

        assertEquals("Hi 张三", result.getSubject());
        assertEquals("<p>张三</p>", result.getEmailContent());
    }
}
