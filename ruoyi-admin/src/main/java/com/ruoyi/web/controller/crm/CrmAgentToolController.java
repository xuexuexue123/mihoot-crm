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
import com.ruoyi.crm.domain.CrmAgentTool;
import com.ruoyi.crm.service.ICrmAgentToolService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * Agent ToolController
 * 
 * @author luohd
 * @date 2025-06-05
 */
@RestController
@RequestMapping("/crm/agentTool")
public class CrmAgentToolController extends BaseController
{
    @Autowired
    private ICrmAgentToolService crmAgentToolService;

    /**
     * 查询Agent Tool列表
     */
    @PreAuthorize("@ss.hasPermi('crm:agentTool:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmAgentTool crmAgentTool)
    {
        startPage();
        List<CrmAgentTool> list = crmAgentToolService.selectCrmAgentToolList(crmAgentTool);
        return getDataTable(list);
    }

    /**
     * 导出Agent Tool列表
     */
    @PreAuthorize("@ss.hasPermi('crm:agentTool:export')")
    @Log(title = "Agent Tool", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmAgentTool crmAgentTool)
    {
        List<CrmAgentTool> list = crmAgentToolService.selectCrmAgentToolList(crmAgentTool);
        ExcelUtil<CrmAgentTool> util = new ExcelUtil<CrmAgentTool>(CrmAgentTool.class);
        util.exportExcel(response, list, "Agent Tool数据");
    }

    /**
     * 获取Agent Tool详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:agentTool:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(crmAgentToolService.selectCrmAgentToolById(id));
    }

    /**
     * 新增Agent Tool
     */
    @PreAuthorize("@ss.hasPermi('crm:agentTool:add')")
    @Log(title = "Agent Tool", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmAgentTool crmAgentTool)
    {
        return toAjax(crmAgentToolService.insertCrmAgentTool(crmAgentTool));
    }

    /**
     * 修改Agent Tool
     */
    @PreAuthorize("@ss.hasPermi('crm:agentTool:edit')")
    @Log(title = "Agent Tool", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmAgentTool crmAgentTool)
    {
        return toAjax(crmAgentToolService.updateCrmAgentTool(crmAgentTool));
    }

    /**
     * 删除Agent Tool
     */
    @PreAuthorize("@ss.hasPermi('crm:agentTool:remove')")
    @Log(title = "Agent Tool", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(crmAgentToolService.deleteCrmAgentToolByIds(ids));
    }
}
