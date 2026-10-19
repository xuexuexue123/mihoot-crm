package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.ruoyi.crm.wechatmp.domain.CrmEventMpOutreachLog;

public interface IEventMpOutreachLogService
{
    CrmEventMpOutreachLog findByEventOpenId(String eventId, String openId, String triggerType);

    /**
     * 尝试插入幂等记录；若已存在返回 false
     */
    boolean tryInsertPending(CrmEventMpOutreachLog row);

    void recordOutcome(CrmEventMpOutreachLog row);

    List<CrmEventMpOutreachLog> selectList(CrmEventMpOutreachLog query);
}
