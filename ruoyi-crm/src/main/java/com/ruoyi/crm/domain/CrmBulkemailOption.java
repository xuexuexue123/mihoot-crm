package com.ruoyi.crm.domain;

public class CrmBulkemailOption extends CrmBulkemail{
    private boolean disableInsert;

    public boolean isDisableInsert() {
        return disableInsert;
    }

    public void setDisableInsert(boolean disableInsert) {
        this.disableInsert = disableInsert;
    }
}
