package com.ruoyi.crm.service.bulkemail;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 定时捞取到点的 scheduled 活动并触发发送
 */
@Component
public class BulkemailSendScheduler
{
    private static final Logger log = LoggerFactory.getLogger(BulkemailSendScheduler.class);
    private static final int BATCH_PER_TENANT = 50;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmBulkemailMapper crmBulkemailMapper;

    @Autowired
    private BulkemailSendWorker bulkemailSendWorker;

    @Autowired
    private IBulkemailProviderConfigService providerConfigService;

    @Scheduled(cron = "0 * * * * ?")
    public void pollDueCampaigns()
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
                List<String> due = crmBulkemailMapper.selectScheduledDue(DateUtils.getNowDate(), BATCH_PER_TENANT);
                for (String bulkId : due)
                {
                    try
                    {
                        try
                        {
                            providerConfigService.requireActive();
                        }
                        catch (Exception ex)
                        {
                            log.warn("Bulkemail scheduler skip bulkId={} tenant={}: {}",
                                    bulkId, tenant.getTenant(), ex.getMessage());
                            continue;
                        }
                        int updated = crmBulkemailMapper.tryTransitionStatus(bulkId,
                                Collections.singletonList(BulkemailLifecycleConstants.STATUS_SCHEDULED),
                                BulkemailLifecycleConstants.STATUS_SENDING,
                                BulkemailLifecycleConstants.SEND_MODE_SCHEDULED,
                                null, null);
                        if (updated > 0)
                        {
                            bulkemailSendWorker.sendAsync(bulkId, tenant.getTenant());
                        }
                    }
                    catch (Exception ex)
                    {
                        log.warn("Bulkemail scheduler bulkId={} tenant={}: {}", bulkId, tenant.getTenant(), ex.getMessage());
                    }
                }
            }
            catch (Exception ex)
            {
                log.warn("Bulkemail scheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
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
}
