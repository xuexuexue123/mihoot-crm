package com.ruoyi.web.controller.crm;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.SmartPushExecutionLog;
import com.ruoyi.crm.domain.SmartPushStrategy;
import com.ruoyi.crm.service.ISmartPushExecutionLogService;
import com.ruoyi.crm.service.ISmartPushStrategyService;
import com.ruoyi.crm.task.BirthdayReminderScheduler;
import com.ruoyi.crm.task.SmartPushScheduler;
import com.ruoyi.common.utils.DateUtils;

/**
 * 智能推送Controller
 *
 * @author ruoyi
 * @date 2026-07-13
 */
@RestController
@RequestMapping("/crm/smart-push")
public class SmartPushController extends BaseController
{
    @Autowired
    private ISmartPushStrategyService smartPushStrategyService;

    @Autowired
    private ISmartPushExecutionLogService smartPushExecutionLogService;

    @Autowired
    private BirthdayReminderScheduler birthdayReminderScheduler;

    @Autowired
    private SmartPushScheduler smartPushScheduler;

    /**
     * 查询策略列表
     */
    @GetMapping("/strategy")
    public AjaxResult listStrategy(String type)
    {
        List<SmartPushStrategy> list = smartPushStrategyService.selectListByType(type);
        return success(list);
    }

    /**
     * 创建策略
     */
    @PostMapping("/strategy")
    public AjaxResult addStrategy(@RequestBody SmartPushStrategy strategy)
    {
        Date now = DateUtils.getNowDate();
        strategy.setCreatedAt(now);
        strategy.setUpdatedAt(now);
        smartPushStrategyService.insert(strategy);
        return success(strategy.getId());
    }

    /**
     * 更新策略
     */
    @PutMapping("/strategy/{id}")
    public AjaxResult updateStrategy(@PathVariable Long id, @RequestBody SmartPushStrategy strategy)
    {
        SmartPushStrategy existing = smartPushStrategyService.selectById(id);
        if (existing == null)
        {
            return error("策略不存在");
        }
        strategy.setId(id);
        strategy.setUpdatedAt(DateUtils.getNowDate());
        smartPushStrategyService.update(strategy);
        return success(strategy.getId());
    }

    /**
     * 查询执行日志
     */
    @GetMapping("/execution-log")
    public TableDataInfo listExecutionLog(Long strategyId)
    {
        startPage();
        SmartPushExecutionLog query = new SmartPushExecutionLog();
        query.setStrategyId(strategyId);
        List<SmartPushExecutionLog> list = smartPushExecutionLogService.selectPage(query);
        return getDataTable(list);
    }

    /**
     * 手动触发生日提醒（用于测试）
     */
    @PostMapping("/trigger-birthday")
    public AjaxResult triggerBirthday()
    {
        birthdayReminderScheduler.processBirthdayReminders();
        return success();
    }

    /**
     * 手动触发策略执行（调试用）。
     * 支持两种参数格式：
     * <ul>
     *   <li>{ "strategyId": 1 } —— 执行指定策略</li>
     *   <li>{} 或不传 —— 执行所有启用 scheduled 策略</li>
     * </ul>
     */
    @PostMapping("/execute")
    public AjaxResult executeStrategy(@RequestBody Map<String, Object> params)
    {
        if (params.containsKey("strategyId") && params.get("strategyId") != null)
        {
            Long strategyId = Long.valueOf(params.get("strategyId").toString());
            smartPushScheduler.executeStrategyById(strategyId);
        }
        else
        {
            smartPushScheduler.executeAll();
        }
        return success();
    }
}
