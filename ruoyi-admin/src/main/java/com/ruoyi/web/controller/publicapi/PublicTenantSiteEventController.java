package com.ruoyi.web.controller.publicapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.PublicWebsiteEventRegisterRequest;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.service.ICrmPublicTenantWebsiteEventService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

/**
 * 租户官网公开活动列表 / 详情 / 报名
 */
@RestController
@RequestMapping("/api/public/tenant-sites/{slug}/events")
public class PublicTenantSiteEventController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(PublicTenantSiteEventController.class);

    @Autowired
    private ICrmPublicTenantWebsiteEventService publicTenantWebsiteEventService;

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping
    public AjaxResult list(@PathVariable String slug,
            @RequestParam(required = false) Integer limit)
    {
        try
        {
            return AjaxResult.success(publicTenantWebsiteEventService.listEvents(slug, limit));
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public events list failed, slug={}", slug, ex);
            return AjaxResult.error("Failed to load events");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @RateLimiter(time = 60, count = 60, limitType = LimitType.IP)
    @GetMapping("/{eventId}")
    public AjaxResult detail(@PathVariable String slug, @PathVariable String eventId)
    {
        try
        {
            return AjaxResult.success(publicTenantWebsiteEventService.getEvent(slug, eventId));
        }
        catch (ServiceException ex)
        {
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("public event detail failed, slug={}, eventId={}", slug, eventId, ex);
            return AjaxResult.error("Failed to load event");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @PostMapping("/{eventId}/register")
    public AjaxResult register(@PathVariable String slug, @PathVariable String eventId,
            @RequestBody PublicWebsiteEventRegisterRequest request)
    {
        try
        {
            OutreachRegistrationSubmitResultVo result =
                    publicTenantWebsiteEventService.register(slug, eventId, request);
            log.info("website event register ok slug={} eventId={} sessionId={} registrationId={}",
                    slug, eventId,
                    request != null ? request.getSessionId() : null,
                    result != null ? result.getRegistrationId() : null);
            return AjaxResult.success(result);
        }
        catch (ServiceException ex)
        {
            log.warn("website event register rejected slug={} eventId={} sessionId={} msg={}",
                    slug, eventId,
                    request != null ? request.getSessionId() : null,
                    ex.getMessage());
            return toError(ex);
        }
        catch (Exception ex)
        {
            log.error("website event register failed, slug={}, eventId={}, mobile={}",
                    slug, eventId, maskMobile(request != null ? request.getMobilePhone() : null), ex);
            return AjaxResult.error("报名提交失败");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private AjaxResult toError(ServiceException ex)
    {
        Integer code = ex.getCode();
        if (code != null)
        {
            return AjaxResult.error(code, ex.getMessage());
        }
        return AjaxResult.error(ex.getMessage());
    }

    private static String maskMobile(String mobile)
    {
        if (StringUtils.isBlank(mobile) || mobile.length() < 7)
        {
            return "***";
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }
}
