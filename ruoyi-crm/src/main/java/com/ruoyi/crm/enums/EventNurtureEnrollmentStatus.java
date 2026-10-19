package com.ruoyi.crm.enums;

/**
 * 培育入组状态
 */
public enum EventNurtureEnrollmentStatus
{
    ACTIVE("active"),
    COMPLETED("completed"),
    EXITED("exited"),
    PAUSED("paused");

    private final String code;

    EventNurtureEnrollmentStatus(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
}
