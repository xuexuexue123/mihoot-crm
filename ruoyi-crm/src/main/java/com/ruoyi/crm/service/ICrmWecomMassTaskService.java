package com.ruoyi.crm.service;

import java.util.List;

import com.alibaba.fastjson2.JSONObject;

import com.ruoyi.crm.domain.CrmWecomMassTask;
import com.ruoyi.crm.domain.dto.wecom.WecomMassSendCreateRequest;

public interface ICrmWecomMassTaskService
{
    JSONObject createMassSend(WecomMassSendCreateRequest req);

    List<CrmWecomMassTask> selectTaskList(CrmWecomMassTask query);

    /**
     * 当前租户数据源下，待拉取腾讯发送结果的任务主键（按 id 升序，最多 {@code limit} 条）。
     */
    List<Long> selectPendingSyncTaskIds(int limit);

    /**
     * 调用腾讯 get_groupmsg_send_result 并写回本地任务。
     *
     * @param updateBy 写入任务表的 update_by（定时任务可传 {@code wecom-scheduler}）
     */
    JSONObject syncSendResultForTask(Long id, String updateBy);

    JSONObject loadTaskDetailSync(Long id);
}
