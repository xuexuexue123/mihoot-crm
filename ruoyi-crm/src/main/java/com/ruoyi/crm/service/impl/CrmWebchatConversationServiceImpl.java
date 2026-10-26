package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWebchatMessage;
import com.ruoyi.crm.domain.CrmWebchatVisitor;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.domain.dto.CrmWebchatConversationQuery;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatMessageMapper;
import com.ruoyi.crm.mapper.CrmWebchatVisitorMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmWebchatConversationService;
import com.ruoyi.crm.webchat.WebchatActivityEventTypes;
import com.ruoyi.crm.webchat.WebchatIdentityResolutionSupport;
import com.ruoyi.crm.webchat.realtime.WebchatInboxEventPublisher;
import com.ruoyi.crm.webchat.realtime.WebchatInboxSsePayloads;
import com.ruoyi.framework.aspectj.DataScopeAspect;
import com.ruoyi.framework.web.service.PermissionService;

@Service
public class CrmWebchatConversationServiceImpl implements ICrmWebchatConversationService
{
    private static final String PERM_CONVERSATIONS = "crm:webchat:conversations";
    private static final String PERM_ASSIGN = "crm:webchat:assign";

    @Autowired
    private CrmWebchatConversationMapper conversationMapper;

    @Autowired
    private CrmWebchatMessageMapper messageMapper;

    @Autowired
    private CrmContactMapper contactMapper;

    @Autowired
    private CrmLeadMapper leadMapper;

    @Autowired
    private CrmWebchatVisitorMapper visitorMapper;

