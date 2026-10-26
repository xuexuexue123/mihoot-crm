package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmSanctionedEntity;
import com.ruoyi.crm.domain.dto.SanctionedEntityBatchRequest;
import com.ruoyi.crm.domain.dto.SanctionedEntityBatchResult;

/**
 * 合规制裁主体 Service
 */
public interface ICrmSanctionedEntityService
{
    CrmSanctionedEntity selectCrmSanctionedEntityById(String id);

    List<CrmSanctionedEntity> selectCrmSanctionedEntityList(CrmSanctionedEntity query);

    CrmSanctionedEntity insertCrmSanctionedEntity(CrmSanctionedEntity row);

    CrmSanctionedEntity updateCrmSanctionedEntity(CrmSanctionedEntity row);

    int deleteCrmSanctionedEntityById(String id);

    SanctionedEntityBatchResult batch(SanctionedEntityBatchRequest request);
}
