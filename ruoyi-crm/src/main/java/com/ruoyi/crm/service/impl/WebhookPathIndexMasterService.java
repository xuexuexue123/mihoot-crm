package com.ruoyi.crm.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.DataSource;
import com.ruoyi.common.enums.DataSourceType;
import com.ruoyi.crm.domain.SysWebhookPathIndex;
import com.ruoyi.crm.mapper.SysWebhookPathIndexMapper;

/**
 * 主库 sys_webhook_path_index 访问（{@link DataSource} 切面会在返回后 clear，调用方需 restore tenant key）
 */
@Service
public class WebhookPathIndexMasterService
{
    @Autowired
    private SysWebhookPathIndexMapper sysWebhookPathIndexMapper;

    @DataSource(DataSourceType.MASTER)
    public SysWebhookPathIndex selectByPath(String path)
    {
        return sysWebhookPathIndexMapper.selectByPath(path);
    }

    @DataSource(DataSourceType.MASTER)
    public List<SysWebhookPathIndex> selectConflictsInTenant(String tenantId, String workflowId, List<String> paths)
    {
        if (paths == null || paths.isEmpty())
        {
            return Collections.emptyList();
        }
        return sysWebhookPathIndexMapper.selectConflictsInTenant(tenantId, workflowId, paths);
    }

    @DataSource(DataSourceType.MASTER)
    public void upsert(SysWebhookPathIndex row)
    {
        sysWebhookPathIndexMapper.upsert(row);
    }

    @DataSource(DataSourceType.MASTER)
    public void deleteByWorkflowId(String workflowId)
    {
        sysWebhookPathIndexMapper.deleteByWorkflowId(workflowId);
    }
}
