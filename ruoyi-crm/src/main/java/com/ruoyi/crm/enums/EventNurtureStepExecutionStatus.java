package com.ruoyi.crm.enums;

/**
 * 培育步骤执行状态
 */
public enum EventNurtureStepExecutionStatus
{
    PENDING("pending"),
    SENT("sent"),
    SKIPPED("skipped"),
    FAILED("failed");

    private final String code;

    EventNurtureStepExecutionStatus(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}
