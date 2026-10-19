package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateSummary;

public class EventCheckInResultVo
{
    private String checkInId;
    private String audienceKey;
    private String displayName;
    private String leadId;
    private LeadConvertEvaluateSummary convertEvaluate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date checkedInAt;

    private boolean alreadyCheckedIn;

    public String getCheckInId() { return checkInId; }
    public void setCheckInId(String checkInId) { this.checkInId = checkInId; }
    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getLeadId() { return leadId; }
    public void setLeadId(String leadId) { this.leadId = leadId; }
    public LeadConvertEvaluateSummary getConvertEvaluate() { return convertEvaluate; }
    public void setConvertEvaluate(LeadConvertEvaluateSummary convertEvaluate) { this.convertEvaluate = convertEvaluate; }
    public Date getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(Date checkedInAt) { this.checkedInAt = checkedInAt; }
    public boolean isAlreadyCheckedIn() { return alreadyCheckedIn; }
    public void setAlreadyCheckedIn(boolean alreadyCheckedIn) { this.alreadyCheckedIn = alreadyCheckedIn; }
}
