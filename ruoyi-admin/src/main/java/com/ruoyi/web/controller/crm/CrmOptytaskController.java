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
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmOptytask;
import com.ruoyi.crm.service.ICrmOptytaskService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 机会任务Controller
 * 
 * @author luohd
 * @date 2024-03-05
 */
@RestController
@RequestMapping("/crm/optytask")
public class CrmOptytaskController extends BaseController
{
    @Autowired
    private ICrmOptytaskService crmOptytaskService;

    /**
     * 查询机会任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:optytask:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOptytask crmOptytask)
    {
        startPage();
        List<CrmOptytask> list = crmOptytaskService.selectCrmOptytaskList(crmOptytask);
        return getDataTable(list);
    }

    /**
     * 导出机会任务列表
     */
    @PreAuthorize("@ss.hasPermi('crm:optytask:export')")
    @Log(title = "机会任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOptytask crmOptytask)
    {
        List<CrmOptytask> list = crmOptytaskService.selectCrmOptytaskList(crmOptytask);
        ExcelUtil<CrmOptytask> util = new ExcelUtil<CrmOptytask>(CrmOptytask.class);
        util.exportExcel(response, list, "机会任务数据");
    }

    /**
     * 获取机会任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:optytask:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable("taskId") String taskId)
    {
        return success(crmOptytaskService.selectCrmOptytaskByTaskId(taskId));
    }

    /**
     * 新增机会任务
     */
    @PreAuthorize("@ss.hasPermi('crm:optytask:add')")
    @Log(title = "机会任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOptytask crmOptytask)
    {
        return toAjax(crmOptytaskService.insertCrmOptytask(crmOptytask));
    }

    /**
     * 修改机会任务
     */
    @PreAuthorize("@ss.hasPermi('crm:optytask:edit')")
    @Log(title = "机会任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOptytask crmOptytask)
    {
        return toAjax(crmOptytaskService.updateCrmOptytask(crmOptytask));
    }

    /**
     * 删除机会任务
     */
    @PreAuthorize("@ss.hasPermi('crm:optytask:remove')")
    @Log(title = "机会任务", businessType = BusinessType.DELETE)
	@DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable String[] taskIds)
    {
        return toAjax(crmOptytaskService.deleteCrmOptytaskByTaskIds(taskIds));
    }
}
