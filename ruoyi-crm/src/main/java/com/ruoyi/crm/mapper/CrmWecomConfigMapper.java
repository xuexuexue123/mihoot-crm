package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomConfig;

public interface CrmWecomConfigMapper
{
    CrmWecomConfig selectFirst();

    /** 回调路径中的 corpId 与配置必须一致，避免多行时误用 {@link #selectFirst()}。 */
    CrmWecomConfig selectByCorpId(@Param("corpId") String corpId);

    int insert(CrmWecomConfig row);

    int update(CrmWecomConfig row);
}
