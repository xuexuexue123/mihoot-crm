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
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpCommentAutoReplyRuleService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

/**
 * 跨租户处理图文留言半自动回复。
 * 目标：{@code wechatMpCommentAutoReplyTask.processAllTenants()}
 */
@Component("wechatMpCommentAutoReplyTask")
public class WechatMpCommentAutoReplyTask
{
    private static final Logger log = LoggerFactory.getLogger(WechatMpCommentAutoReplyTask.class);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private IWechatMpCommentAutoReplyRuleService ruleService;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    public void processAllTenants()
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
                java.util.List<?> configs = configMapper.selectAll();
                if (configs == null || configs.isEmpty())
                {
                    continue;
                }
                Map<String, Object> result = ruleService.processPending(50);
                log.info("MP comment auto-reply tenant {} => {}", tenant.getTenant(), result);
            }
            catch (Exception e)
            {
                log.warn("MP comment auto-reply tenant {}: {}", tenant.getTenant(), e.getMessage());
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
