package com.ruoyi.crm.service.support;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.ruoyi.common.utils.StringUtils;

/**
 * 检测工作流内 CRM 写操作，防止 record trigger 循环 dispatch
 */
public final class RecordTriggerLoopGuard
{
    public static final String WORKFLOW_EXECUTION_HEADER = "X-Workflow-Execution-Id";

    private RecordTriggerLoopGuard()
    {
    }

    public static boolean shouldSkipDispatch()
    {
        return StringUtils.isNotBlank(currentWorkflowExecutionId());
    }

    public static String currentWorkflowExecutionId()
    {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null)
        {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        if (request == null)
        {
            return null;
        }
        String value = request.getHeader(WORKFLOW_EXECUTION_HEADER);
        return StringUtils.isNotBlank(value) ? value.trim() : null;
    }
}
