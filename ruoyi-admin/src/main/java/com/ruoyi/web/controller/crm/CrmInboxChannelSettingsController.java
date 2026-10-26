package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.InboxChannelSettingsUpdateRequest;
import com.ruoyi.crm.service.IInboxChannelSettingsService;

/**
 * Unified Inbox channel enable switches (product-level, decoupled from Integrations config).
 */
@RestController
@RequestMapping("/crm/inbox")
public class CrmInboxChannelSettingsController extends BaseController
{
    @Autowired
    private IInboxChannelSettingsService inboxChannelSettingsService;

    @GetMapping("/channels")
    public AjaxResult getChannels(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        requireTenant(tenant);
        return AjaxResult.success("ok", inboxChannelSettingsService.getView(tenant));
    }

    @PreAuthorize("@ss.hasPermi('crm:inbox:channels:manage') or @ss.hasRole('businessadmin')")
    @PutMapping("/channels")
    public AjaxResult updateChannels(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestBody InboxChannelSettingsUpdateRequest body)
    {
        requireTenant(tenant);
        return AjaxResult.success("ok",
                inboxChannelSettingsService.updatePartial(tenant, body, resolveOperator()));
    }

    private static void requireTenant(String tenant)
    {
        if (StringUtils.isBlank(tenant))
        {
            throw new ServiceException("Missing tenant header", 400);
        }
    }

    private static String resolveOperator()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null)
            {
                return null;
            }
            if (StringUtils.isNotBlank(loginUser.getUsername()))
            {
                return loginUser.getUsername();
            }
            if (loginUser.getUserId() != null)
            {
                return String.valueOf(loginUser.getUserId());
            }
        }
        catch (Exception ignored)
        {
            // anonymous / internal
        }
        return null;
    }
}
