package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.domain.CrmSubjectMap;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CrmSubjectMapMapper;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.event.MessengerActivityEventTypes;
import com.ruoyi.crm.event.SidebarActivityEventTypes;
import com.ruoyi.crm.event.WhatsappActivityEventTypes;
import com.ruoyi.crm.service.ICrmActivityService;
import com.ruoyi.crm.service.support.EventTriggerHookSupport;
import com.ruoyi.crm.service.support.LeadFollowupAnchorSupport;

/**
 * 业务活动Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-21
 */
@Service
public class CrmActivityServiceImpl implements ICrmActivityService 
{
    @Autowired
    private CrmActivityMapper crmActivityMapper;

    @Autowired
    private CrmSubjectMapMapper crmSubjectMapMapper;

    @Autowired
    private EventTriggerHookSupport eventTriggerHookSupport;

    @Autowired
    private LeadFollowupAnchorSupport leadFollowupAnchorSupport;

    /**
     * 查询业务活动
     * 
     * @param activityId 业务活动主键
     * @return 业务活动
     */
    @Override
    public CrmActivity selectCrmActivityByActivityId(String activityId)
    {
        return crmActivityMapper.selectCrmActivityByActivityId(activityId);
    }

    /**
     * 查询业务活动列表
     * 
     * @param crmActivity 业务活动
     * @return 业务活动
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmActivity> selectCrmActivityList(CrmActivity crmActivity)
    {
        return crmActivityMapper.selectCrmActivityList(crmActivity);
    }


    /**
     * 查询本月业务活动列表
     *
     * @param crmActivity 业务活动
     * @return 业务活动
     */
    @Override
    public List<CrmActivity> selectCrmActivityListMonthly(CrmActivity crmActivity) {
        return crmActivityMapper.selectCrmActivityListMonthly(crmActivity);
    }

    /**
     * 新增业务活动
     * 
     * @param crmActivity 业务活动
     * @return 结果
     */
    @Transactional
    @Override
    public int insertCrmActivity(CrmActivity crmActivity)
    {
        if (crmActivity.getActivityId() == null)
        {
            crmActivity.setActivityId(UUID.randomUUID().toString());
        }
        if (crmActivity.getActivitySubject() == null)
        {
            crmActivity.setActivitySubject(crmActivity.getEventName() != null ? crmActivity.getEventName() : "");
        }
        crmActivity.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        crmActivity.setDeptId(loginUser.getDeptId());
        crmActivity.setUserId(loginUser.getUserId());
        int rows = crmActivityMapper.insertCrmActivity(crmActivity);
        insertCrmTask(crmActivity);
        if (rows > 0)
        {
            eventTriggerHookSupport.afterActivityInserted(crmActivity);
            leadFollowupAnchorSupport.onManualActivityInserted(crmActivity);
        }
        return rows;
    }

    /**
     * 修改业务活动
     * 
     * @param crmActivity 业务活动
     * @return 结果
     */
    @Transactional
    @Override
    public int updateCrmActivity(CrmActivity crmActivity)
    {
        crmActivity.setUpdateTime(DateUtils.getNowDate());
        crmActivityMapper.deleteCrmTaskByActivityId(crmActivity.getActivityId());
        insertCrmTask(crmActivity);
        return crmActivityMapper.updateCrmActivity(crmActivity);
    }

    /**
     * 批量删除业务活动
     * 
     * @param activityIds 需要删除的业务活动主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmActivityByActivityIds(String[] activityIds)
    {
        crmActivityMapper.deleteCrmTaskByActivityIds(activityIds);
        return crmActivityMapper.deleteCrmActivityByActivityIds(activityIds);
    }

    /**
     * 删除业务活动信息
     * 
     * @param activityId 业务活动主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deleteCrmActivityByActivityId(String activityId)
    {
        crmActivityMapper.deleteCrmTaskByActivityId(activityId);
        return crmActivityMapper.deleteCrmActivityByActivityId(activityId);
    }

    /**
     * 新增任务信息
     * 
     * @param crmActivity 业务活动对象
     */
    public void insertCrmTask(CrmActivity crmActivity)
    {
        List<CrmTask> crmTaskList = crmActivity.getCrmTaskList();
        String activityId = crmActivity.getActivityId();
        if (StringUtils.isNotNull(crmTaskList))
        {
            List<CrmTask> list = new ArrayList<CrmTask>();
            for (CrmTask crmTask : crmTaskList)
            {
                crmTask.setTaskId(UUID.randomUUID().toString());
                crmTask.setActivityId(activityId);
                list.add(crmTask);
            }
            if (list.size() > 0)
            {
                crmActivityMapper.batchCrmTask(list);
            }
        }
    }

    // ========================
    // Activity Timeline 新增方法
    // ========================

