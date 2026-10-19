package com.ruoyi.tenant.mapper;

import com.ruoyi.tenant.domain.SysWechatMpTenant;
import org.apache.ibatis.annotations.Param;

public interface SysWechatMpTenantMapper
{
    SysWechatMpTenant selectByAppId(@Param("appId") String appId);

    int deleteByTenant(@Param("tenant") String tenant);

    int insert(SysWechatMpTenant row);
}
