package com.ruoyi.crm.service.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.EventAudienceQuery;
import com.ruoyi.crm.domain.vo.EventAudienceChannelDetailVo;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;

/**
 * 多渠道受众合并与筛选（h5 / website / wecom / mp）
 */
@Component
public class AudienceMerger
{
    @Autowired
    private EventAudienceKeyResolver audienceKeyResolver;

    public List<EventAudienceRowVo> merge(List<AudienceSourceRow> h5,
            List<AudienceSourceRow> wecom,
            List<AudienceSourceRow> mp,
            Map<String, String> openidToUnionid,
            Map<String, String> externalUseridToUnionid,
            Map<String, String> contactNames,
            boolean includeChannelDetails)
    {
        return merge(h5, wecom, mp, null, openidToUnionid, externalUseridToUnionid, contactNames,
                includeChannelDetails);
    }

    public List<EventAudienceRowVo> merge(List<AudienceSourceRow> h5,
            List<AudienceSourceRow> wecom,
            List<AudienceSourceRow> mp,
            List<AudienceSourceRow> website,
            Map<String, String> openidToUnionid,
            Map<String, String> externalUseridToUnionid,
            Map<String, String> contactNames,
            boolean includeChannelDetails)
    {
        List<AudienceSourceRow> all = new ArrayList<>();
        if (h5 != null)
        {
            all.addAll(h5);
        }
        if (website != null)
        {
            all.addAll(website);
        }
        if (wecom != null)
        {
            all.addAll(wecom);
        }
        if (mp != null)
        {
            all.addAll(mp);
        }
        if (all.isEmpty())
        {
            return Collections.emptyList();
        }

        UnionFind uf = new UnionFind(all.size());
        Map<String, Integer> contactIdx = new HashMap<>();
        Map<String, Integer> mobileIdx = new HashMap<>();
        Map<String, Integer> unionidIdx = new HashMap<>();
        Map<String, Integer> mpOpenidIdx = new HashMap<>();

        for (int i = 0; i < all.size(); i++)
        {
            AudienceSourceRow row = all.get(i);
            unionByKey(uf, contactIdx, row.getContactId(), i);
            unionByKey(uf, mobileIdx, PhoneNormalizer.normalize(row.getMobilePhone()), i);
            String unionid = resolveUnionid(row, openidToUnionid, externalUseridToUnionid);
            unionByKey(uf, unionidIdx, unionid, i);
            unionByKey(uf, mpOpenidIdx, mpMergeKey(row), i);
        }

        Map<Integer, List<AudienceSourceRow>> groups = new HashMap<>();
        for (int i = 0; i < all.size(); i++)
        {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(all.get(i));
        }

        List<EventAudienceRowVo> result = new ArrayList<>();
        for (List<AudienceSourceRow> group : groups.values())
        {
            result.add(buildMergedRow(group, contactNames, includeChannelDetails));
        }
        result.sort(Comparator.comparing(EventAudienceRowVo::getFirstTouchAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public List<EventAudienceRowVo> applyFilters(List<EventAudienceRowVo> rows, EventAudienceQuery query)
    {
        if (rows == null || rows.isEmpty())
        {
            return Collections.emptyList();
        }
        Set<String> channelFilter = parseChannels(query.getChannels());
        String statusFilter = query.getAudienceStatus();
        String keyword = query.getKeyword();
        String checkInStatusFilter = query.getCheckInStatus();

        return rows.stream()
                .filter(row -> matchesChannels(row, channelFilter))
                .filter(row -> statusFilter == null || statusFilter.isEmpty()
                        || statusFilter.equals(row.getAudienceStatus()))
                .filter(row -> matchesKeyword(row, keyword))
                .filter(row -> matchesCheckInStatus(row, checkInStatusFilter))
                .collect(Collectors.toList());
    }

    private boolean matchesCheckInStatus(EventAudienceRowVo row, String checkInStatusFilter)
    {
        if (checkInStatusFilter == null || checkInStatusFilter.isEmpty())
        {
            return true;
        }
        return checkInStatusFilter.equals(row.getCheckInStatus());
    }

    private EventAudienceRowVo buildMergedRow(List<AudienceSourceRow> group,
            Map<String, String> contactNames,
            boolean includeChannelDetails)
    {
        EventAudienceRowVo vo = new EventAudienceRowVo();
        Set<String> channels = new LinkedHashSet<>();
        Date firstTouch = null;
        Date lastActivity = null;
        String contactId = null;
        String leadId = null;
        String leadStatus = null;
        String leadStage = null;
        boolean leadHasContactOnRow = false;
        String displayName = null;
        String mobilePhone = null;
        String wecomExternalUserid = null;
        String wechatMpOpenid = null;
        String wechatMpAppId = null;
        String owner = null;
        boolean hasH5 = false;
        boolean hasWecom = false;
        boolean hasMp = false;
        List<EventAudienceChannelDetailVo> details = includeChannelDetails ? new ArrayList<>() : null;

        for (AudienceSourceRow row : group)
        {
            channels.add(row.getChannel());
            firstTouch = minDate(firstTouch, row.getTouchedAt());
            lastActivity = maxDate(lastActivity, row.getLastActivityAt());

            if (isRegistrationChannel(row.getChannel()))
            {
                hasH5 = true;
                if (row.getLeadId() != null)
                {
                    leadId = row.getLeadId();
                    leadStatus = row.getLeadStatus();
                    leadStage = row.getLeadStage();
                }
                if (StringUtils.isNotEmpty(row.getContactId()))
                {
                    leadHasContactOnRow = true;
                }
            }
            else if ("wecom".equals(row.getChannel()))
            {
                hasWecom = true;
            }
            else if ("mp".equals(row.getChannel()))
            {
                hasMp = true;
            }

            if (StringUtils.isEmpty(contactId) && StringUtils.isNotEmpty(row.getContactId()))
            {
                contactId = row.getContactId();
            }
            if (StringUtils.isEmpty(mobilePhone) && StringUtils.isNotEmpty(row.getMobilePhone()))
            {
                mobilePhone = PhoneNormalizer.normalize(row.getMobilePhone());
                if (mobilePhone == null)
                {
                    mobilePhone = row.getMobilePhone();
                }
            }
            if (StringUtils.isEmpty(wecomExternalUserid) && StringUtils.isNotEmpty(row.getWecomExternalUserid()))
            {
                wecomExternalUserid = row.getWecomExternalUserid();
            }
            if (StringUtils.isEmpty(wechatMpOpenid) && StringUtils.isNotEmpty(row.getWechatMpOpenid()))
            {
                wechatMpOpenid = row.getWechatMpOpenid();
                wechatMpAppId = row.getWechatMpAppId();
            }
            if (StringUtils.isEmpty(owner))
            {
                owner = StringUtils.isNotEmpty(row.getOwner()) ? row.getOwner() : row.getFollowUserid();
            }

            displayName = pickDisplayName(displayName, row, contactNames);

            if (details != null)
            {
                EventAudienceChannelDetailVo detail = new EventAudienceChannelDetailVo();
                detail.setChannel(row.getChannel());
                detail.setSourceId(row.getSourceId());
                detail.setTouchedAt(row.getTouchedAt());
                if (isRegistrationChannel(row.getChannel()))
                {
                    detail.setRawStatus(StringUtils.isNotEmpty(row.getRegistrationStatus())
                            ? row.getRegistrationStatus()
                            : row.getLeadStatus());
                }
                details.add(detail);
            }
        }

        vo.setChannels(new ArrayList<>(channels));
        vo.setFirstTouchAt(firstTouch);
        vo.setLastActivityAt(lastActivity);
        vo.setContactId(contactId);
        vo.setLeadId(leadId);
        vo.setLeadStatus(leadStatus);
        vo.setContactName(contactId != null && contactNames != null ? contactNames.get(contactId) : null);
        vo.setWecomExternalUserid(wecomExternalUserid);
        vo.setWechatMpOpenid(wechatMpOpenid);
        vo.setWechatMpAppId(wechatMpAppId);
        vo.setOwner(owner);
        vo.setDisplayName(displayName);
        vo.setMobilePhone(mobilePhone);
        String audienceKey = audienceKeyResolver.resolveFromMergedGroup(
                contactId, mobilePhone, null, leadId, wecomExternalUserid, wechatMpOpenid);
        if (StringUtils.isEmpty(audienceKey))
        {
            audienceKey = buildAudienceKeyFallback(group);
        }
        vo.setAudienceKey(audienceKey);
        vo.setChannelDetails(details);

        AudienceStatusResolver.MergedAudienceState state = new AudienceStatusResolver.MergedAudienceState();
        state.setHasH5(hasH5);
        state.setHasWecom(hasWecom);
        state.setHasMp(hasMp);
        state.setContactId(contactId);
        state.setLeadId(leadId);
        state.setLeadStatus(leadStatus);
        state.setLeadStage(leadStage);
        state.setLeadHasContactId(leadHasContactOnRow);
        vo.setAudienceStatus(AudienceStatusResolver.resolve(state));
        return vo;
    }

    private String buildAudienceKeyFallback(List<AudienceSourceRow> group)
    {
        AudienceSourceRow first = group.get(0);
        return first.getChannel() + ":" + first.getSourceId();
    }

    private String buildAudienceKey(String contactId, String mobilePhone, List<AudienceSourceRow> group,
            String wecomExternalUserid, String wechatMpOpenid, String leadId)
    {
        if (StringUtils.isNotEmpty(contactId))
        {
            return "contact:" + contactId;
        }
        if (StringUtils.isNotEmpty(mobilePhone))
        {
            return "mobile:" + mobilePhone;
        }
        if (StringUtils.isNotEmpty(wechatMpOpenid))
        {
            return "mp:" + wechatMpOpenid;
        }
        for (AudienceSourceRow row : group)
        {
            if (isRegistrationChannel(row.getChannel()) && StringUtils.isNotEmpty(row.getLeadId()))
            {
                return row.getChannel() + ":" + row.getLeadId();
            }
        }
        if (StringUtils.isNotEmpty(wecomExternalUserid))
        {
            return "wecom:" + wecomExternalUserid;
        }
        if (StringUtils.isNotEmpty(leadId))
        {
            return "h5:" + leadId;
        }
        AudienceSourceRow first = group.get(0);
        return first.getChannel() + ":" + first.getSourceId();
    }

    private String pickDisplayName(String current, AudienceSourceRow row, Map<String, String> contactNames)
    {
        if (StringUtils.isNotEmpty(current))
        {
            return current;
        }
        if (isRegistrationChannel(row.getChannel()) && StringUtils.isNotEmpty(row.getDisplayName()))
        {
            return row.getDisplayName();
        }
        if (StringUtils.isNotEmpty(row.getContactId()) && contactNames != null)
        {
            String name = contactNames.get(row.getContactId());
            if (StringUtils.isNotEmpty(name))
            {
                return name;
            }
        }
        if (StringUtils.isNotEmpty(row.getWecomName()))
        {
            return row.getWecomName();
        }
        if (StringUtils.isNotEmpty(parseWecomNameFromJson(row.getRawJson())))
        {
            return parseWecomNameFromJson(row.getRawJson());
        }
        if (StringUtils.isNotEmpty(row.getMpNickname()))
        {
            return row.getMpNickname();
        }
        return row.getDisplayName();
    }

    private String parseWecomNameFromJson(String rawJson)
    {
        if (StringUtils.isEmpty(rawJson))
        {
            return null;
        }
        String trimmed = rawJson.trim();
        if (!trimmed.startsWith("{"))
        {
            return null;
        }
        try
        {
            JSONObject obj = JSONObject.parseObject(rawJson);
            JSONObject external = obj.getJSONObject("external_contact");
            if (external != null)
            {
                return external.getString("name");
            }
        }
        catch (Exception ignored)
        {
            // ignore malformed json
        }
        return null;
    }

    static boolean isRegistrationChannel(String channel)
    {
        return "h5".equals(channel) || "website".equals(channel);
    }

    static String mpMergeKey(AudienceSourceRow row)
    {
        if (row == null || StringUtils.isEmpty(row.getWechatMpOpenid()))
        {
            return null;
        }
        if (StringUtils.isNotEmpty(row.getWechatMpAppId()))
        {
            return row.getWechatMpAppId() + ":" + row.getWechatMpOpenid();
        }
        return row.getWechatMpOpenid();
    }

    private void unionByKey(UnionFind uf, Map<String, Integer> index, String key, int i)
    {
        if (StringUtils.isEmpty(key))
        {
            return;
        }
        if (index.containsKey(key))
        {
            uf.union(i, index.get(key));
        }
        else
        {
            index.put(key, i);
        }
    }

    private String resolveUnionid(AudienceSourceRow row,
            Map<String, String> openidToUnionid,
            Map<String, String> externalUseridToUnionid)
    {
        if (StringUtils.isNotEmpty(row.getWechatMpOpenid()) && openidToUnionid != null)
        {
            return openidToUnionid.get(row.getWechatMpOpenid());
        }
        if (StringUtils.isNotEmpty(row.getWecomExternalUserid()) && externalUseridToUnionid != null)
        {
            return externalUseridToUnionid.get(row.getWecomExternalUserid());
        }
        return null;
    }

    private Set<String> parseChannels(String channels)
    {
        if (StringUtils.isEmpty(channels))
        {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<>();
        for (String part : channels.split(","))
        {
            String trimmed = part.trim().toLowerCase(Locale.ROOT);
            if (!trimmed.isEmpty())
            {
                set.add(trimmed);
            }
        }
        return set;
    }

    private boolean matchesChannels(EventAudienceRowVo row, Set<String> channelFilter)
    {
        if (channelFilter.isEmpty())
        {
            return true;
        }
        if (row.getChannels() == null)
        {
            return false;
        }
        for (String ch : row.getChannels())
        {
            if (channelFilter.contains(ch))
            {
                return true;
            }
        }
        return false;
    }

    private boolean matchesKeyword(EventAudienceRowVo row, String keyword)
    {
        if (StringUtils.isEmpty(keyword))
        {
            return true;
        }
        String lower = keyword.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(row.getDisplayName(), lower)
                || containsIgnoreCase(row.getMobilePhone(), lower)
                || containsIgnoreCase(row.getWechatMpOpenid(), lower)
                || containsIgnoreCase(row.getWecomExternalUserid(), lower);
    }

    private boolean containsIgnoreCase(String value, String lowerKeyword)
    {
        return value != null && value.toLowerCase(Locale.ROOT).contains(lowerKeyword);
    }

    private Date minDate(Date a, Date b)
    {
        if (a == null)
        {
            return b;
        }
        if (b == null)
        {
            return a;
        }
        return a.before(b) ? a : b;
    }

    private Date maxDate(Date a, Date b)
    {
        if (a == null)
        {
            return b;
        }
        if (b == null)
        {
            return a;
        }
        return a.after(b) ? a : b;
    }

    private static final class UnionFind
    {
        private final int[] parent;

        UnionFind(int n)
        {
            parent = new int[n];
            for (int i = 0; i < n; i++)
            {
                parent[i] = i;
            }
        }

        int find(int x)
        {
            if (parent[x] != x)
            {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        void union(int a, int b)
        {
            int ra = find(a);
            int rb = find(b);
            if (ra != rb)
            {
                parent[rb] = ra;
            }
        }
    }
}
