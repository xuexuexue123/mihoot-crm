package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMcpServer;

/**
 * MCP Server Mapper
 */
public interface CrmMcpServerMapper
{
    List<CrmMcpServer> selectListByTenant(@Param("tenantId") String tenantId);

    CrmMcpServer selectByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    CrmMcpServer selectEnabledByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    CrmMcpServer selectByNameAndTenant(@Param("name") String name, @Param("tenantId") String tenantId);

    int insert(CrmMcpServer row);

    int update(CrmMcpServer row);

    int deleteByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);
}
