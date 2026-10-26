package com.ruoyi.framework.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.ruoyi.common.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源
 *
 * @author devjd
 */
@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    /**
     * 已注册数据源（含租户）。必须用 ConcurrentHashMap，且勿被 {@link #setTargetDataSources}
     * 替换成非并发 Map，否则调度器并发注册时易丢 key。
     */
    private static final Map<Object, Object> TARGET_DATA_SOURCES = new ConcurrentHashMap<>();

    public DynamicRoutingDataSource()
    {
        // 禁止 lookup 未命中时静默回落 default/master，否则租户 key 已设置仍打到 megaems，
        // 表现为「crm_* does not exist」的误导性缺表报错。
        setLenientFallback(false);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }

    /**
     * 租户 key：直接从注册表取池，不依赖 resolvedDataSources 与 afterPropertiesSet 的竞态窗口。
     * 主库 key / null：走父类默认逻辑。
     */
    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if (lookupKey == null) {
            return super.determineTargetDataSource();
        }
        String key = lookupKey.toString();
        if (DataSourceType.MASTER.name().equals(key) || "default_db".equals(key)) {
            return super.determineTargetDataSource();
        }
        Object ds = TARGET_DATA_SOURCES.get(key);
        if (ds == null) {
            throw new IllegalStateException(
                    "Tenant DataSource [" + key + "] is not registered; "
                            + "ensure the request carries a valid tenant header or bootstrap the tenant pool first");
        }
        return (DataSource) ds;
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        TARGET_DATA_SOURCES.clear();
        if (targetDataSources != null) {
            TARGET_DATA_SOURCES.putAll(targetDataSources);
        }
        super.setTargetDataSources(TARGET_DATA_SOURCES);
        super.afterPropertiesSet();
    }

    /**
     * 注册租户连接池（幂等）。同步避免并发 afterPropertiesSet 与 exist 检查窗口。
     */
    public synchronized void addDataSource(String tenant, Map<String, Object> dataSourceProperties) {
        if (TARGET_DATA_SOURCES.containsKey(tenant)) {
            return;
        }
        TARGET_DATA_SOURCES.put(tenant, dataSource(dataSourceProperties));
        super.setTargetDataSources(TARGET_DATA_SOURCES);
        afterPropertiesSet();
        log.info("Registered tenant DataSource [{}]", tenant);
    }

    public boolean existDataSource(String tenant) {
        return TARGET_DATA_SOURCES.containsKey(tenant);
    }

    public DataSource getDataSource(String tenantId) {
        Object ds = TARGET_DATA_SOURCES.get(tenantId);
        if (ds == null) {
            throw new IllegalArgumentException("No DataSource found for tenant: " + tenantId);
        }
        return (DataSource) ds;
    }

    public DataSource dataSource(Map<String, Object> dataSourceProperties) {
        try {
            return DruidDataSourceFactory.createDataSource(dataSourceProperties);
        } catch (Exception e) {
            log.error("Failed to create DataSource: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create DataSource", e);
        }
    }
}
