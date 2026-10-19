package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactProfile;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactImportResult;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactUpsertRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactUpsertRequest.FollowUserItem;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomContactInfoSyncSupport;
import com.ruoyi.crm.wecom.WecomExternalContactJsonSupport;
import com.ruoyi.crm.wecom.WecomExternalContactProfileResolver;
import com.ruoyi.crm.wecom.WecomFollowUserResolver;
import com.ruoyi.crm.wecom.WecomIdentityResolutionSupport;
import com.ruoyi.crm.wecom.WecomLeadEnrichSupport;
import com.ruoyi.crm.wecom.WecomRawJsonSanitizer;
import com.ruoyi.crm.wecom.WecomStateParseResult;
import com.ruoyi.crm.wecom.WecomStateParser;
import com.ruoyi.crm.wecom.WecomTencentApiClient;
import com.ruoyi.crm.domain.dto.wecom.WecomFollowUserSnapshot;
import com.ruoyi.crm.domain.dto.LeadConvertResult;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmLeadConvertService;
import com.ruoyi.crm.wecom.WecomEventLeadResolveService;
import com.ruoyi.crm.wecom.WecomLeadMergeSupport;

@Service
public class CrmWecomExternalContactServiceImpl implements ICrmWecomExternalContactService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomExternalContactServiceImpl.class);

    private static final int EXTERNAL_USER_STRIPE = 64;

    private static final int MAX_ERRORS = 40;

    /** 同一 JVM 内按 external_userid 分条带锁，降低并发回调双插竞态 */
    private final Object[] externalUserStripes = new Object[EXTERNAL_USER_STRIPE];

    {
        for (int i = 0; i < EXTERNAL_USER_STRIPE; i++)
        {
            externalUserStripes[i] = new Object();
        }
    }

    /** CDATA 或简单文本节点（解密后企微回调 XML） */
    private static final Pattern XML_TAG_VALUE = Pattern.compile(
            "<(\\w+)><!\\[CDATA\\[(.*?)\\]\\]></\\1>|<(\\w+)>([^<]*)</\\3>",
            Pattern.DOTALL);

    @Autowired
    private CrmWecomExternalContactMapper crmWecomExternalContactMapper;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private CrmWecomConfigMapper crmWecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Autowired
    private WecomStateParser wecomStateParser;

    @Autowired
    private WecomExternalContactProfileResolver profileResolver;

    @Autowired
    private WecomIdentityResolutionSupport identityResolutionSupport;

    @Autowired
    private WecomContactInfoSyncSupport contactInfoSyncSupport;

    @Autowired
    private WecomFollowUserResolver wecomFollowUserResolver;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WecomLeadEnrichSupport wecomLeadEnrichSupport;

    @Autowired
    private WecomEventLeadResolveService wecomEventLeadResolveService;

    @Autowired
    private WecomLeadMergeSupport wecomLeadMergeSupport;

    @Autowired
    private ICrmLeadConvertService leadConvertService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    private enum WecomLeadResolution
    {
        NEW_LEAD,
        MERGED_LEAD,
        EXISTING_CONTACT,
        ADDITIONAL_FOLLOW_USER
    }

    @Override
    public List<CrmWecomExternalContact> selectList(CrmWecomExternalContact query)
    {
        return crmWecomExternalContactMapper.selectList(query);
    }

    @Override
    public CrmWecomExternalContact selectById(Long id)
    {
        if (id == null)
        {
            return null;
        }
        return crmWecomExternalContactMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePlainXml(String plainXml)
    {
        if (plainXml == null || plainXml.isEmpty())
        {
            return;
        }
        String event = getCdataOrText(plainXml, "Event");
        if (event == null || !"change_external_contact".equalsIgnoreCase(event.trim()))
        {
            return;
        }
        String changeType = getCdataOrText(plainXml, "ChangeType");
        if (changeType == null)
        {
            return;
        }
        String ct = changeType.trim();
        if (!"add_external_contact".equalsIgnoreCase(ct) && !"add_half_external_contact".equalsIgnoreCase(ct))
        {
            return;
        }
        String externalUserId = firstNonEmpty(
                getCdataOrText(plainXml, "ExternalUserID"),
                getCdataOrText(plainXml, "ExternalUserId"));
        if (externalUserId == null || externalUserId.isEmpty())
        {
            return;
        }
        String userId = getCdataOrText(plainXml, "UserID");
        String state = getCdataOrText(plainXml, "State");

        Optional<WecomExternalContactProfile> profileOpt = profileResolver.fetch(externalUserId);
        String rawJson = profileOpt.map(WecomExternalContactProfile::getRawJson)
                .orElse(snipXml(plainXml));
        String displayName = profileOpt.map(WecomExternalContactProfile::getName).orElse(null);
        String unionId = profileOpt.map(WecomExternalContactProfile::getUnionId).orElse(null);

        String contactId = identityResolutionSupport.resolveContactId(externalUserId, unionId);
        if (contactId != null)
        {
            identityResolutionSupport.bindWecomIdentities(externalUserId, unionId, contactId);
            contactInfoSyncSupport.syncToContact(externalUserId, contactId, rawJson);
        }

        upsertExternalContact(externalUserId, userId, state, rawJson, displayName, "wecom-callback", contactId,
                unionId, profileOpt.orElse(null));
    }

    /**
     * 新建或更新映射；首次出现该 external_userid 时创建 Lead。
     * <p>
     * 支持多对多跟进关系：同一 external_userid 可被多个内部员工跟进，每对 (external_userid, follow_userid)
     * 独立存储一行，记录各自特有的 remark / add_way / tags / description。
     * </p>
     *
     * @param displayName 客户展示名（企微 external_contact.name）；null 时回退 external_userid 截断
     * @return true 若为该 external_userid 首次创建了映射与线索
     */
    private boolean upsertExternalContact(String externalUserid, String followUserid, String state, String rawJson,
            String displayName, String createBy)
    {
        return upsertExternalContact(externalUserid, followUserid, state, rawJson, displayName, createBy, null, null,
                null);
    }

    private boolean upsertExternalContact(String externalUserid, String followUserid, String state, String rawJson,
            String displayName, String createBy, String crmContactId)
    {
        return upsertExternalContact(externalUserid, followUserid, state, rawJson, displayName, createBy, crmContactId,
                null, null);
    }

    private boolean upsertExternalContact(String externalUserid, String followUserid, String state, String rawJson,
            String displayName, String createBy, String crmContactId, String unionId)
    {
        return upsertExternalContact(externalUserid, followUserid, state, rawJson, displayName, createBy, crmContactId,
                unionId, null);
    }

    private boolean upsertExternalContact(String externalUserid, String followUserid, String state, String rawJson,
            String displayName, String createBy, String crmContactId, String unionId,
            WecomExternalContactProfile profile)
    {
        synchronized (stripeLockFor(externalUserid))
        {
            WecomExternalContactProfile effectiveProfile = profile != null
                    ? profile
                    : WecomExternalContactProfileResolver.parseProfileFromRaw(rawJson, externalUserid);
            WecomFollowUserSnapshot follow = WecomExternalContactJsonSupport.resolveFollowUser(rawJson, followUserid);

            CrmWecomExternalContact existing = crmWecomExternalContactMapper
                    .selectByExternalAndFollow(externalUserid, followUserid);
            WecomStateParseResult parsed = wecomStateParser.parse(state);
            String crmEventId = parsed.getCrmEventId();
            String storedRawJson = WecomRawJsonSanitizer.sanitize(rawJson);

            if (existing != null)
            {
                CrmWecomExternalContact upd = new CrmWecomExternalContact();
                upd.setId(existing.getId());
                upd.setState(state);
                if (crmEventId != null)
                {
                    upd.setCrmEventId(crmEventId);
                }
                if (crmContactId != null && !crmContactId.isEmpty())
                {
                    upd.setCrmContactId(crmContactId);
                }
                upd.setRawJson(storedRawJson);
                wecomLeadEnrichSupport.applyFollowToExternalContactRow(upd, follow);
                upd.setUpdateTime(DateUtils.getNowDate());
                crmWecomExternalContactMapper.update(upd);

                // 删除后重加场景：该 (external_userid, follow_userid) 映射已存在，
                // 但回调是 add_external_contact，应为该 Contact 生产 channel.wecom.added
                if (StringUtils.hasText(crmContactId))
                {
                    Date addTime = follow != null && follow.getCreatetime() != null
                            && follow.getCreatetime() > 0
                            ? new Date(follow.getCreatetime() * 1000L)
                            : DateUtils.getNowDate();
                    Integer addWay = follow != null ? follow.getAddWay() : null;
                    activityEventProducer.produceWecomAdded(externalUserid, followUserid, state, parsed,
                            unionId, "Contact", crmContactId, addWay, addTime);
                }
                return false;
            }

            // 判断是否是该 external_userid 的首条记录（决定是否创建 Lead）
            boolean isNewContact = crmWecomExternalContactMapper
                    .selectByExternalUserid(externalUserid).isEmpty();
            // 身份解析是否已命中老 Contact：命中则不再重复建 Lead（P0）
            boolean hasExistingContact = crmContactId != null && !crmContactId.isEmpty();

            String crmLeadId = null;
            WecomLeadResolution leadResolution = null;
            String activityResolution = null;

            if (isNewContact && !hasExistingContact)
            {
                String mobileFromRemark = WecomExternalContactJsonSupport.firstRemarkMobile(follow);
                Optional<CrmLead> existingLead = wecomEventLeadResolveService.resolveInitialLeadForWecomAdd(
                        crmEventId, unionId, externalUserid, mobileFromRemark, null);

                if (existingLead.isPresent())
                {
                    CrmLead matched = existingLead.get();
                    crmLeadId = wecomLeadMergeSupport.mergeWecomIntoExistingLead(matched, effectiveProfile, follow,
                            externalUserid, followUserid, displayName, parsed, mobileFromRemark, createBy);
                    leadResolution = WecomLeadResolution.MERGED_LEAD;
                    activityResolution = "lead_merged";
                    activityEventProducer.produceLeadDuplicated(crmLeadId, "wecomExternalUserid", "wecom");
                }
                else
                {
                    CrmLead lead = new CrmLead();
                    lead.setLeadId(IdUtils.fastUUID());
                    lead.setLeadSource(parsed.getLeadSource() != null ? parsed.getLeadSource() : "wecom");
                    String desc = "state=" + (state != null ? state : "")
                            + ";follow=" + (followUserid != null ? followUserid : "");
                    if (parsed.getChannel() != null && !parsed.getChannel().isEmpty())
                    {
                        desc += ";channel=" + parsed.getChannel();
                    }
                    lead.setDescription(desc);
                    if (crmEventId != null)
                    {
                        lead.setSid(crmEventId);
                    }
                    lead.setCreateTime(DateUtils.getNowDate());
                    lead.setCreateBy(createBy != null ? createBy : "wecom");
                    wecomFollowUserResolver.applyLeadOwnership(lead, followUserid);
                    wecomLeadEnrichSupport.enrichNewLead(lead, effectiveProfile, follow, externalUserid, followUserid,
                            displayName);

                    crmLeadService.insertCrmLead(lead);
                    crmLeadId = lead.getLeadId();
                    leadResolution = WecomLeadResolution.NEW_LEAD;
                    activityResolution = "lead_created";
                }
            }
            else if (isNewContact)
            {
                leadResolution = WecomLeadResolution.EXISTING_CONTACT;
                activityEventProducer.produceWecomContactRetouch(crmContactId, externalUserid,
                        followUserid, state, parsed, unionId);
            }
            else
            {
                leadResolution = WecomLeadResolution.ADDITIONAL_FOLLOW_USER;
                List<CrmWecomExternalContact> existingRows = crmWecomExternalContactMapper
                        .selectByExternalUserid(externalUserid);
                crmLeadId = existingRows.get(0).getCrmLeadId();
            }

            CrmWecomExternalContact row = new CrmWecomExternalContact();
            row.setExternalUserid(externalUserid);
            row.setFollowUserid(followUserid);
            row.setState(state);
            row.setCrmLeadId(crmLeadId);
            if (crmContactId != null && !crmContactId.isEmpty())
            {
                row.setCrmContactId(crmContactId);
            }
            if (crmEventId != null)
            {
                row.setCrmEventId(crmEventId);
            }
            row.setRawJson(storedRawJson);
            wecomLeadEnrichSupport.applyFollowToExternalContactRow(row, follow);
            row.setUpdateTime(DateUtils.getNowDate());
            CrmWecomExternalContact boundRow = crmWecomExternalContactMapper
                    .selectByExternalAndFollow(externalUserid, followUserid);
            if (boundRow != null)
            {
                row.setId(boundRow.getId());
                crmWecomExternalContactMapper.update(row);
            }
            else
            {
                row.setCreateTime(DateUtils.getNowDate());
                crmWecomExternalContactMapper.insert(row);
            }

            String effectiveContactId = crmContactId;
            if (leadResolution == WecomLeadResolution.NEW_LEAD
                    || leadResolution == WecomLeadResolution.MERGED_LEAD)
            {
                if (leadResolution == WecomLeadResolution.NEW_LEAD && StringUtils.hasText(crmLeadId))
                {
                    CrmLead created = crmLeadMapper.selectCrmLeadByLeadId(crmLeadId);
                    activityEventProducer.produceWecomLeadCreated(crmLeadId,
                            created != null ? created.getLeadName() : null,
                            externalUserid, followUserid, state, parsed, crmContactId, unionId);
                }

                LeadConvertResult convertResult = leadConvertService.convertFromWecomCallback(crmLeadId, crmEventId);
                if (convertResult != null && StringUtils.hasText(convertResult.getContactId()))
                {
                    effectiveContactId = convertResult.getContactId();
                    if (row.getId() != null)
                    {
                        CrmWecomExternalContact contactUpd = new CrmWecomExternalContact();
                        contactUpd.setId(row.getId());
                        contactUpd.setCrmContactId(effectiveContactId);
                        contactUpd.setUpdateTime(DateUtils.getNowDate());
                        crmWecomExternalContactMapper.update(contactUpd);
                    }
                }

                emitWecomAddedAfterLeadFlow(externalUserid, followUserid, state, parsed, unionId,
                        effectiveContactId, crmLeadId, follow, activityResolution);
            }
            else if (leadResolution == WecomLeadResolution.EXISTING_CONTACT)
            {
                // 复触仅 produceWecomContactRetouch，不 convert、不重复 channel.wecom.added
            }
            else if (leadResolution == WecomLeadResolution.ADDITIONAL_FOLLOW_USER)
            {
                String subjectContactId = effectiveContactId;
                if (!StringUtils.hasText(subjectContactId) && StringUtils.hasText(crmLeadId))
                {
                    CrmLead linkedLead = crmLeadMapper.selectCrmLeadByLeadId(crmLeadId);
                    if (linkedLead != null && StringUtils.hasText(linkedLead.getContactId()))
                    {
                        subjectContactId = linkedLead.getContactId();
                    }
                }
                emitWecomAddedAfterLeadFlow(externalUserid, followUserid, state, parsed, unionId,
                        subjectContactId, crmLeadId, follow, null);
            }
            return isNewContact;
        }
    }

    private void emitWecomAddedAfterLeadFlow(String externalUserid, String followUserid, String state,
            WecomStateParseResult parsed, String unionId, String contactId, String leadId,
            WecomFollowUserSnapshot follow, String resolution)
    {
        String subjectType;
        String subjectId;
        if (StringUtils.hasText(contactId))
        {
            subjectType = "Contact";
            subjectId = contactId;
        }
        else if (StringUtils.hasText(leadId))
        {
            subjectType = "Lead";
            subjectId = leadId;
        }
        else
        {
            return;
        }
        Date addTime = follow != null && follow.getCreatetime() != null && follow.getCreatetime() > 0
                ? new Date(follow.getCreatetime() * 1000L)
                : DateUtils.getNowDate();
        Integer addWay = follow != null ? follow.getAddWay() : null;
        String mergedLeadId = "lead_merged".equals(resolution) ? leadId : null;
        activityEventProducer.produceWecomAdded(externalUserid, followUserid, state, parsed, unionId,
                subjectType, subjectId, addWay, addTime, resolution, mergedLeadId);
    }

    @Override
    public WecomExternalContactImportResult importFromWecom(List<String> restrictUserids)
    {
        CrmWecomConfig cfg = crmWecomConfigMapper.selectFirst();
        if (cfg == null)
        {
            throw new ServiceException("请先配置企业微信集成");
        }
        String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);

        JSONObject followRes = wecomTencentApiClient.externalContactGetFollowUserList(accessToken);
        int fuErr = followRes.getIntValue("errcode");
        if (fuErr != 0)
        {
            throw new ServiceException("get_follow_user_list 失败 errcode=" + fuErr + " errmsg=" + followRes.getString("errmsg"));
        }

        JSONArray fuArr = followRes.getJSONArray("follow_user");
        List<String> allFollow = new ArrayList<>();
        if (fuArr != null)
        {
            for (int i = 0; i < fuArr.size(); i++)
            {
                String uid = fuArr.getString(i);
                if (uid != null && !uid.isEmpty())
                {
                    allFollow.add(uid);
                }
            }
        }

        List<String> targetUsers = new ArrayList<>();
        WecomExternalContactImportResult result = new WecomExternalContactImportResult();
        if (restrictUserids == null || restrictUserids.isEmpty())
        {
            targetUsers.addAll(allFollow);
        }
        else
        {
            Set<String> allowed = new HashSet<>(allFollow);
            for (String r : restrictUserids)
            {
                if (r == null)
                {
                    continue;
                }
                String t = r.trim();
                if (t.isEmpty())
                {
                    continue;
                }
                if (allowed.contains(t))
                {
                    targetUsers.add(t);
                }
                else
                {
                    addError(result, "userid 不在客户联系成员列表中: " + t);
                }
            }
        }

        if (targetUsers.isEmpty())
        {
            throw new ServiceException("没有可同步的成员（请检查 userid 或客户联系配置）");
        }

        result.setScannedUsers(targetUsers.size());

        String operator = resolveOperatorUsername();

        for (String userid : targetUsers)
        {
            String cursor = "";
            while (true)
            {
                JSONObject body = new JSONObject();
                JSONArray uidList = new JSONArray();
                uidList.add(userid);
                body.put("userid_list", uidList);
                body.put("cursor", cursor != null ? cursor : "");
                body.put("limit", 100);

                JSONObject batchRes = wecomTencentApiClient.externalContactBatchGetByUser(accessToken, body);
                int err = batchRes.getIntValue("errcode");
                if (err != 0)
                {
                    addError(result, "batch/get_by_user userid=" + userid + " errcode=" + err + " errmsg=" + batchRes.getString("errmsg"));
                    break;
                }

                JSONObject failInfo = batchRes.getJSONObject("fail_info");
                if (failInfo != null)
                {
                    JSONArray unlicensed = failInfo.getJSONArray("unlicensed_userid_list");
                    if (unlicensed != null && !unlicensed.isEmpty())
                    {
                        addError(result, "无互通许可的成员: " + unlicensed.toString());
                    }
                }

                JSONArray extList = batchRes.getJSONArray("external_contact_list");
                if (extList != null)
                {
                    for (int i = 0; i < extList.size(); i++)
                    {
                        JSONObject pair = extList.getJSONObject(i);
                        if (pair == null)
                        {
                            continue;
                        }
                        JSONObject ec = pair.getJSONObject("external_contact");
                        JSONObject fi = pair.getJSONObject("follow_info");
                        if (ec == null)
                        {
                            continue;
                        }
                        String extId = ec.getString("external_userid");
                        if (extId == null || extId.isEmpty())
                        {
                            continue;
                        }
                        String name = ec.getString("name");
                        String followUid = fi != null ? fi.getString("userid") : userid;
                        String stateVal = fi != null ? fi.getString("state") : null;
                        String raw = snipJson(pair);

                        WecomExternalContactProfile importProfile = WecomExternalContactProfileResolver
                                .parseProfileFromRaw(raw, extId);
                        String unionId = importProfile != null ? importProfile.getUnionId() : null;
                        String contactId = identityResolutionSupport.resolveContactId(extId, unionId);
                        if (contactId != null && !contactId.isEmpty())
                        {
                            identityResolutionSupport.bindWecomIdentities(extId, unionId, contactId);
                            contactInfoSyncSupport.syncToContact(extId, contactId, raw);
                        }

                        boolean created = upsertExternalContact(extId, followUid, stateVal, raw, name, operator,
                                contactId, unionId, importProfile);
                        result.setProcessedRecords(result.getProcessedRecords() + 1);
                        if (created)
                        {
                            result.setCreatedLeads(result.getCreatedLeads() + 1);
                        }
                        else
                        {
                            result.setUpdatedRows(result.getUpdatedRows() + 1);
                        }
                    }
                }

                String next = batchRes.getString("next_cursor");
                if (next == null || next.isEmpty())
                {
                    break;
                }
                cursor = next;
                sleepThrottle();
            }
            sleepThrottle();
        }

        return result;
    }

    @Override
    public Long upsertFromKfGetInfo(WecomExternalContactUpsertRequest request)
    {
        CrmWecomExternalContact row = new CrmWecomExternalContact();
        row.setExternalUserid(request.getExternalUserid());
        row.setFollowUserid(request.getFollowUserid());
        row.setRawJson(request.getRawJson());

        List<FollowUserItem> followUser = request.getFollowUser();
        if (followUser != null && !followUser.isEmpty())
        {
            FollowUserItem first = followUser.get(0);
            row.setRemark(first.getRemark());
            row.setAddWay(first.getAddWay());
            row.setDescription(first.getDescription());
            row.setOperUserid(first.getOperUserid());
            if (first.getTags() != null)
            {
                row.setTags(JSONObject.toJSONString(first.getTags()));
            }
            if (first.getCreatetime() != null)
            {
                row.setFollowCreateTime(new java.util.Date(first.getCreatetime() * 1000L));
            }
            if (first.getRemarkMobiles() != null)
            {
                row.setRemarkMobiles(JSON.toJSONString(first.getRemarkMobiles()));
            }
        }

        // P1-3: KF 场景自动身份解析。命中已知 Contact 则自动回写 crm_contact_id + 绑定身份 + 同步展示字段，
        //       未命中则维持现状（留待人工 /link 兜底）。
        String externalUserid = request.getExternalUserid();
        String resolvedContactId = null;
        if (externalUserid != null && !externalUserid.isEmpty())
        {
            String unionId = extractUnionIdFromRawJson(request.getRawJson());
            resolvedContactId = identityResolutionSupport.resolveContactId(externalUserid, unionId);
            if (resolvedContactId != null && !resolvedContactId.isEmpty())
            {
                identityResolutionSupport.bindWecomIdentities(externalUserid, unionId, resolvedContactId);
                row.setCrmContactId(resolvedContactId);
            }
        }

        // 不设置 crmLeadId / crmEventId / state，由后续关联管理 API 设置
        crmWecomExternalContactMapper.upsertKfInfo(row);

        if (resolvedContactId != null && !resolvedContactId.isEmpty())
        {
            contactInfoSyncSupport.syncToContact(externalUserid, resolvedContactId, request.getRawJson());
        }
        return row.getId();
    }

    @Override
    public void upsertFromCrmBinding(String externalUserid, String followUserid,
            String contactId, String leadId)
    {
        if (externalUserid == null || externalUserid.isEmpty())
        {
            return;
        }

        String effectiveFollow = followUserid;
        if (effectiveFollow == null || effectiveFollow.isEmpty())
        {
            try
            {
                effectiveFollow = SecurityUtils.getLoginUser().getUser().getSnUserid();
            }
            catch (Exception ignored)
            {
                // 无登录态（回调等）时保持为空
            }
        }
        if (effectiveFollow == null || effectiveFollow.isEmpty())
        {
            log.warn("upsertFromCrmBinding skipped: no follow_userid for externalUserid={}", externalUserid);
            return;
        }

        CrmWecomExternalContact existing = crmWecomExternalContactMapper
                .selectByExternalAndFollow(externalUserid, effectiveFollow);
        if (existing != null)
        {
            CrmWecomExternalContact upd = new CrmWecomExternalContact();
            upd.setId(existing.getId());
            if (contactId != null && !contactId.isEmpty())
            {
                upd.setCrmContactId(contactId);
            }
            if (leadId != null && !leadId.isEmpty())
            {
                upd.setCrmLeadId(leadId);
            }
            upd.setUpdateTime(DateUtils.getNowDate());
            crmWecomExternalContactMapper.update(upd);
            return;
        }

        CrmWecomExternalContact row = new CrmWecomExternalContact();
        row.setExternalUserid(externalUserid);
        row.setFollowUserid(effectiveFollow);
        row.setCrmContactId(contactId);
        row.setCrmLeadId(leadId);
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateTime(DateUtils.getNowDate());
        crmWecomExternalContactMapper.insert(row);
    }

    /**
     * 从 KF/externalcontact raw_json 中尽力提取 unionid（兼容 external_contact.unionid 与顶层 unionid）。
     */
    private static String extractUnionIdFromRawJson(String rawJson)
    {
        if (rawJson == null || rawJson.isEmpty())
        {
            return null;
        }
        try
        {
            JSONObject root = JSONObject.parseObject(rawJson);
            if (root == null)
            {
                return null;
            }
            JSONObject extContact = root.getJSONObject("external_contact");
            if (extContact != null && extContact.getString("unionid") != null)
            {
                return extContact.getString("unionid");
            }
            return root.getString("unionid");
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static String resolveOperatorUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "wecom-import";
        }
    }

    private static void addError(WecomExternalContactImportResult result, String msg)
    {
        List<String> errs = result.getErrors();
        if (errs.size() >= MAX_ERRORS)
        {
            return;
        }
        errs.add(msg);
    }

    private static void sleepThrottle()
    {
        try
        {
            Thread.sleep(100L);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private static String snipJson(JSONObject o)
    {
        if (o == null)
        {
            return null;
        }
        String s = o.toJSONString();
        int max = 8000;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private Object stripeLockFor(String externalUserId)
    {
        int h = externalUserId != null ? externalUserId.hashCode() : 0;
        return externalUserStripes[(h & Integer.MAX_VALUE) % EXTERNAL_USER_STRIPE];
    }

    private static String firstNonEmpty(String a, String b)
    {
        if (a != null && !a.isEmpty())
        {
            return a;
        }
        return b;
    }

    private static String getCdataOrText(String xml, String tagName)
    {
        if (xml == null)
        {
            return null;
        }
        Matcher m = XML_TAG_VALUE.matcher(xml);
        while (m.find())
        {
            String t1 = m.group(1);
            String t3 = m.group(3);
            if (tagName.equals(t1))
            {
                return m.group(2);
            }
            if (tagName.equals(t3))
            {
                return m.group(4);
            }
        }
        return null;
    }

    private static String snipXml(String xml)
    {
        if (xml == null)
        {
            return null;
        }
        int max = 8000;
        return xml.length() <= max ? xml : xml.substring(0, max);
    }
}
