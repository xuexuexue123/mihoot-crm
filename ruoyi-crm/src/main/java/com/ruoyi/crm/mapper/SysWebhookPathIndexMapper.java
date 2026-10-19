package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.SysWebhookPathIndex;

/**
 * 主库 sys_webhook_path_index Mapper
 */
public interface SysWebhookPathIndexMapper
{
    SysWebhookPathIndex selectByPath(@Param("path") String path);

    List<SysWebhookPathIndex> selectConflictsInTenant(
            @Param("tenantId") String tenantId,
            @Param("workflowId") String workflowId,
            @Param("paths") List<String> paths);

    int upsert(SysWebhookPathIndex row);

    int deleteByWorkflowId(@Param("workflowId") String workflowId);
}
