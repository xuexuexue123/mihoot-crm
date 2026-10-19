package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class SysCaptcha extends BaseEntity {

    private String mobile;

    private String email;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "SysCaptcha{" +
                "mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
