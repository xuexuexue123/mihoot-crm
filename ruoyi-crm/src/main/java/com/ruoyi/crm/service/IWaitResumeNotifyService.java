package com.ruoyi.crm.service;

/**
 * Java CRM 写库后通知 sevencrm，由 Next.js 发送 Inngest {@code wait.resume.{resumeKey}} 事件。
 *
 * @see docs/wait-resume-inngest-integration.md §7.2
 */
public interface IWaitResumeNotifyService
{
    /**
     * 在 tenant 库 {@code w_wait_resume} 更新成功后，回调
     * {@code POST {notify-base-url}/waitresume-notify/{resumeKey}}。
     * 失败仅记日志，不抛异常。
     */
    void notifyAfterCrmUpdate(String resumeKey, String tenantId);
}
