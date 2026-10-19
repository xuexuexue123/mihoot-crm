package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

public class EventTriggerNodeSupportTest
{
    private final EventTriggerNodeSupport support = new EventTriggerNodeSupport();

    @Test
    public void validateBeforeSaveRejectsDuplicateEventTypeInGraph()
    {
        WorkflowNodeVO a = triggerNode("n1", "engagement.survey.completed");
        WorkflowNodeVO b = triggerNode("n2", "engagement.survey.completed");
        assertThrows(ServiceException.class,
                () -> support.validateBeforeSave("demo", "wf-1", "wf-1", Arrays.asList(a, b)));
    }

    private WorkflowNodeVO triggerNode(String id, String eventType)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(EventTriggerUtils.NODE_TYPE);
        Map<String, Object> data = new HashMap<>();
        data.put("eventType", eventType);
        vo.setData(data);
        return vo;
    }
}
