package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateRequest;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.LeadConvertRequest;
import com.ruoyi.crm.domain.dto.LeadConvertResult;

public interface ICrmLeadConvertService
{
    LeadConvertResult convert(String leadId, LeadConvertRequest request, String operator);

    LeadConvertEvaluateResult evaluate(String leadId, LeadConvertEvaluateRequest request);

    LeadConvertEvaluateResult evaluateStageArrived(CrmLead before, CrmLead after);

    LeadConvertEvaluateResult evaluateEventCheckIn(String eventId, String registrationId, String audienceKey);

    String resolveLeadIdForCheckIn(String eventId, String registrationId, String audienceKey);

    /**
     * 企微活码加好友回调后无条件转化（不要求手机号；orgId 缺失时返回 null）。
     */
    LeadConvertResult convertFromWecomCallback(String leadId, String eventId);
}
