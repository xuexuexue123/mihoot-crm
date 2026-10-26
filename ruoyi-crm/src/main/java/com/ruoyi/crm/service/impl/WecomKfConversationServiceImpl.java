package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomKfConfig;
import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.CrmWecomKfMessage;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.dto.WecomKfConversationQuery;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomKfConversationMapper;
import com.ruoyi.crm.mapper.CrmWecomKfMessageMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.IInboxAgentChannelService;
import com.ruoyi.crm.service.IWecomKfConversationService;
import com.ruoyi.crm.service.IWecomKfMessageService;
import com.ruoyi.crm.service.InboxChannelGateService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.domain.enums.InboxChannel;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.crm.wecom.WecomTencentApiClient;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxEventPublisher;
import com.ruoyi.crm.wecom.realtime.WecomKfInboxSsePayloads;
import com.ruoyi.framework.aspectj.DataScopeAspect;
import com.ruoyi.framework.web.service.PermissionService;

@Service
public class WecomKfConversationServiceImpl implements IWecomKfConversationService
{
    private static final String PERM_CONVERSATIONS = "crm:wecom_kf:conversations";
    private static final String PERM_ASSIGN = "crm:wecom_kf:assign";
    private static final String IDENTITY_TYPE = "wechat_kf_openid";

    @Autowired
    private CrmWecomKfConversationMapper conversationMapper;

    @Autowired
    private CrmWecomKfMessageMapper messageMapper;

    @Autowired
    private CrmWecomKfConfigMapper kfConfigMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private WecomAccessTokenService accessTokenService;

    @Autowired
    private WecomTencentApiClient apiClient;

    @Autowired
    private IWecomKfMessageService wecomKfMessageService;

    @Autowired
    private WecomKfInboxEventPublisher inboxEventPublisher;

    @Autowired
    private InboxChannelGateService inboxChannelGateService;

    @Autowired
    @Lazy
    private IWecomKfConversationService self;

