package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.LeadRecycleConfigUpsertDto;
import com.ruoyi.crm.domain.vo.LeadRecycleConfigVo;
import com.ruoyi.crm.domain.vo.LeadRecycleDedupeConfigVo;

public interface ICrmLeadRecycleConfigService
{
    LeadRecycleConfigVo getConfig();

    LeadRecycleConfigVo saveConfig(LeadRecycleConfigUpsertDto dto);

    LeadRecycleDedupeConfigVo getDedupeConfig();
}
