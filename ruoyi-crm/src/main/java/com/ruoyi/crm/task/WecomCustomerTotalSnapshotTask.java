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
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmDashboardSnapshotService;
import com.ruoyi.crm.service.ICrmWecomCustomerTotalService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 企业微信全企业客户总数每日快照：全量拉取企微 API 并 UPSERT 到统一快照表。
 * 在「定时任务」中配置调用目标：{@code wecomCustomerTotalSnapshotTask.snapshotForAllTenants()}，
 * Cron 示例：{@code 0 0 3 * * ?}（每天 03:00）。
 */
@Component("wecomCustomerTotalSnapshotTask")
public class WecomCustomerTotalSnapshotTask
{
    private static final Logger log = LoggerFactory.getLogger(WecomCustomerTotalSnapshotTask.class);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private ICrmWecomCustomerTotalService customerTotalService;

    @Autowired
    private ICrmDashboardSnapshotService dashboardSnapshotService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    public void snapshotForAllTenants()
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
            if (tenant.getExpirationDate() != null
                    && tenant.getExpirationDate().before(DateUtils.getNowDate()))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

                CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
                if (cfg == null || !Boolean.TRUE.equals(cfg.getConnected()))
                {
                    log.debug("WeCom config not found or not connected for tenant {}, skip snapshot",
                            tenant.getTenant());
                    continue;
                }

                WecomCustomerTotalResponse data = customerTotalService.pullCustomerTotal();
                String corpId = cfg.getCorpId();
                dashboardSnapshotService.saveCustomerTotalToday(corpId, data, null);
                log.info("WeCom customer total snapshot saved for tenant {}: staff={}, unique={}, raw={}",
                        tenant.getTenant(), data.getTotalStaff(),
                        data.getTotalUniqueCustomers(), data.getTotalRawCustomers());
            }
            catch (Exception e)
            {
                log.warn("WeCom customer total snapshot tenant {}: {}", tenant.getTenant(), e.getMessage());
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
