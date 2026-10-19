package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmActivity;

/**
 * 业务活动Service接口
 * 
 * @author luohd
 * @date 2024-01-21
 */
public interface ICrmActivityService 
{
    /**
     * 查询业务活动
     * 
     * @param activityId 业务活动主键
     * @return 业务活动
     */
    public CrmActivity selectCrmActivityByActivityId(String activityId);

    /**
     * 查询业务活动列表
     * 
     * @param crmActivity 业务活动
     * @return 业务活动集合
     */
    public List<CrmActivity> selectCrmActivityList(CrmActivity crmActivity);

    /**
     * 查询本月业务活动列表
     *
     * @param crmActivity 业务活动
     * @return 业务活动集合
     */
    public List<CrmActivity> selectCrmActivityListMonthly(CrmActivity crmActivity);

    /**
     * 新增业务活动
     * 
     * @param crmActivity 业务活动
     * @return 结果
     */
    public int insertCrmActivity(CrmActivity crmActivity);

    /**
     * 修改业务活动
     * 
     * @param crmActivity 业务活动
     * @return 结果
     */
    public int updateCrmActivity(CrmActivity crmActivity);

    /**
     * 批量删除业务活动
     * 
     * @param activityIds 需要删除的业务活动主键集合
     * @return 结果
     */
    public int deleteCrmActivityByActivityIds(String[] activityIds);

    /**
     * 删除业务活动信息
     * 
     * @param activityId 业务活动主键
     * @return 结果
     */
    public int deleteCrmActivityByActivityId(String activityId);

    // ========================
    // Activity Timeline 新增方法
    // ========================

    /**
     * 按 pulseId 查询 Activity
     */
    CrmActivity selectByPulseId(String pulseId);

    /**
     * 按 Subject 查询 Activity Timeline 分页列表
     * 调用方需先执行 PageHelper.startPage()
     */
    List<CrmActivity> selectTimelineList(CrmActivity query);

    /**
     * 按 Subject 查询 Activity Timeline（含子 Subject 继承事件）
     * 调用方需先执行 PageHelper.startPage()
     */
    List<CrmActivity> selectTimelineWithChildren(CrmActivity query);

    /**
     * 按 pulseId 批量查询（兼容旧 listPulseByIds）
     */
    List<CrmActivity> selectByPulseIds(String[] pulseIds);

    /**
     * 创建 Timeline Activity 事件
     */
    int insertTimelineActivity(CrmActivity crmActivity);

    /**
     * 更新 Timeline Activity 事件
     */
    int updateTimelineActivity(CrmActivity crmActivity);

    /**
     * 按 pulseId 删除 Timeline Activity
     */
    int deleteByPulseId(String pulseId);
}
