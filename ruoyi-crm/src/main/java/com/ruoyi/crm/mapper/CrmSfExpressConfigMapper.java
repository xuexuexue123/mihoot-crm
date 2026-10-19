package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmSfExpressConfig;

public interface CrmSfExpressConfigMapper
{
    CrmSfExpressConfig selectFirst();

    int insert(CrmSfExpressConfig row);

    int update(CrmSfExpressConfig row);
}
