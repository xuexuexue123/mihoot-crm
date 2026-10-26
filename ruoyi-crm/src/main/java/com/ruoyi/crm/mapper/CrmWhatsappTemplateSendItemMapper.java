package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappTemplateSendItem;

public interface CrmWhatsappTemplateSendItemMapper
{
    int insertBatch(@Param("list") List<CrmWhatsappTemplateSendItem> list);

    int insert(CrmWhatsappTemplateSendItem row);

    List<CrmWhatsappTemplateSendItem> selectByTaskId(
            @Param("taskId") Long taskId,
            @Param("tenantId") String tenantId,
            @Param("failedFirst") boolean failedFirst);

    List<CrmWhatsappTemplateSendItem> selectPendingByTaskId(@Param("taskId") Long taskId);

    int updateSendResult(@Param("id") Long id,
                         @Param("status") String status,
                         @Param("messageId") String messageId,
                         @Param("errorCode") String errorCode,
                         @Param("errorMessage") String errorMessage);

    int updateStatusByMessageId(@Param("tenantId") String tenantId,
                                @Param("messageId") String messageId,
                                @Param("status") String status);

    int countByRuleAndBusinessKey(@Param("ruleId") Long ruleId,
                                  @Param("businessKey") String businessKey);

    /** 将 failed items 重置为 pending，返回影响行数 */
    int resetFailedToPending(@Param("taskId") Long taskId, @Param("tenantId") String tenantId);

    int countByTaskAndStatus(@Param("taskId") Long taskId, @Param("status") String status);
}
