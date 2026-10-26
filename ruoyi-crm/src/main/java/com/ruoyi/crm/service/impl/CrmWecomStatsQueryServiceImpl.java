package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsMemberRow;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorMemberAggregation;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorMemberRow;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsResponse;
import com.ruoyi.crm.mapper.CrmWecomBehaviorDailyMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupchatDailyMapper;
import com.ruoyi.crm.mapper.CrmWecomStatsMemberNameRow;
import com.ruoyi.crm.service.ICrmWecomStatsInitService;
import com.ruoyi.crm.service.ICrmWecomStatsQueryService;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 从本地快照表聚合查询企微统计数据
 */
@Service
public class CrmWecomStatsQueryServiceImpl implements ICrmWecomStatsQueryService
{
    @Autowired
    private CrmWecomBehaviorDailyMapper behaviorDailyMapper;

    @Autowired
    private CrmWecomGroupchatDailyMapper groupchatDailyMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ICrmWecomStatsInitService statsInitService;

    @Override
    public WecomBehaviorDataResponse queryBehaviorFromSnapshot(WecomBehaviorDataRequest request)
    {
        statsInitService.ensureInitialized();
        List<String> userids = resolveBehaviorUserids(request);
        WecomBehaviorDataResponse response = new WecomBehaviorDataResponse();
        response.setErrcode(0);
        response.setErrmsg("ok");
        if (userids != null && userids.isEmpty())
        {
            response.setBehaviorData(Collections.emptyList());
            return response;
        }
        List<String> filter = (userids == null || userids.isEmpty()) ? null : userids;
        response.setBehaviorData(behaviorDailyMapper.aggregateBehaviorByStatTime(
                request.getStartTime(), request.getEndTime(), filter));
        return response;
    }

    @Override
    public WecomBehaviorMemberAggregation queryBehaviorByMember(WecomBehaviorDataRequest request)
    {
        statsInitService.ensureInitialized();
        List<String> userids = resolveBehaviorUserids(request);
        WecomBehaviorMemberAggregation aggregation = new WecomBehaviorMemberAggregation();
        if (userids != null && userids.isEmpty())
        {
            aggregation.setMembers(Collections.emptyList());
            return aggregation;
        }
        List<String> filter = (userids == null || userids.isEmpty()) ? null : userids;
        List<WecomBehaviorMemberRow> rows = behaviorDailyMapper.aggregateBehaviorByMember(
                request.getStartTime(), request.getEndTime(), filter);

        Set<String> activeUserids = loadActiveUserids();
        if (rows != null)
        {
            for (WecomBehaviorMemberRow row : rows)
            {
                row.setActive(activeUserids.contains(row.getUserid()));
            }
        }
        aggregation.setMembers(rows != null ? rows : Collections.emptyList());
        return aggregation;
    }

    @Override
    public WecomGroupChatStatsResponse queryGroupchatFromSnapshot(WecomGroupChatStatsRequest request)
    {
        statsInitService.ensureInitialized();
        List<String> ownerUserids = null;
        if (request.getOwnerFilter() != null
                && request.getOwnerFilter().getUseridList() != null
                && !request.getOwnerFilter().getUseridList().isEmpty())
        {
            ownerUserids = request.getOwnerFilter().getUseridList();
        }
        WecomGroupChatStatsResponse response = new WecomGroupChatStatsResponse();
        response.setErrcode(0);
        response.setErrmsg("ok");
        response.setItems(groupchatDailyMapper.aggregateGroupchatByStatTime(
                request.getDayBeginTime(), request.getDayEndTime(), ownerUserids));
        return response;
    }

    @Override
    public List<CrmWecomStatsMemberRow> listHistoricalMembers()
    {
        Map<String, String> nameByUserid = new HashMap<>();
        List<CrmWecomStatsMemberNameRow> behaviorMembers = behaviorDailyMapper.selectDistinctMemberNames();
        if (behaviorMembers != null)
        {
            for (CrmWecomStatsMemberNameRow row : behaviorMembers)
            {
                mergeName(nameByUserid, row.getUserid(), row.getName());
            }
        }
        List<CrmWecomStatsMemberNameRow> groupchatOwners = groupchatDailyMapper.selectDistinctOwnerNames();
        if (groupchatOwners != null)
        {
            for (CrmWecomStatsMemberNameRow row : groupchatOwners)
            {
                mergeName(nameByUserid, row.getUserid(), row.getName());
            }
        }

        Set<String> activeUserids = loadActiveUserids();

        List<CrmWecomStatsMemberRow> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : nameByUserid.entrySet())
        {
            CrmWecomStatsMemberRow row = new CrmWecomStatsMemberRow();
            row.setUserid(entry.getKey());
            row.setName(entry.getValue());
            row.setActive(activeUserids.contains(entry.getKey()));
            result.add(row);
        }
        result.sort((a, b) -> {
            int activeCompare = Boolean.compare(Boolean.TRUE.equals(b.getActive()), Boolean.TRUE.equals(a.getActive()));
            if (activeCompare != 0)
            {
                return activeCompare;
            }
            String nameA = a.getName() != null ? a.getName() : a.getUserid();
            String nameB = b.getName() != null ? b.getName() : b.getUserid();
            return nameA.compareToIgnoreCase(nameB);
        });
        return result;
    }

    private List<String> resolveBehaviorUserids(WecomBehaviorDataRequest request)
    {
        if (request.getUserid() != null && !request.getUserid().isEmpty())
        {
            return request.getUserid();
        }
        if (request.getPartyid() != null && !request.getPartyid().isEmpty())
        {
            List<String> snUserids = sysUserMapper.selectSnUseridsByDeptIds(request.getPartyid());
            return snUserids != null ? snUserids : Collections.emptyList();
        }
        return null;
    }

    private static void mergeName(Map<String, String> nameByUserid, String userid, String name)
    {
        if (StringUtils.isEmpty(userid))
        {
            return;
        }
        if (StringUtils.isNotEmpty(name))
        {
            nameByUserid.put(userid, name);
        }
        else
        {
            nameByUserid.putIfAbsent(userid, userid);
        }
    }

    private Set<String> loadActiveUserids()
    {
        Set<String> activeUserids = new HashSet<>();
        List<SysUser> activeUsers = sysUserMapper.selectUsersWithSnUserid();
        if (activeUsers != null)
        {
            for (SysUser user : activeUsers)
            {
                if (user != null && StringUtils.isNotEmpty(user.getSnUserid()))
                {
                    activeUserids.add(user.getSnUserid());
                }
            }
        }
        return activeUserids;
    }
}
