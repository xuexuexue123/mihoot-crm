package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.dto.CrmWebchatConversationQuery;

public interface ICrmWebchatConversationService
{
    List<CrmWebchatConversation> selectConversationList(CrmWebchatConversationQuery query);

    Map<String, Object> getConversationDetail(Long conversationId, Long beforeId, Integer pageSize);

    Map<String, Object> markRead(Long conversationId, String tenantId);

    Map<String, Object> claim(Long conversationId, String tenantId);

    Map<String, Object> release(Long conversationId, String tenantId);

    Map<String, Object> assign(Long conversationId, String tenantId, Long targetUserId);

    Map<String, Object> updateHostedByAi(Long conversationId, String tenantId, Boolean hostedByAi);

    Map<String, Object> claimForAiBot(Long conversationId, String tenantId, Long botUserId, String botOwnerName);

    Map<String, Object> linkLead(Long conversationId, String tenantId, String leadId);

    void enrichContactDisplay(String contactId, Map<String, Object> target);

    void enrichLeadDisplay(String leadId, Map<String, Object> target);

    void enrichVisitorProfile(String tenantId, String visitorId, Map<String, Object> target);

    Map<String, Object> toConversationApiMap(CrmWebchatConversation conversation);

    void putAssignmentFields(CrmWebchatConversation conv, Map<String, Object> target);

    void assertOwnedByCurrentUser(CrmWebchatConversation conv);

    boolean isConversationInTeamScope(String tenantId, Long conversationId);

    int countInTeamScope(CrmWebchatConversationQuery query);
}
