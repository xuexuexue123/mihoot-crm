package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WScheduledTask;

/**
 * 工作流定时任务 CRUD
 */
public interface IWScheduledTaskService
{
    WScheduledTask selectWScheduledTaskById(String id);

    List<WScheduledTask> selectWScheduledTaskList(WScheduledTask query);

    int insertWScheduledTask(WScheduledTask task);

    int updateWScheduledTask(WScheduledTask task);

    int deleteWScheduledTaskByIds(String[] ids);

    int deleteWScheduledTaskById(String id);
}
