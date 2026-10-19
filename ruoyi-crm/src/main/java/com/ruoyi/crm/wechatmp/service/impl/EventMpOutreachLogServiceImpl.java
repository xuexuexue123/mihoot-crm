package com.ruoyi.crm.wechatmp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.wechatmp.domain.CrmEventMpOutreachLog;
import com.ruoyi.crm.wechatmp.mapper.CrmEventMpOutreachLogMapper;
import com.ruoyi.crm.wechatmp.service.IEventMpOutreachLogService;

@Service
public class EventMpOutreachLogServiceImpl implements IEventMpOutreachLogService
{
    @Autowired
    private CrmEventMpOutreachLogMapper outreachLogMapper;

    @Override
    public CrmEventMpOutreachLog findByEventOpenId(String eventId, String openId, String triggerType)
    {
        return outreachLogMapper.selectByEventOpenIdTrigger(eventId, openId, triggerType);
    }

    @Override
    public boolean tryInsertPending(CrmEventMpOutreachLog row)
    {
        CrmEventMpOutreachLog existing = findByEventOpenId(
                row.getEventId(), row.getOpenId(), row.getTriggerType());
        if (existing != null)
        {
            return false;
        }
        return outreachLogMapper.insertIgnore(row) > 0;
    }

    @Override
    public void recordOutcome(CrmEventMpOutreachLog row)
    {
        CrmEventMpOutreachLog existing = findByEventOpenId(
                row.getEventId(), row.getOpenId(), row.getTriggerType());
        if (existing == null)
        {
            outreachLogMapper.insertIgnore(row);
            return;
        }
        outreachLogMapper.updateByEventOpenIdTrigger(row);
    }

    @Override
    public List<CrmEventMpOutreachLog> selectList(CrmEventMpOutreachLog query)
    {
        return outreachLogMapper.selectList(query);
    }
}
