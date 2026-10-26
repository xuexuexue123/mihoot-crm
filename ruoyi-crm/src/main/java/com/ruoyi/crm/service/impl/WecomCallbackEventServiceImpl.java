package com.ruoyi.crm.service.impl;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmWecomCallbackEvent;
import com.ruoyi.crm.mapper.CrmWecomCallbackEventMapper;
import com.ruoyi.crm.service.IWecomCallbackEventService;

/**
 * 企业微信回调事件记录服务实现。
 * 从解密后的明文 XML 中提取 Event/ChangeType 及各业务标识字段，统一写入 crm_wecom_callback_event 表。
 */
@Service
public class WecomCallbackEventServiceImpl implements IWecomCallbackEventService
{
    private static final Logger log = LoggerFactory.getLogger(WecomCallbackEventServiceImpl.class);

    @Autowired
    private CrmWecomCallbackEventMapper mapper;

    @Override
    public void logEvent(String plainXml)
    {
        if (plainXml == null || plainXml.isEmpty())
        {
            return;
        }

        try
        {
            CrmWecomCallbackEvent record = new CrmWecomCallbackEvent();

            String event = getCdataOrText(plainXml, "Event");
            if (event == null || event.isEmpty())
            {
                return;
            }
            record.setEvent(event.trim());

            String toUserName = getCdataOrText(plainXml, "ToUserName");
            record.setCorpId(toUserName != null ? toUserName.trim() : "");

            record.setChangeType(trimOrNull(getCdataOrText(plainXml, "ChangeType")));
            record.setUserId(trimOrNull(getCdataOrText(plainXml, "UserID")));
            record.setExternalUserid(trimOrNull(firstNonEmpty(
                    getCdataOrText(plainXml, "ExternalUserID"),
                    getCdataOrText(plainXml, "ExternalUserId"))));
            record.setChatId(trimOrNull(getCdataOrText(plainXml, "ChatId")));
            record.setTagId(trimOrNull(getCdataOrText(plainXml, "Id")));
            record.setTagType(trimOrNull(getCdataOrText(plainXml, "TagType")));
            record.setUpdateDetail(trimOrNull(getCdataOrText(plainXml, "UpdateDetail")));
            record.setState(trimOrNull(getCdataOrText(plainXml, "State")));
            record.setSource(trimOrNull(getCdataOrText(plainXml, "Source")));
            record.setFailReason(trimOrNull(getCdataOrText(plainXml, "FailReason")));
            record.setRawXml(plainXml);
            record.setCreateTime(DateUtils.getNowDate());

            mapper.insert(record);
        }
        catch (Exception e)
        {
            log.warn("WecomCallbackEvent logEvent failed: {}", e.getMessage());
        }
    }

    @Override
    public List<CrmWecomCallbackEvent> selectList(CrmWecomCallbackEvent query)
    {
        return mapper.selectList(query);
    }

    @Override
    public List<Map<String, Object>> countByEventType(CrmWecomCallbackEvent query)
    {
        return mapper.countByEventType(query);
    }

    private static String getCdataOrText(String xml, String tag)
    {
        Pattern p = Pattern.compile(
                "<" + tag + "><!\\[CDATA\\[(.*?)\\]\\]></" + tag + ">|<" + tag + ">([^<]*)</" + tag + ">",
                Pattern.DOTALL);
        Matcher m = p.matcher(xml);
        if (m.find())
        {
            String v = m.group(1) != null ? m.group(1) : m.group(2);
            return v != null ? v.trim() : null;
        }
        return null;
    }

    private static String firstNonEmpty(String a, String b)
    {
        if (a != null && !a.isEmpty())
        {
            return a.trim();
        }
        if (b != null && !b.isEmpty())
        {
            return b.trim();
        }
        return null;
    }

    private static String trimOrNull(String s)
    {
        if (s == null || s.trim().isEmpty())
        {
            return null;
        }
        return s.trim();
    }
}
