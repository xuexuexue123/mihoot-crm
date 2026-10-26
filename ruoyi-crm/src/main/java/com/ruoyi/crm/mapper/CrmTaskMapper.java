package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmTask;

/**
 * 任务Mapper接口
 * 
 * @author luohd
 * @date 2024-01-21
 */
public interface CrmTaskMapper 
{
    /**
     * 查询任务
     * 
     * @param taskId 任务主键
     * @return 任务
     */
    public CrmTask selectCrmTaskByTaskId(String taskId);

    /**
     * 根据关联的id，查询所有关联的任务
     * @param relatedToids
     * @return
     */
    public List<CrmTask> selectCrmTaskListByTaskIds(String[] relatedToids);

    /**
     * 查询任务列表
     * 
     * @param crmTask 任务
     * @return 任务集合
     */
    public List<CrmTask> selectCrmTaskList(CrmTask crmTask);

    /**
     * 选择不等于指定状态的任务列表
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListUncompleted(CrmTask crmTask);

    /**
     * 选择不等于系统创建的任务列表
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListNotsystem(CrmTask crmTask);

    /**
     * 选择系统创建的DD任务列表
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListDD(CrmTask crmTask);

    /**
     * 选择本周未完成的任务
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListUncompletedWeekly(CrmTask crmTask);

    /**
     * 选择下周未完成的任务
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListUncompletedNextWeekly(CrmTask crmTask);



    /**
     * 选择本月未完成的任务
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListUncompletedMonthly(CrmTask crmTask);

    /**
     * 选择本月完成的任务
     * @param crmTask
     * @return
     */
    public List<CrmTask> selectCrmTaskListCompletedMonthly(CrmTask crmTask);

    /**
     * 新增任务
     * 
     * @param crmTask 任务
     * @return 结果
     */
    public int insertCrmTask(CrmTask crmTask);

    /**
     * 修改任务
     * 
     * @param crmTask 任务
     * @return 结果
     */
    public int updateCrmTask(CrmTask crmTask);

    /**
     * 删除任务
     * 
     * @param taskId 任务主键
     * @return 结果
     */
    public int deleteCrmTaskByTaskId(String taskId);

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmTaskByTaskIds(String[] taskIds);
}
