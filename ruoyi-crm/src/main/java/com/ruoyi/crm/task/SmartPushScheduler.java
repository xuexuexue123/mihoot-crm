package com.ruoyi.crm.task;

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
import com.ruoyi.crm.rule.SmartPushRuleEngine;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 智能推送定时任务入口，每天 08:00 执行所有启用的 birthday + scheduled 策略。
 * <p>
 * 支持三种策略类型：birthday | scheduled | on_event。
 * 其中 birthday 和 scheduled 由本调度器定时执行，on_event 由 SmartPushEventExecutor 实时触发。
 * 手动触发 API（/execute）仅执行 scheduled 类型，不包含 birthday。
 */
@Component
public class SmartPushScheduler
{
    private static final Logger log = LoggerFactory.getLogger(SmartPushScheduler.class);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private SmartPushRuleEngine ruleEngine;

    /**
     * 每天 08:00 执行所有启用的 birthday + scheduled 推送策略。
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void processScheduledPushes()
    {
        log.info("SmartPushScheduler: starting scheduled push execution");

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
                ruleEngine.executeScheduled();
                ruleEngine.executeBirthday();
            }
            catch (Exception ex)
            {
                log.warn("SmartPushScheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }

        log.info("SmartPushScheduler: scheduled push execution completed");
    }

    /**
     * 手动触发单条策略（按策略 ID）。
     * 由 SmartPushController 的调试 API 调用。
     */
    public void executeStrategyById(Long strategyId)
    {
        ruleEngine.executeStrategyById(strategyId);
    }

    /**
     * 手动触发所有启用的 scheduled 类型策略（不含 birthday）。
     * 由 SmartPushController 的调试 API 调用。
     */
    public void executeAll()
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
                ruleEngine.executeScheduled();
            }
            catch (Exception ex)
            {
                log.warn("SmartPushScheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
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
