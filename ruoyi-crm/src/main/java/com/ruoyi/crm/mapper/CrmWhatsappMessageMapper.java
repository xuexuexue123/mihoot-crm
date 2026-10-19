package com.ruoyi.crm.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappMessage;

public interface CrmWhatsappMessageMapper
{
    int insert(CrmWhatsappMessage row);

    CrmWhatsappMessage selectByMessageId(
            @Param("tenantId") String tenantId,
            @Param("messageId") String messageId);

    int updateStatusByMessageId(
            @Param("tenantId") String tenantId,
            @Param("messageId") String messageId,
            @Param("status") String status);

    List<CrmWhatsappMessage> selectByConversationId(
            @Param("conversationId") Long conversationId,
            @Param("beforeMsgId") Long beforeMsgId,
            @Param("pageSize") Integer pageSize
    );

    CrmWhatsappMessage selectByMediaId(
            @Param("tenantId") String tenantId,
            @Param("mediaId") String mediaId);

    CrmWhatsappMessage selectById(
            @Param("tenantId") String tenantId,
            @Param("id") Long id);

    int updateMediaUrl(
            @Param("tenantId") String tenantId,
            @Param("id") Long id,
            @Param("mediaUrl") String mediaUrl);

    /** 出站模板消息按 status 计数（时间窗口：send_time 毫秒） */
    long countTemplateOutbound(
            @Param("tenantId") String tenantId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("phoneNumberId") String phoneNumberId,
            @Param("statuses") java.util.List<String> statuses);

    /**
     * 模板发送后 N 毫秒内有入站回复的会话数。
     */
    long countTemplateConversationsWithReply(
            @Param("tenantId") String tenantId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("phoneNumberId") String phoneNumberId,
            @Param("replyWindowMs") long replyWindowMs);

    long countTemplateConversations(
            @Param("tenantId") String tenantId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("phoneNumberId") String phoneNumberId);

    /** 指定 wa_id 是否有过入站消息 */
    long countInboundByWaId(@Param("tenantId") String tenantId, @Param("waId") String waId);

    /**
     * 按自然日聚合模板运营指标。
     * 每行：date, sent, delivered, read, template_conv, template_conv_replied
     */
    List<Map<String, Object>> selectTemplateDailyStats(
            @Param("tenantId") String tenantId,
            @Param("startTime") Long startTime,
            @Param("endTime") Long endTime,
            @Param("phoneNumberId") String phoneNumberId,
            @Param("replyWindowMs") long replyWindowMs);
}
