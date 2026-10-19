package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmTenantEnterprise;
import com.ruoyi.crm.domain.dto.TenantEnterpriseBatchRequest;
import com.ruoyi.crm.domain.dto.TenantEnterpriseBatchResult;

/**
 * 入驻企业 Service
 */
public interface ICrmTenantEnterpriseService
{
    CrmTenantEnterprise selectCrmTenantEnterpriseById(String id);

    List<CrmTenantEnterprise> selectCrmTenantEnterpriseList(CrmTenantEnterprise query);

    CrmTenantEnterprise insertCrmTenantEnterprise(CrmTenantEnterprise row);

    CrmTenantEnterprise updateCrmTenantEnterprise(CrmTenantEnterprise row);

    int deleteCrmTenantEnterpriseById(String id);

    TenantEnterpriseBatchResult batch(TenantEnterpriseBatchRequest request);
}
