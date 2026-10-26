package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.EventSelfCheckInRequest;
import com.ruoyi.crm.domain.vo.EventSelfCheckInResultVo;
import com.ruoyi.crm.domain.vo.OutreachEventCheckInVo;
import com.ruoyi.crm.service.IEventOnsiteSelfCheckInService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpOAuthService;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpSessionPayload;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.common.utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@RestController
public class OutreachEventCheckInController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(OutreachEventCheckInController.class);

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private OutreachCheckInTokenService checkInTokenService;

    @Autowired
    private IEventOnsiteSelfCheckInService selfCheckInService;

    @Autowired
    private OutreachMpOAuthService mpOAuthService;

    @GetMapping("/outreacheventcheckin")
    public AjaxResult getCheckInPage(@RequestParam(value = "token", required = false) String token,
            HttpServletRequest request)
    {
        if (StringUtils.isBlank(token))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }
        try
        {
            VerifiedCheckInPayload payload = checkInTokenService.verify(token);
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }
            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            payload = selfCheckInService.verifyAndResolveSession(token, tenant.getTenant());
            OutreachMpSessionPayload mpSession = mpOAuthService.readSession(
                    request, payload.getTenantId(), payload.getEventId());
            OutreachEventCheckInVo vo = selfCheckInService.getCheckInPageInfo(
                    token, payload, request.getHeader("User-Agent"), mpSession);
            return AjaxResult.success(vo);
        }
        catch (ServiceException ex)
        {
            return serviceExceptionResult(ex);
        }
        catch (Exception ex)
        {
            log.error("获取签到页失败: ", ex);
            return AjaxResult.error("Failed to load check-in page");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/outreacheventcheckin")
    public AjaxResult submitCheckIn(@RequestBody(required = false) EventSelfCheckInRequest request,
            HttpServletRequest httpRequest)
    {
        if (request == null || StringUtils.isBlank(request.getCheckInToken()))
        {
            return AjaxResult.error(HttpStatus.FORBIDDEN, "Wrong request");
        }
        try
        {
            VerifiedCheckInPayload payload = checkInTokenService.verify(request.getCheckInToken());
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }
            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

            payload = selfCheckInService.verifyAndResolveSession(request.getCheckInToken(), tenant.getTenant());
            OutreachMpSessionPayload mpSession = mpOAuthService.readSession(
                    httpRequest, payload.getTenantId(), payload.getEventId());
            EventSelfCheckInResultVo vo = selfCheckInService.submitSelfCheckIn(payload, request, mpSession);
            if (vo.isAlreadyCheckedIn())
            {
                return new AjaxResult(HttpStatus.CONFLICT, "ALREADY_CHECKED_IN", vo);
            }
            return AjaxResult.success(vo);
        }
        catch (ServiceException ex)
        {
            return serviceExceptionResult(ex);
        }
        catch (Exception ex)
        {
            log.error("自助签到失败: ", ex);
            return AjaxResult.error("Check-in failed");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    private AjaxResult serviceExceptionResult(ServiceException ex)
    {
        Integer code = ex.getCode();
        if (code != null)
        {
            return AjaxResult.error(code, ex.getMessage());
        }
        return AjaxResult.error(ex.getMessage());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant)
    {
        if (StringUtils.isBlank(tenant.getTenant()))
        {
            throw new RuntimeException("Failed to get required information.");
        }
        if (!dynamicRoutingDataSource.existDataSource(tenant.getTenant()))
        {
            if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
            {
                throw new RuntimeException("Application [" + tenant.getTenant() + "] expired");
            }
            if (tenant.getExpirationDate() != null && tenant.getExpirationDate().before(DateUtils.getNowDate()))
            {
                throw new RuntimeException("Application [" + tenant.getTenant() + "] expired");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenant.getTenant(), map);
        }
    }
}
