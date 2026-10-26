package com.ruoyi.crm.wechatmp.domain;

import java.time.LocalDateTime;

/**
 * 微信公众号回调原始日志
 *
 * @author luohd
 * @date 2026-06-18
 */
public class MpCallbackLog
{
    private Long id;
    private String msgType;
    private String event;
    private String fromUser;
    private String toUser;
    private String rawXml;
    private Boolean parsed;
    private String errorMsg;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMsgType() { return msgType; }
    public void setMsgType(String msgType) { this.msgType = msgType; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public String getFromUser() { return fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }

    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public String getRawXml() { return rawXml; }
    public void setRawXml(String rawXml) { this.rawXml = rawXml; }

    public Boolean getParsed() { return parsed; }
    public void setParsed(Boolean parsed) { this.parsed = parsed; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
