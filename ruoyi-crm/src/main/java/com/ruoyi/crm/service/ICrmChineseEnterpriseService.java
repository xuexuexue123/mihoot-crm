package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmChineseEnterprise;
import com.ruoyi.crm.domain.dto.ChineseEnterpriseBatchRequest;
import com.ruoyi.crm.domain.dto.ChineseEnterpriseBatchResult;

/**
 * 中资企业 Service
 */
public interface ICrmChineseEnterpriseService
{
    CrmChineseEnterprise selectCrmChineseEnterpriseById(String id);

    List<CrmChineseEnterprise> selectCrmChineseEnterpriseList(CrmChineseEnterprise query);

    CrmChineseEnterprise insertCrmChineseEnterprise(CrmChineseEnterprise row);

    CrmChineseEnterprise updateCrmChineseEnterprise(CrmChineseEnterprise row);

    int deleteCrmChineseEnterpriseById(String id);

    ChineseEnterpriseBatchResult batch(ChineseEnterpriseBatchRequest request);
}
