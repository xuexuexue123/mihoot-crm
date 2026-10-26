package com.ruoyi.crm.service.followup;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.mapper.CrmFollowupRuleEnrollmentMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 每 15 分钟跨租户抢占到期 enrollment 并建任务（漏跑补跑）。
 */
@Component
public class FollowupRuleScheduler
{
    private static final Logger log = LoggerFactory.getLogger(FollowupRuleScheduler.class);

    private final Set<String> missingSchemaWarnedTenants = ConcurrentHashMap.newKeySet();

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${followup-rule.scheduler-enabled:true}")
    private boolean schedulerEnabled;

    @Value("${followup-rule.max-retry:3}")
    private int maxRetry;

    @Value("${followup-rule.claim-limit:200}")
    private int claimLimit;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmFollowupRuleEnrollmentMapper enrollmentMapper;

    @Autowired
    private FollowupRuleEngine engine;

    @Scheduled(cron = "0 */15 * * * ?")
    public void processDueEnrollments()
    {
        if (!schedulerEnabled)
        {
            return;
        }
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        for (MasterTenant tenant : tenants)
        {
            if (!isActiveTenant(tenant))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                List<CrmFollowupRuleEnrollment> due = enrollmentMapper.selectDuePending(
                        tenant.getTenant(), new Date(), maxRetry, claimLimit);
                missingSchemaWarnedTenants.remove(tenant.getTenant());
                for (CrmFollowupRuleEnrollment enrollment : due)
                {
                    try
                    {
                        engine.processDueEnrollment(enrollment);
                    }
                    catch (Exception ex)
                    {
                        log.warn("followup due enrollment {} failed: {}", enrollment.getEnrollmentId(),
                                ex.getMessage());
                    }
                }
            }
            catch (Exception ex)
            {
                if (isMissingSchema(ex))
                {
                    if (missingSchemaWarnedTenants.add(tenant.getTenant()))
                    {
                        log.warn("Followup-rule scheduler skipped tenant {}: tables missing; "
                                + "run scripts/sql/seed-crm-followup-rule-ddl.sql", tenant.getTenant());
                    }
                }
                else
                {
                    log.warn("Followup-rule scheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
                }
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    private boolean isActiveTenant(MasterTenant tenant)
    {
        if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
        {
            return false;
        }
        if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
        {
            return false;
        }
        return tenant.getExpirationDate() == null || !tenant.getExpirationDate().before(DateUtils.getNowDate());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant)
    {
        String tenantId = tenant.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
    }

    private static boolean isMissingSchema(Throwable ex)
    {
        Throwable current = ex;
        while (current != null)
        {
            String message = current.getMessage();
            if (message != null && message.contains("crm_followup_rule") && message.contains("does not exist"))
            {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
