package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmTenantEnterprise;

/**
 * 入驻企业 Mapper
 */
public interface CrmTenantEnterpriseMapper
{
    CrmTenantEnterprise selectCrmTenantEnterpriseById(String id);

    List<CrmTenantEnterprise> selectCrmTenantEnterpriseList(CrmTenantEnterprise query);

    int insertCrmTenantEnterprise(CrmTenantEnterprise row);

    int updateCrmTenantEnterprise(CrmTenantEnterprise row);

    int logicalDeleteById(@Param("id") String id, @Param("updateBy") String updateBy);
}
