package com.ruoyi.crm.domain;

public class CrmOutreachtrack extends CrmCollect{

    private static final long serialVersionUID = 1L;

    private String tenantId;
    private String email;
    private String uniqueId;


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        return "CrmOutreachtrack{" +
                "tenantId='" + tenantId + '\'' +
                '}';
    }
}
