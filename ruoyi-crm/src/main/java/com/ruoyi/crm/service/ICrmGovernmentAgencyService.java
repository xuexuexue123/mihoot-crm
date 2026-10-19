package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmGovernmentAgency;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchRequest;
import com.ruoyi.crm.domain.dto.GovernmentAgencyBatchResult;

/**
 * 政府招商机构 Service
 */
public interface ICrmGovernmentAgencyService
{
    CrmGovernmentAgency selectCrmGovernmentAgencyById(String id);

    List<CrmGovernmentAgency> selectCrmGovernmentAgencyList(CrmGovernmentAgency query);

    CrmGovernmentAgency insertCrmGovernmentAgency(CrmGovernmentAgency row);

    CrmGovernmentAgency updateCrmGovernmentAgency(CrmGovernmentAgency row);

    int deleteCrmGovernmentAgencyById(String id);

    GovernmentAgencyBatchResult batch(GovernmentAgencyBatchRequest request);
}
