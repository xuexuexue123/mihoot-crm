package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMcpApiKey;

/**
 * MCP API Key Mapper
 */
public interface CrmMcpApiKeyMapper
{
    List<CrmMcpApiKey> selectListByTenant(@Param("tenantId") String tenantId);

    CrmMcpApiKey selectByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId);

    CrmMcpApiKey selectByKeyPrefix(@Param("keyPrefix") String keyPrefix);

    CrmMcpApiKey selectByNameAndTenant(@Param("name") String name, @Param("tenantId") String tenantId);

    int insert(CrmMcpApiKey row);

    int update(CrmMcpApiKey row);

    int disableByIdAndTenant(@Param("id") Long id, @Param("tenantId") String tenantId,
            @Param("updateBy") String updateBy, @Param("updateTime") java.util.Date updateTime);

    int updateServiceTokenEnc(CrmMcpApiKey row);
}
