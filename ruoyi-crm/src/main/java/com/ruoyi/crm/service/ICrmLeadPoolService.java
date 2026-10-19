package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.CrmLeadConvertedQuery;
import com.ruoyi.crm.domain.dto.CrmLeadPoolQuery;
import com.ruoyi.crm.domain.dto.LeadAssignRequest;
import com.ruoyi.crm.domain.dto.LeadBatchAssignRequest;
import com.ruoyi.crm.domain.dto.LeadReleaseRequest;
import com.ruoyi.crm.domain.vo.LeadBatchAssignResult;
import com.ruoyi.crm.domain.vo.LeadSourceLeadResponse;

public interface ICrmLeadPoolService
{
    List<CrmLead> selectPoolList(CrmLeadPoolQuery query);

    List<CrmLead> selectConvertedList(CrmLeadConvertedQuery query);

    CrmLead claim(String leadId);

    CrmLead release(String leadId, LeadReleaseRequest request, boolean forceReclaim);

    CrmLead assign(String leadId, LeadAssignRequest request);

    LeadBatchAssignResult batchAssign(LeadBatchAssignRequest request);

    LeadSourceLeadResponse findSourceLeadByContactId(String contactId);
}
