package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmSemanticProfile;
import com.ruoyi.crm.domain.vo.SemanticProfileVO;

/**
 * 租户语义层配置 Mapper
 */
public interface CrmSemanticProfileMapper
{
    SemanticProfileVO selectByTenantId(@Param("tenantId") String tenantId);

    CrmSemanticProfile selectEntityByTenantIdAny(@Param("tenantId") String tenantId);

    int insertCrmSemanticProfile(CrmSemanticProfile row);

    int updateCrmSemanticProfile(CrmSemanticProfile row);

    int logicalDeleteByTenantId(@Param("tenantId") String tenantId, @Param("updateBy") String updateBy);
}
