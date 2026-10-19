package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.dto.CrmWhatsappConversationQuery;

public interface ICrmWhatsappConversationService
{
    List<CrmWhatsappConversation> selectConversationList(CrmWhatsappConversationQuery query);

    Map<String, Object> getConversationDetail(Long conversationId, Long beforeMsgId, Integer pageSize);

    /**
     * @return null if conversation not found or tenant mismatch
     */
    Map<String, Object> markRead(Long conversationId, String tenantId);

    /**
     * @return null if conversation not found or tenant mismatch
     * @throws IllegalArgumentException if status invalid
     */
    Map<String, Object> updateStatus(Long conversationId, String tenantId, String status);

    Map<String, Object> claim(Long conversationId, String tenantId);

    Map<String, Object> release(Long conversationId, String tenantId);

    Map<String, Object> assign(Long conversationId, String tenantId, Long targetUserId);

    /**
     * 更新 AI 托管态。hostedByAi: true=交回 AI；false=人工接管；null=跟随渠道策略。
     */
    Map<String, Object> updateHostedByAi(Long conversationId, String tenantId, Boolean hostedByAi);

    /**
     * Inbox AI Bot 领取公共池会话；保持 hostedByAi（不强制改成 false）。
     * 已有负责人则原样返回；仅当 userId 为空时写入 bot。
     */
    Map<String, Object> claimForAiBot(Long conversationId, String tenantId, Long botUserId, String botOwnerName);

    /**
     * 负责人回写 lead_id（仅本人负责的会话）
     */
    Map<String, Object> linkLead(Long conversationId, String tenantId, String leadId);

    /** Resolve contact display fields; keys contactName / contactAvatar (nullable). */
    void enrichContactDisplay(String contactId, Map<String, Object> target);

    /** Put ownership fields onto an API/SSE map. */
    void putAssignmentFields(CrmWhatsappConversation conv, Map<String, Object> target);

    /** Assert current user owns the conversation; throws 403 otherwise. */
    void assertOwnedByCurrentUser(CrmWhatsappConversation conv);

    /** 当前用户 DataScope 下是否可见该已分配会话（团队范围）。 */
    boolean isConversationInTeamScope(String tenantId, Long conversationId);

    /** DataScope 计数（内部 / 自调用走代理）。 */
    int countInTeamScope(CrmWhatsappConversationQuery query);
}
