package com.ruoyi.web.controller.crm;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.WWorkflow;
import com.ruoyi.crm.service.IWWorkflowService;
import com.ruoyi.crm.service.support.WorkflowTemplateGuard;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WWorkflowController
 * 
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wworkflow")
public class WWorkflowController extends BaseController
{
    @Autowired
    private IWWorkflowService wWorkflowService;

    /**
     * 避免 query isTemplate=0/1 被 Spring 直接绑定到 Boolean 字段导致类型错误
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.setDisallowedFields("isTemplate");
    }

    /**
     * 查询WWorkflow列表
     * 未传 isTemplate 时默认仅返回实例（is_template=false）
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:list')")
    @GetMapping("/list")
    public TableDataInfo list(WWorkflow wWorkflow, HttpServletRequest request)
    {
        applyListTemplateFilter(wWorkflow, request.getParameter("isTemplate"));
        startPage();
        List<WWorkflow> list = wWorkflowService.selectWWorkflowList(wWorkflow);
        return getDataTable(list);
    }

    /**
     * 导出WWorkflow列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:export')")
    @Log(title = "WWorkflow", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WWorkflow wWorkflow, HttpServletRequest request)
    {
        applyListTemplateFilter(wWorkflow, request.getParameter("isTemplate"));
        List<WWorkflow> list = wWorkflowService.selectWWorkflowList(wWorkflow);
        ExcelUtil<WWorkflow> util = new ExcelUtil<WWorkflow>(WWorkflow.class);
        util.exportExcel(response, list, "WWorkflow数据");
    }

    /**
     * 获取WWorkflow详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wWorkflowService.selectWWorkflowById(id));
    }

    /**
     * 新增WWorkflow
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:add')")
    @Log(title = "WWorkflow", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WWorkflow wWorkflow)
    {
        return toAjax(wWorkflowService.insertWWorkflow(wWorkflow));
    }

    /**
     * 修改WWorkflow
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:edit')")
    @Log(title = "WWorkflow", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WWorkflow wWorkflow)
    {
        return toAjax(wWorkflowService.updateWWorkflow(wWorkflow));
    }

    /**
     * 仅修改工作流名称（不更新节点与连线）
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:edit')")
    @Log(title = "WWorkflow", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/name")
    public AjaxResult updateName(@PathVariable("id") String id, @RequestBody WWorkflow wWorkflow)
    {
        wWorkflow.setId(id);
        return toAjax(wWorkflowService.updateWWorkflowName(wWorkflow));
    }

    /**
     * 仅更新元数据 name / description（不更新节点与连线）
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:edit')")
    @Log(title = "WWorkflow", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/meta")
    public AjaxResult updateMeta(@PathVariable("id") String id, @RequestBody Map<String, Object> body)
    {
        String name = null;
        String description = null;
        if (body != null)
        {
            if (body.containsKey("name"))
            {
                Object raw = body.get("name");
                name = raw != null ? String.valueOf(raw) : null;
            }
            if (body.containsKey("description"))
            {
                Object raw = body.get("description");
                description = raw != null ? String.valueOf(raw) : "";
            }
        }
        return success(wWorkflowService.updateWWorkflowMeta(id, name, description));
    }

    /**
     * 更新模板标记；设为模板时强制清发布
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:edit')")
    @Log(title = "WWorkflow", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/template-flag")
    public AjaxResult updateTemplateFlag(@PathVariable("id") String id, @RequestBody Map<String, Object> body)
    {
        Boolean isTemplate = WorkflowTemplateGuard.parseIsTemplateFlag(
                body != null ? body.get("isTemplate") : null);
        return success(wWorkflowService.updateTemplateFlag(id, isTemplate));
    }

    /**
     * 从流程深拷贝为新实例
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:add')")
    @Log(title = "WWorkflow", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/clone")
    public AjaxResult cloneAsInstance(@PathVariable("id") String id,
            @RequestBody(required = false) Map<String, Object> body)
    {
        String name = null;
        String description = null;
        if (body != null)
        {
            if (body.get("name") != null)
            {
                name = String.valueOf(body.get("name"));
            }
            if (body.containsKey("description"))
            {
                Object raw = body.get("description");
                description = raw != null ? String.valueOf(raw) : "";
            }
        }
        return success(wWorkflowService.cloneAsInstance(id, name, description));
    }

    /**
     * 从流程深拷贝为新模板
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:add')")
    @Log(title = "WWorkflow", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/save-as-template")
    public AjaxResult saveAsTemplate(@PathVariable("id") String id,
            @RequestBody(required = false) Map<String, Object> body)
    {
        String name = null;
        String description = null;
        if (body != null)
        {
            if (body.get("name") != null)
            {
                name = String.valueOf(body.get("name"));
            }
            if (body.containsKey("description"))
            {
                Object raw = body.get("description");
                description = raw != null ? String.valueOf(raw) : "";
            }
        }
        return success(wWorkflowService.saveAsTemplate(id, name, description));
    }

    /**
     * 重算全表 publish_assistant / publish_sidebar（一次性 backfill）
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:edit')")
    @Log(title = "WWorkflow", businessType = BusinessType.UPDATE)
    @PostMapping("/backfill-publish-flags")
    public AjaxResult backfillPublishFlags()
    {
        int updated = wWorkflowService.backfillPublishFlags();
        return success(updated);
    }

    /**
     * 删除WWorkflow
     */
    @PreAuthorize("@ss.hasPermi('crm:wworkflow:remove')")
    @Log(title = "WWorkflow", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wWorkflowService.deleteWWorkflowByIds(ids));
    }

    private static void applyListTemplateFilter(WWorkflow wWorkflow, String isTemplateParam)
    {
        Boolean parsed = WorkflowTemplateGuard.parseIsTemplateFlag(isTemplateParam);
        if (parsed == null)
        {
            wWorkflow.setIsTemplate(Boolean.FALSE);
        }
        else
        {
            wWorkflow.setIsTemplate(parsed);
        }
    }
}
