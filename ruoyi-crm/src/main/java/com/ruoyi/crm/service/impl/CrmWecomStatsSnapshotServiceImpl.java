package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomBehaviorDaily;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomGroupchatDaily;
import com.ruoyi.crm.domain.dto.wecom.CrmWecomStatsBackfillResult;
import com.ruoyi.crm.domain.dto.wecom.GroupChatDayItem;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataItem;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsResponse;
import com.ruoyi.crm.mapper.CrmWecomBehaviorDailyMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomGroupchatDailyMapper;
import com.ruoyi.crm.service.ICrmWecomBehaviorService;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;
import com.ruoyi.crm.service.ICrmWecomStatsSnapshotService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.system.mapper.SysUserMapper;

/**
 * 逐成员/群主拉取企微统计 API 并 UPSERT 本地快照表
 */
@Service
public class CrmWecomStatsSnapshotServiceImpl implements ICrmWecomStatsSnapshotService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomStatsSnapshotServiceImpl.class);

    private static final long SECONDS_PER_DAY = 86400L;
    private static final int MAX_BACKFILL_DAYS = 180;
    private static final int MAX_SPAN_DAYS = 30;
    private static final int WECOM_ERR_USER_NOT_FOUND = 60111;
    private static final int WECOM_ERR_GROUPCHAT_SCOPE = 81017;
    private static final long RATE_LIMIT_MS = 150L;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private ICrmWecomBehaviorService behaviorService;

    @Autowired
    private ICrmWecomGroupChatService groupChatService;

    @Autowired
    private CrmWecomBehaviorDailyMapper behaviorDailyMapper;

    @Autowired
    private CrmWecomGroupchatDailyMapper groupchatDailyMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void snapshotYesterday()
    {
        CrmWecomConfig cfg = requireConnectedConfig();
        if (cfg == null)
        {
            return;
        }
        long day = yesterdayStartEpoch();
        List<MemberRosterEntry> members = loadMemberRoster();
        if (members.isEmpty())
        {
            log.info("WeCom stats snapshot skipped: no members with sn_userid");
            return;
        }
        String accessToken = obtainAccessToken(cfg);
        if (accessToken == null)
        {
            return;
        }
        snapshotBehaviorForDay(cfg, accessToken, members, day, day, null);
        snapshotGroupchatForDay(cfg, accessToken, members, day, day, null);
    }

    @Override
    public CrmWecomStatsBackfillResult backfill(int days, String dataset)
    {
        return backfillTenant(days, dataset);
    }

    @Override
    public CrmWecomStatsBackfillResult backfillTenant(int days, String dataset)
    {
        CrmWecomStatsBackfillResult result = new CrmWecomStatsBackfillResult();
        CrmWecomConfig cfg = requireConnectedConfig();
        if (cfg == null)
        {
            result.getErrors().add("未找到已连接的企业微信配置");
            return result;
        }
        int effectiveDays = Math.min(Math.max(days, 1), MAX_BACKFILL_DAYS);
        String mode = normalizeDataset(dataset);
        List<MemberRosterEntry> members = loadMemberRoster();
        result.setMembers(members.size());
        if (members.isEmpty())
        {
            return result;
        }
        String accessToken = obtainAccessToken(cfg);
        if (accessToken == null)
        {
            result.getErrors().add("获取 access_token 失败");
            return result;
        }

        long endDay = yesterdayStartEpoch();
        long startDay = endDay - (effectiveDays - 1L) * SECONDS_PER_DAY;

        if ("behavior".equals(mode) || "both".equals(mode))
        {
            int rows = backfillBehavior(cfg, accessToken, members, startDay, endDay, result.getErrors());
            result.setBehaviorRows(rows);
        }
        if ("groupchat".equals(mode) || "both".equals(mode))
        {
            int rows = backfillGroupchat(cfg, accessToken, members, startDay, endDay, result.getErrors());
            result.setGroupchatRows(rows);
        }
        return result;
    }

    @Override
    public void backfillTenantAsync(String tenantKey, int days, String dataset,
            Consumer<CrmWecomStatsBackfillResult> onSuccess, Consumer<Exception> onFailure)
    {
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    CrmWecomStatsBackfillResult result = backfillTenant(days, dataset);
                    if (onSuccess != null)
                    {
                        onSuccess.accept(result);
                    }
                }
                catch (Exception e)
                {
                    log.error("WeCom stats async backfill failed tenant={}", tenantKey, e);
                    if (onFailure != null)
                    {
                        onFailure.accept(e);
                    }
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }

    private int backfillBehavior(CrmWecomConfig cfg, String accessToken, List<MemberRosterEntry> members,
            long startDay, long endDay, List<String> errors)
    {
        int totalRows = 0;
        for (MemberRosterEntry member : members)
        {
            long chunkStart = startDay;
            while (chunkStart <= endDay)
            {
                long chunkEnd = Math.min(chunkStart + (MAX_SPAN_DAYS - 1L) * SECONDS_PER_DAY, endDay);
                totalRows += snapshotBehaviorForMemberRange(cfg, accessToken, member, chunkStart, chunkEnd, errors);
                chunkStart = chunkEnd + SECONDS_PER_DAY;
                sleepRateLimit();
            }
        }
        return totalRows;
    }

    private int backfillGroupchat(CrmWecomConfig cfg, String accessToken, List<MemberRosterEntry> members,
            long startDay, long endDay, List<String> errors)
    {
        int totalRows = 0;
        for (MemberRosterEntry member : members)
        {
            long chunkStart = startDay;
            while (chunkStart <= endDay)
            {
                long chunkEnd = Math.min(chunkStart + (MAX_SPAN_DAYS - 1L) * SECONDS_PER_DAY, endDay);
                totalRows += snapshotGroupchatForMemberRange(cfg, accessToken, member, chunkStart, chunkEnd, errors);
                chunkStart = chunkEnd + SECONDS_PER_DAY;
                sleepRateLimit();
            }
        }
        return totalRows;
    }

    private void snapshotBehaviorForDay(CrmWecomConfig cfg, String accessToken, List<MemberRosterEntry> members,
            long startDay, long endDay, List<String> errors)
    {
        for (MemberRosterEntry member : members)
        {
            snapshotBehaviorForMemberRange(cfg, accessToken, member, startDay, endDay, errors);
            sleepRateLimit();
        }
    }

    private void snapshotGroupchatForDay(CrmWecomConfig cfg, String accessToken, List<MemberRosterEntry> members,
            long startDay, long endDay, List<String> errors)
    {
        for (MemberRosterEntry member : members)
        {
            snapshotGroupchatForMemberRange(cfg, accessToken, member, startDay, endDay, errors);
            sleepRateLimit();
        }
    }

    private int snapshotBehaviorForMemberRange(CrmWecomConfig cfg, String accessToken, MemberRosterEntry member,
            long startDay, long endDay, List<String> errors)
    {
        WecomBehaviorDataResponse resp;
        try
        {
            resp = behaviorService.queryBehaviorDataForUser(accessToken, member.userid, startDay, endDay);
        }
        catch (Exception e)
        {
            log.error("WeCom behavior snapshot failed: userid={}", member.userid, e);
            if (errors != null)
            {
                errors.add("behavior " + member.userid + ": " + e.getMessage());
            }
            return 0;
        }
        if (resp == null)
        {
            return 0;
        }
        if (resp.getErrcode() != null && resp.getErrcode() == WECOM_ERR_USER_NOT_FOUND)
        {
            log.warn("WeCom behavior snapshot skip deleted user: userid={}", member.userid);
            return 0;
        }
        if (resp.getErrcode() != null && resp.getErrcode() != 0)
        {
            log.error("WeCom behavior snapshot errcode={} userid={} msg={}",
                    resp.getErrcode(), member.userid, resp.getErrmsg());
            if (errors != null)
            {
                errors.add("behavior " + member.userid + ": " + resp.getErrmsg());
            }
            return 0;
        }
        return upsertBehaviorItems(cfg, member, resp.getBehaviorData());
    }

    private int snapshotGroupchatForMemberRange(CrmWecomConfig cfg, String accessToken, MemberRosterEntry member,
            long startDay, long endDay, List<String> errors)
    {
        WecomGroupChatStatsResponse resp;
        try
        {
            resp = groupChatService.queryGroupChatStatsForOwner(accessToken, member.userid, startDay, endDay);
        }
        catch (Exception e)
        {
            log.error("WeCom groupchat snapshot failed: owner={}", member.userid, e);
            if (errors != null)
            {
                errors.add("groupchat " + member.userid + ": " + e.getMessage());
            }
            return 0;
        }
        if (resp == null)
        {
            return 0;
        }
        if (resp.getErrcode() != null
                && (resp.getErrcode() == WECOM_ERR_USER_NOT_FOUND || resp.getErrcode() == WECOM_ERR_GROUPCHAT_SCOPE))
        {
            log.warn("WeCom groupchat snapshot skip owner={} errcode={}", member.userid, resp.getErrcode());
            return 0;
        }
        if (resp.getErrcode() != null && resp.getErrcode() != 0)
        {
            log.error("WeCom groupchat snapshot errcode={} owner={} msg={}",
                    resp.getErrcode(), member.userid, resp.getErrmsg());
            if (errors != null)
            {
                errors.add("groupchat " + member.userid + ": " + resp.getErrmsg());
            }
            return 0;
        }
        return upsertGroupchatItems(cfg, member, resp.getItems());
    }

    private int upsertBehaviorItems(CrmWecomConfig cfg, MemberRosterEntry member, List<WecomBehaviorDataItem> items)
    {
        if (items == null || items.isEmpty())
        {
            return 0;
        }
        int count = 0;
        for (WecomBehaviorDataItem item : items)
        {
            if (item == null || item.getStatTime() == null)
            {
                continue;
            }
            CrmWecomBehaviorDaily row = new CrmWecomBehaviorDaily();
            row.setCorpId(cfg.getCorpId());
            row.setUserid(member.userid);
            row.setUserName(member.name);
            row.setStatTime(item.getStatTime());
            row.setNewApplyCnt(defaultInt(item.getNewApplyCnt()));
            row.setNewContactCnt(defaultInt(item.getNewContactCnt()));
            row.setChatCnt(defaultInt(item.getChatCnt()));
            row.setMessageCnt(defaultInt(item.getMessageCnt()));
            row.setNegativeFeedbackCnt(defaultInt(item.getNegativeFeedbackCnt()));
            if (item.getReplyPercentage() != null)
            {
                row.setReplyPercentage(BigDecimal.valueOf(item.getReplyPercentage()));
            }
            row.setAvgReplyTime(item.getAvgReplyTime());
            row.setRawJson(toRawJson(item));
            behaviorDailyMapper.upsertBehaviorDaily(row);
            count++;
        }
        return count;
    }

    private int upsertGroupchatItems(CrmWecomConfig cfg, MemberRosterEntry member, List<GroupChatDayItem> items)
    {
        if (items == null || items.isEmpty())
        {
            return 0;
        }
        int count = 0;
        for (GroupChatDayItem item : items)
        {
            if (item == null || item.getStatTime() == null || item.getData() == null)
            {
                continue;
            }
            GroupChatDayItem.GroupChatData data = item.getData();
            CrmWecomGroupchatDaily row = new CrmWecomGroupchatDaily();
            row.setCorpId(cfg.getCorpId());
            row.setOwnerUserid(member.userid);
            row.setOwnerName(member.name);
            row.setStatTime(item.getStatTime());
            row.setNewChatCnt(defaultInt(data.getNewChatCnt()));
            row.setChatTotal(defaultInt(data.getChatTotal()));
            row.setChatHasMsg(defaultInt(data.getChatHasMsg()));
            row.setNewMemberCnt(defaultInt(data.getNewMemberCnt()));
            row.setMemberTotal(defaultInt(data.getMemberTotal()));
            row.setMemberHasMsg(defaultInt(data.getMemberHasMsg()));
            row.setMsgTotal(defaultInt(data.getMsgTotal()));
            row.setMigrateTraineeChatCnt(defaultInt(data.getMigrateTraineeChatCnt()));
            row.setRawJson(toRawJson(item));
            groupchatDailyMapper.upsertGroupchatDaily(row);
            count++;
        }
        return count;
    }

    private List<MemberRosterEntry> loadMemberRoster()
    {
        List<SysUser> users = sysUserMapper.selectUsersWithSnUserid();
        if (users == null || users.isEmpty())
        {
            return Collections.emptyList();
        }
        List<MemberRosterEntry> roster = new ArrayList<>();
        for (SysUser user : users)
        {
            if (user == null || StringUtils.isEmpty(user.getSnUserid()))
            {
                continue;
            }
            String name = StringUtils.isNotEmpty(user.getNickName()) ? user.getNickName() : user.getSnUserid();
            roster.add(new MemberRosterEntry(user.getSnUserid(), name));
        }
        return roster;
    }

    private CrmWecomConfig requireConnectedConfig()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isEmpty(cfg.getCorpId()))
        {
            log.warn("WeCom stats snapshot skipped: no wecom config");
            return null;
        }
        if (!Boolean.TRUE.equals(cfg.getConnected()))
        {
            log.info("WeCom stats snapshot skipped: wecom not connected corpId={}", cfg.getCorpId());
            return null;
        }
        return cfg;
    }

    private String obtainAccessToken(CrmWecomConfig cfg)
    {
        try
        {
            return wecomAccessTokenService.getAgentAccessToken(cfg);
        }
        catch (Exception e)
        {
            log.error("WeCom stats snapshot access_token failed corpId={}", cfg.getCorpId(), e);
            return null;
        }
    }

    private static long yesterdayStartEpoch()
    {
        LocalDate yesterday = LocalDate.now(ZoneId.systemDefault()).minusDays(1);
        return yesterday.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }

    private static int defaultInt(Integer value)
    {
        return value != null ? value : 0;
    }

    private static String normalizeDataset(String dataset)
    {
        if ("behavior".equalsIgnoreCase(dataset) || "groupchat".equalsIgnoreCase(dataset))
        {
            return dataset.toLowerCase();
        }
        return "both";
    }

    private String toRawJson(Object value)
    {
        try
        {
            return objectMapper.writeValueAsString(value);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static void sleepRateLimit()
    {
        try
        {
            Thread.sleep(RATE_LIMIT_MS);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private static final class MemberRosterEntry
    {
        private final String userid;
        private final String name;

        private MemberRosterEntry(String userid, String name)
        {
            this.userid = userid;
            this.name = name;
        }
    }
}
