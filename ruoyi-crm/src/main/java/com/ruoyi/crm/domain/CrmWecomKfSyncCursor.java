package com.ruoyi.crm.domain;

import java.util.Date;

/**
 * 微信客服 sync_msg 游标。
 */
public class CrmWecomKfSyncCursor
{
    private Long id;
    private String corpId;
    private String openKfid;
    private String cursor;
    private Date enabledAt;
    private Date updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCorpId() { return corpId; }
    public void setCorpId(String corpId) { this.corpId = corpId; }

    public String getOpenKfid() { return openKfid; }
    public void setOpenKfid(String openKfid) { this.openKfid = openKfid; }

    public String getCursor() { return cursor; }
    public void setCursor(String cursor) { this.cursor = cursor; }

    public Date getEnabledAt() { return enabledAt; }
    public void setEnabledAt(Date enabledAt) { this.enabledAt = enabledAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
