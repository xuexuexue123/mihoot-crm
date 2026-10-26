package com.ruoyi.tenant.mapper;

import com.ruoyi.tenant.domain.SysMessengerPageTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMessengerPageTenantMapper
{
    SysMessengerPageTenant selectByPageId(@Param("pageId") String pageId);

    List<SysMessengerPageTenant> selectAll();

    int deleteByTenant(@Param("tenant") String tenant);

    int insert(SysMessengerPageTenant row);
}
