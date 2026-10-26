package com.ruoyi.flowable.factory;

import com.ruoyi.ems.service.ISysAudittrailService;
import com.ruoyi.flowable.config.TenantContext;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import lombok.Getter;
import org.flowable.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * flowable 引擎注入封装
 * @author XuanXuan
 * @date 2021-04-03
 */
@Component
@Getter
public class FlowServiceFactory {

    @Autowired
    private TenantProcessEngineManager tenantProcessEngineManager;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    // 使用 ThreadLocal 保存当前租户上下文
//    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();
//
//    public void setCurrentTenant(String tenantId) {
//        currentTenant.set(tenantId);
//    }
//
//    public void clear() {
//        currentTenant.remove();
//    }

    private ProcessEngine getProcessEngine() {
//        String tenantId = currentTenant.get();
//        String tenantId = "tenant5";
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID not set in FlowServiceFactory");
        }
        return tenantProcessEngineManager.getProcessEngine(tenantId);
    }

    public RepositoryService getRepositoryService() {
        return getProcessEngine().getRepositoryService();
    }

    public RuntimeService getRuntimeService() {
        return getProcessEngine().getRuntimeService();
    }

    public TaskService getTaskService() {
        return getProcessEngine().getTaskService();
    }

    public HistoryService getHistoryService() {
        return getProcessEngine().getHistoryService();
    }

    public IdentityService getIdentityService() {
        return getProcessEngine().getIdentityService();
    }

    public FormService getFormService() {
        return getProcessEngine().getFormService();
    }

    public ManagementService getManagementService() {
        return getProcessEngine().getManagementService();
    }

    public ProcessEngine getProcessEngineDirect() {
        return getProcessEngine();
    }
}
