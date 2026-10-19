package com.ruoyi.flowable.service;

import javax.sql.DataSource;

public interface TenantDataSourceProvider {
    /**
     * 根据租户ID获取对应的DataSource
     * @param tenantId 租户ID
     * @return DataSource实例，如果不存在返回null或抛异常
     */
    DataSource getDataSource(String tenantId);
}
