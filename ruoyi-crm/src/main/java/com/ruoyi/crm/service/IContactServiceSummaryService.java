package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.ContactServiceSummaryPatchDto;
import com.ruoyi.crm.domain.dto.ContactServiceSummaryRefreshDto;
import com.ruoyi.crm.domain.vo.ContactServiceSummaryVo;
import com.ruoyi.crm.domain.vo.KfSummaryCompatVo;

public interface IContactServiceSummaryService
{
    ContactServiceSummaryVo get(String tenantId, String contactId);

    ContactServiceSummaryVo refresh(String tenantId, String contactId, ContactServiceSummaryRefreshDto dto);

    ContactServiceSummaryVo patch(String tenantId, String contactId, ContactServiceSummaryPatchDto dto);

    /**
     * Convert / link 后异步 bootstrap（幂等：已有 ready 摘要则跳过，除非 force）。
     */
    void enqueueBootstrap(String tenantId, String contactId, String trigger, boolean force);

    /**
     * contact/link 成功：合并会话 draft 后 bootstrap。
     */
    void onContactLinked(String tenantId, String contactId, String channel, String conversationKey);

    /**
     * P1 入口：消息增量入队（60s 防抖）。
     */
    void enqueueIncremental(String tenantId, String contactId);

    KfSummaryCompatVo generateKfSummary(String tenantId, String externalUserid, String openKfid);
}
