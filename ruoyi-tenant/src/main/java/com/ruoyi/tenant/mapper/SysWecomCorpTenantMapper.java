package com.ruoyi.tenant.mapper;

import com.ruoyi.tenant.domain.SysWecomCorpTenant;
import org.apache.ibatis.annotations.Param;

public interface SysWecomCorpTenantMapper
{
    SysWecomCorpTenant selectByCorpId(@Param("corpId") String corpId);

    int deleteByTenant(@Param("tenant") String tenant);

    int insert(SysWecomCorpTenant row);
}
