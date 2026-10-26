package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.LeadQualifySurveySubmitRequest;
import com.ruoyi.crm.service.support.LeadQualifySurveyService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.QualifyVerifiedPayload;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 认定潜客问卷外投公开接口（token 自校验，无 JWT）
 */
@RestController
public class CrmQualifyOutreachController extends BaseController
{
    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private LeadQualifySurveyService leadQualifySurveyService;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @GetMapping("/outreach/qualify-survey")
    public AjaxResult getQualifySurvey(@RequestParam(value = "token", required = false) String token)
    {
        if (StringUtils.isBlank(token))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }

        try
        {
            QualifyVerifiedPayload payload = leadQualifySurveyService.verifyToken(token);
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }

            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
            return AjaxResult.success(leadQualifySurveyService.loadSurvey(payload));
        }
        catch (ServiceException ex)
        {
            Integer code = ex.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            logger.error("获取认定问卷失败: ", ex);
            return AjaxResult.error("Failed to load qualify survey");
        }
        finally
        {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @PostMapping("/outreach/qualify-survey")
    public AjaxResult submitQualifySurvey(@RequestBody LeadQualifySurveySubmitRequest request)
    {
        if (request == null || StringUtils.isBlank(request.getToken()))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "Missing required parameters");
        }

        try
        {
            QualifyVerifiedPayload payload = leadQualifySurveyService.verifyToken(request.getToken());
            MasterTenant tenant = masterTenantService.selectTenantByTenantId(payload.getTenantId());
            if (tenant == null)
            {
                return AjaxResult.error(HttpStatus.NOT_FOUND, "Failed to found tenant");
            }

            ensureTenantDataSourceReady(tenant);
            DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
            return AjaxResult.success(leadQualifySurveyService.submit(request));
        }
        catch (ServiceException ex)
        {
            Integer code = ex.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, ex.getMessage());
            }
            return AjaxResult.error(ex.getMessage());
        }
        catch (Exception ex)
        {
            logger.error("提交认定问卷失败: ", ex);
            return AjaxResult.error("Failed to submit qualify survey");
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
