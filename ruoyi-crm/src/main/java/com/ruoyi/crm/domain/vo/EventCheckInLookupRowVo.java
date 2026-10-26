package com.ruoyi.crm.domain.vo;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class EventCheckInLookupRowVo
{
    private String audienceKey;
    private String displayName;
    private String mobilePhone;
    private List<String> channels;
    private String registrationStatus;
    private String registrationId;
    private String checkInStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date checkedInAt;

    public String getAudienceKey() { return audienceKey; }
    public void setAudienceKey(String audienceKey) { this.audienceKey = audienceKey; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public List<String> getChannels() { return channels; }
    public void setChannels(List<String> channels) { this.channels = channels; }
    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }
    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }
    public String getCheckInStatus() { return checkInStatus; }
    public void setCheckInStatus(String checkInStatus) { this.checkInStatus = checkInStatus; }
    public Date getCheckedInAt() { return checkedInAt; }
    public void setCheckedInAt(Date checkedInAt) { this.checkedInAt = checkedInAt; }
}
