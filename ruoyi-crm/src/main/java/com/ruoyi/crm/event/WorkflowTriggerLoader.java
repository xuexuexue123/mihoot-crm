package com.ruoyi.crm.event;


import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWorkflowTrigger;

import com.ruoyi.crm.event.EventDispatcher;
import com.ruoyi.crm.service.ICrmWorkflowTriggerService;
import com.ruoyi.crm.enums.EventType;
import com.ruoyi.flowable.config.TenantContext;
import com.ruoyi.flowable.service.IFlowDefinitionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WorkflowTriggerLoader implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(WorkflowTriggerLoader.class);

    @Autowired
    private EventDispatcher eventDispatcher;

    @Autowired
    private ICrmWorkflowTriggerService crmWorkflowTriggerService;

    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    public void loadTriggersForTenant(MasterTenant tenant) {
        if (!isActiveTenant(tenant))
        {
            return;
        }

        ensureTenantDataSourceReady(tenant);

        DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());

        CrmWorkflowTrigger crmWorkflowTrigger = new CrmWorkflowTrigger();
        try {
            List<CrmWorkflowTrigger> rules = crmWorkflowTriggerService.selectCrmWorkflowTriggerList(crmWorkflowTrigger);

            for (CrmWorkflowTrigger rule : rules) {
                EventType eventType = EventType.valueOf(rule.getEventType());
                String sourceId = rule.getTriggerSourceId();
                String processDefinitionKey = rule.getProcessDefinitionKey();

                eventDispatcher.register(eventType, sourceId, (payload) -> {
                    TenantContext.setCurrentTenant(tenant.getTenant());
                    DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                    flowDefinitionService.startProcessInstanceByProcDefId(processDefinitionKey, "123123", payload);
                });

                log.info("Registered workflow trigger for tenant {}: {} -> {}",
                        tenant.getTenant(), eventType, processDefinitionKey);
            }

        } catch (Exception e) {
            log.warn("WorkflowTriggerLoader tenant {} skipped: {}", tenant.getTenant(), e.getMessage());
        } finally {
            DynamicDataSourceContextHolder.clearDataSourceKey();
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Initializing workflow trigger rules...");

        MasterTenant masterTenant = new MasterTenant();
        for (MasterTenant tenant : masterTenantService.selectMasterTenantList(masterTenant)) {
            loadTriggersForTenant(tenant);
        }
        log.info("Workflow trigger rule registration completed.");
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
        return tenant.getExpirationDate() == null
                || !tenant.getExpirationDate().before(DateUtils.getNowDate());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant) {
        if (StringUtils.isBlank(tenant.getTenant())) {
            throw new RuntimeException("Failed to get required information.");
        }

        if (!dynamicRoutingDataSource.existDataSource(tenant.getTenant())) {
            // 检查租户状态
            if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus())) {
                throw new RuntimeException("Application [" + tenant.getTenant() + "] expired");
            }

            if (tenant.getExpirationDate() != null && tenant.getExpirationDate().before(DateUtils.getNowDate())) {
                throw new RuntimeException("Application [" + tenant.getTenant() + "] expired");
            }

            // 注册数据源
            Map<String, Object> map = new HashMap<>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());

            dynamicRoutingDataSource.addDataSource(tenant.getTenant(), map);
        }
    }
}