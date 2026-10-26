package com.ruoyi.crm.wechatminiapp.mapper;

import com.ruoyi.crm.wechatminiapp.domain.CrmWechatMiniappConfig;

public interface CrmWechatMiniappConfigMapper
{
    CrmWechatMiniappConfig selectSingleton();

    CrmWechatMiniappConfig selectByAppId(String appId);

    int insert(CrmWechatMiniappConfig row);

    int update(CrmWechatMiniappConfig row);

    int deleteSingleton();
}
