package com.ruoyi.crm.wechatmp.service;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMassTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassPreviewRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassQuotaVo;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassSendRequest;

public interface IWechatMpMassService
{
    JSONObject preview(WechatMpMassPreviewRequest req);

    CrmWechatMpMassTask send(WechatMpMassSendRequest req);

    List<CrmWechatMpMassTask> selectTaskList(CrmWechatMpMassTask query);

    CrmWechatMpMassTask getTask(Long id);

    CrmWechatMpMassTask refreshTask(Long id);

    CrmWechatMpMassTask deleteTask(Long id, Integer articleIdx);

    WechatMpMassQuotaVo getQuota(String appId);

    /**
     * 回调更新群发结果（幂等）。找不到任务时仅记日志。
     */
    void applyMassSendJobFinish(String appId, String msgId, String wxStatus,
            Integer totalCount, Integer filterCount, Integer sentCount, Integer errorCount);
}
