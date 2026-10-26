package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.dto.CrmWebchatConversationQuery;

public interface CrmWebchatConversationMapper
{
    List<CrmWebchatConversation> selectPage(CrmWebchatConversationQuery query);

    int countInScope(CrmWebchatConversationQuery query);

    CrmWebchatConversation selectById(@Param("id") Long id);

    CrmWebchatConversation selectByIdForUpdate(@Param("id") Long id);

    CrmWebchatConversation selectActiveByVisitorId(@Param("tenantId") String tenantId,
                                                   @Param("visitorId") String visitorId);

    int insert(CrmWebchatConversation row);

    int updateLastMessage(CrmWebchatConversation row);

    int clearUnread(@Param("id") Long id, @Param("tenantId") String tenantId);

    int updateStatus(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("status") String status);

    int updateAssignment(@Param("id") Long id, @Param("tenantId") String tenantId,
                         @Param("userId") Long userId, @Param("owner") String owner,
                         @Param("assignedAt") Date assignedAt);

    int clearAssignment(@Param("id") Long id, @Param("tenantId") String tenantId);

    int updateHostedByAi(@Param("id") Long id, @Param("tenantId") String tenantId,
                         @Param("hostedByAi") Boolean hostedByAi);

    int updateContactLead(@Param("id") Long id, @Param("tenantId") String tenantId,
                          @Param("contactId") String contactId, @Param("leadId") String leadId);

    int updateLeadId(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("leadId") String leadId);

    int closeConversation(@Param("id") Long id, @Param("tenantId") String tenantId,
                          @Param("closedAt") Date closedAt);

    int migrateLeadToContact(@Param("oldLeadId") String oldLeadId,
                             @Param("newContactId") String newContactId);
}
