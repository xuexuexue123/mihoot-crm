package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * 企微成员行为数据聚合响应，按 member 聚合。
 */
public class WecomBehaviorMemberAggregation
{
    private List<WecomBehaviorMemberRow> members;

    public List<WecomBehaviorMemberRow> getMembers()
    {
        return members;
    }

    public void setMembers(List<WecomBehaviorMemberRow> members)
    {
        this.members = members;
    }
}
