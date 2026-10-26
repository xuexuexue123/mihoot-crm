package com.ruoyi.crm.domain.vo;

import java.io.Serializable;

public class RecomputeResultVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int affectedContacts;
    private int affectedAccounts;
    private long costMs;

    public int getAffectedContacts() { return affectedContacts; }
    public void setAffectedContacts(int affectedContacts) { this.affectedContacts = affectedContacts; }
    public int getAffectedAccounts() { return affectedAccounts; }
    public void setAffectedAccounts(int affectedAccounts) { this.affectedAccounts = affectedAccounts; }
    public long getCostMs() { return costMs; }
    public void setCostMs(long costMs) { this.costMs = costMs; }
}