    @Autowired
    private WebchatInboxEventPublisher inboxEventPublisher;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    @Lazy
    private ICrmWebchatConversationService self;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmWebchatConversation> selectConversationList(CrmWebchatConversationQuery query)
    {
        if (query == null)
        {
            query = new CrmWebchatConversationQuery();
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
    public Map<String, Object> getConversationDetail(Long conversationId, Long beforeId, Integer pageSize)
    {
        CrmWebchatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null)
        {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> convInfo = toConversationApiMap(conversation);
        result.put("conversation", convInfo);

        List<CrmWebchatMessage> messages = messageMapper.selectByConversationId(
                conversationId, beforeId, pageSize != null ? pageSize : 50);
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (CrmWebchatMessage msg : messages)
        {
            msgList.add(WebchatInboxSsePayloads.toMessageRecord(msg));
        }
        Map<String, Object> msgResult = new HashMap<>();
        msgResult.put("rows", msgList);
        result.put("messages", msgResult);
        return result;
    }

    @Override
    public Map<String, Object> markRead(Long conversationId, String tenantId)
    {
        CrmWebchatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null || !tenantId.equals(conversation.getTenantId()))
        {
            return null;
        }
        conversationMapper.clearUnread(conversationId, tenantId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("unreadCount", 0);
        putAssignmentFields(conversation, data);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                WebchatInboxSsePayloads.conversationUpdated(tenantId,
                        WebchatInboxSsePayloads.conversationPartialFrom(conversationId, 0, null,
                                conversation.getUserId(), conversation.getOwner(), conversation.getAssignedAt(),
                                conversation.getHostedByAi()),
                        true));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claim(Long conversationId, String tenantId)
    {
        CrmWebchatConversation conv = lockAndGet(conversationId, tenantId);
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
        produceAssignedActivity(conv, patch.getUserId());
        return assignmentResponseAndPublish(tenantId, conversationId, patch.getUserId(), patch.getOwner(),
                assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> release(Long conversationId, String tenantId)
    {
        CrmWebchatConversation conv = lockAndGet(conversationId, tenantId);
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
        CrmWebchatConversation conv = lockAndGet(conversationId, tenantId);
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
        produceAssignedActivity(conv, patch.getUserId());
        return assignmentResponseAndPublish(tenantId, conversationId, patch.getUserId(), patch.getOwner(),
                assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateHostedByAi(Long conversationId, String tenantId, Boolean hostedByAi)
    {
        CrmWebchatConversation conv = lockAndGet(conversationId, tenantId);
        conversationMapper.updateHostedByAi(conversationId, tenantId, hostedByAi);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("userId", conv.getUserId());
        data.put("owner", conv.getOwner());
        data.put("assignedAt", conv.getAssignedAt());
        data.put("hostedByAi", hostedByAi);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                WebchatInboxSsePayloads.conversationUpdated(tenantId,
                        WebchatInboxSsePayloads.conversationPartialFrom(conversationId,
                                conv.getUnreadCount(), conv.getStatus(),
                                conv.getUserId(), conv.getOwner(), conv.getAssignedAt(), hostedByAi),
                        true));
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
        CrmWebchatConversation conv = lockAndGet(conversationId, tenantId);
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
        CrmWebchatConversation conv = lockAndGet(conversationId, tenantId);
        assertCanLinkCrmSubject(conv);

        String normalizedLeadId = null;
        if (leadId != null)
        {
            String trimmed = leadId.trim();
            if ("null".equalsIgnoreCase(trimmed) || trimmed.isEmpty())
            {
                normalizedLeadId = null;
            }
            else
            {
                normalizedLeadId = trimmed;
            }
        }

        if (StringUtils.hasText(normalizedLeadId))
        {
            CrmLead lead = leadMapper.selectCrmLeadByLeadId(normalizedLeadId);
            if (lead == null)
            {
                throw new ServiceException("Lead not found", HttpStatus.NOT_FOUND);
            }
            if (StringUtils.hasText(lead.getOrgId()) && !tenantId.equals(lead.getOrgId()))
            {
                throw new ServiceException("leadId does not belong to tenant", HttpStatus.BAD_REQUEST);
            }
        }

        String previousLeadId = conv.getLeadId();
        conversationMapper.updateLeadId(conversationId, tenantId, normalizedLeadId);
        conv.setLeadId(normalizedLeadId);

        CrmWebchatVisitor visitor = visitorMapper.selectByVisitorId(tenantId, conv.getVisitorId());
        if (visitor != null && !StringUtils.hasText(visitor.getContactId()))
        {
            visitorMapper.updateSubjects(tenantId, conv.getVisitorId(), null, normalizedLeadId);
        }

        boolean linked = StringUtils.hasText(normalizedLeadId);
        boolean unlinked = !linked && StringUtils.hasText(previousLeadId);
        if (linked)
        {
            produceLeadLinkActivity(conv, WebchatActivityEventTypes.LEAD_LINKED,
                    WebchatActivityEventTypes.LEAD_LINKED_NAME,
                    WebchatActivityEventTypes.LEAD_LINKED_CATEGORY,
                    WebchatActivityEventTypes.LEAD_LINKED_IMPORTANCE,
                    normalizedLeadId);
        }
        else if (unlinked)
        {
            produceLeadLinkActivity(conv, WebchatActivityEventTypes.LEAD_UNLINKED,
                    WebchatActivityEventTypes.LEAD_UNLINKED_NAME,
                    WebchatActivityEventTypes.LEAD_UNLINKED_CATEGORY,
                    WebchatActivityEventTypes.LEAD_UNLINKED_IMPORTANCE,
                    previousLeadId);
        }

        Map<String, Object> data = toConversationApiMap(conv);
        Map<String, Object> partial = WebchatInboxSsePayloads.toConversationPartial(conv);
        partial.put("leadId", normalizedLeadId);
        partial.put("contactId", conv.getContactId());
        enrichContactDisplay(conv.getContactId(), partial);
        enrichLeadDisplay(normalizedLeadId, partial);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                WebchatInboxSsePayloads.conversationUpdated(tenantId, partial, true));
        return data;
    }

    @Override
    public boolean isConversationInTeamScope(String tenantId, Long conversationId)
    {
        return isOwnedConversationInTeamScope(tenantId, conversationId);
    }

    private boolean isOwnedConversationInTeamScope(String tenantId, Long conversationId)
    {
        CrmWebchatConversationQuery query = new CrmWebchatConversationQuery();
        query.setTenantId(tenantId);
        query.setScope("team");
        query.setConversationId(conversationId);
        return self.countInTeamScope(query) > 0;
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public int countInTeamScope(CrmWebchatConversationQuery query)
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
    public void enrichLeadDisplay(String leadId, Map<String, Object> target)
    {
        if (target == null)
        {
            return;
        }
        if (!StringUtils.hasText(leadId))
        {
            target.put("leadName", null);
            return;
        }
        try
        {
            CrmLead lead = leadMapper.selectCrmLeadByLeadId(leadId);
            if (lead == null)
            {
                target.put("leadName", null);
                return;
            }
            String name = StringUtils.hasText(lead.getLeadName()) ? lead.getLeadName() : lead.getFullName();
            target.put("leadName", StringUtils.hasText(name) ? name : null);
        }
        catch (Exception e)
        {
            target.put("leadName", null);
        }
    }

    @Override
    public void enrichVisitorProfile(String tenantId, String visitorId, Map<String, Object> target)
    {
        if (target == null)
        {
            return;
        }
        if (!StringUtils.hasText(tenantId) || !StringUtils.hasText(visitorId))
        {
            target.put("displayName", null);
            target.put("email", null);
            target.put("mobilePhone", null);
            return;
        }
        try
        {
            CrmWebchatVisitor visitor = visitorMapper.selectByVisitorId(tenantId, visitorId);
            if (visitor == null)
            {
                target.put("displayName", null);
                target.put("email", null);
                target.put("mobilePhone", null);
                return;
            }
            target.put("displayName", visitor.getDisplayName());
            target.put("email", visitor.getEmail());
            target.put("mobilePhone", visitor.getMobilePhone());
        }
        catch (Exception e)
        {
            target.put("displayName", null);
            target.put("email", null);
            target.put("mobilePhone", null);
        }
    }

    @Override
    public Map<String, Object> toConversationApiMap(CrmWebchatConversation conversation)
    {
        Map<String, Object> convInfo = new HashMap<>();
        if (conversation == null)
        {
            return convInfo;
        }
        convInfo.put("id", conversation.getId());
        convInfo.put("visitorId", conversation.getVisitorId());
        convInfo.put("profileName", conversation.getProfileName());
        convInfo.put("contactId", conversation.getContactId());
        convInfo.put("leadId", conversation.getLeadId());
        convInfo.put("lastMessageAt", conversation.getLastMessageAt());
        convInfo.put("lastMessagePreview", conversation.getLastMessagePreview());
        convInfo.put("unreadCount", conversation.getUnreadCount() != null ? conversation.getUnreadCount() : 0);
        convInfo.put("status", conversation.getStatus());
        convInfo.put("lastOutboundAt", conversation.getLastOutboundAt());
        convInfo.put("lastCustomerMessageAt", conversation.getLastCustomerMessageAt());
        convInfo.put("closedAt", conversation.getClosedAt());
        putAssignmentFields(conversation, convInfo);
        convInfo.put("createdAt", conversation.getCreateTime());
        convInfo.put("updatedAt", conversation.getUpdateTime());
        enrichContactDisplay(conversation.getContactId(), convInfo);
        enrichLeadDisplay(conversation.getLeadId(), convInfo);
        enrichVisitorProfile(conversation.getTenantId(), conversation.getVisitorId(), convInfo);
        return convInfo;
    }

    @Override
    public void putAssignmentFields(CrmWebchatConversation conv, Map<String, Object> target)
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
    public void assertOwnedByCurrentUser(CrmWebchatConversation conv)
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

    private void assertCanLinkCrmSubject(CrmWebchatConversation conv)
    {
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        if (permissionService.hasPermi(PERM_ASSIGN))
        {
            return;
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

    private void produceLeadLinkActivity(CrmWebchatConversation conv, String eventType, String eventName,
            String eventCategory, int importance, String subjectLeadId)
    {
        if (conv == null || !StringUtils.hasText(subjectLeadId))
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:lead:" + eventType + ":" + conv.getId() + ":" + subjectLeadId);
            dto.setEventType(eventType);
            dto.setEventName(eventName);
            dto.setEventCategory(eventCategory);
            dto.setImportance(importance);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_INBOX);
            dto.setCreateBy("webchat-inbox");
            dto.setStartedDate(new Date());
            dto.setSubjectType("Lead");
            dto.setSubjectId(subjectLeadId);
            dto.setMessageData(eventName);
            JSONObject payload = new JSONObject();
            payload.put("conversationId", conv.getId());
            payload.put("visitorId", conv.getVisitorId());
            payload.put("leadId", subjectLeadId);
            if (StringUtils.hasText(conv.getContactId()))
            {
                payload.put("contactId", conv.getContactId());
            }
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception ignored)
        {
        }
    }

    private CrmWebchatConversation lockAndGet(Long conversationId, String tenantId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWebchatConversation conv = conversationMapper.selectByIdForUpdate(conversationId);
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
                WebchatInboxSsePayloads.conversationUpdated(tenantId,
                        WebchatInboxSsePayloads.conversationPartialFrom(conversationId, null, null,
                                userId, owner, assignedAt, hostedByAi),
                        true));
        return data;
    }

    private void produceAssignedActivity(CrmWebchatConversation conv, Long assigneeUserId)
    {
        if (conv == null)
        {
            return;
        }
        boolean hasContact = StringUtils.hasText(conv.getContactId());
        boolean hasLead = StringUtils.hasText(conv.getLeadId());
        if (!hasContact && !hasLead)
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:assigned:" + conv.getId() + ":" + System.currentTimeMillis());
            dto.setEventType(WebchatActivityEventTypes.CONVERSATION_ASSIGNED);
            dto.setEventName(WebchatActivityEventTypes.CONVERSATION_ASSIGNED_NAME);
            dto.setEventCategory(WebchatActivityEventTypes.CONVERSATION_ASSIGNED_CATEGORY);
            dto.setImportance(WebchatActivityEventTypes.CONVERSATION_ASSIGNED_IMPORTANCE);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_INBOX);
            dto.setCreateBy("webchat");
            dto.setStartedDate(new Date());
            if (hasContact)
            {
                dto.setSubjectType("Contact");
                dto.setSubjectId(conv.getContactId());
            }
            else
            {
                dto.setSubjectType("Lead");
                dto.setSubjectId(conv.getLeadId());
            }
            JSONObject payload = new JSONObject();
            payload.put("conversation_id", conv.getId());
            payload.put("visitor_id", conv.getVisitorId());
            payload.put("assignee_user_id", assigneeUserId);
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception ignored)
        {
        }
    }

    private static void normalizeScope(CrmWebchatConversationQuery query)
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
}
