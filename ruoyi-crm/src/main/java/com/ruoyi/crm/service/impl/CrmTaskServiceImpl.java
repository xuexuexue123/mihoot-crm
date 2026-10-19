package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmTaskMapper;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.service.ICrmTaskService;

/**
 * 任务Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-21
 */
@Service
public class CrmTaskServiceImpl implements ICrmTaskService 
{
    @Autowired
    private CrmTaskMapper crmTaskMapper;

    @Autowired
    private com.ruoyi.crm.service.support.LeadFollowupAnchorSupport leadFollowupAnchorSupport;

    /**
     * 查询任务
     * 
     * @param taskId 任务主键
     * @return 任务
     */
    @Override
    public CrmTask selectCrmTaskByTaskId(String taskId)
    {
        return crmTaskMapper.selectCrmTaskByTaskId(taskId);
    }


    /**
     * 根据关联的id，查询所有关联的任务
     * @param relatedToids
     * @return
     */
    public List<CrmTask> selectCrmTaskListByTaskIds(String[] relatedToids) {
        return crmTaskMapper.selectCrmTaskListByTaskIds(relatedToids);
    }

    /**
     * 查询任务列表
     * 
     * @param crmTask 任务
     * @return 任务
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskList(CrmTask crmTask)
    {
        return crmTaskMapper.selectCrmTaskList(crmTask);
    }

    /**
     * 选择系统创建的DD任务列表
     * @param crmTask
     * @return
     */
    @Override
    public List<CrmTask> selectCrmTaskListDD(CrmTask crmTask) {
        return crmTaskMapper.selectCrmTaskListDD(crmTask);
    }
    /**
     * 选择不等于指定状态的任务列表
     * @param crmTask
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskListUncompleted(CrmTask crmTask)
    {
        return crmTaskMapper.selectCrmTaskListUncompleted(crmTask);
    }

    /**
     * 选择不等于指定状态的任务列表
     * @param crmTask
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskListNotsystem(CrmTask crmTask)
    {
        return crmTaskMapper.selectCrmTaskListNotsystem(crmTask);
    }

    /**
     * 查询本周过期的任务
     * @param crmTask
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskListUncompletedWeekly(CrmTask crmTask) {
        return crmTaskMapper.selectCrmTaskListUncompletedWeekly(crmTask);
    }

    /**
     * 查询下周过期的任务
     * @param crmTask
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskListUncompletedNextWeekly(CrmTask crmTask) {
        return crmTaskMapper.selectCrmTaskListUncompletedNextWeekly(crmTask);
    }

    /**
     * 查询本月过期的任务
     * @param crmTask
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskListUncompletedMonthly(CrmTask crmTask) {
        return crmTaskMapper.selectCrmTaskListUncompletedMonthly(crmTask);
    }

    /**
     * 查询本月完成的任务
     * @param crmTask
     * @return
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmTask> selectCrmTaskListCompletedMonthly(CrmTask crmTask) {
        return crmTaskMapper.selectCrmTaskListCompletedMonthly(crmTask);
    }


    /**
     * 新增任务
     * 
     * @param crmTask 任务
     * @return 结果
     */
    @Override
    public int insertCrmTask(CrmTask crmTask)
    {
        crmTask.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
//        crmTask.setUserId(loginUser.getUserId());
//        crmTask.setDeptId(loginUser.getDeptId());
        return crmTaskMapper.insertCrmTask(crmTask);
    }

    /**
     * 修改任务
     * 
     * @param crmTask 任务
     * @return 结果
     */
    @Override
    public int updateCrmTask(CrmTask crmTask)
    {
        CrmTask previous = crmTask.getTaskId() != null
                ? crmTaskMapper.selectCrmTaskByTaskId(crmTask.getTaskId()) : null;
        crmTask.setUpdateTime(DateUtils.getNowDate());
        int rows = crmTaskMapper.updateCrmTask(crmTask);
        if (rows > 0)
        {
            leadFollowupAnchorSupport.onTaskCompleted(previous, crmTask);
        }
        return rows;
    }

    /**
     * 批量删除任务
     * 
     * @param taskIds 需要删除的任务主键
     * @return 结果
     */
    @Override
    public int deleteCrmTaskByTaskIds(String[] taskIds)
    {
        return crmTaskMapper.deleteCrmTaskByTaskIds(taskIds);
    }

    /**
     * 删除任务信息
     * 
     * @param taskId 任务主键
     * @return 结果
     */
    @Override
    public int deleteCrmTaskByTaskId(String taskId)
    {
        return crmTaskMapper.deleteCrmTaskByTaskId(taskId);
    }
}
