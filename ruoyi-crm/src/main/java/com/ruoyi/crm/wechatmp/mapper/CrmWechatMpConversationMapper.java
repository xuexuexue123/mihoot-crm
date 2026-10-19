package com.ruoyi.crm.wechatmp.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpConversation;
import com.ruoyi.crm.wechatmp.dto.WechatMpConversationQuery;

public interface CrmWechatMpConversationMapper
{
    List<CrmWechatMpConversation> selectPage(WechatMpConversationQuery query);

    int countInScope(WechatMpConversationQuery query);

    CrmWechatMpConversation selectById(@Param("id") Long id);

    CrmWechatMpConversation selectByIdForUpdate(@Param("id") Long id);

    CrmWechatMpConversation selectByAppOpenId(@Param("appId") String appId, @Param("openId") String openId);

    int upsertOnInbound(CrmWechatMpConversation row);

    int upsertOnOutbound(CrmWechatMpConversation row);

    int clearUnread(@Param("id") Long id);

    int updateAssignment(@Param("id") Long id, @Param("userId") Long userId,
            @Param("owner") String owner, @Param("assignedAt") Date assignedAt);

    int clearAssignment(@Param("id") Long id);

    int updateHostedByAi(@Param("id") Long id, @Param("hostedByAi") Boolean hostedByAi);

    int updateLeadId(@Param("id") Long id, @Param("leadId") String leadId);

    int updateContactId(@Param("appId") String appId, @Param("openId") String openId,
            @Param("contactId") String contactId);

    int updateProfile(@Param("appId") String appId, @Param("openId") String openId,
            @Param("nickname") String nickname, @Param("avatarUrl") String avatarUrl);
}
