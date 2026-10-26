package com.ruoyi.crm.service.nurture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.mapper.EventNurtureEnrollmentMapper;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

@Component
public class EventNurtureScheduler
{
    private static final Logger log = LoggerFactory.getLogger(EventNurtureScheduler.class);
    private static final int BATCH = 500;

    /** 已提示缺少培育表的租户，避免每分钟刷 WARN */
    private final Set<String> missingSchemaWarnedTenants = ConcurrentHashMap.newKeySet();

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private EventNurtureEnrollmentMapper enrollmentMapper;

    @Autowired
    private IEventNurtureEnrollmentService enrollmentService;

    @Scheduled(cron = "0 * * * * ?")
    public void processDueEnrollments()
    {
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
                List<CrmEventNurtureEnrollment> due = enrollmentMapper.selectDue(
                        tenant.getTenant(), DateUtils.getNowDate(), BATCH);
                missingSchemaWarnedTenants.remove(tenant.getTenant());
                for (CrmEventNurtureEnrollment enrollment : due)
                {
                    scheduleProcess(tenant.getTenant(), enrollment.getId());
                }
            }
            catch (Exception ex)
            {
                if (isMissingNurtureSchema(ex))
                {
                    if (missingSchemaWarnedTenants.add(tenant.getTenant()))
                    {
                        log.warn("Nurture scheduler skipped tenant {}: nurture tables missing; "
                                + "run scripts/sql/patch-event-nurture-automation.sql on this tenant database",
                                tenant.getTenant());
                    }
                }
                else
                {
                    log.warn("Nurture scheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
                }
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    void scheduleProcess(String tenantKey, Long enrollmentId)
    {
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    enrollmentService.processDueEnrollment(enrollmentId, tenantKey);
                }
                catch (Exception ex)
                {
                    log.error("Nurture process failed tenant={} enrollment={}", tenantKey, enrollmentId, ex);
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
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

    private static boolean isMissingNurtureSchema(Throwable ex)
    {
        Throwable current = ex;
        while (current != null)
        {
            String message = current.getMessage();
            if (message != null && message.contains("crm_event_nurture")
                    && message.contains("does not exist"))
            {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
