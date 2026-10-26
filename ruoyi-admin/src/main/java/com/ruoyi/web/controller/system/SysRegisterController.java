package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.constant.TenantConstants;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.web.service.TenantRegisterService;
import com.ruoyi.tenant.domain.MasterRegistration;
import com.ruoyi.tenant.dto.TenantDatabaseDTO;
import com.ruoyi.tenant.form.TenantRegisterBody;
import com.ruoyi.tenant.service.IMasterRegistrationService;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.tenant.util.TenantWebsiteSlugUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.framework.web.service.SysRegisterService;
import com.ruoyi.system.service.ISysConfigService;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private TenantRegisterService tenantRegisterService;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private IMasterRegistrationService masterRegistrationService;

    @Autowired
    private TenantWebsiteSlugUtils tenantWebsiteSlugUtils;


    @RateLimiter(time = 86400, count = 10, limitType = LimitType.IP)
    @PostMapping("/register")
    public AjaxResult registerTenant(@Valid @RequestBody TenantRegisterBody tenantRegisterBody){

        String email = tenantRegisterBody.getEmail();
        //添加数据库表Master_Registration

        //添加数据库表Master_tenant

//        if (TenantConstants.NOT_UNIQUE.equals(masterTenantService.checkTenantNameUnique(tenantRegisterBody.tenantName)))
//        {
//            return AjaxResult.error("注册'" + tenantRegisterBody.getTenantName() + "'失败，账号已存在");
//        }
        if (TenantConstants.NOT_UNIQUE.equals(masterRegistrationService.checkCompanyNameUnique(tenantRegisterBody.companyName)))
        {
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，公司已存在");
        }
        if (TenantConstants.NOT_UNIQUE.equals(masterRegistrationService.checkMobileUnique(tenantRegisterBody.mobile)))
        {
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，手机号已存在");
        }
        if (TenantConstants.NOT_UNIQUE.equals(masterRegistrationService.checkEmailUnique(tenantRegisterBody.email)))
        {
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，邮箱已存在");
        }
        if(email.indexOf("@") == -1) {
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，邮箱错误！");
        }

        //生成Tenant Name（直接使用公司缩写）
        String tenantAlias = tenantRegisterBody.getTenantAlias();
        String tenantName = tenantAlias;
        if (TenantConstants.NOT_UNIQUE.equals(masterTenantService.checkTenantNameUnique(tenantAlias))) {
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，公司缩写已存在");
        }
        String websiteSlug = TenantWebsiteSlugUtils.fromTenantAlias(tenantAlias);
        AjaxResult slugValidation = validateWebsiteSlug(websiteSlug);
        if (slugValidation != null) {
            return slugValidation;
        }
        tenantRegisterBody.setTenantName(tenantName);

        //生成admin用户名
        String adminName = "";
        if (email!="" && email != null) {
            email = email.substring(email.indexOf("@"));
            adminName = "admin" + email;
            tenantRegisterBody.setAdminName(adminName);
        } else {
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，邮箱错误！");
        }

        TenantDatabaseDTO tenantDatabase=null;
        try {

            //初始化数据库表
            tenantDatabase=tenantRegisterService.initDatabase(tenantRegisterBody);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，创建租户时发生错误");
        }catch (Exception ex){
            ex.printStackTrace();
            return AjaxResult.error("注册'" + tenantRegisterBody.getCompanyName() + "'失败，请与我们联系");
        }
        //插入master_registration
        MasterRegistration masterRegistration = new MasterRegistration();
        masterRegistration.setContactName(tenantRegisterBody.getContactName());
        masterRegistration.setCompanyName(tenantRegisterBody.getCompanyName());
        masterRegistration.setEmail(tenantRegisterBody.getEmail());
        masterRegistration.setMobile(tenantRegisterBody.getMobile());
        masterRegistration.setStatus(1L);
        //设置plan
        masterRegistration.setPlan("1");
        Date now = DateUtils.getNowDate();
        Date last_date = DateUtils.addDays(now, 365);

        masterRegistration.setStartDate(now);
        masterRegistration.setEndDate(last_date);

        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        masterRegistration.setOperIp(ip);

        int j = masterRegistrationService.insertMasterRegistration(masterRegistration);
        //插入master_tenant表
        tenantDatabase.setWebsiteSlug(websiteSlug);
        tenantDatabase.setCompanyName(tenantRegisterBody.getCompanyName());
        int i = masterTenantService.insertMasterTenant(tenantDatabase);

        return toAjax(i);
    }

    /**
     * 检查租户标识（tenantAlias）是否可用
     * 前端注册向导在用户输入时实时调用，避免提交时才发现冲突
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @PostMapping("/check-tenant-alias")
    public AjaxResult checkTenantAlias(@RequestBody Map<String, String> body) {
        String tenantAlias = body.get("tenantAlias");
        if (tenantAlias == null || tenantAlias.isEmpty()) {
            return error("格式不正确");
        }
        if (!tenantAlias.matches("^[a-z][a-z0-9_]{2,29}$")) {
            return error("格式不正确");
        }
        String websiteSlug = TenantWebsiteSlugUtils.fromTenantAlias(tenantAlias);
        if (TenantConstants.NOT_UNIQUE.equals(masterTenantService.checkTenantNameUnique(tenantAlias))) {
            return AjaxResult.success()
                    .put("available", false)
                    .put("message", "租户标识已被占用")
                    .put("websiteSlug", websiteSlug)
                    .put("websiteSlugAvailable", false);
        }
        if (!TenantWebsiteSlugUtils.isValidFormat(websiteSlug)) {
            return AjaxResult.success()
                    .put("available", false)
                    .put("message", "子域名格式不正确")
                    .put("websiteSlug", websiteSlug)
                    .put("websiteSlugAvailable", false);
        }
        if (tenantWebsiteSlugUtils.isReserved(websiteSlug)) {
            return AjaxResult.success()
                    .put("available", false)
                    .put("message", "该子域名不可用")
                    .put("websiteSlug", websiteSlug)
                    .put("websiteSlugAvailable", false);
        }
        if (TenantConstants.NOT_UNIQUE.equals(masterTenantService.checkWebsiteSlugUnique(websiteSlug))) {
            return AjaxResult.success()
                    .put("available", false)
                    .put("message", "子域名已被占用")
                    .put("websiteSlug", websiteSlug)
                    .put("websiteSlugAvailable", false);
        }
        return AjaxResult.success()
                .put("available", true)
                .put("websiteSlug", websiteSlug)
                .put("websiteSlugAvailable", true)
                .put("previewUrl", tenantWebsiteSlugUtils.buildPreviewUrl(websiteSlug));
    }

    private AjaxResult validateWebsiteSlug(String websiteSlug) {
        if (!TenantWebsiteSlugUtils.isValidFormat(websiteSlug)) {
            return AjaxResult.error("注册失败，子域名格式不正确");
        }
        if (tenantWebsiteSlugUtils.isReserved(websiteSlug)) {
            return AjaxResult.error("注册失败，该子域名不可用");
        }
        if (TenantConstants.NOT_UNIQUE.equals(masterTenantService.checkWebsiteSlugUnique(websiteSlug))) {
            return AjaxResult.error("注册失败，子域名已被占用");
        }
        return null;
    }
}
