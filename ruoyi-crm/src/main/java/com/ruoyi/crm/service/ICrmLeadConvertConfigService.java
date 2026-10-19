package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.LeadConvertConfigUpsertDto;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;

public interface ICrmLeadConvertConfigService
{
    LeadConvertConfigVo getConfig(String tenantId);

    LeadConvertConfigVo upsert(String tenantId, LeadConvertConfigUpsertDto dto);
}
