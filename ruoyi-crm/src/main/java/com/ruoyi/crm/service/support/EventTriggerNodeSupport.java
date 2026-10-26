package com.ruoyi.crm.service.support;

import java.util.List;
import org.springframework.stereotype.Component;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

/**
 * CRM_EVENT_TRIGGER 节点：工作流保存前校验
 */
@Component
public class EventTriggerNodeSupport
{
    public void validateBeforeSave(String tenantId, String workflowId, String workflowName, List<WorkflowNodeVO> nodes)
    {
        EventTriggerUtils.validateBeforeSave(nodes, workflowName);
    }
}
