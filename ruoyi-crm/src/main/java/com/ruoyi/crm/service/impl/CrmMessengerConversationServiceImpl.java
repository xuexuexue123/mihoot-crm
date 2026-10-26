package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmMessengerConfig;
import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.CrmMessengerMessage;
import com.ruoyi.crm.domain.dto.CrmMessengerConversationQuery;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmMessengerConfigMapper;
import com.ruoyi.crm.mapper.CrmMessengerConversationMapper;
import com.ruoyi.crm.mapper.CrmMessengerMessageMapper;
import com.ruoyi.crm.messenger.MessengerClient;
import com.ruoyi.crm.messenger.realtime.MessengerInboxEventPublisher;
import com.ruoyi.crm.messenger.realtime.MessengerInboxSsePayloads;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmMessengerConversationService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.framework.aspectj.DataScopeAspect;
import com.ruoyi.framework.web.service.PermissionService;

@Service
public class CrmMessengerConversationServiceImpl implements ICrmMessengerConversationService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerConversationServiceImpl.class);

    private static final Set<String> ALLOWED_STATUS = new HashSet<>(
            Arrays.asList("active", "archived", "blocked"));

    /** claim/release 本人会话使用 conversations；强制释放 / 指派使用 assign */
    private static final String PERM_CONVERSATIONS = "crm:messenger:conversations";
    private static final String PERM_ASSIGN = "crm:messenger:assign";

    @Autowired
    private CrmMessengerConversationMapper conversationMapper;

    @Autowired
    private CrmMessengerMessageMapper messageMapper;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private CrmMessengerConfigMapper configMapper;

    @Autowired
    private MessengerClient messengerClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    @Autowired
    private MessengerInboxEventPublisher inboxEventPublisher;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    @Lazy
    private ICrmMessengerConversationService self;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmMessengerConversation> selectConversationList(CrmMessengerConversationQuery query)
    {
        if (query == null)
        {
            query = new CrmMessengerConversationQuery();
        }
        normalizeScope(query);
        if ("team".equals(query.getScope()))
        {
            assertHasTeamDataScope();
        }
        if ("mine".equals(query.getScope()))
        {
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        return conversationMapper.selectPage(query);
    }

    @Override
    public Map<String, Object> getConversationDetail(Long conversationId, Long beforeMsgId, Integer pageSize)
    {
        CrmMessengerConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null)
        {
            return null;
        }

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> convInfo = new HashMap<>();
        convInfo.put("id", conversation.getId());
        convInfo.put("psid", conversation.getPsid());
        convInfo.put("pageId", conversation.getPageId());
        convInfo.put("profileName", conversation.getProfileName());
        convInfo.put("contactId", conversation.getContactId());
        convInfo.put("leadId", conversation.getLeadId());
        convInfo.put("lastMessageAt", conversation.getLastMessageAt());
        convInfo.put("lastMessagePreview", conversation.getLastMessagePreview());
        convInfo.put("unreadCount", conversation.getUnreadCount() != null ? conversation.getUnreadCount() : 0);
        convInfo.put("messagingWindowRemainingSec", calcMessagingWindowRemaining(conversation.getLastCustomerMessageAt()));
        convInfo.put("status", conversation.getStatus());
        convInfo.put("lastOutboundAt", conversation.getLastOutboundAt());
        convInfo.put("lastCustomerMessageAt", conversation.getLastCustomerMessageAt());
        putAssignmentFields(conversation, convInfo);
        convInfo.put("createdAt", conversation.getCreateTime());
        convInfo.put("updatedAt", conversation.getUpdateTime());
        enrichContactDisplay(conversation.getContactId(), convInfo);
        result.put("conversation", convInfo);

        List<CrmMessengerMessage> messages = messageMapper.selectByConversationId(
                conversationId, beforeMsgId, pageSize != null ? pageSize : 50);

        List<Map<String, Object>> msgList = new ArrayList<>();
        for (CrmMessengerMessage msg : messages)
        {
            Map<String, Object> msgInfo = new HashMap<>();
            msgInfo.put("id", msg.getId());
            msgInfo.put("psid", msg.getPsid());
            msgInfo.put("messageId", msg.getMessageId());
            msgInfo.put("messageType", msg.getMessageType());
            msgInfo.put("content", msg.getContent());
            msgInfo.put("rawJson", msg.getRawJson());
            msgInfo.put("direction", msg.getDirection());
            msgInfo.put("sendTime", msg.getSendTime());
            msgInfo.put("status", msg.getStatus());
            msgInfo.put("mediaId", msg.getMediaId());
            msgInfo.put("mediaUrl", resolveDisplayMediaUrl(msg));
            msgInfo.put("mediaMimeType", msg.getMediaMimeType());
            msgInfo.put("filename", msg.getFilename());
            msgInfo.put("source", msg.getSource());
            msgInfo.put("createdAt", msg.getCreateTime());
            msgList.add(msgInfo);
        }

        Map<String, Object> msgResult = new HashMap<>();
        msgResult.put("rows", msgList);
        result.put("messages", msgResult);

        return result;
    }

    @Override
    public Map<String, Object> markRead(Long conversationId, String tenantId)
    {
        CrmMessengerConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !tenantId.equals(conversation.getTenantId()))
        {
            return null;
        }
        conversationMapper.clearUnread(conversationId, tenantId);
        bestEffortMarkSeen(conversation, tenantId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("unreadCount", 0);
        putAssignmentFields(conversation, data);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                MessengerInboxSsePayloads.conversationUpdated(tenantId,
                        MessengerInboxSsePayloads.conversationPartialFrom(conversationId, 0, null,
                                conversation.getUserId(), conversation.getOwner(), conversation.getAssignedAt(),
                                conversation.getHostedByAi())));
        return data;
    }

    @Override
    public Map<String, Object> updateStatus(Long conversationId, String tenantId, String status)
    {
        if (!StringUtils.hasText(status) || !ALLOWED_STATUS.contains(status))
        {
            throw new IllegalArgumentException("status must be one of: active, archived, blocked");
        }
        CrmMessengerConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !tenantId.equals(conversation.getTenantId()))
        {
            return null;
        }
        conversationMapper.updateStatus(conversationId, tenantId, status);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("status", status);
        putAssignmentFields(conversation, data);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                MessengerInboxSsePayloads.conversationUpdated(tenantId,
                        MessengerInboxSsePayloads.conversationPartialFrom(conversationId, null, status,
                                conversation.getUserId(), conversation.getOwner(), conversation.getAssignedAt(),
                                conversation.getHostedByAi())));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claim(Long conversationId, String tenantId)
    {
        CrmMessengerConversation conv = lockAndGet(conversationId, tenantId);
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
        conversationMapper.updateAssignment(conversationId, tenantId,
                patch.getUserId(), patch.getOwner(), assignedAt);
        conversationMapper.updateHostedByAi(conversationId, tenantId, Boolean.FALSE);

        return assignmentResponseAndPublish(tenantId, conversationId, patch.getUserId(), patch.getOwner(),
                assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> release(Long conversationId, String tenantId)
    {
        CrmMessengerConversation conv = lockAndGet(conversationId, tenantId);
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
            if (!isOwnedConversationInTeamScope(tenantId, conversationId))
            {
                throw new ServiceException("无权释放该会话", HttpStatus.FORBIDDEN);
            }
        }

        conversationMapper.clearAssignment(conversationId, tenantId);
        return assignmentResponseAndPublish(tenantId, conversationId, null, null, null, conv.getHostedByAi());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> assign(Long conversationId, String tenantId, Long targetUserId)
    {
        if (targetUserId == null)
        {
            throw new ServiceException("userId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMessengerConversation conv = lockAndGet(conversationId, tenantId);

        if (conv.getUserId() != null)
        {
            Long currentUserId = SecurityUtils.getUserId();
            boolean isOwner = conv.getUserId().equals(currentUserId);
            if (!isOwner && !isOwnedConversationInTeamScope(tenantId, conversationId))
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
        conversationMapper.updateAssignment(conversationId, tenantId,
                patch.getUserId(), patch.getOwner(), assignedAt);
        conversationMapper.updateHostedByAi(conversationId, tenantId, Boolean.FALSE);

        return assignmentResponseAndPublish(tenantId, conversationId, patch.getUserId(), patch.getOwner(),
                assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateHostedByAi(Long conversationId, String tenantId, Boolean hostedByAi)
    {
        CrmMessengerConversation conv = lockAndGet(conversationId, tenantId);
        conversationMapper.updateHostedByAi(conversationId, tenantId, hostedByAi);

        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("userId", conv.getUserId());
        data.put("owner", conv.getOwner());
        data.put("assignedAt", conv.getAssignedAt());
        data.put("hostedByAi", hostedByAi);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                MessengerInboxSsePayloads.conversationUpdated(tenantId,
                        MessengerInboxSsePayloads.conversationPartialFrom(conversationId,
                                conv.getUnreadCount(), conv.getStatus(),
                                conv.getUserId(), conv.getOwner(), conv.getAssignedAt(), hostedByAi)));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claimForAiBot(Long conversationId, String tenantId, Long botUserId, String botOwnerName)
    {
        if (botUserId == null)
        {
            throw new ServiceException("botUserId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMessengerConversation conv = lockAndGet(conversationId, tenantId);
        if (conv.getUserId() != null)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("id", conversationId);
            data.put("userId", conv.getUserId());
            data.put("owner", conv.getOwner());
            data.put("assignedAt", conv.getAssignedAt());
            data.put("hostedByAi", conv.getHostedByAi());
            data.put("alreadyOwned", true);
            return data;
        }
        Date assignedAt = new Date();
        String owner = StringUtils.hasText(botOwnerName) ? botOwnerName.trim() : "Inbox AI";
        conversationMapper.updateAssignment(conversationId, tenantId, botUserId, owner, assignedAt);
        return assignmentResponseAndPublish(tenantId, conversationId, botUserId, owner, assignedAt,
                conv.getHostedByAi());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> linkLead(Long conversationId, String tenantId, String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            throw new ServiceException("leadId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMessengerConversation conv = lockAndGet(conversationId, tenantId);
        assertOwnedByCurrentUser(conv);
        conversationMapper.updateLeadId(conversationId, tenantId, leadId.trim());

        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("leadId", leadId.trim());
        putAssignmentFields(conv, data);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                MessengerInboxSsePayloads.conversationUpdated(tenantId,
                        MessengerInboxSsePayloads.conversationPartialFrom(conversationId,
                                conv.getUnreadCount(), conv.getStatus(),
                                conv.getUserId(), conv.getOwner(), conv.getAssignedAt(),
                                conv.getHostedByAi())));
        return data;
    }

    @Override
    public boolean isConversationInTeamScope(String tenantId, Long conversationId)
    {
        return isOwnedConversationInTeamScope(tenantId, conversationId);
    }

    private boolean isOwnedConversationInTeamScope(String tenantId, Long conversationId)
    {
        CrmMessengerConversationQuery query = new CrmMessengerConversationQuery();
        query.setTenantId(tenantId);
        query.setScope("team");
        query.setConversationId(conversationId);
        return self.countInTeamScope(query) > 0;
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public int countInTeamScope(CrmMessengerConversationQuery query)
    {
        if (query == null)
        {
            return 0;
        }
        query.setScope("team");
        return conversationMapper.countInScope(query);
    }

    @Override
    public void enrichContactDisplay(String contactId, Map<String, Object> target)
    {
        if (!StringUtils.hasText(contactId) || target == null)
        {
            if (target != null)
            {
                target.put("contactName", null);
                target.put("contactAvatar", null);
            }
            return;
        }
        try
        {
            CrmContact contact = contactMapper.selectCrmContactByContactId(contactId);
            if (contact != null)
            {
                target.put("contactName", contact.getName());
                target.put("contactAvatar", contact.getAvatar());
            }
            else
            {
                target.put("contactName", null);
                target.put("contactAvatar", null);
            }
        }
        catch (Exception e)
        {
            target.put("contactName", null);
            target.put("contactAvatar", null);
        }
    }

    @Override
    public void putAssignmentFields(CrmMessengerConversation conv, Map<String, Object> target)
    {
        if (target == null)
        {
            return;
        }
        if (conv == null)
        {
            target.put("userId", null);
            target.put("owner", null);
            target.put("assignedAt", null);
            target.put("hostedByAi", null);
            return;
        }
        target.put("userId", conv.getUserId());
        target.put("owner", conv.getOwner());
        target.put("assignedAt", conv.getAssignedAt());
        target.put("hostedByAi", conv.getHostedByAi());
    }

    @Override
    public void assertOwnedByCurrentUser(CrmMessengerConversation conv)
    {
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        Long currentUserId = SecurityUtils.getUserId();
        if (conv.getUserId() == null)
        {
            throw new ServiceException("请先从公共池领取", HttpStatus.FORBIDDEN);
        }
        if (!conv.getUserId().equals(currentUserId))
        {
            throw new ServiceException("仅会话负责人可操作", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Inbox 展示媒体地址：
     * - OSS / 公网 https：直接返回原始 URL（浏览器直链，避免代理破坏签名）
     * - Meta CDN：走本系统代理 {@code /crm/messenger/media?messageId=}
     */
    public static String resolveDisplayMediaUrl(CrmMessengerMessage msg)
    {
        if (msg == null)
        {
            return null;
        }
        if (StringUtils.hasText(msg.getMediaUrl()))
        {
            String raw = msg.getMediaUrl().trim();
            if ((raw.startsWith("https://") || raw.startsWith("http://"))
                    && !MessengerClient.isMetaMediaHost(raw))
            {
                return raw;
            }
        }
        if (msg.getId() != null)
        {
            return "/crm/messenger/media?messageId=" + msg.getId();
        }
        if (StringUtils.hasText(msg.getMediaUrl()))
        {
            return msg.getMediaUrl();
        }
        return null;
    }

    private void bestEffortMarkSeen(CrmMessengerConversation conversation, String tenantId)
    {
        if (conversation == null || !StringUtils.hasText(conversation.getPsid()))
        {
            return;
        }
        try
        {
            CrmMessengerConfig config = configMapper.selectByTenantId(tenantId);
            if (config == null || !StringUtils.hasText(config.getAccessTokenEnc()))
            {
                return;
            }
            String pageId = StringUtils.hasText(conversation.getPageId())
                    ? conversation.getPageId() : config.getPageId();
            if (!StringUtils.hasText(pageId))
            {
                return;
            }
            String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());
            messengerClient.markSeen(pageId, conversation.getPsid(), accessToken);
        }
        catch (Exception e)
        {
            log.debug("Messenger mark_seen best-effort failed conversationId={}: {}",
                    conversation.getId(), e.getMessage());
        }
    }

    private CrmMessengerConversation lockAndGet(Long conversationId, String tenantId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmMessengerConversation conv = conversationMapper.selectByIdForUpdate(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        return conv;
    }

    private Map<String, Object> assignmentResponseAndPublish(String tenantId, Long conversationId,
            Long userId, String owner, Date assignedAt, Boolean hostedByAi)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("userId", userId);
        data.put("owner", owner);
        data.put("assignedAt", assignedAt);
        data.put("hostedByAi", hostedByAi);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                MessengerInboxSsePayloads.conversationUpdated(tenantId,
                        MessengerInboxSsePayloads.conversationPartialFrom(conversationId, null, null,
                                userId, owner, assignedAt, hostedByAi)));
        return data;
    }

    private static void normalizeScope(CrmMessengerConversationQuery query)
    {
        if (!StringUtils.hasText(query.getScope()))
        {
            if (StringUtils.hasText(query.getStatus()))
            {
                query.setScope(null);
            }
            else
            {
                query.setScope("open");
            }
            return;
        }
        String scope = query.getScope().trim().toLowerCase();
        if (!"open".equals(scope) && !"mine".equals(scope) && !"team".equals(scope))
        {
            throw new ServiceException("scope must be open, mine or team", HttpStatus.BAD_REQUEST);
        }
        query.setScope(scope);
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

    private Long calcMessagingWindowRemaining(Date lastCustomerMessageAt)
    {
        if (lastCustomerMessageAt == null)
        {
            return null;
        }
        long windowEnd = lastCustomerMessageAt.getTime() + 86400_000L;
        long now = System.currentTimeMillis();
        long remaining = (windowEnd - now) / 1000;
        return remaining > 0 ? remaining : 0L;
    }
}
