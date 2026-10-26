package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageBatchRequest;

public interface IWechatMpMessageTaskService
{
    CrmWechatMpMessageTask createBatch(String tenantId, WechatMpMessageBatchRequest req);

    List<CrmWechatMpMessageTask> selectTaskList(CrmWechatMpMessageTask query);

    CrmWechatMpMessageTask getTaskDetail(Long id);

    void processPendingTasks();
}
