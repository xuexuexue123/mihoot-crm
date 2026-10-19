package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.crm.domain.CrmEventSpeaker;
import com.ruoyi.crm.mapper.CrmEventSpeakerMapper;
import com.ruoyi.crm.service.ICrmEventSpeakerService;

/**
 * 活动演讲者Service业务层处理
 * 
 * @author luohd
 * @date 2025-07-20
 */
@Service
public class CrmEventSpeakerServiceImpl implements ICrmEventSpeakerService
{
    @Autowired
    private CrmEventSpeakerMapper speakerMapper;

    @Override
    public List<CrmEventSpeaker> selectSpeakerByEventId(String eventId)
    {
        return speakerMapper.selectCrmEventSpeakerByEventId(eventId);
    }

    @Override
    public CrmEventSpeaker selectSpeakerById(Long id)
    {
        return speakerMapper.selectCrmEventSpeakerById(id);
    }

    @Override
    public int insertSpeaker(CrmEventSpeaker speaker)
    {
        if (speaker.getCreateTime() == null)
        {
            speaker.setCreateTime(new Date());
        }
        return speakerMapper.insertCrmEventSpeaker(speaker);
    }

    @Override
    public int updateSpeaker(CrmEventSpeaker speaker)
    {
        return speakerMapper.updateCrmEventSpeaker(speaker);
    }

    @Override
    public int deleteSpeakerById(Long id)
    {
        return speakerMapper.deleteCrmEventSpeakerById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceSpeakers(String eventId, List<CrmEventSpeaker> speakers)
    {
        speakerMapper.deleteCrmEventSpeakerByEventId(eventId);
        if (speakers != null && !speakers.isEmpty())
        {
            Date now = new Date();
            for (CrmEventSpeaker s : speakers)
            {
                s.setEventId(eventId);
                if (s.getCreateTime() == null)
                {
                    s.setCreateTime(now);
                }
            }
            speakerMapper.batchInsertCrmEventSpeaker(speakers);
        }
    }

    @Override
    public int deleteSpeakerByEventIds(String[] eventIds)
    {
        if (eventIds == null || eventIds.length == 0)
        {
            return 0;
        }
        return speakerMapper.deleteCrmEventSpeakerByEventIds(eventIds);
    }
}
