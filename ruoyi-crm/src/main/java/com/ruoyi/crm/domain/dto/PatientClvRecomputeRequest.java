package com.ruoyi.crm.domain.dto;

import java.util.List;

public class PatientClvRecomputeRequest
{
    private String scope;
    private List<String> contactIds;
    private List<String> accountIds;
    private Boolean tenantOnly;

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public List<String> getContactIds() { return contactIds; }
    public void setContactIds(List<String> contactIds) { this.contactIds = contactIds; }
    public List<String> getAccountIds() { return accountIds; }
    public void setAccountIds(List<String> accountIds) { this.accountIds = accountIds; }
    public Boolean getTenantOnly() { return tenantOnly; }
    public void setTenantOnly(Boolean tenantOnly) { this.tenantOnly = tenantOnly; }
}