    @Override
    public CrmActivity selectByPulseId(String pulseId)
    {
        return crmActivityMapper.selectByPulseId(pulseId);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmActivity> selectTimelineList(CrmActivity query)
    {
        return crmActivityMapper.selectTimelineList(query);
    }

    @Override
    public List<CrmActivity> selectTimelineWithChildren(CrmActivity query)
    {
        // 先查直接归属该 subject 的事件
        List<CrmActivity> directList = crmActivityMapper.selectTimelineList(query);

        // 查找子 subject 映射
        List<CrmSubjectMap> childMaps = crmSubjectMapMapper.selectByParent(
                query.getSubjectType(), query.getSubjectId());

        if (childMaps == null || childMaps.isEmpty())
        {
            return directList;
        }

        // 合并子 subject 事件
        List<CrmActivity> allList = new ArrayList<>(directList);
        Set<String> existingIds = directList.stream()
                .map(CrmActivity::getPulseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        for (CrmSubjectMap childMap : childMaps)
        {
            CrmActivity childQuery = new CrmActivity();
            childQuery.setSubjectType(childMap.getChildSubjectType());
            childQuery.setSubjectId(childMap.getChildSubjectId());
            // 复制查询参数
            childQuery.setEventType(query.getEventType());
            childQuery.setEventCategory(query.getEventCategory());
            childQuery.setImportanceMin(query.getImportanceMin());
            childQuery.setSource(query.getSource());
            childQuery.setStatus(query.getStatus());
            childQuery.setStartedDateFrom(query.getStartedDateFrom());
            childQuery.setStartedDateTo(query.getStartedDateTo());
            childQuery.getParams().putAll(query.getParams());

            List<CrmActivity> childEvents = crmActivityMapper.selectTimelineList(childQuery);
            for (CrmActivity event : childEvents)
            {
                if (event.getPulseId() == null || !existingIds.contains(event.getPulseId()))
                {
                    allList.add(event);
                    if (event.getPulseId() != null)
                    {
                        existingIds.add(event.getPulseId());
                    }
                }
            }
        }

        // 按 startedDate 降序排序
        allList.sort((a, b) -> {
            if (a.getStartedDate() == null && b.getStartedDate() == null) return 0;
            if (a.getStartedDate() == null) return 1;
            if (b.getStartedDate() == null) return -1;
            return b.getStartedDate().compareTo(a.getStartedDate());
        });

        return allList;
    }

    @Override
    public List<CrmActivity> selectByPulseIds(String[] pulseIds)
    {
        return crmActivityMapper.selectByPulseIds(pulseIds);
    }

    @Override
    @Transactional
    public int insertTimelineActivity(CrmActivity crmActivity)
    {
        if (crmActivity.getActivityId() == null)
        {
            crmActivity.setActivityId(UUID.randomUUID().toString());
        }
        if (crmActivity.getPulseId() == null)
        {
            crmActivity.setPulseId(crmActivity.getActivityId());
        }
        if (crmActivity.getImportance() == null)
        {
            crmActivity.setImportance(10);
        }
        if (crmActivity.getStatus() == null)
        {
            crmActivity.setStatus("completed");
        }
        if (crmActivity.getSource() == null)
        {
            crmActivity.setSource("manual");
        }
        if (crmActivity.getActivitySubject() == null)
        {
            crmActivity.setActivitySubject(crmActivity.getEventName() != null ? crmActivity.getEventName() : "");
        }
        if (crmActivity.getStatusDetail() == null)
        {
            crmActivity.setStatusDetail(crmActivity.getStatus() != null ? crmActivity.getStatus() : "completed");
        }
        if (crmActivity.getEventCategory() == null && StringUtils.isNotEmpty(crmActivity.getEventType()))
        {
            crmActivity.setEventCategory(inferEventCategory(crmActivity.getEventType()));
        }
        crmActivity.setCreateTime(DateUtils.getNowDate());
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                if (StringUtils.isEmpty(crmActivity.getCreateBy()))
                {
                    crmActivity.setCreateBy(loginUser.getUsername());
                }
                if (crmActivity.getDeptId() == null)
                {
                    crmActivity.setDeptId(loginUser.getDeptId());
                }
                if (crmActivity.getUserId() == null)
                {
                    crmActivity.setUserId(loginUser.getUserId());
                }
            }
        }
        catch (Exception ignored)
        {
            // 无登录态（如系统回调）时保留 DTO 已填的 userId/deptId/createBy
        }
        int rows = crmActivityMapper.insertTimelineActivity(crmActivity);
        if (rows > 0)
        {
            eventTriggerHookSupport.afterActivityInserted(crmActivity);
        }
        return rows;
    }

    @Override
    @Transactional
    public int updateTimelineActivity(CrmActivity crmActivity)
    {
        crmActivity.setUpdateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        crmActivity.setUpdateBy(loginUser.getUsername());
        return crmActivityMapper.updateTimelineActivity(crmActivity);
    }

    @Override
    @Transactional
    public int deleteByPulseId(String pulseId)
    {
        return crmActivityMapper.deleteByPulseId(pulseId);
    }

    private static String inferEventCategory(String eventType)
    {
        if (SidebarActivityEventTypes.CONTENT_SENT.equals(eventType))
        {
            return SidebarActivityEventTypes.CONTENT_SENT_CATEGORY;
        }
        if (WhatsappActivityEventTypes.CONTENT_SENT.equals(eventType))
        {
            return WhatsappActivityEventTypes.CONTENT_SENT_CATEGORY;
        }
        if (SidebarActivityEventTypes.DELIVER_SENT.equals(eventType))
        {
            return SidebarActivityEventTypes.DELIVER_SENT_CATEGORY;
        }
        if (eventType.startsWith("workflow."))
        {
            return "workflow";
        }
        if (eventType.startsWith("channel."))
        {
            return "channel";
        }
        if (eventType.startsWith("engagement."))
        {
            return "engagement";
        }
        if (eventType.startsWith("whatsapp."))
        {
            if (WhatsappActivityEventTypes.LINKED.equals(eventType))
            {
                return "channel";
            }
            return "engagement";
        }
        if (eventType.startsWith("messenger."))
        {
            if (MessengerActivityEventTypes.LINKED.equals(eventType))
            {
                return "channel";
            }
            return "engagement";
        }
        return null;
    }
}
