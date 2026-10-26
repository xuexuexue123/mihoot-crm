package com.ruoyi.flowable.factory;

import com.ruoyi.flowable.service.TenantDataSourceProvider;
import org.flowable.engine.*;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantProcessEngineManager {

    private static final Logger logger = LoggerFactory.getLogger(TenantProcessEngineManager.class);

    // key: tenantId, value: ProcessEngine实例
    private final Map<String, ProcessEngine> tenantEngines = new ConcurrentHashMap<>();

    // 用于构造租户专属数据源和配置的服务，这里假设你有类似的服务
    private final TenantDataSourceProvider tenantDataSourceProvider;

    @Autowired
    private PlatformTransactionManager defaultTransactionManager;

    @Autowired
    private ApplicationContext applicationContext;

    public TenantProcessEngineManager(TenantDataSourceProvider tenantDataSourceProvider) {
        this.tenantDataSourceProvider = tenantDataSourceProvider;
    }

    /**
     * 获取租户对应的 ProcessEngine，如果不存在则创建
     */
    public ProcessEngine getProcessEngine(String tenantId) {
        return tenantEngines.computeIfAbsent(tenantId, this::createProcessEngineForTenant);
    }

    /**
     * 创建租户独立的 ProcessEngine 实例
     */
    private ProcessEngine createProcessEngineForTenant(String tenantId) {
        logger.info("Creating ProcessEngine for tenant {}", tenantId);

        DataSource tenantDataSource = tenantDataSourceProvider.getDataSource(tenantId);


        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        config.setDataSource(tenantDataSource);

        // 使用默认事务管理器，或为每个租户配置自己的 TransactionManager
        config.setTransactionManager(defaultTransactionManager);

        config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        config.setAsyncExecutorActivate(true);

        // Required so JUEL (${...}) bean resolution uses a non-null BeanFactoryProxyMap — otherwise identifiers
        // that are neither variables nor beans can throw NPE in SpringBeanFactoryProxyMap.containsKey.
        config.setApplicationContext(applicationContext);

        // ✅ 构建引擎
        ProcessEngine processEngine = config.buildProcessEngine();

        // ✅ 启动租户独立的 AsyncExecutor
        AsyncExecutor asyncExecutor = config.getAsyncExecutor();
        if (asyncExecutor != null) {
            logger.info("Starting AsyncExecutor for tenant {}", tenantId);
            asyncExecutor.start();
        } else {
            logger.warn("AsyncExecutor is null for tenant {}", tenantId);
        }

        logger.info("ProcessEngine for tenant {} created successfully", tenantId);
        return processEngine;
    }

    /**
     * 关闭所有引擎，释放资源
     */
    @PreDestroy
    public void shutdownAll() {
        logger.info("Shutting down all tenant ProcessEngines");
        for (ProcessEngine engine : tenantEngines.values()) {
            try {
                engine.close();
            } catch (Exception e) {
                logger.error("Error closing ProcessEngine", e);
            }
        }
        tenantEngines.clear();
    }
}
