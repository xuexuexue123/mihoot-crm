package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysReWorktask;

/**
 * 任务Service接口
 * 
 * @author luohd
 * @date 2023-04-07
 */
public interface ISysReWorktaskService 
{
    /**
     * 查询任务
     * 
     * @param taskId 任务主键
     * @return 任务
     */
    public SysReWorktask selectSysReWorktaskByTaskId(String taskId);


    /**
     * 查询任务列表
     * 
     * @param sysReWorktask 任务
     * @return 任务集合
     */
    public List<SysReWorktask> selectSysReWorktaskList(SysReWorktask sysReWorktask);

    /**
     *
     * @param assignee
     * @return
     */
    public List<SysReWorktask> selectSysReWorktaskListByAssignee(String assignee);


    /**
     *
     * @param assignee
     * @return
     */
    public List<SysReWorktask> selectSysReWorktaskListByAssigneeComp(String assignee);

    /**
     *
     * @param assignee
     * @return
     */
    public List<SysReWorktask> selectSysReWorktaskListByAssigneeWIP(String assignee);

    /**
     * 新增任务
     * 
     * @param sysReWorktask 任务
     * @return 结果
     */
    public int insertSysReWorktask(SysReWorktask sysReWorktask);

    /**
     * 修改任务
     * 
     * @param sysReWorktask 任务
     * @return 结果
     */
    public int updateSysReWorktask(SysReWorktask sysReWorktask);

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务主键集合
     * @return 结果
     */
    public int deleteSysReWorktaskByTaskIds(String[] taskIds);

    /**
     * 删除任务信息
     * 
     * @param taskId 任务主键
     * @return 结果
     */
    public int deleteSysReWorktaskByTaskId(String taskId);

    /**
     * 删除指定的Stage下所有任务
     * @param pyId
     * @return
     */
    public int deleteSysReWorktaskByBoardId(String boardId);
}
