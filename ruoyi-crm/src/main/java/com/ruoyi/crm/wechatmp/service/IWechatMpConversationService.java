package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.dto.WechatMpConversationQuery;

public interface IWechatMpConversationService
{
    List<CrmWechatMpConversation> selectConversationList(WechatMpConversationQuery query);

    Map<String, Object> toApiMap(CrmWechatMpConversation conv);

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

    Map<String, Object> linkLead(Long conversationId, String leadId);

    Map<String, Object> linkContact(String appId, String openId, String contactId, boolean setPrimary);

    void upsertOnInbound(String appId, String openId, String msgType, String content, java.util.Date createTime);

    void upsertOnOutbound(String appId, String openId, String msgType, String content, java.util.Date createTime);

    CrmWechatMpConversation findByAppOpenId(String appId, String openId);

    void assertOwnedByCurrentUser(CrmWechatMpConversation conv);

    /** DataScope 计数（自调用走代理） */
    int countInTeamScope(WechatMpConversationQuery query);

    List<CrmWechatMpMessage> listMessages(String appId, String openId);
}
