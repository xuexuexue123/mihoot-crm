package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.domain.CrmWecomKfMessage;
import com.ruoyi.crm.mapper.CrmWecomKfMessageMapper;
import com.ruoyi.crm.service.IWecomKfMessageService;

/**
 * 微信客服消息持久化服务实现。
 * <p>
 * incoming 消息通过 (corp_id, msgid) 唯一约束保证幂等；
 * outgoing 消息不对 msgid 做唯一约束，每次记录独立一行。
 */
@Service
public class WecomKfMessageServiceImpl implements IWecomKfMessageService
{
    private static final Logger log = LoggerFactory.getLogger(WecomKfMessageServiceImpl.class);

    @Autowired
    private CrmWecomKfMessageMapper kfMessageMapper;

    @Override
    public boolean insertMessage(CrmWecomKfMessage record)
    {
        try
        {
            record.setCreatedAt(new Date());
            kfMessageMapper.insert(record);
            return true;
        }
        catch (DuplicateKeyException e)
        {
            log.info("WecomKfMessage insert skipped (duplicate msgid): corpId={} msgid={}",
                    record.getCorpId(), record.getMsgid());
            return false;
        }
        catch (Exception e)
        {
            log.error("WecomKfMessage insert FAILED: corpId={} msgid={} externalUserid={} openKfid={} msgtype={} sendTime={}",
                    record.getCorpId(), record.getMsgid(), record.getExternalUserid(),
                    record.getOpenKfid(), record.getMsgtype(), record.getSendTime(), e);
            return false;
        }
    }

    @Override
    public void insertReply(CrmWecomKfMessage record)
    {
        record.setDirection(2);
        record.setCreatedAt(new Date());
        if (record.getSendTime() == null)
        {
            record.setSendTime(System.currentTimeMillis() / 1000);
        }
        kfMessageMapper.insert(record);
    }

    @Override
    public List<CrmWecomKfMessage> selectMessages(String corpId, String externalUserid, String openKfid,
            Integer direction)
    {
        return kfMessageMapper.selectByExternalUserid(corpId, externalUserid, openKfid, direction);
    }

    @Override
    public CrmWecomKfMessage selectByMsgid(String corpId, String msgid)
    {
        return kfMessageMapper.selectByMsgid(corpId, msgid);
    }

    @Override
    public List<CrmWecomKfMessage> selectByContactId(String corpId, String contactId, String openKfid)
    {
        return kfMessageMapper.selectByContactId(corpId, contactId, openKfid);
    }

    @Override
    public List<CrmWecomKfMessage> selectMessagesByDate(
            String corpId, String openKfid, Long startTime, Long endTime)
    {
        return kfMessageMapper.selectMessagesByDate(corpId, openKfid, startTime, endTime);
    }
}
