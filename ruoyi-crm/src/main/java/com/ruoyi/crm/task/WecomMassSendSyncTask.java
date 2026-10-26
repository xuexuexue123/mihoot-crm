package com.ruoyi.crm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.service.ICrmWecomMassTaskService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 企业微信客户群发：按租户轮询 {@code status=created} 任务并拉取腾讯发送结果。
 * <p>
 * 在「定时任务」中配置调用目标：{@code wecomMassSendSyncTask.syncPendingMassSendResults()}，
 * Cron 示例：{@code 0 0/10 * * * ?}（每 10 分钟，可按环境调整）。
 */
@Component("wecomMassSendSyncTask")
public class WecomMassSendSyncTask
{
    private static final Logger log = LoggerFactory.getLogger(WecomMassSendSyncTask.class);

    private static final int BATCH_PER_TENANT = 20;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private ICrmWecomMassTaskService crmWecomMassTaskService;

    public void syncPendingMassSendResults()
    {
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        for (MasterTenant tenant : tenants)
        {
            if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
            {
                continue;
            }
            if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
            {
                continue;
            }
            if (tenant.getExpirationDate() != null && tenant.getExpirationDate().before(DateUtils.getNowDate()))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                List<Long> ids = crmWecomMassTaskService.selectPendingSyncTaskIds(BATCH_PER_TENANT);
                for (Long id : ids)
                {
                    try
                    {
                        crmWecomMassTaskService.syncSendResultForTask(id, "wecom-scheduler");
                    }
                    catch (Exception e)
                    {
                        log.warn("WeCom mass sync task id={} tenant={}: {}", id, tenant.getTenant(), e.getMessage());
                    }
                }
            }
            catch (Exception e)
            {
                log.warn("WeCom mass sync tenant {}: {}", tenant.getTenant(), e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
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
