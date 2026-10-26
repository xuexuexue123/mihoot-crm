package com.ruoyi.crm.service.nurture.support;

/**
 * 单步执行结果
 */
public class EventNurtureStepResult
{
    private final boolean success;
    private final boolean skipped;
    private final boolean retryable;
    private final String status;
    private final String skipReason;
    private final String externalRef;
    private final String errorMsg;

    private EventNurtureStepResult(boolean success, boolean skipped, boolean retryable,
            String status, String skipReason, String externalRef, String errorMsg)
    {
        this.success = success;
        this.skipped = skipped;
        this.retryable = retryable;
        this.status = status;
        this.skipReason = skipReason;
        this.externalRef = externalRef;
        this.errorMsg = errorMsg;
    }

    public static EventNurtureStepResult sent(String externalRef)
    {
        return new EventNurtureStepResult(true, false, false, "sent", null, externalRef, null);
    }

    public static EventNurtureStepResult skipped(String reason)
    {
        return new EventNurtureStepResult(false, true, false, "skipped", reason, null, null);
    }

    public static EventNurtureStepResult failed(String errorMsg, boolean retryable)
    {
        return new EventNurtureStepResult(false, false, retryable, "failed", null, null, errorMsg);
    }

    public static EventNurtureStepResult duplicate()
    {
        return skipped("duplicate");
    }

    public boolean isSuccess()
    {
        return success;
    }

    public boolean isSkipped()
    {
        return skipped;
    }

    public boolean isRetryable()
    {
        return retryable;
    }

    public String getStatus()
    {
        return status;
    }

    public String getSkipReason()
    {
        return skipReason;
    }

    public String getExternalRef()
    {
        return externalRef;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }
}