    @Autowired(required = false)
    private com.ruoyi.crm.service.support.ContactServiceSummaryHooks contactServiceSummaryHooks;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmWecomKfConversation> selectConversationList(WecomKfConversationQuery query)
    {
        if (query == null)
        {
            query = new WecomKfConversationQuery();
        }
        prepareConversationListQuery(query);
        return conversationMapper.selectPage(query);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmWecomKfConversation> selectConversationList(WecomKfConversationQuery query, int pageNum,
            int pageSize)
    {
        if (query == null)
        {
            query = new WecomKfConversationQuery();
        }
        prepareConversationListQuery(query);
        int pn = pageNum > 0 ? pageNum : 1;
        int ps = pageSize > 0 ? pageSize : 50;
        PageHelper.startPage(pn, ps);
        return conversationMapper.selectPage(query);
    }

    private void prepareConversationListQuery(WecomKfConversationQuery query)
    {
        normalizeScope(query);
        if ("team".equals(query.getScope()))
        {
            assertHasAssignPerm();
            assertHasTeamDataScope();
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        if ("mine".equals(query.getScope()))
        {
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        // 含 selectFirst(LIMIT 1)，调用方须保证此时尚未 PageHelper.startPage
        resolveOpenKfidFilter(query);
    }

    @Override
    public Map<String, Object> toApiMap(CrmWecomKfConversation conv)
    {
        Map<String, Object> item = new HashMap<>();
        if (conv == null)
        {
            return item;
        }
        item.put("id", conv.getId());
        item.put("corpId", conv.getCorpId());
        item.put("openKfid", conv.getOpenKfid());
        item.put("externalUserid", conv.getExternalUserid());
        item.put("nickname", conv.getNickname());
        item.put("avatarUrl", conv.getAvatarUrl());
        item.put("accountName", conv.getAccountName());
        item.put("lastMessage", conv.getLastMessage());
        item.put("lastMessageAt", conv.getLastMessageAt());
        item.put("messageCount", conv.getMessageCount() != null ? conv.getMessageCount() : 0);
        item.put("unreadCount", conv.getUnreadCount() != null ? conv.getUnreadCount() : 0);
        item.put("canReply", conv.getCanReply() == null || conv.getCanReply() == 1);
        item.put("userId", conv.getUserId() != null ? String.valueOf(conv.getUserId()) : null);
        item.put("owner", conv.getOwner());
        item.put("assignedAt", conv.getAssignedAt());
        item.put("hostedByAi", conv.getHostedByAi());
        item.put("contactId", conv.getContactId());
        item.put("contactName", conv.getContactName());
        return item;
    }

    @Override
    public Map<String, Object> getConversationDetail(Long conversationId, Integer pageNum, Integer pageSize)
    {
        CrmWecomKfConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null)
        {
            return null;
        }
        int pn = pageNum != null && pageNum > 0 ? pageNum : 1;
        int ps = pageSize != null && pageSize > 0 ? pageSize : 50;
        PageHelper.startPage(pn, ps);
        List<CrmWecomKfMessage> messages = messageMapper.selectByConversation(
                conversation.getOpenKfid(), conversation.getExternalUserid());
        PageInfo<CrmWecomKfMessage> pageInfo = new PageInfo<>(messages);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmWecomKfMessage m : messages)
        {
            rows.add(toMessageApiMap(m));
        }

        Map<String, Object> messagesWrap = new HashMap<>();
        messagesWrap.put("code", 200);
        messagesWrap.put("total", pageInfo.getTotal());
        messagesWrap.put("rows", rows);

        Map<String, Object> result = new HashMap<>();
        result.put("conversation", toApiMap(conversation));
        result.put("messages", messagesWrap);
        return result;
    }

    @Override
    public Map<String, Object> markRead(Long conversationId)
    {
        CrmWecomKfConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null)
        {
            return null;
        }
        conversationMapper.clearUnread(conversationId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("unreadCount", 0);
        publishConversationUpdated(conversation.getOpenKfid(),
                WecomKfInboxSsePayloads.conversationPartialFrom(conversationId, conversation.getOpenKfid(),
                        0, conversation.getUserId(), conversation.getOwner(), conversation.getAssignedAt()));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claim(Long conversationId)
    {
        CrmWecomKfConversation conv = lockAndGet(conversationId);
        if (conv.getUserId() != null)
        {
            throw new ServiceException("会话已被他人领取", HttpStatus.CONFLICT);
        }

        Long currentUserId = SecurityUtils.getUserId();
        AssignableResolveResult resolve = assignableUserService.resolve(null, currentUserId, "lead_claim");
        if (!resolve.isResolved())
        {
            throw new ServiceException("当前用户不可领取会话", HttpStatus.FORBIDDEN);
        }

        LeadAssignPatch patch = resolve.getLeadAssignPatch();
        Date assignedAt = new Date();
        conversationMapper.updateAssignment(conversationId, patch.getUserId(), patch.getOwner(), assignedAt);
        conversationMapper.updateHostedByAi(conversationId, Boolean.FALSE);
        return assignmentResponse(conv, patch.getUserId(), patch.getOwner(), assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> release(Long conversationId)
    {
        CrmWecomKfConversation conv = lockAndGet(conversationId);
        if (conv.getUserId() == null)
        {
            throw new ServiceException("会话已在公共池", HttpStatus.BAD_REQUEST);
        }

        Long currentUserId = SecurityUtils.getUserId();
        boolean isOwner = conv.getUserId().equals(currentUserId);
        if (isOwner)
        {
            if (!permissionService.hasPermi(PERM_CONVERSATIONS) && !permissionService.hasPermi(PERM_ASSIGN))
            {
                throw new ServiceException("无释放权限", HttpStatus.FORBIDDEN);
            }
        }
        else
        {
            if (!permissionService.hasPermi(PERM_ASSIGN))
            {
                throw new ServiceException("无强制释放权限", HttpStatus.FORBIDDEN);
            }
            if (!isOwnedConversationInTeamScope(conversationId))
            {
                throw new ServiceException("无权释放该会话", HttpStatus.FORBIDDEN);
            }
        }

        conversationMapper.clearAssignment(conversationId);
        return assignmentResponse(conv, null, null, null, conv.getHostedByAi());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> assign(Long conversationId, Long targetUserId)
    {
        if (targetUserId == null)
        {
            throw new ServiceException("userId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWecomKfConversation conv = lockAndGet(conversationId);

        if (conv.getUserId() != null)
        {
            Long currentUserId = SecurityUtils.getUserId();
            boolean isOwner = conv.getUserId().equals(currentUserId);
            if (!isOwner && !isOwnedConversationInTeamScope(conversationId))
            {
                throw new ServiceException("无权转派该会话", HttpStatus.FORBIDDEN);
            }
        }

        AssignableResolveResult resolve = assignableUserService.resolve(null, targetUserId, "lead_assign");
        if (!resolve.isResolved())
        {
            throw new ServiceException("目标用户不可分配: " + resolve.getReason(), HttpStatus.BAD_REQUEST);
        }

        LeadAssignPatch patch = resolve.getLeadAssignPatch();
        Date assignedAt = new Date();
        conversationMapper.updateAssignment(conversationId, patch.getUserId(), patch.getOwner(), assignedAt);
        conversationMapper.updateHostedByAi(conversationId, Boolean.FALSE);
        return assignmentResponse(conv, patch.getUserId(), patch.getOwner(), assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateHostedByAi(Long conversationId, Boolean hostedByAi)
    {
        assertChannelEnabled();
        CrmWecomKfConversation conv = lockAndGet(conversationId);
        conversationMapper.updateHostedByAi(conversationId, hostedByAi);

        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("userId", conv.getUserId() != null ? String.valueOf(conv.getUserId()) : null);
        data.put("owner", conv.getOwner());
        data.put("assignedAt", conv.getAssignedAt());
        data.put("hostedByAi", hostedByAi);
        data.put("openKfid", conv.getOpenKfid());
        publishConversationUpdated(conv.getOpenKfid(),
                WecomKfInboxSsePayloads.conversationPartialFrom(conversationId, conv.getOpenKfid(),
                        conv.getUnreadCount(), conv.getUserId(), conv.getOwner(), conv.getAssignedAt(),
                        hostedByAi));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claimForAiBot(Long conversationId, Long botUserId, String botOwnerName)
    {
        if (botUserId == null)
        {
            throw new ServiceException("botUserId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWecomKfConversation conv = lockAndGet(conversationId);
        if (conv.getUserId() != null)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("id", conversationId);
            data.put("userId", String.valueOf(conv.getUserId()));
            data.put("owner", conv.getOwner());
            data.put("assignedAt", conv.getAssignedAt());
            data.put("hostedByAi", conv.getHostedByAi());
            data.put("alreadyOwned", true);
            data.put("openKfid", conv.getOpenKfid());
            return data;
        }
        Date assignedAt = new Date();
        String owner = StringUtils.hasText(botOwnerName) ? botOwnerName.trim() : "Inbox AI";
        conversationMapper.updateAssignment(conversationId, botUserId, owner, assignedAt);
        // 不改 hostedByAi：缺省跟随渠道；若已是 true/null 保持
        return assignmentResponse(conv, botUserId, owner, assignedAt, conv.getHostedByAi());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendText(Long conversationId, String content)
    {
        return send(conversationId, "text", content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> send(Long conversationId, String msgType, String content)
    {
        return doSend(conversationId, msgType, content, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> sendTextAsAi(Long conversationId, String content)
    {
        return doSend(conversationId, "text", content, true);
    }

    private Map<String, Object> doSend(Long conversationId, String msgType, String content, boolean asAi)
    {
        String type = StringUtils.hasText(msgType) ? msgType.trim().toLowerCase() : "text";
        if (!"text".equals(type) && !"image".equals(type) && !"file".equals(type))
        {
            throw new ServiceException("msgType 仅支持 text/image/file", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(content))
        {
            throw new ServiceException("content 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWecomKfConversation conv = conversationMapper.selectById(conversationId);
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        if (asAi)
        {
            if (Boolean.FALSE.equals(conv.getHostedByAi()))
            {
                throw new ServiceException("Conversation taken over by human", HttpStatus.CONFLICT);
            }
            if (conv.getCanReply() != null && conv.getCanReply() == 0)
            {
                throw new ServiceException("WECOM_KF_CANNOT_REPLY: canReply=false", HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            assertOwnedByCurrentUser(conv);
        }

        CrmWecomConfig wecomCfg = wecomConfigMapper.selectByCorpId(conv.getCorpId());
        if (wecomCfg == null)
        {
            wecomCfg = wecomConfigMapper.selectFirst();
        }
        if (wecomCfg == null)
        {
            throw new ServiceException("未找到企业微信配置", HttpStatus.BAD_REQUEST);
        }

        String mediaId = null;
        String preview;
        JSONObject body = new JSONObject();
        body.put("touser", conv.getExternalUserid());
        body.put("open_kfid", conv.getOpenKfid());
        body.put("msgtype", type);
        if ("text".equals(type))
        {
            JSONObject text = new JSONObject();
            text.put("content", content.trim());
            body.put("text", text);
            preview = content.trim().length() > 200 ? content.trim().substring(0, 200) : content.trim();
        }
        else
        {
            mediaId = resolveMediaIdFromContent(content);
            if (!StringUtils.hasText(mediaId))
            {
                throw new ServiceException("mediaId 不能为空", HttpStatus.BAD_REQUEST);
            }
            JSONObject media = new JSONObject();
            media.put("media_id", mediaId.trim());
            body.put(type, media);
            preview = "image".equals(type) ? "[图片]" : "[文件]";
        }

        String accessToken = accessTokenService.getAgentAccessToken(wecomCfg);
        JSONObject res = apiClient.kfSendMsg(accessToken, body);
        int errcode = res.getIntValue("errcode");
        if (errcode != 0)
        {
            String errmsg = res.getString("errmsg");
            publishMessageStatusFailed(conv, null, String.valueOf(errcode), errmsg);
            throw new ServiceException("WECOM_KF_CANNOT_REPLY: errcode=" + errcode
                    + (errmsg != null ? (" " + errmsg) : ""), HttpStatus.BAD_REQUEST);
        }

        String msgid = res.getString("msgid");
        if (!StringUtils.hasText(msgid))
        {
            msgid = "crm-out-" + UUID.randomUUID().toString().replace("-", "");
        }

        Date now = new Date();
        CrmWecomKfMessage record = new CrmWecomKfMessage();
        record.setCorpId(conv.getCorpId());
        record.setOpenKfid(conv.getOpenKfid());
        record.setExternalUserid(conv.getExternalUserid());
        record.setMsgid(msgid);
        record.setMsgtype(type);
        record.setContent("text".equals(type) ? content.trim() : preview);
        record.setDirection(2);
        record.setSendTime(now.getTime() / 1000L);
        record.setConversationId(conv.getId());
        record.setMediaId(mediaId);
        record.setRawJson(res.toJSONString());
        record.setCreatedAt(now);
        if (asAi)
        {
            record.setSource(IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT);
        }
        wecomKfMessageService.insertMessage(record);

        upsertOnOutbound(conv.getCorpId(), conv.getOpenKfid(), conv.getExternalUserid(), preview, now);

        CrmWecomKfConversation saved = conversationMapper.selectById(conv.getId());
        publishOutboundEvents(record, saved != null ? saved : conv, true);

        Map<String, Object> data = new HashMap<>();
        data.put("msgid", msgid);
        data.put("conversationId", conv.getId());
        data.put("msgType", type);
        data.put("content", record.getContent());
        data.put("mediaId", mediaId);
        data.put("direction", 2);
        data.put("sendTime", record.getSendTime());
        if (asAi)
        {
            data.put("source", IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT);
            data.put("senderType", "ai");
        }
        if (StringUtils.hasText(mediaId))
        {
            data.put("mediaUrl", "/crm/wecom/kf/messages/media?openKfid=" + conv.getOpenKfid()
                    + "&mediaId=" + mediaId);
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> linkContact(String externalUserid, String contactId, String openKfid,
            boolean setPrimary)
    {
        if (!StringUtils.hasText(externalUserid) || !StringUtils.hasText(contactId))
        {
            throw new ServiceException("externalUserid、contactId 不能为空", HttpStatus.BAD_REQUEST);
        }
        String ext = externalUserid.trim();
        String cid = contactId.trim();

        String existing = identityService.findContactByIdentity(IDENTITY_TYPE, ext);
        if (existing != null && !existing.equals(cid))
        {
            throw new ServiceException("wechat_kf_openid 已绑定其他联系人", HttpStatus.CONFLICT);
        }
        if (existing == null)
        {
            identityService.bindIdentity(cid, IDENTITY_TYPE, ext, "wecom_kf_inbox");
        }

        String contactName = null;
        try
        {
            CrmContact contact = contactMapper.selectCrmContactByContactId(cid);
            if (contact != null)
            {
                contactName = contact.getName();
            }
        }
        catch (Exception ignored)
        {
        }

        if (StringUtils.hasText(openKfid))
        {
            conversationMapper.updateContact(openKfid.trim(), ext, cid, contactName);
            CrmWecomKfConversation conv = conversationMapper.selectByOpenKfidExternalUserid(openKfid.trim(), ext);
            if (conv != null)
            {
                Map<String, Object> api = toApiMap(conv);
                publishConversationUpdated(conv.getOpenKfid(), WecomKfInboxSsePayloads.toConversationPartial(conv));
                if (contactServiceSummaryHooks != null)
                {
                    contactServiceSummaryHooks.onContactLinked(
                            TenantDataSourceSupport.currentTenantKey(), cid, "wecom_kf", ext);
                }
                return api;
            }
        }
        else
        {
            conversationMapper.updateContactByExternalUserid(ext, cid, contactName);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("externalUserid", ext);
        data.put("contactId", cid);
        data.put("contactName", contactName);
        data.put("openKfid", openKfid);
        if (contactServiceSummaryHooks != null)
        {
            contactServiceSummaryHooks.onContactLinked(
                    TenantDataSourceSupport.currentTenantKey(), cid, "wecom_kf", ext);
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unlinkContact(String externalUserid, String openKfid)
    {
        if (!StringUtils.hasText(externalUserid))
        {
            throw new ServiceException("externalUserid 不能为空", HttpStatus.BAD_REQUEST);
        }
        String ext = externalUserid.trim();
        String existing = identityService.findContactByIdentity(IDENTITY_TYPE, ext);
        if (existing != null)
        {
            identityService.unbindIdentity(existing, IDENTITY_TYPE, ext);
        }
        if (StringUtils.hasText(openKfid))
        {
            conversationMapper.clearContact(openKfid.trim(), ext);
        }
        else
        {
            conversationMapper.clearContactByExternalUserid(ext);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("externalUserid", ext);
        data.put("openKfid", openKfid);
        return data;
    }

    @Override
    public void assertOwnedByCurrentUser(CrmWecomKfConversation conv)
    {
        if (conv == null)
        {
            throw new ServiceException("会话不存在，请先从公共池领取", HttpStatus.FORBIDDEN);
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (conv.getUserId() == null || !conv.getUserId().equals(currentUserId))
        {
            throw new ServiceException("仅会话负责人可发送消息", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public int countInTeamScope(WecomKfConversationQuery query)
    {
        if (query == null)
        {
            return 0;
        }
        query.setScope("team");
        if (query.getCurrentUserId() == null)
        {
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        return conversationMapper.countInScope(query);
    }

    @Override
    public void upsertOnOutbound(String corpId, String openKfid, String externalUserid, String preview, Date at)
    {
        CrmWecomKfConversation row = new CrmWecomKfConversation();
        row.setCorpId(corpId);
        row.setOpenKfid(openKfid);
        row.setExternalUserid(externalUserid);
        row.setLastMessage(preview);
        row.setLastMessageAt(at != null ? at : new Date());
        conversationMapper.upsertOnOutbound(row);
    }

    private static Map<String, Object> toMessageApiMap(CrmWecomKfMessage m)
    {
        Map<String, Object> item = new HashMap<>();
        if (m == null)
        {
            return item;
        }
        item.put("id", m.getId());
        item.put("msgid", m.getMsgid());
        item.put("msgType", m.getMsgtype());
        item.put("content", m.getContent());
        item.put("direction", m.getDirection());
        item.put("sendTime", m.getSendTime());
        item.put("createTime", m.getCreatedAt());
        item.put("openKfid", m.getOpenKfid());
        item.put("externalUserid", m.getExternalUserid());
        item.put("conversationId", m.getConversationId());
        item.put("mediaId", m.getMediaId());
        item.put("picUrl", m.getPicUrl());
        if (StringUtils.hasText(m.getSource()))
        {
            item.put("source", m.getSource());
            if (IInboxAgentChannelService.SOURCE_AI_INBOX_AGENT.equals(m.getSource()))
            {
                item.put("senderType", "ai");
            }
        }
        if (m.getMediaId() != null && !m.getMediaId().isEmpty()
                && m.getOpenKfid() != null && !m.getOpenKfid().isEmpty())
        {
            item.put("mediaUrl", "/crm/wecom/kf/messages/media?openKfid=" + m.getOpenKfid()
                    + "&mediaId=" + m.getMediaId());
        }
        return item;
    }

    private boolean isOwnedConversationInTeamScope(Long conversationId)
    {
        WecomKfConversationQuery query = new WecomKfConversationQuery();
        query.setScope("team");
        query.setConversationId(conversationId);
        query.setCurrentUserId(SecurityUtils.getUserId());
        return self.countInTeamScope(query) > 0;
    }

    private CrmWecomKfConversation lockAndGet(Long conversationId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWecomKfConversation conv = conversationMapper.selectByIdForUpdate(conversationId);
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        return conv;
    }

    private Map<String, Object> assignmentResponse(CrmWecomKfConversation conv, Long userId, String owner,
            Date assignedAt, Boolean hostedByAi)
    {
        Long conversationId = conv.getId();
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("userId", userId != null ? String.valueOf(userId) : null);
        data.put("owner", owner);
        data.put("assignedAt", assignedAt);
        data.put("hostedByAi", hostedByAi);
        data.put("openKfid", conv.getOpenKfid());
        publishConversationUpdated(conv.getOpenKfid(),
                WecomKfInboxSsePayloads.conversationPartialFrom(conversationId, conv.getOpenKfid(),
                        null, userId, owner, assignedAt, hostedByAi));
        return data;
    }

    private void assertChannelEnabled()
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId)
                || !inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECOM_KF))
        {
            throw new ServiceException("inbox_channel_disabled", HttpStatus.FORBIDDEN);
        }
    }

    private void publishOutboundEvents(CrmWecomKfMessage record, CrmWecomKfConversation conv, boolean sentStatus)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId)
                || !inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECOM_KF))
        {
            return;
        }
        inboxEventPublisher.publishMessageCreated(tenantId,
                WecomKfInboxSsePayloads.messageCreated(record, conv));
        if (conv != null)
        {
            inboxEventPublisher.publishConversationUpdated(tenantId,
                    WecomKfInboxSsePayloads.conversationUpdated(conv));
        }
        if (sentStatus)
        {
            inboxEventPublisher.publishMessageStatus(tenantId,
                    WecomKfInboxSsePayloads.messageStatus(conv != null ? conv.getOpenKfid() : record.getOpenKfid(),
                            conv != null ? conv.getId() : record.getConversationId(),
                            record.getMsgid(), "sent", null, null));
        }
    }

    private void publishMessageStatusFailed(CrmWecomKfConversation conv, String messageId, String errorCode,
            String errorMessage)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId)
                || !inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECOM_KF))
        {
            return;
        }
        inboxEventPublisher.publishMessageStatus(tenantId,
                WecomKfInboxSsePayloads.messageStatus(conv.getOpenKfid(), conv.getId(), messageId,
                        "failed", errorCode, errorMessage));
    }

    private void publishConversationUpdated(String openKfid, Map<String, Object> partial)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId)
                || !inboxChannelGateService.isEnabled(tenantId, InboxChannel.WECOM_KF))
        {
            return;
        }
        inboxEventPublisher.publishConversationUpdated(tenantId,
                WecomKfInboxSsePayloads.conversationUpdated(openKfid, partial));
    }

    static String resolveMediaIdFromContent(String content)
    {
        if (!StringUtils.hasText(content))
        {
            return null;
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("{"))
        {
            try
            {
                JSONObject obj = JSON.parseObject(trimmed);
                if (obj != null)
                {
                    String mid = obj.getString("media_id");
                    if (!StringUtils.hasText(mid))
                    {
                        mid = obj.getString("mediaId");
                    }
                    return mid;
                }
            }
            catch (Exception ignored)
            {
            }
        }
        return trimmed;
    }

    private void resolveOpenKfidFilter(WecomKfConversationQuery query)
    {
        if (StringUtils.hasText(query.getOpenKfid()))
        {
            query.setOpenKfid(query.getOpenKfid().trim());
            query.setOpenKfids(null);
            return;
        }
        CrmWecomKfConfig kfCfg = kfConfigMapper.selectFirst();
        List<String> openKfids = new ArrayList<>();
        if (kfCfg != null && StringUtils.hasText(kfCfg.getOpenKfids()))
        {
            try
            {
                List<String> parsed = JSON.parseArray(kfCfg.getOpenKfids(), String.class);
                if (parsed != null)
                {
                    for (String s : parsed)
                    {
                        if (StringUtils.hasText(s))
                        {
                            openKfids.add(s.trim());
                        }
                    }
                }
            }
            catch (Exception ignored)
            {
            }
            if (kfCfg.getCorpId() != null)
            {
                query.setCorpId(kfCfg.getCorpId());
            }
        }
        if (!openKfids.isEmpty())
        {
            query.setOpenKfids(openKfids);
        }
    }

    private static void normalizeScope(WecomKfConversationQuery query)
    {
        if (!StringUtils.hasText(query.getScope()))
        {
            query.setScope("mine");
            return;
        }
        String scope = query.getScope().trim().toLowerCase();
        if (!"open".equals(scope) && !"mine".equals(scope) && !"team".equals(scope))
        {
            throw new ServiceException("scope must be open, mine or team", HttpStatus.BAD_REQUEST);
        }
        query.setScope(scope);
    }

    private void assertHasAssignPerm()
    {
        if (!permissionService.hasPermi(PERM_ASSIGN))
        {
            throw new ServiceException("无团队会话权限", HttpStatus.FORBIDDEN);
        }
    }

    private static void assertHasTeamDataScope()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user != null && user.isAdmin())
        {
            return;
        }
        List<SysRole> roles = user != null ? user.getRoles() : null;
        if (roles == null || roles.isEmpty())
        {
            throw new ServiceException("无团队数据权限", HttpStatus.FORBIDDEN);
        }
        boolean hasTeam = false;
        for (SysRole role : roles)
        {
            if (role == null || !StringUtils.hasText(role.getDataScope()))
            {
                continue;
            }
            String ds = role.getDataScope();
            if (DataScopeAspect.DATA_SCOPE_ALL.equals(ds)
                    || DataScopeAspect.DATA_SCOPE_CUSTOM.equals(ds)
                    || DataScopeAspect.DATA_SCOPE_DEPT.equals(ds)
                    || DataScopeAspect.DATA_SCOPE_DEPT_AND_CHILD.equals(ds))
            {
                hasTeam = true;
                break;
            }
        }
        if (!hasTeam)
        {
            throw new ServiceException("无团队数据权限", HttpStatus.FORBIDDEN);
        }
    }
}
