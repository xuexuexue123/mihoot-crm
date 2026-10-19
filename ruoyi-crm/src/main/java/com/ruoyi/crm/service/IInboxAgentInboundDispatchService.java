package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.InboxAgentInboundPayload;

/**
 * 入站落库后异步通知 sevencrm Inbox AI Agent worker（非 SSE）。
 */
public interface IInboxAgentInboundDispatchService
{
    /**
     * 若有活跃事务则 afterCommit 再发；否则立即异步发送。
     * 永不抛出到调用方（失败只打日志）。
     */
    void scheduleDispatch(String tenant, InboxAgentInboundPayload payload);
}
