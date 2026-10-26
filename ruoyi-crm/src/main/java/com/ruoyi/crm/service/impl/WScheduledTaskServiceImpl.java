package com.ruoyi.crm.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.WScheduledTask;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.mapper.WScheduledTaskMapper;
import com.ruoyi.crm.mapper.WWorkflowMapper;
import com.ruoyi.crm.service.IWScheduledTaskService;
import com.ruoyi.crm.service.support.WorkflowTemplateGuard;

/**
 * 工作流定时任务 CRUD
 */
@Service
public class WScheduledTaskServiceImpl implements IWScheduledTaskService
{
    public static final String SCHEDULE_DAILY = "daily";
    public static final String SCHEDULE_WEEKLY = "weekly";
    public static final String ENABLED = "1";
    public static final String DISABLED = "0";

    private static final Pattern RUN_TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):(00|30)$");

    @Autowired
    private WScheduledTaskMapper wScheduledTaskMapper;

    @Autowired
    private WWorkflowMapper wWorkflowMapper;

    @Override
    public WScheduledTask selectWScheduledTaskById(String id)
    {
        return wScheduledTaskMapper.selectWScheduledTaskById(id);
    }

    @Override
    public List<WScheduledTask> selectWScheduledTaskList(WScheduledTask query)
    {
        return wScheduledTaskMapper.selectWScheduledTaskList(query);
    }

    @Override
    public int insertWScheduledTask(WScheduledTask task)
    {
        validateForWrite(task, true);
        if (StringUtils.isBlank(task.getId()))
        {
            task.setId(IdUtils.fastSimpleUUID());
        }
        if (StringUtils.isBlank(task.getEnabled()))
        {
            task.setEnabled(ENABLED);
        }
        String username = safeUsername();
        task.setCreateBy(username);
        task.setCreateTime(DateUtils.getNowDate());
        task.setUpdateBy(username);
        task.setUpdateTime(DateUtils.getNowDate());
        normalizeDaysOfWeek(task);
        return wScheduledTaskMapper.insertWScheduledTask(task);
    }

    @Override
    public int updateWScheduledTask(WScheduledTask task)
    {
        if (StringUtils.isBlank(task.getId()))
        {
            throw new ServiceException("任务 id 不能为空", HttpStatus.BAD_REQUEST);
        }
        validateForWrite(task, false);
        task.setUpdateBy(safeUsername());
        task.setUpdateTime(DateUtils.getNowDate());
        normalizeDaysOfWeek(task);
        return wScheduledTaskMapper.updateWScheduledTask(task);
    }

    @Override
    public int deleteWScheduledTaskByIds(String[] ids)
    {
        return wScheduledTaskMapper.deleteWScheduledTaskByIds(ids);
    }

    @Override
    public int deleteWScheduledTaskById(String id)
    {
        return wScheduledTaskMapper.deleteWScheduledTaskById(id);
    }

    /**
     * 校验 scheduleType / runTime / daysOfWeek / enabled（公开供单测）
     */
    public void validateForWrite(WScheduledTask task, boolean requireCoreFields)
    {
        if (requireCoreFields || task.getName() != null)
        {
            if (StringUtils.isBlank(task.getName()))
            {
                throw new ServiceException("任务名称不能为空", HttpStatus.BAD_REQUEST);
            }
        }
        if (requireCoreFields || task.getWorkflowId() != null)
        {
            if (StringUtils.isBlank(task.getWorkflowId()))
            {
                throw new ServiceException("workflowId 不能为空", HttpStatus.BAD_REQUEST);
            }
            WWorkflow workflow = wWorkflowMapper.selectWWorkflowById(task.getWorkflowId().trim());
            if (workflow == null)
            {
                throw new ServiceException("工作流不存在", HttpStatus.BAD_REQUEST);
            }
            WorkflowTemplateGuard.assertExecutable(workflow);
        }
        if (requireCoreFields || task.getScheduleType() != null)
        {
            validateScheduleType(task.getScheduleType());
        }
        if (requireCoreFields || task.getRunTime() != null)
        {
            validateRunTime(task.getRunTime());
        }
        String scheduleType = task.getScheduleType();
        if (SCHEDULE_WEEKLY.equals(scheduleType)
                || (scheduleType == null && StringUtils.isNotBlank(task.getDaysOfWeek())))
        {
            if (SCHEDULE_WEEKLY.equals(scheduleType) || requireCoreFields)
            {
                validateDaysOfWeek(task.getDaysOfWeek(), SCHEDULE_WEEKLY.equals(scheduleType));
            }
        }
        else if (SCHEDULE_DAILY.equals(scheduleType))
        {
            // daily 允许 daysOfWeek 为空；写入时归一为 null
        }
        if (task.getEnabled() != null && !ENABLED.equals(task.getEnabled()) && !DISABLED.equals(task.getEnabled()))
        {
            throw new ServiceException("enabled 仅支持 1 或 0", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateRunTime(String runTime)
    {
        if (StringUtils.isBlank(runTime) || !RUN_TIME_PATTERN.matcher(runTime).matches())
        {
            throw new ServiceException("runTime 必须为 HH:mm 且分钟仅为 00 或 30", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateScheduleType(String scheduleType)
    {
        if (!SCHEDULE_DAILY.equals(scheduleType) && !SCHEDULE_WEEKLY.equals(scheduleType))
        {
            throw new ServiceException("scheduleType 仅支持 daily 或 weekly", HttpStatus.BAD_REQUEST);
        }
    }

    public static void validateDaysOfWeek(String daysOfWeek, boolean required)
    {
        if (StringUtils.isBlank(daysOfWeek))
        {
            if (required)
            {
                throw new ServiceException("weekly 任务必须提供 daysOfWeek", HttpStatus.BAD_REQUEST);
            }
            return;
        }
        String[] parts = daysOfWeek.split(",");
        Set<Integer> seen = new HashSet<>();
        for (String part : parts)
        {
            String trimmed = part.trim();
            if (trimmed.isEmpty())
            {
                throw new ServiceException("daysOfWeek 格式非法", HttpStatus.BAD_REQUEST);
            }
            int day;
            try
            {
                day = Integer.parseInt(trimmed);
            }
            catch (NumberFormatException e)
            {
                throw new ServiceException("daysOfWeek 必须为 1-7 的整数", HttpStatus.BAD_REQUEST);
            }
            if (day < 1 || day > 7)
            {
                throw new ServiceException("daysOfWeek 必须为 1-7（ISO，1=周一）", HttpStatus.BAD_REQUEST);
            }
            if (!seen.add(day))
            {
                throw new ServiceException("daysOfWeek 存在重复星期", HttpStatus.BAD_REQUEST);
            }
        }
    }

    private void normalizeDaysOfWeek(WScheduledTask task)
    {
        if (SCHEDULE_DAILY.equals(task.getScheduleType()))
        {
            task.setDaysOfWeek(null);
            return;
        }
        if (StringUtils.isNotBlank(task.getDaysOfWeek()))
        {
            String[] parts = task.getDaysOfWeek().split(",");
            StringBuilder sb = new StringBuilder();
            for (String part : parts)
            {
                if (sb.length() > 0)
                {
                    sb.append(',');
                }
                sb.append(part.trim());
            }
            task.setDaysOfWeek(sb.toString());
        }
    }

    private String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
