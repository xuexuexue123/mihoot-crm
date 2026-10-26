package com.ruoyi.crm.wechatmp.domain;

import java.time.LocalDateTime;

/**
 * 未匹配身份的 Activity 记录
 *
 * @author luohd
 * @date 2026-06-18
 */
public class ActivityUnmatched
{
    private Long id;
    private String pulseId;
    private String identityType;
    private String identityValue;
    private String unionId;
    private String unionIdSource;
    private String appId;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime nextRetryAt;
    private LocalDateTime matchedAt;
    private String contactId;
    private Boolean abandoned;
    private String rawPayload;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPulseId() { return pulseId; }
    public void setPulseId(String pulseId) { this.pulseId = pulseId; }

    public String getIdentityType() { return identityType; }
    public void setIdentityType(String identityType) { this.identityType = identityType; }

    public String getIdentityValue() { return identityValue; }
    public void setIdentityValue(String identityValue) { this.identityValue = identityValue; }

    public String getUnionId() { return unionId; }
    public void setUnionId(String unionId) { this.unionId = unionId; }

    public String getUnionIdSource() { return unionIdSource; }
    public void setUnionIdSource(String unionIdSource) { this.unionIdSource = unionIdSource; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    public void setNextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }

    public LocalDateTime getMatchedAt() { return matchedAt; }
    public void setMatchedAt(LocalDateTime matchedAt) { this.matchedAt = matchedAt; }

    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public Boolean getAbandoned() { return abandoned; }
    public void setAbandoned(Boolean abandoned) { this.abandoned = abandoned; }

    public String getRawPayload() { return rawPayload; }
    public void setRawPayload(String rawPayload) { this.rawPayload = rawPayload; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
