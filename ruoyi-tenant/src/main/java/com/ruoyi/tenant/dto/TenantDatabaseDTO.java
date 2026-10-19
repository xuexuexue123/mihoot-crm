package com.ruoyi.tenant.dto;

import lombok.Data;

/**
 * @Author: eddie
 * @Date: 2022/1/22
 * @Description:
 */
@Data
public class TenantDatabaseDTO {
    public String companyName;
    public String tenantDatabase;
    public String dbUser;
    public String dbPass;
    public String adminName;
    public String adminPass;
    public String tenantName;
    public String url;
    public String websiteSlug;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTenantDatabase() {
        return tenantDatabase;
    }

    public void setTenantDatabase(String tenantDatabase) {
        this.tenantDatabase = tenantDatabase;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPass() {
        return adminPass;
    }

    public void setAdminPass(String adminPass) {
        this.adminPass = adminPass;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
