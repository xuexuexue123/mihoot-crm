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
import com.ruoyi.crm.service.ICrmWecomStatsSnapshotService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 企业微信统计数据每日快照：按成员/群主逐个拉取昨天数据并 UPSERT 本地库。
 * <p>
 * 在「定时任务」中配置调用目标：{@code wecomStatsSnapshotTask.snapshotYesterdayForAllTenants()}，
 * Cron 示例：{@code 0 0 2 * * ?}（每天 02:00）。
 */
@Component("wecomStatsSnapshotTask")
public class WecomStatsSnapshotTask
{
    private static final Logger log = LoggerFactory.getLogger(WecomStatsSnapshotTask.class);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private ICrmWecomStatsSnapshotService statsSnapshotService;

    public void snapshotYesterdayForAllTenants()
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
                statsSnapshotService.snapshotYesterday();
            }
            catch (Exception e)
            {
                log.warn("WeCom stats snapshot tenant {}: {}", tenant.getTenant(), e.getMessage());
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
