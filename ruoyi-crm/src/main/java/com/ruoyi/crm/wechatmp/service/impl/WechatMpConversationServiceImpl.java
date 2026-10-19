package com.ruoyi.crm.wechatmp.service.impl;

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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConfig;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.dto.WechatMpConversationQuery;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConfigMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpConversationMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpFanMapper;
import com.ruoyi.crm.wechatmp.mapper.CrmWechatMpMessageMapper;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxEventPublisher;
import com.ruoyi.crm.wechatmp.realtime.WechatMpInboxSsePayloads;
import com.ruoyi.crm.wechatmp.service.IWechatMpConversationService;
import com.ruoyi.framework.aspectj.DataScopeAspect;
import com.ruoyi.framework.web.service.PermissionService;

@Service
public class WechatMpConversationServiceImpl implements IWechatMpConversationService
{
    private static final String PERM_CONVERSATIONS = "crm:wechat_mp:conversations";
    private static final String PERM_ASSIGN = "crm:wechat_mp:assign";
    private static final long WINDOW_MS = 48L * 3600_000L;

    @Autowired
    private CrmWechatMpConversationMapper conversationMapper;

    @Autowired
    private CrmWechatMpMessageMapper messageMapper;

    @Autowired
    private CrmWechatMpConfigMapper configMapper;

    @Autowired
    private CrmWechatMpFanMapper fanMapper;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private WechatMpInboxEventPublisher inboxEventPublisher;

    @Autowired
    @Lazy
    private IWechatMpConversationService self;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmWechatMpConversation> selectConversationList(WechatMpConversationQuery query)
    {
        if (query == null)
        {
            query = new WechatMpConversationQuery();
        }
        normalizeScope(query);
        if ("team".equals(query.getScope()) && !query.isLegacyAll())
        {
            assertHasAssignPerm();
            assertHasTeamDataScope();
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        if ("mine".equals(query.getScope()))
        {
            query.setCurrentUserId(SecurityUtils.getUserId());
        }
        resolveAppFilter(query);
        return conversationMapper.selectPage(query);
    }

    @Override
    public Map<String, Object> toApiMap(CrmWechatMpConversation conv)
    {
        Map<String, Object> item = new HashMap<>();
        if (conv == null)
        {
            return item;
        }
        item.put("id", conv.getId());
        item.put("appId", conv.getAppId());
        item.put("openId", conv.getOpenId());
        item.put("nickname", conv.getNickname());
        item.put("avatarUrl", conv.getAvatarUrl());
        item.put("accountName", conv.getAccountName());
        item.put("accountType", conv.getAccountType());
        item.put("lastMessage", conv.getLastMessage());
        item.put("lastMessageAt", conv.getLastMessageAt());
        item.put("messageCount", conv.getMessageCount() != null ? conv.getMessageCount() : 0);
        item.put("unreadCount", conv.getUnreadCount() != null ? conv.getUnreadCount() : 0);
        item.put("messagingWindowRemainingSec", calcMessagingWindowRemaining(conv.getLastCustomerMessageAt()));
        item.put("userId", conv.getUserId() != null ? String.valueOf(conv.getUserId()) : null);
        item.put("owner", conv.getOwner());
        item.put("assignedAt", conv.getAssignedAt());
        item.put("hostedByAi", conv.getHostedByAi());
        item.put("contactId", conv.getContactId());
        item.put("contactName", conv.getContactName());
        item.put("leadId", conv.getLeadId());
        item.put("leadName", conv.getLeadName());
        return item;
    }

    @Override
    public Map<String, Object> getConversationDetail(Long conversationId, Integer pageNum, Integer pageSize)
    {
        CrmWechatMpConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null)
        {
            return null;
        }
        int pn = pageNum != null && pageNum > 0 ? pageNum : 1;
        int ps = pageSize != null && pageSize > 0 ? pageSize : 50;
        PageHelper.startPage(pn, ps);
        List<CrmWechatMpMessage> messages = messageMapper.selectByConversation(
                conversation.getAppId(), conversation.getOpenId());
        PageInfo<CrmWechatMpMessage> pageInfo = new PageInfo<>(messages);

        Map<String, Object> messagesWrap = new HashMap<>();
        messagesWrap.put("code", 200);
        messagesWrap.put("total", pageInfo.getTotal());
        messagesWrap.put("rows", messages);

        Map<String, Object> result = new HashMap<>();
        result.put("conversation", toApiMap(conversation));
        result.put("messages", messagesWrap);
        return result;
    }

