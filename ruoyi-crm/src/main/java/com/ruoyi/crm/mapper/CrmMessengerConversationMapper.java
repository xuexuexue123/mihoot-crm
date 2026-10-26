package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmMessengerConversation;
import com.ruoyi.crm.domain.dto.CrmMessengerConversationQuery;

public interface CrmMessengerConversationMapper
{
    List<CrmMessengerConversation> selectPage(CrmMessengerConversationQuery query);

    int countInScope(CrmMessengerConversationQuery query);

    CrmMessengerConversation selectById(@Param("id") Long id);

    CrmMessengerConversation selectByIdForUpdate(@Param("id") Long id);

    CrmMessengerConversation selectByPsid(@Param("tenantId") String tenantId, @Param("psid") String psid);

    int upsertConversation(CrmMessengerConversation row);

    int updateContactId(@Param("tenantId") String tenantId, @Param("psid") String psid,
                        @Param("contactId") String contactId);

    int updateLeadId(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("leadId") String leadId);

    int updateLastMessage(CrmMessengerConversation row);

    int clearUnread(@Param("id") Long id, @Param("tenantId") String tenantId);

    int updateStatus(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("status") String status);

    int updateAssignment(@Param("id") Long id, @Param("tenantId") String tenantId,
                         @Param("userId") Long userId, @Param("owner") String owner,
                         @Param("assignedAt") Date assignedAt);

    int clearAssignment(@Param("id") Long id, @Param("tenantId") String tenantId);

    int updateHostedByAi(@Param("id") Long id, @Param("tenantId") String tenantId,
                         @Param("hostedByAi") Boolean hostedByAi);

    long countActive(
            @Param("tenantId") String tenantId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime);
}
