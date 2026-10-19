package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

import com.ruoyi.crm.domain.CrmLead;

/**
 * 批量指派结果
 */
public class LeadBatchAssignResult
{
    private List<String> success = new ArrayList<>();
    private List<LeadBatchAssignFailure> failed = new ArrayList<>();

    public List<String> getSuccess()
    {
        return success;
    }

    public void setSuccess(List<String> success)
    {
        this.success = success;
    }

    public List<LeadBatchAssignFailure> getFailed()
    {
        return failed;
    }

    public void setFailed(List<LeadBatchAssignFailure> failed)
    {
        this.failed = failed;
    }

    public static class LeadBatchAssignFailure
    {
        private String leadId;
        private String reason;

        public LeadBatchAssignFailure()
        {
        }

        public LeadBatchAssignFailure(String leadId, String reason)
        {
            this.leadId = leadId;
            this.reason = reason;
        }

        public String getLeadId()
        {
            return leadId;
        }

        public void setLeadId(String leadId)
        {
            this.leadId = leadId;
        }

        public String getReason()
        {
            return reason;
        }

        public void setReason(String reason)
        {
            this.reason = reason;
        }
    }
}
