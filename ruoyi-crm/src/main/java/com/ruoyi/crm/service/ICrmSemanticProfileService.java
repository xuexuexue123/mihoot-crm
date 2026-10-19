package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.SemanticProfileSaveRequest;
import com.ruoyi.crm.domain.vo.SemanticProfileVO;

/**
 * 租户语义层配置 Service
 */
public interface ICrmSemanticProfileService
{
    SemanticProfileVO getCurrent(String tenantId);

    void upsert(String tenantId, SemanticProfileSaveRequest request);

    void deleteCurrent(String tenantId);
}
