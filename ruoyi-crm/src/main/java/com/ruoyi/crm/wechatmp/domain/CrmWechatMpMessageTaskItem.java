package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

public class CrmWechatMpMessageTaskItem
{
    private Long id;
    private Long taskId;
    private String openId;
    private String status;
    private Integer errcode;
    private String errmsg;
    private Date sentAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getErrcode() { return errcode; }
    public void setErrcode(Integer errcode) { this.errcode = errcode; }
    public String getErrmsg() { return errmsg; }
    public void setErrmsg(String errmsg) { this.errmsg = errmsg; }
    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }
}
