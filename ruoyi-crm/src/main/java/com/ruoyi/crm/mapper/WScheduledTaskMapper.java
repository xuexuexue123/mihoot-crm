package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.WScheduledTask;

/**
 * 工作流定时任务 Mapper
 */
public interface WScheduledTaskMapper
{
    WScheduledTask selectWScheduledTaskById(String id);

    List<WScheduledTask> selectWScheduledTaskList(WScheduledTask query);

    int insertWScheduledTask(WScheduledTask task);

    int updateWScheduledTask(WScheduledTask task);

    int deleteWScheduledTaskById(String id);

    int deleteWScheduledTaskByIds(String[] ids);

    /**
     * 到期候选：enabled、run_time 匹配，且尚未抢占当前 slot；weekly 需包含 dayOfWeek
     */
    List<WScheduledTask> selectDueCandidates(@Param("runTime") String runTime,
            @Param("dayOfWeek") String dayOfWeek, @Param("slot") String slot);

    /**
     * 原子抢占：仅当 last_fired_slot 为空或不等于 slot 时更新
     *
     * @return 影响行数（1=抢占成功）
     */
    int claimSlot(@Param("id") String id, @Param("slot") String slot);
}
