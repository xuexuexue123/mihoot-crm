package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmSfExpressToken;

public interface CrmSfExpressTokenMapper
{
    CrmSfExpressToken selectFirst();

    int insert(CrmSfExpressToken row);

    int update(CrmSfExpressToken row);

    int deleteAll();
}
