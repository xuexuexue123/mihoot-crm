package com.ruoyi.crm.service.support;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.WWorkflow;

/**
 * 工作流模板硬约束：模板不可作为可执行目标。
 */
public final class WorkflowTemplateGuard
{
    public static final String TEMPLATE_NOT_EXECUTABLE = "模板不可执行";

    private WorkflowTemplateGuard()
    {
    }

    public static boolean isTemplate(WWorkflow workflow)
    {
        return workflow != null && Boolean.TRUE.equals(workflow.getIsTemplate());
    }

    /**
     * 对模板抛出 400。
     */
    public static void assertExecutable(WWorkflow workflow)
    {
        if (isTemplate(workflow))
        {
            throw new ServiceException(TEMPLATE_NOT_EXECUTABLE, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 解析 query / body 中的 isTemplate：兼容 0/1/true/false。
     */
    public static Boolean parseIsTemplateFlag(Object raw)
    {
        if (raw == null)
        {
            return null;
        }
        if (raw instanceof Boolean)
        {
            return (Boolean) raw;
        }
        String text = String.valueOf(raw).trim();
        if (text.isEmpty())
        {
            return null;
        }
        if ("1".equals(text) || "true".equalsIgnoreCase(text))
        {
            return Boolean.TRUE;
        }
        if ("0".equals(text) || "false".equalsIgnoreCase(text))
        {
            return Boolean.FALSE;
        }
        throw new ServiceException("isTemplate 仅支持 0/1/true/false", HttpStatus.BAD_REQUEST);
    }
}
