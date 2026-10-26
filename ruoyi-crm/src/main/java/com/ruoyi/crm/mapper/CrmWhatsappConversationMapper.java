package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappConversation;
import com.ruoyi.crm.domain.dto.CrmWhatsappConversationQuery;

public interface CrmWhatsappConversationMapper
{
    List<CrmWhatsappConversation> selectPage(CrmWhatsappConversationQuery query);

    int countInScope(CrmWhatsappConversationQuery query);

    CrmWhatsappConversation selectById(@Param("id") Long id);

    CrmWhatsappConversation selectByIdForUpdate(@Param("id") Long id);

    CrmWhatsappConversation selectByWaId(@Param("tenantId") String tenantId, @Param("waId") String waId);

    int upsertConversation(CrmWhatsappConversation row);

    int updateContactId(@Param("tenantId") String tenantId, @Param("waId") String waId,
                        @Param("contactId") String contactId);

    int updateLeadId(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("leadId") String leadId);

    int updateLastMessage(CrmWhatsappConversation row);

    int clearUnread(@Param("id") Long id, @Param("tenantId") String tenantId);

    int updateStatus(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("status") String status);

    int updateAssignment(@Param("id") Long id, @Param("tenantId") String tenantId,
                         @Param("userId") Long userId, @Param("owner") String owner,
                         @Param("assignedAt") Date assignedAt);

    int clearAssignment(@Param("id") Long id, @Param("tenantId") String tenantId);

    int updateHostedByAi(@Param("id") Long id, @Param("tenantId") String tenantId,
                         @Param("hostedByAi") Boolean hostedByAi);

    /**
     * 时间窗内活跃或新建的会话数：
     * last_message_at / create_time 落入 [start, end]，或窗内有消息。
     */
    long countActiveInWindow(
            @Param("tenantId") String tenantId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("phoneNumberId") String phoneNumberId);
}
