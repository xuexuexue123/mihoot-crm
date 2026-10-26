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
import com.ruoyi.crm.domain.CrmStagehistory;
import com.ruoyi.crm.service.ICrmStagehistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 阶段记录Controller
 * 
 * @author luohd
 * @date 2024-02-07
 */
@RestController
@RequestMapping("/crm/stagehistory")
public class CrmStagehistoryController extends BaseController
{
    @Autowired
    private ICrmStagehistoryService crmStagehistoryService;

    /**
     * 查询阶段记录列表
     */
    @PreAuthorize("@ss.hasPermi('crm:stagehistory:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmStagehistory crmStagehistory)
    {
        startPage();
        List<CrmStagehistory> list = crmStagehistoryService.selectCrmStagehistoryList(crmStagehistory);
        return getDataTable(list);
    }

    /**
     * 导出阶段记录列表
     */
    @PreAuthorize("@ss.hasPermi('crm:stagehistory:export')")
    @Log(title = "阶段记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmStagehistory crmStagehistory)
    {
        List<CrmStagehistory> list = crmStagehistoryService.selectCrmStagehistoryList(crmStagehistory);
        ExcelUtil<CrmStagehistory> util = new ExcelUtil<CrmStagehistory>(CrmStagehistory.class);
        util.exportExcel(response, list, "阶段记录数据");
    }

    /**
     * 获取阶段记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:stagehistory:query')")
    @GetMapping(value = "/{shId}")
    public AjaxResult getInfo(@PathVariable("shId") String shId)
    {
        return success(crmStagehistoryService.selectCrmStagehistoryByShId(shId));
    }

    /**
     * 新增阶段记录
     */
    @PreAuthorize("@ss.hasPermi('crm:stagehistory:add')")
    @Log(title = "阶段记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmStagehistory crmStagehistory)
    {
        return toAjax(crmStagehistoryService.insertCrmStagehistory(crmStagehistory));
    }

    /**
     * 修改阶段记录
     */
    @PreAuthorize("@ss.hasPermi('crm:stagehistory:edit')")
    @Log(title = "阶段记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmStagehistory crmStagehistory)
    {
        return toAjax(crmStagehistoryService.updateCrmStagehistory(crmStagehistory));
    }

    /**
     * 删除阶段记录
     */
    @PreAuthorize("@ss.hasPermi('crm:stagehistory:remove')")
    @Log(title = "阶段记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{shIds}")
    public AjaxResult remove(@PathVariable String[] shIds)
    {
        return toAjax(crmStagehistoryService.deleteCrmStagehistoryByShIds(shIds));
    }
}
