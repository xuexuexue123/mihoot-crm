package com.ruoyi.crm.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class CrmInvestor  extends CrmContact {

    private static final long serialVersionUID = 1L;

    private String region;

    private String hangye;

    private String checkSize;

    private String decisionTime;

    private String chair;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHangye() {
        return hangye;
    }

    public void setHangye(String hangye) {
        this.hangye = hangye;
    }

    public String getCheckSize() {
        return checkSize;
    }

    public void setCheckSize(String checkSize) {
        this.checkSize = checkSize;
    }

    public String getDecisionTime() {
        return decisionTime;
    }

    public void setDecisionTime(String decisionTime) {
        this.decisionTime = decisionTime;
    }

    public String getChair() {
        return chair;
    }

    public void setChair(String chair) {
        this.chair = chair;
    }

    @Override
    public String toString() {
        return "CrmInvestor{" +
                "region='" + region + '\'' +
                ", hangye='" + hangye + '\'' +
                ", checkSize='" + checkSize + '\'' +
                ", decisionTime='" + decisionTime + '\'' +
                ", chair='" + chair + '\'' +
                '}';
    }
}
