package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.domain.CustomerMetrics;

/**
 * 业务活动Mapper接口
 * 
 * @author luohd
 * @date 2024-01-21
 */
public interface CrmActivityMapper 
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
     * 删除业务活动
     * 
     * @param activityId 业务活动主键
     * @return 结果
     */
    public int deleteCrmActivityByActivityId(String activityId);

    /**
     * 批量删除业务活动
     * 
     * @param activityIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmActivityByActivityIds(String[] activityIds);

    /**
     * 批量删除任务
     * 
     * @param activityIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmTaskByActivityIds(String[] activityIds);
    
    /**
     * 批量新增任务
     * 
     * @param crmTaskList 任务列表
     * @return 结果
     */
    public int batchCrmTask(List<CrmTask> crmTaskList);
    

    /**
     * 通过业务活动主键删除任务信息
     * 
     * @param activityId 业务活动ID
     * @return 结果
     */
    public int deleteCrmTaskByActivityId(String activityId);

    // ========================
    // Activity Timeline 新增方法
    // ========================

    /**
     * 按 pulseId 查询
     */
    public CrmActivity selectByPulseId(String pulseId);

    /**
     * 按 Subject 查询 Activity Timeline 列表
     */
    public List<CrmActivity> selectTimelineList(CrmActivity query);

    /**
     * 按 pulseId 批量查询（兼容旧 listPulseByIds）
     */
    public List<CrmActivity> selectByPulseIds(String[] pulseIds);

    /**
     * 插入 Timeline Activity（带 timeline 字段）
     */
    public int insertTimelineActivity(CrmActivity crmActivity);

    /**
     * 更新 Timeline Activity（带 timeline 字段）
     */
    public int updateTimelineActivity(CrmActivity crmActivity);

    /**
     * 按 pulseId 删除
     */
    public int deleteByPulseId(String pulseId);

    // ========================
    // Customer Metrics 聚合相关方法
    // ========================

    /**
     * 获取所有有事件的 Contact subject_id（用于全量重算）
     */
    public List<String> selectSubjectIdsForMetrics();

    /**
     * 按 subject_id 聚合所有指标（单条 GROUP BY SQL）
     * 返回 CustomerMetrics 列表，contactId 已填充
     */
    public List<CustomerMetrics> selectMetricsAggregate();

    /**
     * 批量标记 crm_activity 事件为已聚合
     */
    public int markAsAggregated(@Param("subjectIds") List<String> subjectIds);

    /**
     * 同活动公众号 follow/scan 活动最近挂接的 Contact subject_id
     */
    String selectLatestFollowContactIdByEventAndOpenId(@Param("eventId") String eventId,
            @Param("openId") String openId);

    /**
     * Lead 转 Contact 时，将可迁移 Timeline 活动的 subject 从 Lead 迁移到 Contact
     *（website.page.view、event.registration.submitted、engagement.assessment.completed、lead.created）
     *
     * @param oldSubjectId 原 subject_id（leadId）
     * @param newSubjectId 新 subject_id（contactId）
     * @return 更新行数
     */
    int migrateWebsitePageViewSubject(@Param("oldSubjectId") String oldSubjectId,
            @Param("newSubjectId") String newSubjectId);

    /**
     * 按 channel + channel_event_id 查 activity_id（用于 page_view 回写）
     */
    String selectActivityIdByChannelEventId(@Param("channel") String channel,
            @Param("channelEventId") String channelEventId);
}
