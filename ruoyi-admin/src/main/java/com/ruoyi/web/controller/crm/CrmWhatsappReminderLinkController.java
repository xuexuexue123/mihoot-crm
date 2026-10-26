package com.ruoyi.web.controller.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.ReminderLinkCreateRequest;
import com.ruoyi.crm.domain.vo.ReminderLinkCreateResultVo;
import com.ruoyi.crm.service.ICrmReminderLinkService;

/**
 * WhatsApp Reminder Link 鉴权创建
 */
@RestController
@RequestMapping("/crm/whatsapp/reminder")
public class CrmWhatsappReminderLinkController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappReminderLinkController.class);

    @Autowired
    private ICrmReminderLinkService reminderLinkService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:send')")
    @PostMapping("/links")
    public AjaxResult create(@RequestBody ReminderLinkCreateRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        try
        {
            String createdBy = null;
            try
            {
                createdBy = getUsername();
            }
            catch (Exception ignored)
            {
                // anonymous should not reach here due to PreAuthorize
            }
            ReminderLinkCreateResultVo result = reminderLinkService.create(tenant, createdBy, body);
            return AjaxResult.success(result);
        }
        catch (ServiceException e)
        {
            log.warn("POST /crm/whatsapp/reminder/links rejected tenant={} msg={}", tenant, e.getMessage());
            int code = e.getCode() != null ? e.getCode() : 400;
            return AjaxResult.error(code, e.getMessage());
        }
    }
}
