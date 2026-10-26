package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReWorktaskMapper;
import com.ruoyi.ems.domain.SysReWorktask;
import com.ruoyi.ems.service.ISysReWorktaskService;

/**
 * 任务Service业务层处理
 * 
 * @author luohd
 * @date 2023-04-07
 */
@Service
public class SysReWorktaskServiceImpl implements ISysReWorktaskService 
{
    @Autowired
    private SysReWorktaskMapper sysReWorktaskMapper;

    /**
     * 查询任务
     * 
     * @param taskId 任务主键
     * @return 任务
     */
    @Override
    public SysReWorktask selectSysReWorktaskByTaskId(String taskId)
    {
        return sysReWorktaskMapper.selectSysReWorktaskByTaskId(taskId);
    }

    /**
     * 查询任务列表
     * 
     * @param sysReWorktask 任务
     * @return 任务
     */
    @Override
    public List<SysReWorktask> selectSysReWorktaskList(SysReWorktask sysReWorktask)
    {
        return sysReWorktaskMapper.selectSysReWorktaskList(sysReWorktask);
    }


    /**
     *
     * @param assignee
     * @return
     */
    @Override
    public List<SysReWorktask> selectSysReWorktaskListByAssignee(String assignee) {
        return sysReWorktaskMapper.selectSysReWorktaskListByAssignee(assignee);
    }

    /**
     *
     * @param assignee
     * @return
     */
    @Override
    public List<SysReWorktask> selectSysReWorktaskListByAssigneeComp(String assignee) {
        return sysReWorktaskMapper.selectSysReWorktaskListByAssigneeComp(assignee);
    }

    /**
     *
     * @param assignee
     * @return
     */
    @Override
    public List<SysReWorktask> selectSysReWorktaskListByAssigneeWIP(String assignee) {
        return sysReWorktaskMapper.selectSysReWorktaskListByAssigneeWIP(assignee);
    }

    /**
     * 新增任务
     * 
     * @param sysReWorktask 任务
     * @return 结果
     */
    @Override
    public int insertSysReWorktask(SysReWorktask sysReWorktask)
    {
        if (sysReWorktask.getCreateTime() == null) {

            sysReWorktask.setCreateTime(DateUtils.getNowDate());
        }
        return sysReWorktaskMapper.insertSysReWorktask(sysReWorktask);
    }

    /**
     * 修改任务
     * 
     * @param sysReWorktask 任务
     * @return 结果
     */
    @Override
    public int updateSysReWorktask(SysReWorktask sysReWorktask)
    {
        sysReWorktask.setUpdateTime(DateUtils.getNowDate());
        return sysReWorktaskMapper.updateSysReWorktask(sysReWorktask);
    }

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务主键
     * @return 结果
     */
    @Override
    public int deleteSysReWorktaskByTaskIds(String[] taskIds)
    {
        return sysReWorktaskMapper.deleteSysReWorktaskByTaskIds(taskIds);
    }

    /**
     * 删除任务信息
     * 
     * @param taskId 任务主键
     * @return 结果
     */
    @Override
    public int deleteSysReWorktaskByTaskId(String taskId)
    {
        return sysReWorktaskMapper.deleteSysReWorktaskByTaskId(taskId);
    }


    /**
     * 删除任务信息
     *
     * @param taskId 任务主键
     * @return 结果
     */
    @Override
    public int deleteSysReWorktaskByBoardId(String boardId)
    {
        return sysReWorktaskMapper.deleteSysReWorktaskByBoardId(boardId);
    }
}
