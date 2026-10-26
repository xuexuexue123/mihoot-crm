package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmRecallPolicy;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.domain.dto.RecallPolicySaveRequest;

public interface ICrmRecallPolicyService
{
    List<CrmRecallPolicy> selectList(String tenantId, String enabled);

    CrmRecallPolicy getById(String tenantId, String policyId);

    void add(String tenantId, RecallPolicySaveRequest request);

    void update(String tenantId, RecallPolicySaveRequest request);

    void remove(String tenantId, String policyId);

    List<CrmRecallSchedule> selectScheduleList(String tenantId, String contactId, String status);
}
