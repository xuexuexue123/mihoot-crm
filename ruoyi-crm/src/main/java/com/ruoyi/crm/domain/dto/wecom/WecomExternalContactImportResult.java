package com.ruoyi.crm.domain.dto.wecom;

import java.util.ArrayList;
import java.util.List;

/** 外部联系人同步结果统计 */
public class WecomExternalContactImportResult
{
    /** 参与同步的成员 userid 数量 */
    private int scannedUsers;

    /** 从腾讯拉取并处理的 (external_contact, follow) 条数 */
    private int processedRecords;

    private int createdLeads;

    private int updatedRows;

    private List<String> errors = new ArrayList<>();

    public int getScannedUsers()
    {
        return scannedUsers;
    }

    public void setScannedUsers(int scannedUsers)
    {
        this.scannedUsers = scannedUsers;
    }

    public int getProcessedRecords()
    {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords)
    {
        this.processedRecords = processedRecords;
    }

    public int getCreatedLeads()
    {
        return createdLeads;
    }

    public void setCreatedLeads(int createdLeads)
    {
        this.createdLeads = createdLeads;
    }

    public int getUpdatedRows()
    {
        return updatedRows;
    }

    public void setUpdatedRows(int updatedRows)
    {
        this.updatedRows = updatedRows;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors(List<String> errors)
    {
        this.errors = errors != null ? errors : new ArrayList<>();
    }
}
