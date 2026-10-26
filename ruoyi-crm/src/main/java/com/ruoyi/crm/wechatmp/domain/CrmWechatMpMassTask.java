package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

/**
 * 公众号官方群发任务（crm_wechat_mp_mass_task）。
 */
public class CrmWechatMpMassTask
{
    private Long id;
    private String appId;
    private String msgType;
    private String contentJson;
    private String audienceType;
    private Long tagId;
    private Boolean isToAll;
    private String clientMsgId;
    private String status;
    private String msgId;
    private String msgDataId;
    private Integer totalCount;
    private Integer filterCount;
    private Integer sentCount;
    private Integer errorCount;
    private Integer errorCode;
    private String errorMsg;
    private String createBy;
    private Date createTime;
    private Date updateTime;
    private Date finishTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
    public String getContentJson() { return contentJson; }
    public void setContentJson(String contentJson) { this.contentJson = contentJson; }
    public String getAudienceType() { return audienceType; }
    public void setAudienceType(String audienceType) { this.audienceType = audienceType; }
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }
    public Boolean getIsToAll() { return isToAll; }
    public void setIsToAll(Boolean isToAll) { this.isToAll = isToAll; }
    public String getClientMsgId() { return clientMsgId; }
    public void setClientMsgId(String clientMsgId) { this.clientMsgId = clientMsgId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMsgId() { return msgId; }
    public void setMsgId(String msgId) { this.msgId = msgId; }
    public String getMsgDataId() { return msgDataId; }
    public void setMsgDataId(String msgDataId) { this.msgDataId = msgDataId; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public Integer getFilterCount() { return filterCount; }
    public void setFilterCount(Integer filterCount) { this.filterCount = filterCount; }
    public Integer getSentCount() { return sentCount; }
    public void setSentCount(Integer sentCount) { this.sentCount = sentCount; }
    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }
    public Integer getErrorCode() { return errorCode; }
    public void setErrorCode(Integer errorCode) { this.errorCode = errorCode; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
}
