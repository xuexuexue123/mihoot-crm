package com.ruoyi.crm.service.support;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.vo.WorkflowNodeVO;

public class RecordTriggerNodeSupportTest
{
    private final RecordTriggerNodeSupport support = new RecordTriggerNodeSupport();

    @Test
    public void validateBeforeSaveRejectsDuplicateEntityEventInGraph()
    {
        WorkflowNodeVO a = triggerNode("n1", "lead", "created");
        WorkflowNodeVO b = triggerNode("n2", "lead", "created");
        assertThrows(ServiceException.class,
                () -> support.validateBeforeSave("demo", "wf-1", "wf-1", Arrays.asList(a, b)));
    }

    private WorkflowNodeVO triggerNode(String id, String entityType, String eventType)
    {
        WorkflowNodeVO vo = new WorkflowNodeVO();
        vo.setId(id);
        vo.setType(RecordTriggerUtils.NODE_TYPE);
        Map<String, Object> data = new HashMap<>();
        data.put("entityType", entityType);
        data.put("eventType", eventType);
        vo.setData(data);
        return vo;
    }
}
