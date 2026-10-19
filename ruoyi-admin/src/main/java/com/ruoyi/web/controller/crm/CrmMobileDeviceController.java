package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.dto.MobileDeviceRegisterRequest;
import com.ruoyi.crm.domain.dto.MobileDeviceUnregisterRequest;
import com.ruoyi.crm.mobile.push.MobilePushDeviceService;

/**
 * Inbox 移动壳设备 token 注册 / 解绑。
 */
@RestController
@RequestMapping("/crm/mobile")
public class CrmMobileDeviceController extends BaseController
{
    private static final String INBOX_PERMS =
            "crm:whatsapp:conversations,crm:messenger:conversations,"
                    + "crm:wechat_mp:conversations,crm:webchat:conversations";

    @Autowired
    private MobilePushDeviceService deviceService;

    @PreAuthorize("@ss.hasAnyPermi('" + INBOX_PERMS + "')")
    @PostMapping("/device")
    public AjaxResult registerDevice(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestBody MobileDeviceRegisterRequest body)
    {
        requireTenant(tenant);
        deviceService.register(tenant, SecurityUtils.getUserId(), body);
        return AjaxResult.success("ok");
    }

    @PreAuthorize("@ss.hasAnyPermi('" + INBOX_PERMS + "')")
    @DeleteMapping("/device")
    public AjaxResult unregisterDevice(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestBody(required = false) MobileDeviceUnregisterRequest body,
            @RequestParam(value = "pushToken", required = false) String pushTokenQuery)
    {
        requireTenant(tenant);
        String pushToken = body != null && body.getPushToken() != null
                ? body.getPushToken() : pushTokenQuery;
        deviceService.unregister(tenant, SecurityUtils.getUserId(), pushToken);
        return AjaxResult.success("ok");
    }

    private static void requireTenant(String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            throw new ServiceException("Missing tenant header", 400);
        }
    }
}
