package com.ruoyi.crm.service.support;

import java.util.List;
import org.springframework.stereotype.Component;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * CRM_RECORD_TRIGGER 节点：工作流保存前校验（路由查 tenant w_node，无主库索引）
 */
@Component
public class RecordTriggerNodeSupport
{
    public void validateBeforeSave(String tenantId, String workflowId, String workflowName, List<WorkflowNodeVO> nodes)
    {
        RecordTriggerUtils.validateBeforeSave(nodes, workflowName);
    }
}
