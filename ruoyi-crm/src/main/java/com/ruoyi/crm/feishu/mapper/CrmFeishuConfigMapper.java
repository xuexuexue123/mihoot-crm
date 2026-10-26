package com.ruoyi.crm.feishu.mapper;

import com.ruoyi.crm.feishu.domain.CrmFeishuConfig;

public interface CrmFeishuConfigMapper
{
    CrmFeishuConfig selectSingleton();

    CrmFeishuConfig selectByAppId(String appId);

    int insert(CrmFeishuConfig row);

    int update(CrmFeishuConfig row);

    int deleteSingleton();
}
