package com.ruoyi.crm.domain.dto;

import java.util.Map;

public class EventSelfCheckInRequest
{
    private String checkInToken;
    /** check_in_only | register_and_check_in */
    private String mode;
    private String mobilePhone;
    private String displayName;
    private Map<String, Object> formData;

    public String getCheckInToken() { return checkInToken; }
    public void setCheckInToken(String checkInToken) { this.checkInToken = checkInToken; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Map<String, Object> getFormData() { return formData; }
    public void setFormData(Map<String, Object> formData) { this.formData = formData; }
}
