package com.ruoyi.crm.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmLeadPoolConfig;
import com.ruoyi.crm.domain.dto.LeadReleaseRequest;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmLeadPoolService;
import com.ruoyi.crm.service.impl.CrmLeadPoolConfigServiceImpl;
import com.ruoyi.crm.service.support.LeadRecyclePolicyDefaults;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 跨租户：超时未跟进回收 + 超持有量回收。
 */
@Component("leadPoolRecycleTask")
public class LeadPoolRecycleTask
{
    private static final Logger log = LoggerFactory.getLogger(LeadPoolRecycleTask.class);
    private static final int BATCH_LIMIT = 200;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmLeadPoolConfigServiceImpl poolConfigService;

    @Autowired
    private ICrmLeadPoolService leadPoolService;

    @Scheduled(cron = "0 */15 * * * ?")
    public void recycleStaleAssignedLeads()
    {
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        if (tenants == null || tenants.isEmpty())
        {
            return;
        }
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
                recycleCurrentTenant(tenant.getTenant());
            }
            catch (Exception ex)
            {
                log.warn("Lead pool recycle tenant {}: {}", tenant.getTenant(), ex.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    void recycleCurrentTenant(String tenantId)
    {
        CrmLeadPoolConfig config = poolConfigService.getConfig();
        if (config.getRecycleEnabled() != null && !config.getRecycleEnabled())
        {
            return;
        }
        int idleHours = LeadRecyclePolicyDefaults.resolveIdleHours(config.getIdleToPoolHours(),
                config.getProtectDays());
        int processed = recycleIdle(idleHours);
        int holdProcessed = recycleOverHold(LeadRecyclePolicyDefaults.resolveMaxHold(config.getMaxHoldCount()));
        if (processed + holdProcessed > 0)
        {
            log.info("Lead pool auto recycle tenant={} idle={} hold={}", tenantId, processed, holdProcessed);
        }
    }

    private int recycleIdle(int idleHours)
    {
        List<CrmLead> stale = crmLeadMapper.selectLeadsForAutoRecycle(idleHours, BATCH_LIMIT);
        return releaseAll(stale);
    }

    private int recycleOverHold(int maxHold)
    {
        List<Long> userIds = crmLeadMapper.selectUserIdsOverHold(maxHold);
        if (userIds == null || userIds.isEmpty())
        {
            return 0;
        }
        int processed = 0;
        for (Long userId : userIds)
        {
            if (userId == null)
            {
                continue;
            }
            int assigned = crmLeadMapper.countAssignedLeadsByUserId(userId);
            int excess = assigned - maxHold;
            if (excess <= 0)
            {
                continue;
            }
            List<CrmLead> overflow = crmLeadMapper.selectAssignedLeadsOverHold(userId,
                    Math.min(excess, BATCH_LIMIT));
            processed += releaseAll(overflow);
        }
        return processed;
    }

    private int releaseAll(List<CrmLead> leads)
    {
        if (leads == null || leads.isEmpty())
        {
            return 0;
        }
        LeadReleaseRequest request = new LeadReleaseRequest();
        request.setReason("auto_recycle");
        int processed = 0;
        for (CrmLead lead : leads)
        {
            try
            {
                leadPoolService.release(lead.getLeadId(), request, true);
                processed++;
            }
            catch (Exception ex)
            {
                log.warn("Auto recycle skipped leadId={}: {}", lead.getLeadId(), ex.getMessage());
            }
        }
        return processed;
    }

    private boolean isActiveTenant(MasterTenant tenant)
    {
        if (tenant == null || StringUtils.isEmpty(tenant.getTenant()))
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
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
    }
}
