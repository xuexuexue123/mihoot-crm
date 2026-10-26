package com.ruoyi.web.controller.publicapi;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.ReminderLinkPublicVo;
import com.ruoyi.crm.service.ICrmReminderLinkService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * Reminder Link 公开查询（免登录）。
 * 租户解析方式 A：query {@code tenant} 或 header {@code X-Tenant-Slug} → website slug → tenant_id。
 */
@RestController
@RequestMapping("/crm/public/reminder/links")
public class PublicReminderLinkController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(PublicReminderLinkController.class);

    private static final String HEADER_TENANT_SLUG = "X-Tenant-Slug";

    @Autowired
    private ICrmReminderLinkService reminderLinkService;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/{token}")
    public AjaxResult getByToken(@PathVariable String token,
            @RequestParam(value = "tenant", required = false) String tenantQuery,
            HttpServletRequest httpRequest)
    {
        String tenantSlug = firstNonBlank(tenantQuery, httpRequest.getHeader(HEADER_TENANT_SLUG));
        try
        {
            ReminderLinkPublicVo vo = reminderLinkService.getPublicByToken(token, tenantSlug);
            return AjaxResult.success(vo);
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public reminder link get failed token={}", token, ex);
            return AjaxResult.error("链接无效");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private static String firstNonBlank(String a, String b)
    {
        if (StringUtils.isNotBlank(a))
        {
            return a.trim();
        }
        if (StringUtils.isNotBlank(b))
        {
            return b.trim();
        }
        return null;
    }

    private static AjaxResult toError(ServiceException ex)
    {
        Integer code = ex.getCode();
        if (code != null)
        {
            return AjaxResult.error(code, ex.getMessage());
        }
        return AjaxResult.error(ex.getMessage());
    }
}
