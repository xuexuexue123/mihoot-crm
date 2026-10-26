package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWhatsappTemplateSendTask;

public interface CrmWhatsappTemplateSendTaskMapper
{
    int insert(CrmWhatsappTemplateSendTask row);

    CrmWhatsappTemplateSendTask selectById(@Param("id") Long id, @Param("tenantId") String tenantId);

    List<CrmWhatsappTemplateSendTask> selectPage(
            @Param("tenantId") String tenantId,
            @Param("status") String status);

    /** 乐观抢占：pending → running，返回影响行数 */
    int claimPending(@Param("id") Long id, @Param("tenantId") String tenantId,
                     @Param("startedAt") Date startedAt);

    int updateCounts(@Param("id") Long id,
                     @Param("successCount") int successCount,
                     @Param("failedCount") int failedCount,
                     @Param("skippedCount") int skippedCount);

    int markFinished(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("errorMessage") String errorMessage,
                     @Param("finishedAt") Date finishedAt,
                     @Param("successCount") Integer successCount,
                     @Param("failedCount") Integer failedCount,
                     @Param("skippedCount") Integer skippedCount);

    /** 失败重试：回到 pending，清空 finished，下调 failed_count */
    int reopenForRetry(@Param("id") Long id,
                       @Param("tenantId") String tenantId,
                       @Param("failedCount") int failedCount);
}
