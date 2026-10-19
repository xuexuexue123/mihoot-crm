package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmWecomKfMessage;
import com.ruoyi.crm.domain.dto.ServiceSummaryDeltaItem;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CrmWecomKfMessageMapper;

/**
 * 加载 Contact 服务摘要增量输入（活动时间线 + KF 消息）。
 */
@Component
public class ContactServiceSummaryDeltaLoader
{
    public static final int DEFAULT_LIMIT = 80;

    @Autowired
    private CrmActivityMapper activityMapper;

    @Autowired(required = false)
    private CrmWecomKfMessageMapper kfMessageMapper;

    public List<ServiceSummaryDeltaItem> loadBootstrap(String contactId, int limit)
    {
        List<ServiceSummaryDeltaItem> items = new ArrayList<>();
        items.addAll(loadFromActivities(contactId, limit));
        // 去重并按时间升序，便于 prompt
        items.sort((a, b) -> {
            Date da = a.getAt();
            Date db = b.getAt();
            if (da == null && db == null) return 0;
            if (da == null) return -1;
            if (db == null) return 1;
            return da.compareTo(db);
        });
        if (items.size() > limit)
        {
            return items.subList(items.size() - limit, items.size());
        }
        return items;
    }

    public List<ServiceSummaryDeltaItem> loadIncremental(String contactId, String lastEventId, int limit)
    {
        List<ServiceSummaryDeltaItem> all = loadBootstrap(contactId, limit * 2);
        if (StringUtils.isEmpty(lastEventId))
        {
            return all.size() > limit ? all.subList(all.size() - limit, all.size()) : all;
        }
        int idx = -1;
        for (int i = 0; i < all.size(); i++)
        {
            if (lastEventId.equals(all.get(i).getEventId()))
            {
                idx = i;
                break;
            }
        }
        if (idx < 0)
        {
            return all.size() > limit ? all.subList(all.size() - limit, all.size()) : all;
        }
        List<ServiceSummaryDeltaItem> after = all.subList(idx + 1, all.size());
        return after.size() > limit ? after.subList(after.size() - limit, after.size()) : after;
    }

    public List<ServiceSummaryDeltaItem> loadKfByExternalUserid(String corpId, String externalUserid,
            String openKfid, int limit)
    {
        if (kfMessageMapper == null || StringUtils.isEmpty(externalUserid))
        {
            return Collections.emptyList();
        }
        List<CrmWecomKfMessage> messages = kfMessageMapper.selectByExternalUserid(corpId, externalUserid,
                openKfid, null);
        List<ServiceSummaryDeltaItem> items = new ArrayList<>();
        if (messages == null)
        {
            return items;
        }
        int from = Math.max(0, messages.size() - limit);
        for (int i = messages.size() - 1; i >= from; i--)
        {
            CrmWecomKfMessage m = messages.get(i);
            ServiceSummaryDeltaItem item = new ServiceSummaryDeltaItem();
            item.setEventId(m.getMsgid());
            item.setChannel("wecom_kf");
            boolean inbound = m.getDirection() != null && m.getDirection() == 1;
            item.setDirection(inbound ? "in" : "out");
            item.setActor(inbound ? "customer" : "agent");
            item.setText(placeholderText(m.getMsgtype(), m.getContent()));
            if (m.getSendTime() != null)
            {
                item.setAt(new Date(m.getSendTime() * 1000L));
            }
            items.add(item);
        }
        return items;
    }

    public Set<String> collectChannels(List<ServiceSummaryDeltaItem> items)
    {
        Set<String> channels = new LinkedHashSet<>();
        if (items == null)
        {
            return channels;
        }
        for (ServiceSummaryDeltaItem item : items)
        {
            if (StringUtils.isNotEmpty(item.getChannel()))
            {
                channels.add(item.getChannel());
            }
        }
        return channels;
    }

    private List<ServiceSummaryDeltaItem> loadFromActivities(String contactId, int limit)
    {
        if (StringUtils.isEmpty(contactId))
        {
            return Collections.emptyList();
        }
        CrmActivity query = new CrmActivity();
        query.setSubjectType("Contact");
        query.setSubjectId(contactId);
        List<CrmActivity> activities = activityMapper.selectTimelineList(query);
        if (activities == null || activities.isEmpty())
        {
            return Collections.emptyList();
        }
        List<ServiceSummaryDeltaItem> items = new ArrayList<>();
        int from = Math.max(0, activities.size() - limit);
        for (int i = from; i < activities.size(); i++)
        {
            CrmActivity a = activities.get(i);
            ServiceSummaryDeltaItem item = new ServiceSummaryDeltaItem();
            item.setEventId(a.getActivityId());
            item.setChannel(StringUtils.isNotEmpty(a.getChannel()) ? a.getChannel() : "crm");
            String text = StringUtils.isNotEmpty(a.getMessageData()) ? a.getMessageData() : a.getEventName();
            if (StringUtils.isEmpty(text))
            {
                continue;
            }
            item.setText(text);
            item.setAt(a.getStartedDate() != null ? a.getStartedDate() : a.getCreateTime());
            String et = a.getEventType() != null ? a.getEventType() : "";
            boolean outbound = et.contains(".sent") || et.contains("message_sent") || et.contains(".outbound");
            item.setDirection(outbound ? "out" : "in");
            item.setActor(outbound ? "agent" : "customer");
            items.add(item);
        }
        return items;
    }

    private static String placeholderText(String msgtype, String content)
    {
        if (StringUtils.isNotEmpty(content))
        {
            return content;
        }
        if ("image".equalsIgnoreCase(msgtype))
        {
            return "[图片]";
        }
        if ("voice".equalsIgnoreCase(msgtype) || "audio".equalsIgnoreCase(msgtype))
        {
            return "[语音]";
        }
        if ("video".equalsIgnoreCase(msgtype))
        {
            return "[视频]";
        }
        if ("file".equalsIgnoreCase(msgtype))
        {
            return "[文件]";
        }
        return "[媒体]";
    }

    public static List<String> parseStringList(String json)
    {
        if (StringUtils.isEmpty(json))
        {
            return new ArrayList<>();
        }
        try
        {
            JSONArray arr = JSON.parseArray(json);
            List<String> list = new ArrayList<>();
            if (arr != null)
            {
                for (int i = 0; i < arr.size(); i++)
                {
                    list.add(arr.getString(i));
                }
            }
            return list;
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }
}
