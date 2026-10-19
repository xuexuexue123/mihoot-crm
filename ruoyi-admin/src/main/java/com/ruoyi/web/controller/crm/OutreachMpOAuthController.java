package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpOAuthService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OutreachMpOAuthController
{
    private static final Logger log = LoggerFactory.getLogger(OutreachMpOAuthController.class);

    @Autowired
    private OutreachMpOAuthService mpOAuthService;

    @Autowired
    private OutreachCheckInTokenService checkInTokenService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @GetMapping("/outreach/wechat/oauth/authorize")
    public void authorize(@RequestParam("checkInToken") String checkInToken,
            @RequestParam("redirectUri") String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        VerifiedCheckInPayload payload = checkInTokenService.verify(checkInToken);
        MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
        if (tenant == null)
        {
            response.sendError(HttpStatus.NOT_FOUND, "Tenant not found");
            return;
        }
        try
        {
            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
            String wechatUrl = mpOAuthService.buildAuthorizeRedirect(checkInToken, redirectUri, request);
            response.sendRedirect(wechatUrl);
        }
        catch (ServiceException ex)
        {
            response.sendError(ex.getCode() != null ? ex.getCode() : HttpStatus.FORBIDDEN, ex.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @GetMapping("/outreach/wechat/oauth/callback")
    public void callback(@RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        try
        {
            VerifiedCheckInPayload payload = mpOAuthService.verifyStatePayload(state);
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                response.sendError(HttpStatus.NOT_FOUND, "Tenant not found");
                return;
            }
            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
            String redirectUri = mpOAuthService.handleCallback(code, state, request, response);
            response.sendRedirect(redirectUri);
        }
        catch (ServiceException ex)
        {
            log.warn("WeChat OAuth callback failed: {}", ex.getMessage());
            response.sendError(ex.getCode() != null ? ex.getCode() : HttpStatus.FORBIDDEN, ex.getMessage());
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
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
