package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.service.ICrmActivityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 业务活动Controller
 * 
 * @author luohd
 * @date 2024-01-21
 */
@RestController
@RequestMapping("/crm/activity")
public class CrmActivityController extends BaseController
{
    @Autowired
    private ICrmActivityService crmActivityService;

    /**
     * 查询业务活动列表
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmActivity crmActivity)
    {
        startPage();
        List<CrmActivity> list = crmActivityService.selectCrmActivityList(crmActivity);
        return getDataTable(list);
    }


    /**
     * 查询本月业务活动列表
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @GetMapping("/listmonthly")
    public TableDataInfo listmonthly(CrmActivity crmActivity)
    {
        startPage();
        List<CrmActivity> list = crmActivityService.selectCrmActivityListMonthly(crmActivity);
        return getDataTable(list);
    }


    /**
     * 导出业务活动列表
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:export')")
    @Log(title = "业务活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmActivity crmActivity)
    {
        List<CrmActivity> list = crmActivityService.selectCrmActivityList(crmActivity);
        ExcelUtil<CrmActivity> util = new ExcelUtil<CrmActivity>(CrmActivity.class);
        util.exportExcel(response, list, "业务活动数据");
    }

    /**
     * 获取业务活动详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:query')")
    @GetMapping(value = "/{activityId}")
    public AjaxResult getInfo(@PathVariable("activityId") String activityId)
    {
        return success(crmActivityService.selectCrmActivityByActivityId(activityId));
    }

    /**
     * 新增业务活动
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:add')")
    @Log(title = "业务活动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmActivity crmActivity)
    {
        return toAjax(crmActivityService.insertCrmActivity(crmActivity));
    }

    /**
     * 修改业务活动
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:edit')")
    @Log(title = "业务活动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmActivity crmActivity)
    {
        return toAjax(crmActivityService.updateCrmActivity(crmActivity));
    }

    /**
     * 删除业务活动
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:remove')")
    @Log(title = "业务活动", businessType = BusinessType.DELETE)
	@DeleteMapping("/{activityIds}")
    public AjaxResult remove(@PathVariable String[] activityIds)
    {
        return toAjax(crmActivityService.deleteCrmActivityByActivityIds(activityIds));
    }

    // ========================
    // Activity Timeline 新增端点
    // ========================

    /**
     * 按 Subject 查询 Activity Timeline 分页列表
     * 替代旧 listPulseByIds
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @GetMapping("/timeline")
    public TableDataInfo timelineList(
            @RequestParam(value = "subjectType", required = false) String subjectType,
            @RequestParam(value = "subjectId", required = false) String subjectId,
            @RequestParam(value = "eventType", required = false) String eventType,
            @RequestParam(value = "eventCategory", required = false) String eventCategory,
            @RequestParam(value = "importanceMin", required = false) Integer importanceMin,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "relateType", required = false) String relateType,
            @RequestParam(value = "relateId", required = false) String relateId,
            @RequestParam(value = "startedDateFrom", required = false) String startedDateFrom,
            @RequestParam(value = "startedDateTo", required = false) String startedDateTo,
            @RequestParam(value = "includeChildSubjects", required = false) Boolean includeChildSubjects)
    {
        CrmActivity query = new CrmActivity();
        query.setSubjectType(subjectType);
        query.setSubjectId(subjectId);
        query.setEventType(eventType);
        query.setEventCategory(eventCategory);
        query.setImportanceMin(importanceMin);
        query.setSource(source);
        query.setRelateType(relateType);
        query.setRelateId(relateId);
        query.setStartedDateFrom(startedDateFrom);
        query.setStartedDateTo(startedDateTo);

        startPage();

        if (Boolean.TRUE.equals(includeChildSubjects)
                && subjectType != null && !subjectType.isEmpty()
                && subjectId != null && !subjectId.isEmpty())
        {
            // 含子 Subject 继承事件（需在 Java 层合并，无法使用 PageHelper 分页）
            List<CrmActivity> list = crmActivityService.selectTimelineWithChildren(query);
            return getDataTable(list);
        }

        List<CrmActivity> list = crmActivityService.selectTimelineList(query);
        return getDataTable(list);
    }

    /**
     * 按 Contact ID 查询客户全量 Timeline（便捷端点）
     * 支持 channel、eventCategory、importanceMin 等过滤参数
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @GetMapping("/timeline/{contactId}")
    public TableDataInfo timelineByContact(
            @PathVariable("contactId") String contactId,
            @RequestParam(value = "channel", required = false) String channel,
            @RequestParam(value = "eventCategory", required = false) String eventCategory,
            @RequestParam(value = "importanceMin", required = false) Integer importanceMin,
            @RequestParam(value = "startedDateFrom", required = false) String startedDateFrom,
            @RequestParam(value = "startedDateTo", required = false) String startedDateTo,
            @RequestParam(value = "includeChildSubjects", defaultValue = "true") Boolean includeChildSubjects)
    {
        CrmActivity query = new CrmActivity();
        query.setSubjectType("Contact");
        query.setSubjectId(contactId);
        query.setChannel(channel);
        query.setEventCategory(eventCategory);
        query.setImportanceMin(importanceMin);
        query.setStartedDateFrom(startedDateFrom);
        query.setStartedDateTo(startedDateTo);

        startPage();

        if (Boolean.TRUE.equals(includeChildSubjects))
        {
            List<CrmActivity> list = crmActivityService.selectTimelineWithChildren(query);
            return getDataTable(list);
        }

        List<CrmActivity> list = crmActivityService.selectTimelineList(query);
        return getDataTable(list);
    }

    /**
     * 按 pulseId 查询单条 Activity 详情
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:query')")
    @GetMapping("/detail/{pulseId}")
    public AjaxResult getByPulseId(@PathVariable("pulseId") String pulseId)
    {
        return success(crmActivityService.selectByPulseId(pulseId));
    }

    /**
     * 创建 Timeline Activity 事件
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:add')")
    @Log(title = "Activity Timeline", businessType = BusinessType.INSERT)
    @PostMapping("/timeline")
    public AjaxResult addTimeline(@RequestBody CrmActivity crmActivity)
    {
        int rows = crmActivityService.insertTimelineActivity(crmActivity);
        // ON CONFLICT DO NOTHING 产生 0 rows 时视为幂等成功
        return rows >= 0 ? success(rows == 0 ? "Event idempotent (already exists)" : null) : error();
    }

    /**
     * 更新 Timeline Activity 事件
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:edit')")
    @Log(title = "Activity Timeline", businessType = BusinessType.UPDATE)
    @PutMapping("/timeline")
    public AjaxResult editTimeline(@RequestBody CrmActivity crmActivity)
    {
        return toAjax(crmActivityService.updateTimelineActivity(crmActivity));
    }

    /**
     * 按 pulseId 删除 Timeline Activity
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:remove')")
    @Log(title = "Activity Timeline", businessType = BusinessType.DELETE)
    @DeleteMapping("/timeline/{pulseId}")
    public AjaxResult removeTimeline(@PathVariable("pulseId") String pulseId)
    {
        return toAjax(crmActivityService.deleteByPulseId(pulseId));
    }

    /**
     * 按 pulseId 批量查询（兼容旧 listPulseByIds）
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:list')")
    @GetMapping("/listbyids/{ids}")
    public AjaxResult listByIds(@PathVariable("ids") String ids)
    {
        String[] idArray = ids.split(",");
        List<CrmActivity> list = crmActivityService.selectByPulseIds(idArray);
        return success(list);
    }
}
