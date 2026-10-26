package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.crm.domain.dto.datatable.DataTableRuntimeRequest;

public interface IDataTableOperationService
{
    Map<String, Object> runtimeInsert(String tenantId, DataTableRuntimeRequest request);

    Map<String, Object> runtimeUpdate(String tenantId, DataTableRuntimeRequest request);

    Map<String, Object> runtimeUpsert(String tenantId, DataTableRuntimeRequest request);

    Map<String, Object> runtimeGet(String tenantId, DataTableRuntimeRequest request);

    List<Map<String, Object>> runtimeList(String tenantId, DataTableRuntimeRequest request);

    Map<String, Object> runtimeDelete(String tenantId, DataTableRuntimeRequest request);

    Map<String, Object> runtimeEnsureColumns(String tenantId, DataTableRuntimeRequest request);
}
