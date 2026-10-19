package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTask;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTaskItem;

public interface CrmWechatMpMessageTaskMapper
{
    int insertTask(CrmWechatMpMessageTask task);

    int updateTask(CrmWechatMpMessageTask task);

    CrmWechatMpMessageTask selectTaskById(@Param("id") Long id);

    List<CrmWechatMpMessageTask> selectTaskList(CrmWechatMpMessageTask query);

    List<Long> selectPendingTaskIds(@Param("limit") int limit);

    int insertTaskItem(CrmWechatMpMessageTaskItem item);

    int insertTaskItemsBatch(@Param("items") List<CrmWechatMpMessageTaskItem> items);

    int updateTaskItem(CrmWechatMpMessageTaskItem item);

    List<CrmWechatMpMessageTaskItem> selectItemsByTaskId(@Param("taskId") Long taskId);

    List<CrmWechatMpMessageTaskItem> selectPendingItems(@Param("taskId") Long taskId, @Param("limit") int limit);
}
