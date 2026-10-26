package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomKfConfig;

public interface CrmWecomKfConfigMapper
{
    CrmWecomKfConfig selectFirst();

    CrmWecomKfConfig selectByCorpId(@Param("corpId") String corpId);

    int insert(CrmWecomKfConfig row);

    int update(CrmWecomKfConfig row);
}
