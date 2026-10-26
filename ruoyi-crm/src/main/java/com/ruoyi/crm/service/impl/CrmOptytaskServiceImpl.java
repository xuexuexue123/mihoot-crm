package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmOptytaskMapper;
import com.ruoyi.crm.domain.CrmOptytask;
import com.ruoyi.crm.service.ICrmOptytaskService;

/**
 * 机会任务Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-05
 */
@Service
public class CrmOptytaskServiceImpl implements ICrmOptytaskService 
{
    @Autowired
    private CrmOptytaskMapper crmOptytaskMapper;

    /**
     * 查询机会任务
     * 
     * @param taskId 机会任务主键
     * @return 机会任务
     */
    @Override
    public CrmOptytask selectCrmOptytaskByTaskId(String taskId)
    {
        return crmOptytaskMapper.selectCrmOptytaskByTaskId(taskId);
    }

    /**
     * 查询机会任务列表
     * 
     * @param crmOptytask 机会任务
     * @return 机会任务
     */
    @Override
    public List<CrmOptytask> selectCrmOptytaskList(CrmOptytask crmOptytask)
    {
        return crmOptytaskMapper.selectCrmOptytaskList(crmOptytask);
    }

    /**
     * 新增机会任务
     * 
     * @param crmOptytask 机会任务
     * @return 结果
     */
    @Override
    public int insertCrmOptytask(CrmOptytask crmOptytask)
    {
        crmOptytask.setCreateTime(DateUtils.getNowDate());
        return crmOptytaskMapper.insertCrmOptytask(crmOptytask);
    }

    /**
     * 修改机会任务
     * 
     * @param crmOptytask 机会任务
     * @return 结果
     */
    @Override
    public int updateCrmOptytask(CrmOptytask crmOptytask)
    {
        crmOptytask.setUpdateTime(DateUtils.getNowDate());
        return crmOptytaskMapper.updateCrmOptytask(crmOptytask);
    }

    /**
     * 批量删除机会任务
     * 
     * @param taskIds 需要删除的机会任务主键
     * @return 结果
     */
    @Override
    public int deleteCrmOptytaskByTaskIds(String[] taskIds)
    {
        return crmOptytaskMapper.deleteCrmOptytaskByTaskIds(taskIds);
    }

    /**
     * 删除机会任务信息
     * 
     * @param taskId 机会任务主键
     * @return 结果
     */
    @Override
    public int deleteCrmOptytaskByTaskId(String taskId)
    {
        return crmOptytaskMapper.deleteCrmOptytaskByTaskId(taskId);
    }
}
