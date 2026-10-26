package com.ruoyi.crm.domain.dto.wecom;

import java.util.ArrayList;
import java.util.List;

/**
 * 企微统计数据回补结果
 */
public class CrmWecomStatsBackfillResult
{
    private int members;
    private int behaviorRows;
    private int groupchatRows;
    private List<String> errors = new ArrayList<>();

    public int getMembers()
    {
        return members;
    }

    public void setMembers(int members)
    {
        this.members = members;
    }

    public int getBehaviorRows()
    {
        return behaviorRows;
    }

    public void setBehaviorRows(int behaviorRows)
    {
        this.behaviorRows = behaviorRows;
    }

    public int getGroupchatRows()
    {
        return groupchatRows;
    }

    public void setGroupchatRows(int groupchatRows)
    {
        this.groupchatRows = groupchatRows;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void setErrors(List<String> errors)
    {
        this.errors = errors;
    }
}
