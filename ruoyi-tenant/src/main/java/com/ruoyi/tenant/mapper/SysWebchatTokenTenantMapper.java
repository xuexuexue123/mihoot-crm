package com.ruoyi.tenant.mapper;

import com.ruoyi.tenant.domain.SysWebchatTokenTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysWebchatTokenTenantMapper
{
    SysWebchatTokenTenant selectByWebsiteToken(@Param("websiteToken") String websiteToken);

    SysWebchatTokenTenant selectByTenant(@Param("tenant") String tenant);

    List<SysWebchatTokenTenant> selectAll();

    int deleteByTenant(@Param("tenant") String tenant);

    int insert(SysWebchatTokenTenant row);
}
