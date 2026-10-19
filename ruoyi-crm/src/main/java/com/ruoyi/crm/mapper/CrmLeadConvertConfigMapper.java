package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmLeadConvertConfig;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;

public interface CrmLeadConvertConfigMapper
{
    LeadConvertConfigVo selectVoByTenantId(String tenantId);

    CrmLeadConvertConfig selectEntityByTenantId(String tenantId);

    int insert(CrmLeadConvertConfig row);

    int update(CrmLeadConvertConfig row);
}
