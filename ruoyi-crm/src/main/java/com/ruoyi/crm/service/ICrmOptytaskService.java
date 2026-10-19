package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmOptytask;

/**
 * 机会任务Service接口
 * 
 * @author luohd
 * @date 2024-03-05
 */
public interface ICrmOptytaskService 
{
    /**
     * 查询机会任务
     * 
     * @param taskId 机会任务主键
     * @return 机会任务
     */
    public CrmOptytask selectCrmOptytaskByTaskId(String taskId);

    /**
     * 查询机会任务列表
     * 
     * @param crmOptytask 机会任务
     * @return 机会任务集合
     */
    public List<CrmOptytask> selectCrmOptytaskList(CrmOptytask crmOptytask);

    /**
     * 新增机会任务
     * 
     * @param crmOptytask 机会任务
     * @return 结果
     */
    public int insertCrmOptytask(CrmOptytask crmOptytask);

    /**
     * 修改机会任务
     * 
     * @param crmOptytask 机会任务
     * @return 结果
     */
    public int updateCrmOptytask(CrmOptytask crmOptytask);

    /**
     * 批量删除机会任务
     * 
     * @param taskIds 需要删除的机会任务主键集合
     * @return 结果
     */
    public int deleteCrmOptytaskByTaskIds(String[] taskIds);

    /**
     * 删除机会任务信息
     * 
     * @param taskId 机会任务主键
     * @return 结果
     */
    public int deleteCrmOptytaskByTaskId(String taskId);
}