    @Override
    public Map<String, Object> markRead(Long conversationId)
    {
        CrmWechatMpConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null)
        {
            return null;
        }
        conversationMapper.clearUnread(conversationId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("unreadCount", 0);
        publishConversationUpdated(conversation.getAppId(),
                WechatMpInboxSsePayloads.conversationPartialFrom(conversationId, conversation.getAppId(),
                        0, conversation.getUserId(), conversation.getOwner(), conversation.getAssignedAt()));
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claim(Long conversationId)
    {
        CrmWechatMpConversation conv = lockAndGet(conversationId);
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
        return assignmentResponse(conversationId, patch.getUserId(), patch.getOwner(), assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> release(Long conversationId)
    {
        CrmWechatMpConversation conv = lockAndGet(conversationId);
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
        return assignmentResponse(conversationId, null, null, null, conv.getHostedByAi());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> assign(Long conversationId, Long targetUserId)
    {
        if (targetUserId == null)
        {
            throw new ServiceException("userId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConversation conv = lockAndGet(conversationId);

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
        return assignmentResponse(conversationId, patch.getUserId(), patch.getOwner(), assignedAt, Boolean.FALSE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateHostedByAi(Long conversationId, Boolean hostedByAi)
    {
        CrmWechatMpConversation conv = lockAndGet(conversationId);
        conversationMapper.updateHostedByAi(conversationId, hostedByAi);
        CrmWechatMpConversation updated = conversationMapper.selectById(conversationId);
        CrmWechatMpConversation result = updated != null ? updated : conv;
        if (result != null)
        {
            result.setHostedByAi(hostedByAi);
            inboxEventPublisher.publishConversationUpdated(TenantDataSourceSupport.currentTenantKey(),
                    WechatMpInboxSsePayloads.conversationUpdated(result));
        }
        return toApiMap(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> claimForAiBot(Long conversationId, Long botUserId, String botOwnerName)
    {
        if (botUserId == null)
        {
            throw new ServiceException("botUserId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConversation conv = lockAndGet(conversationId);
        if (conv.getUserId() != null)
        {
            Map<String, Object> data = toApiMap(conv);
            data.put("alreadyOwned", true);
            return data;
        }
        Date assignedAt = new Date();
        String owner = StringUtils.hasText(botOwnerName) ? botOwnerName.trim() : "Inbox AI";
        conversationMapper.updateAssignment(conversationId, botUserId, owner, assignedAt);
        return assignmentResponse(conversationId, botUserId, owner, assignedAt, conv.getHostedByAi());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> linkLead(Long conversationId, String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            throw new ServiceException("leadId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConversation conv = lockAndGet(conversationId);
        conversationMapper.updateLeadId(conversationId, leadId.trim());
        CrmWechatMpConversation updated = conversationMapper.selectById(conversationId);
        CrmWechatMpConversation result = updated != null ? updated : conv;
        if (result != null)
        {
            inboxEventPublisher.publishConversationUpdated(TenantDataSourceSupport.currentTenantKey(),
                    WechatMpInboxSsePayloads.conversationUpdated(result));
        }
        return toApiMap(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> linkContact(String appId, String openId, String contactId, boolean setPrimary)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(openId) || !StringUtils.hasText(contactId))
        {
            throw new ServiceException("appId、openId、contactId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConfig cfg = configMapper.selectByAppId(appId.trim());
        if (cfg == null)
        {
            throw new ServiceException("公众号配置不存在或不属于当前租户", HttpStatus.FORBIDDEN);
        }

        String existing = identityService.findContactByIdentity("wechat_mp_openid", openId.trim());
        if (existing != null && !existing.equals(contactId.trim()))
        {
            throw new ServiceException("openId 已绑定其他联系人", HttpStatus.CONFLICT);
        }

        if (existing == null)
        {
            identityService.bindIdentity(contactId.trim(), "wechat_mp_openid", openId.trim(), "wechat_mp_inbox");
        }

        conversationMapper.updateContactId(appId.trim(), openId.trim(), contactId.trim());

        CrmWechatMpFan fan = fanMapper.selectByOpenId(appId.trim(), openId.trim());
        if (fan != null)
        {
            fan.setContactId(contactId.trim());
            fanMapper.upsert(fan);
        }
        else if (setPrimary)
        {
            // no fan row — conversation contact_id is enough
        }

        CrmWechatMpConversation conv = conversationMapper.selectByAppOpenId(appId.trim(), openId.trim());
        if (conv == null)
        {
            Map<String, Object> data = new HashMap<>();
            data.put("appId", appId.trim());
            data.put("openId", openId.trim());
            data.put("contactId", contactId.trim());
            return data;
        }
        inboxEventPublisher.publishConversationUpdated(TenantDataSourceSupport.currentTenantKey(),
                WechatMpInboxSsePayloads.conversationUpdated(conv));
        return toApiMap(conv);
    }

    @Override
    public void upsertOnInbound(String appId, String openId, String msgType, String content, Date createTime)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(openId))
        {
            return;
        }
        CrmWechatMpConversation row = new CrmWechatMpConversation();
        row.setAppId(appId);
        row.setOpenId(openId);
        row.setLastMessage(previewOf(msgType, content));
        row.setLastMessageAt(createTime != null ? createTime : new Date());
        row.setLastCustomerMessageAt(row.getLastMessageAt());
        row.setMessageCount(1);
        row.setUnreadCount(1);
        fillFanProfile(row);
        conversationMapper.upsertOnInbound(row);
    }

    @Override
    public void upsertOnOutbound(String appId, String openId, String msgType, String content, Date createTime)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(openId))
        {
            return;
        }
        CrmWechatMpConversation row = new CrmWechatMpConversation();
        row.setAppId(appId);
        row.setOpenId(openId);
        row.setLastMessage(previewOf(msgType, content));
        row.setLastMessageAt(createTime != null ? createTime : new Date());
        fillFanProfile(row);
        conversationMapper.upsertOnOutbound(row);
    }

    @Override
    public CrmWechatMpConversation findByAppOpenId(String appId, String openId)
    {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(openId))
        {
            return null;
        }
        return conversationMapper.selectByAppOpenId(appId, openId);
    }

    @Override
    public void assertOwnedByCurrentUser(CrmWechatMpConversation conv)
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
    public int countInTeamScope(WechatMpConversationQuery query)
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
    public List<CrmWechatMpMessage> listMessages(String appId, String openId)
    {
        return messageMapper.selectByConversation(appId, openId);
    }

    private void fillFanProfile(CrmWechatMpConversation row)
    {
        try
        {
            CrmWechatMpFan fan = fanMapper.selectByOpenId(row.getAppId(), row.getOpenId());
            if (fan != null)
            {
                row.setNickname(fan.getNickname());
                row.setAvatarUrl(fan.getAvatarUrl());
                row.setContactId(fan.getContactId());
            }
        }
        catch (Exception ignored)
        {
            // fan table may be empty
        }
        if (row.getContactId() == null)
        {
            try
            {
                row.setContactId(identityService.findContactByIdentity("wechat_mp_openid", row.getOpenId()));
            }
            catch (Exception ignored)
            {
            }
        }
    }

    private boolean isOwnedConversationInTeamScope(Long conversationId)
    {
        WechatMpConversationQuery query = new WechatMpConversationQuery();
        query.setScope("team");
        query.setConversationId(conversationId);
        query.setCurrentUserId(SecurityUtils.getUserId());
        return self.countInTeamScope(query) > 0;
    }

    private CrmWechatMpConversation lockAndGet(Long conversationId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmWechatMpConversation conv = conversationMapper.selectByIdForUpdate(conversationId);
        if (conv == null)
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        return conv;
    }

    private Map<String, Object> assignmentResponse(Long conversationId, Long userId, String owner, Date assignedAt,
            Boolean hostedByAi)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("id", conversationId);
        data.put("userId", userId != null ? String.valueOf(userId) : null);
        data.put("owner", owner);
        data.put("assignedAt", assignedAt);
        data.put("hostedByAi", hostedByAi);
        CrmWechatMpConversation fresh = conversationMapper.selectById(conversationId);
        String appId = fresh != null ? fresh.getAppId() : null;
        data.put("appId", appId);
        publishConversationUpdated(appId,
                WechatMpInboxSsePayloads.conversationPartialFrom(conversationId, appId, null, userId, owner,
                        assignedAt, hostedByAi));
        return data;
    }

    private void publishConversationUpdated(String appId, Map<String, Object> partial)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (!StringUtils.hasText(tenantId))
        {
            return;
        }
        inboxEventPublisher.publishConversationUpdated(tenantId,
                WechatMpInboxSsePayloads.conversationUpdated(appId, partial));
    }

    private void resolveAppFilter(WechatMpConversationQuery query)
    {
        if (StringUtils.hasText(query.getAppId()))
        {
            CrmWechatMpConfig cfg = configMapper.selectByAppId(query.getAppId().trim());
            if (cfg == null)
            {
                throw new ServiceException("公众号配置不存在或不属于当前租户", HttpStatus.FORBIDDEN);
            }
            query.setAppId(query.getAppId().trim());
            query.setAppIds(null);
            return;
        }
        List<CrmWechatMpConfig> configs = configMapper.selectAll();
        List<String> appIds = new ArrayList<>();
        if (configs != null)
        {
            for (CrmWechatMpConfig cfg : configs)
            {
                if (cfg != null && StringUtils.hasText(cfg.getAppId()))
                {
                    appIds.add(cfg.getAppId());
                }
            }
        }
        if (appIds.isEmpty())
        {
            // empty IN list would error; use impossible filter
            appIds.add("__none__");
        }
        query.setAppIds(appIds);
    }

    private static void normalizeScope(WechatMpConversationQuery query)
    {
        if (query.isLegacyAll())
        {
            query.setScope(null);
            return;
        }
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

    public static Long calcMessagingWindowRemaining(Date lastCustomerMessageAt)
    {
        if (lastCustomerMessageAt == null)
        {
            return null;
        }
        long remaining = (lastCustomerMessageAt.getTime() + WINDOW_MS - System.currentTimeMillis()) / 1000;
        return remaining > 0 ? remaining : 0L;
    }

    static String previewOf(String msgType, String content)
    {
        if ("image".equals(msgType))
        {
            return "[图片]";
        }
        if ("template".equals(msgType))
        {
            return "[模板消息]";
        }
        if ("news".equals(msgType))
        {
            return "[图文]";
        }
        if (content == null)
        {
            return null;
        }
        return content.length() > 200 ? content.substring(0, 200) : content;
    }
}
