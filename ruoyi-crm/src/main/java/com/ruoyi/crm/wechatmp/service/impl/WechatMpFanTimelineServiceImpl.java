package com.ruoyi.crm.wechatmp.service.impl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.wechatmp.domain.ActivityUnmatched;
import com.ruoyi.crm.wechatmp.dto.WechatMpFanTimelineItem;
import com.ruoyi.crm.wechatmp.mapper.WechatMpFanTimelineMapper;
import com.ruoyi.crm.wechatmp.service.IWechatMpFanTimelineService;

@Service
public class WechatMpFanTimelineServiceImpl implements IWechatMpFanTimelineService
{
    private static final int MAX_PAGE_SIZE = 100;

    @Autowired
    private WechatMpFanTimelineMapper timelineMapper;

    @Override
    public TableDataInfo listTimeline(String appId, String openId, int pageNum, int pageSize)
    {
        if (!StringUtils.hasText(appId))
        {
            throw new ServiceException("appId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(openId))
        {
            throw new ServiceException("openId 不能为空", HttpStatus.BAD_REQUEST);
        }

        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);

        List<CrmActivity> activities = timelineMapper.selectActivitiesByOpenId(appId, openId);
        List<ActivityUnmatched> unmatchedList = timelineMapper.selectUnmatchedByOpenId(appId, openId);

        List<WechatMpFanTimelineItem> merged = new ArrayList<>(activities.size() + unmatchedList.size());

        for (CrmActivity activity : activities)
        {
            WechatMpFanTimelineItem item = new WechatMpFanTimelineItem();
            item.setSource("activity");
            item.setSortTime(toOffsetDateTime(coalesce(activity.getStartedDate(), activity.getCreateTime())));
            item.setActivity(activity);
            merged.add(item);
        }

        for (ActivityUnmatched unmatched : unmatchedList)
        {
            WechatMpFanTimelineItem item = new WechatMpFanTimelineItem();
            item.setSource("unmatched");
            item.setSortTime(toOffsetDateTime(coalesce(unmatched.getCreateTime(), unmatched.getMatchedAt())));
            item.setUnmatched(unmatched);
            merged.add(item);
        }

        merged.sort(Comparator.comparing(WechatMpFanTimelineItem::getSortTime,
                Comparator.nullsLast(Comparator.reverseOrder())));

        int total = merged.size();
        int fromIndex = (safePageNum - 1) * safePageSize;
        List<WechatMpFanTimelineItem> pageRows;
        if (fromIndex >= total)
        {
            pageRows = new ArrayList<>();
        }
        else
        {
            int toIndex = Math.min(fromIndex + safePageSize, total);
            pageRows = merged.subList(fromIndex, toIndex);
        }

        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("success");
        data.setTotal(total);
        data.setRows(pageRows);
        return data;
    }

    private static Date coalesce(Date primary, Date fallback)
    {
        return primary != null ? primary : fallback;
    }

    private static LocalDateTime coalesce(LocalDateTime primary, LocalDateTime fallback)
    {
        return primary != null ? primary : fallback;
    }

    private static OffsetDateTime toOffsetDateTime(Date date)
    {
        if (date == null)
        {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private static OffsetDateTime toOffsetDateTime(LocalDateTime dateTime)
    {
        if (dateTime == null)
        {
            return null;
        }
        return dateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
