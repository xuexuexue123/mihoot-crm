package com.ruoyi.crm.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.dto.WecomKfConversationQuery;

public interface IWecomKfConversationService
{
    List<CrmWecomKfConversation> selectConversationList(WecomKfConversationQuery query);

    /**
     * 列表查询（分页）。须在 Service 内先 resolve openKfid 再 PageHelper.startPage，
     * 避免 selectFirst 的 LIMIT 1 被 PageHelper 再叠一层。
     */
    List<CrmWecomKfConversation> selectConversationList(WecomKfConversationQuery query, int pageNum, int pageSize);

    Map<String, Object> toApiMap(CrmWecomKfConversation conv);

    Map<String, Object> getConversationDetail(Long conversationId, Integer pageNum, Integer pageSize);

    Map<String, Object> markRead(Long conversationId);

    Map<String, Object> claim(Long conversationId);

    Map<String, Object> release(Long conversationId);

    Map<String, Object> assign(Long conversationId, Long targetUserId);

    /**
     * 更新 AI 托管态。hostedByAi: true=交回 AI；false=人工接管；null=跟随渠道策略。
     */
    Map<String, Object> updateHostedByAi(Long conversationId, Boolean hostedByAi);

    /**
     * Inbox AI Bot 领取公共池会话；保持 hostedByAi（不强制改成 false）。
     */
    Map<String, Object> claimForAiBot(Long conversationId, Long botUserId, String botOwnerName);

    Map<String, Object> sendText(Long conversationId, String content);

    Map<String, Object> send(Long conversationId, String msgType, String content);

    /**
     * Inbox AI 代发文本：跳过坐席负责人校验；source=ai_inbox_agent。
     * 若 hostedByAi==false 或 canReply==0 则拒绝。
     */
    Map<String, Object> sendTextAsAi(Long conversationId, String content);

    Map<String, Object> linkContact(String externalUserid, String contactId, String openKfid, boolean setPrimary);

    Map<String, Object> unlinkContact(String externalUserid, String openKfid);

    void assertOwnedByCurrentUser(CrmWecomKfConversation conv);

    int countInTeamScope(WecomKfConversationQuery query);

    void upsertOnOutbound(String corpId, String openKfid, String externalUserid, String preview, java.util.Date at);
}
