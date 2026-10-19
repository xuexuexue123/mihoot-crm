package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomKfConversation;
import com.ruoyi.crm.domain.dto.WecomKfConversationQuery;

public interface CrmWecomKfConversationMapper
{
    List<CrmWecomKfConversation> selectPage(WecomKfConversationQuery query);

    int countInScope(WecomKfConversationQuery query);

    CrmWecomKfConversation selectById(@Param("id") Long id);

    CrmWecomKfConversation selectByIdForUpdate(@Param("id") Long id);

    CrmWecomKfConversation selectByOpenKfidExternalUserid(
            @Param("openKfid") String openKfid,
            @Param("externalUserid") String externalUserid);

    int upsertOnInbound(CrmWecomKfConversation row);

    int upsertOnOutbound(CrmWecomKfConversation row);

    int clearUnread(@Param("id") Long id);

    int updateAssignment(@Param("id") Long id, @Param("userId") Long userId,
            @Param("owner") String owner, @Param("assignedAt") Date assignedAt);

    int updateHostedByAi(@Param("id") Long id, @Param("hostedByAi") Boolean hostedByAi);

    int clearAssignment(@Param("id") Long id);

    int updateContact(@Param("openKfid") String openKfid,
            @Param("externalUserid") String externalUserid,
            @Param("contactId") String contactId,
            @Param("contactName") String contactName);

    int updateContactByExternalUserid(@Param("externalUserid") String externalUserid,
            @Param("contactId") String contactId,
            @Param("contactName") String contactName);

    int clearContact(@Param("openKfid") String openKfid,
            @Param("externalUserid") String externalUserid);

    int clearContactByExternalUserid(@Param("externalUserid") String externalUserid);

    int updateProfile(@Param("openKfid") String openKfid,
            @Param("externalUserid") String externalUserid,
            @Param("nickname") String nickname,
            @Param("avatarUrl") String avatarUrl);
}
