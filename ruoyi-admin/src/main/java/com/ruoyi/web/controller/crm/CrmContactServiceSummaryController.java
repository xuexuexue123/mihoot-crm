package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.ContactServiceSummaryPatchDto;
import com.ruoyi.crm.domain.dto.ContactServiceSummaryRefreshDto;
import com.ruoyi.crm.domain.vo.ContactServiceSummaryVo;
import com.ruoyi.crm.service.IContactServiceSummaryService;

/**
 * Contact 服务摘要 API
 */
@RestController
@RequestMapping("/crm/contact")
public class CrmContactServiceSummaryController extends BaseController
{
    @Autowired
    private IContactServiceSummaryService contactServiceSummaryService;

    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping("/{contactId}/service-summary")
    public AjaxResult get(@PathVariable("contactId") String contactId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        ContactServiceSummaryVo data = contactServiceSummaryService.get(tenant, contactId);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:contact:edit')")
    @PostMapping("/{contactId}/service-summary/refresh")
    public AjaxResult refresh(@PathVariable("contactId") String contactId,
            @RequestBody(required = false) ContactServiceSummaryRefreshDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (body == null)
        {
            body = new ContactServiceSummaryRefreshDto();
            body.setReason("manual");
            body.setForceFull(Boolean.FALSE);
        }
        ContactServiceSummaryVo data = contactServiceSummaryService.refresh(tenant, contactId, body);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:contact:edit')")
    @PatchMapping("/{contactId}/service-summary")
    public AjaxResult patch(@PathVariable("contactId") String contactId,
            @RequestBody ContactServiceSummaryPatchDto body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        ContactServiceSummaryVo data = contactServiceSummaryService.patch(tenant, contactId, body);
        return success(data);
    }
}
