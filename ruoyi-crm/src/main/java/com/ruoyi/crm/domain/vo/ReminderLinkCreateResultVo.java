package com.ruoyi.crm.domain.vo;

import java.util.Date;

/**
 * 创建 Reminder Link 响应
 */
public class ReminderLinkCreateResultVo
{
    private Long id;
    private String token;
    private String publicUrl;
    private Date expiresAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getPublicUrl() { return publicUrl; }
    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
}
