package com.ruoyi.crm.domain.dto.whatsapp;

import java.util.List;

/**
 * Meta 电话号码信息
 */
public class WhatsAppPhoneInfo
{
    private String phoneNumberId;
    private String displayPhoneNumber;
    private String verifiedName;
    private String codeVerificationStatus;
    private String qualityRating;
    private List<String> verifiedPhoneNumbers;

    public String getPhoneNumberId() { return phoneNumberId; }
    public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }

    public String getDisplayPhoneNumber() { return displayPhoneNumber; }
    public void setDisplayPhoneNumber(String displayPhoneNumber) { this.displayPhoneNumber = displayPhoneNumber; }

    public String getVerifiedName() { return verifiedName; }
    public void setVerifiedName(String verifiedName) { this.verifiedName = verifiedName; }

    public String getCodeVerificationStatus() { return codeVerificationStatus; }
    public void setCodeVerificationStatus(String codeVerificationStatus) { this.codeVerificationStatus = codeVerificationStatus; }

    public String getQualityRating() { return qualityRating; }
    public void setQualityRating(String qualityRating) { this.qualityRating = qualityRating; }

    public List<String> getVerifiedPhoneNumbers() { return verifiedPhoneNumbers; }
    public void setVerifiedPhoneNumbers(List<String> verifiedPhoneNumbers) { this.verifiedPhoneNumbers = verifiedPhoneNumbers; }
}
