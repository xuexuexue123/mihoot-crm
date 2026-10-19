package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto.WecomSidebarGroupBlockDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto.WecomSidebarGroupConsistencyDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto.WecomSidebarActiveOpportunityDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto.WecomSidebarCorpTagDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto.WecomSidebarCrmBlockDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto.WecomSidebarWecomBlockDto;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;
import com.ruoyi.crm.service.ICrmWecomSidebarResolveService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomExternalContactJsonSupport;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.system.service.ISysDictTypeService;

@Service
public class CrmWecomSidebarResolveServiceImpl implements ICrmWecomSidebarResolveService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomSidebarResolveServiceImpl.class);

    private static final String STATUS_LINKED = "linked_contact";
    private static final String STATUS_UNLINKED = "unlinked";
    private static final String STATUS_FORBIDDEN = "forbidden";
    private static final String DICT_CLOSED_WON_STAGES = "crm_opty_stage_closed_won";

    @Autowired
    private WecomExternalContactProfileResolver profileResolver;

    @Autowired
    private WecomIdentityResolutionSupport identityResolutionSupport;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmOptyMapper crmOptyMapper;

    @Autowired
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private ICrmWecomGroupChatService groupChatService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Value("${crm.dental.closed-won-stages:}")
    private String closedWonStagesFallback;

    @Override
    public WecomSidebarResolveResultDto resolve(String externalUserid)
    {
        if (!StringUtils.hasText(externalUserid))
        {
            throw new ServiceException("externalUserid 不能为空", 400);
        }

        String snUserid = requireCurrentSnUserid();
        String trimmedExternalUserid = externalUserid.trim();

        Optional<WecomExternalContactProfile> profileOpt = profileResolver.fetch(trimmedExternalUserid);
        CrmWecomExternalContact localRow = wecomExternalContactMapper.selectByExternalAndFollow(
                trimmedExternalUserid, snUserid);

        if (!profileOpt.isPresent() && localRow == null)
        {
            throw new ServiceException("企业微信客户详情获取失败", 502);
        }

        if (!isCurrentFollower(snUserid, profileOpt, localRow))
        {
            return buildForbidden(trimmedExternalUserid, profileOpt.orElse(null));
        }

        String unionId = profileOpt.map(WecomExternalContactProfile::getUnionId).orElse(null);
        String contactId = resolveContactId(trimmedExternalUserid, unionId, localRow);

        WecomSidebarWecomBlockDto wecom = buildWecomBlock(
                trimmedExternalUserid, profileOpt.orElse(null), localRow, snUserid);

        WecomSidebarResolveResultDto result = new WecomSidebarResolveResultDto();
        result.setWecom(wecom);

        if (!StringUtils.hasText(contactId))
        {
            result.setCrmStatus(STATUS_UNLINKED);
            return result;
        }

        result.setCrmStatus(STATUS_LINKED);
        result.setContactId(contactId);
        result.setCrm(buildCrmBlock(contactId));
        return result;
    }

    @Override
    public WecomSidebarGroupResolveResultDto resolveGroup(String chatId)
    {
        if (!StringUtils.hasText(chatId))
        {
            throw new ServiceException("chatId 不能为空", 400);
        }
        String trimmedChatId = chatId.trim();
        String tenantId = requireTenant();

        List<CrmWecomContactGroup> bindings = contactGroupMapper.selectListByChatId(tenantId, trimmedChatId);
        if (bindings == null)
        {
            bindings = Collections.emptyList();
        }

        WecomSidebarGroupResolveResultDto result = new WecomSidebarGroupResolveResultDto();
        result.setEntry(WecomSidebarGroupResolveResultDto.ENTRY_GROUP_CHAT_TOOLS);
        result.setChatId(trimmedChatId);

        if (bindings.size() > 1)
        {
            result.setCrmStatus(WecomSidebarGroupResolveResultDto.STATUS_DUPLICATE_BINDING);
            return result;
        }

        if (bindings.isEmpty())
        {
            result.setCrmStatus(WecomSidebarGroupResolveResultDto.STATUS_UNLINKED_GROUP);
            GroupChatSnapshot snapshot = fetchGroupChatSnapshot(trimmedChatId);
            if (snapshot != null && snapshot.detail != null)
            {
                result.setGroup(toGroupBlock(snapshot.detail));
            }
            return result;
        }

        CrmWecomContactGroup binding = bindings.get(0);
        String contactId = binding.getContactId();
        result.setCrmStatus(WecomSidebarGroupResolveResultDto.STATUS_LINKED);
        result.setContactId(contactId);

        CrmContact contact = StringUtils.hasText(contactId)
                ? crmContactMapper.selectCrmContactByContactId(contactId)
                : null;
        String externalUserid = contact != null ? contact.getWecomExternalUserid() : null;

        if (!StringUtils.hasText(externalUserid))
        {
            result.setConsistency(new WecomSidebarGroupConsistencyDto(
                    WecomSidebarGroupResolveResultDto.CONSISTENCY_NO_WECOM_IDENTITY));
            if (StringUtils.hasText(contactId))
            {
                result.setCrm(buildCrmBlock(contactId));
            }
            return result;
        }

        String snUserid = tryCurrentSnUserid();
        Optional<WecomExternalContactProfile> profileOpt = profileResolver.fetch(externalUserid.trim());
        CrmWecomExternalContact localRow = resolveLocalExternalRow(externalUserid.trim(), snUserid);
        result.setWecom(buildWecomBlock(externalUserid.trim(), profileOpt.orElse(null), localRow, snUserid));
        result.setCrm(buildCrmBlock(contactId));

        GroupChatSnapshot snapshot = fetchGroupChatSnapshot(trimmedChatId);
        if (snapshot == null || snapshot.detail == null)
        {
            result.setConsistency(new WecomSidebarGroupConsistencyDto(
                    WecomSidebarGroupResolveResultDto.CONSISTENCY_GROUP_DETAIL_UNAVAILABLE));
            return result;
        }

        result.setGroup(toGroupBlock(snapshot.detail));
        boolean inGroup = containsExternalMember(snapshot.detail, externalUserid.trim());
        result.setConsistency(new WecomSidebarGroupConsistencyDto(inGroup
                ? WecomSidebarGroupResolveResultDto.CONSISTENCY_OK
                : WecomSidebarGroupResolveResultDto.CONSISTENCY_MISMATCH));
        return result;
    }

    private CrmWecomExternalContact resolveLocalExternalRow(String externalUserid, String snUserid)
    {
        if (StringUtils.hasText(snUserid))
        {
            CrmWecomExternalContact byFollow = wecomExternalContactMapper.selectByExternalAndFollow(
                    externalUserid, snUserid);
            if (byFollow != null)
            {
                return byFollow;
            }
        }
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectByExternalUserid(externalUserid);
        if (rows == null || rows.isEmpty())
        {
            return null;
        }
        return rows.get(0);
    }

    private GroupChatSnapshot fetchGroupChatSnapshot(String chatId)
    {
        try
        {
            CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
            if (cfg == null || !StringUtils.hasText(cfg.getCorpId()))
            {
                log.warn("sidebar resolve-group groupchat/get skip: wecom config missing chatId={}", chatId);
                return null;
            }
            String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
            WecomGroupChatDetailResponse resp = groupChatService.getGroupChat(accessToken, chatId, 0);
            if (resp == null)
            {
                log.warn("sidebar resolve-group groupchat/get empty response chatId={}", chatId);
                return null;
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0)
            {
                log.warn("sidebar resolve-group groupchat/get failed chatId={} errcode={} errmsg={}",
                        chatId, resp.getErrcode(), resp.getErrmsg());
                return null;
            }
            if (resp.getGroupChat() == null)
            {
                log.warn("sidebar resolve-group groupchat/get missing group_chat chatId={}", chatId);
                return null;
            }
            GroupChatSnapshot snapshot = new GroupChatSnapshot();
            snapshot.detail = resp.getGroupChat();
            return snapshot;
        }
        catch (Exception e)
        {
            log.warn("sidebar resolve-group groupchat/get exception chatId={}: {}", chatId, e.getMessage());
            return null;
        }
    }

    private static WecomSidebarGroupBlockDto toGroupBlock(
            WecomGroupChatDetailResponse.WecomGroupChatDetail detail)
    {
        WecomSidebarGroupBlockDto group = new WecomSidebarGroupBlockDto();
        group.setName(detail.getName());
        group.setOwnerUserid(detail.getOwner());
        if (detail.getMemberList() != null)
        {
            group.setMemberCount(detail.getMemberList().size());
        }
        return group;
    }

    private static boolean containsExternalMember(
            WecomGroupChatDetailResponse.WecomGroupChatDetail detail, String externalUserid)
    {
        if (detail.getMemberList() == null || !StringUtils.hasText(externalUserid))
        {
            return false;
        }
        for (WecomGroupChatDetailResponse.GroupChatMember member : detail.getMemberList())
        {
            if (member != null && member.getType() != null && member.getType() == 2
                    && externalUserid.equals(member.getUserid()))
            {
                return true;
            }
        }
        return false;
    }

    private static String requireTenant()
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("缺少租户上下文", 503);
        }
        return tenantId.trim();
    }

    private static String tryCurrentSnUserid()
    {
        try
        {
            String snUserid = SecurityUtils.getLoginUser().getUser().getSnUserid();
            return StringUtils.hasText(snUserid) ? snUserid.trim() : null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static final class GroupChatSnapshot
    {
        private WecomGroupChatDetailResponse.WecomGroupChatDetail detail;
    }

    private String resolveContactId(String externalUserid, String unionId, CrmWecomExternalContact localRow)
    {
        String viaIdentity = identityResolutionSupport.resolveContactId(externalUserid, unionId);
        if (StringUtils.hasText(viaIdentity))
        {
            return viaIdentity;
        }
        if (localRow != null && StringUtils.hasText(localRow.getCrmContactId()))
        {
            return localRow.getCrmContactId();
        }
        return null;
    }

    private boolean isCurrentFollower(
            String snUserid,
            Optional<WecomExternalContactProfile> profileOpt,
            CrmWecomExternalContact localRow)
    {
        if (profileOpt.isPresent() && StringUtils.hasText(profileOpt.get().getRawJson()))
        {
            Boolean tencentFollow = hasTencentFollowUser(profileOpt.get().getRawJson(), snUserid);
            if (tencentFollow != null)
            {
                return tencentFollow;
            }
        }
        return localRow != null && snUserid.equals(localRow.getFollowUserid());
    }

    /**
     * @return true/false if follow list is explicit; null if tencent payload has no follow section
     */
    private static Boolean hasTencentFollowUser(String rawJson, String snUserid)
    {
        try
        {
            JSONObject root = JSONObject.parseObject(rawJson);
            if (root == null)
            {
                return null;
            }
            JSONArray followUsers = root.getJSONArray("follow_user");
            if (followUsers != null)
            {
                for (int i = 0; i < followUsers.size(); i++)
                {
                    JSONObject fu = followUsers.getJSONObject(i);
                    if (fu != null && snUserid.equals(fu.getString("userid")))
                    {
                        return true;
                    }
                }
                return false;
            }
            JSONObject followInfo = root.getJSONObject("follow_info");
            if (followInfo != null)
            {
                return snUserid.equals(followInfo.getString("userid"));
            }
        }
        catch (Exception ignored)
        {
            // best-effort
        }
        return null;
    }

    private WecomSidebarResolveResultDto buildForbidden(
            String externalUserid,
            WecomExternalContactProfile profile)
    {
        WecomSidebarResolveResultDto result = new WecomSidebarResolveResultDto();
        result.setCrmStatus(STATUS_FORBIDDEN);
        WecomSidebarWecomBlockDto wecom = new WecomSidebarWecomBlockDto();
        wecom.setExternalUserid(externalUserid);
        result.setWecom(wecom);
        return result;
    }

    private WecomSidebarWecomBlockDto buildWecomBlock(
            String externalUserid,
            WecomExternalContactProfile profile,
            CrmWecomExternalContact localRow,
            String snUserid)
    {
        WecomSidebarWecomBlockDto wecom = new WecomSidebarWecomBlockDto();
        wecom.setExternalUserid(externalUserid);

        if (profile != null)
        {
            wecom.setName(profile.getName());
            wecom.setAvatar(profile.getAvatar());
        }

        WecomFollowUserSnapshot follow = null;
        if (profile != null && StringUtils.hasText(profile.getRawJson()))
        {
            follow = WecomExternalContactJsonSupport.resolveFollowUser(profile.getRawJson(), snUserid);
        }
        if (follow == null && localRow != null && StringUtils.hasText(localRow.getRawJson()))
        {
            follow = WecomExternalContactJsonSupport.resolveFollowUser(localRow.getRawJson(), snUserid);
        }

        if (follow != null)
        {
            wecom.setRemark(follow.getRemark());
            wecom.setCorpTags(parseCorpTags(follow.getTags()));
        }
        else if (localRow != null)
        {
            wecom.setRemark(localRow.getRemark());
            if (StringUtils.hasText(localRow.getTags()))
            {
                try
                {
                    wecom.setCorpTags(parseCorpTags(JSON.parseArray(localRow.getTags())));
                }
                catch (Exception ignored)
                {
                    wecom.setCorpTags(Collections.emptyList());
                }
            }
        }

        if (!StringUtils.hasText(wecom.getName()) && profile == null && localRow != null
                && StringUtils.hasText(localRow.getRawJson()))
        {
            WecomExternalContactProfile parsed = WecomExternalContactProfileResolver.parseProfileFromRaw(
                    localRow.getRawJson(), externalUserid);
            if (parsed != null)
            {
                wecom.setName(parsed.getName());
                wecom.setAvatar(parsed.getAvatar());
            }
        }

        if (wecom.getCorpTags() == null)
        {
            wecom.setCorpTags(Collections.emptyList());
        }
        return wecom;
    }

    private WecomSidebarCrmBlockDto buildCrmBlock(String contactId)
    {
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            return null;
        }

        WecomSidebarCrmBlockDto crm = new WecomSidebarCrmBlockDto();
        crm.setDisplayName(contact.getName());
        crm.setPhone(contact.getMobilePhone());
        crm.setTags(parseContactTags(contact.getTag()));

        CrmOpty activeOpty = crmOptyMapper.selectLatestActiveByContactId(contactId, buildExcludedStages());
        if (activeOpty != null)
        {
            WecomSidebarActiveOpportunityDto opty = new WecomSidebarActiveOpportunityDto();
            opty.setOptyId(activeOpty.getOptyId());
            opty.setName(activeOpty.getName());
            opty.setStage(activeOpty.getStage());
            crm.setActiveOpportunity(opty);
        }
        return crm;
    }

    private List<String> buildExcludedStages()
    {
        Set<String> excluded = new HashSet<>();
        excluded.add("赢单");
        excluded.add("输单");

        List<SysDictData> dict = dictTypeService.selectDictDataByType(DICT_CLOSED_WON_STAGES);
        if (dict != null)
        {
            for (SysDictData d : dict)
            {
                if ("0".equals(d.getStatus()) && StringUtils.hasText(d.getDictValue()))
                {
                    excluded.add(d.getDictValue().trim());
                }
            }
        }
        else if (StringUtils.hasText(closedWonStagesFallback))
        {
            for (String part : closedWonStagesFallback.split(","))
            {
                if (StringUtils.hasText(part))
                {
                    excluded.add(part.trim());
                }
            }
        }
        return new ArrayList<>(excluded);
    }

    private static List<WecomSidebarCorpTagDto> parseCorpTags(JSONArray tags)
    {
        if (tags == null || tags.isEmpty())
        {
            return Collections.emptyList();
        }
        List<WecomSidebarCorpTagDto> result = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++)
        {
            JSONObject tag = tags.getJSONObject(i);
            if (tag == null)
            {
                continue;
            }
            String tagName = tag.getString("tag_name");
            if (!StringUtils.hasText(tagName))
            {
                continue;
            }
            WecomSidebarCorpTagDto dto = new WecomSidebarCorpTagDto();
            dto.setGroupName(tag.getString("group_name"));
            dto.setTagName(tagName.trim());
            result.add(dto);
        }
        return result;
    }

    private static List<String> parseContactTags(String tagJson)
    {
        if (!StringUtils.hasText(tagJson))
        {
            return Collections.emptyList();
        }
        try
        {
            JSONArray arr = JSON.parseArray(tagJson);
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                if (obj == null)
                {
                    continue;
                }
                String text = obj.getString("text");
                if (StringUtils.hasText(text))
                {
                    tags.add(text.trim());
                }
            }
            return tags;
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }

    private static String requireCurrentSnUserid()
    {
        try
        {
            String snUserid = SecurityUtils.getLoginUser().getUser().getSnUserid();
            if (!StringUtils.hasText(snUserid))
            {
                throw new ServiceException("当前用户未绑定企微 userid（sn_userid）");
            }
            return snUserid.trim();
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("无法获取当前登录用户");
        }
    }
}
