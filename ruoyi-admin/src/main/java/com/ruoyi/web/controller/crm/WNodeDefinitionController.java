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
import com.ruoyi.crm.domain.WNodeDefinition;
import com.ruoyi.crm.service.IWNodeDefinitionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WNodeDefinitionController
 * 
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wnodedefinition")
public class WNodeDefinitionController extends BaseController
{
    @Autowired
    private IWNodeDefinitionService wNodeDefinitionService;

    /**
     * 查询WNodeDefinition列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wnodedefinition:list')")
    @GetMapping("/list")
    public TableDataInfo list(WNodeDefinition wNodeDefinition)
    {
        startPage();
        List<WNodeDefinition> list = wNodeDefinitionService.selectWNodeDefinitionList(wNodeDefinition);
        return getDataTable(list);
    }

    /**
     * 导出WNodeDefinition列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wnodedefinition:export')")
    @Log(title = "WNodeDefinition", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WNodeDefinition wNodeDefinition)
    {
        List<WNodeDefinition> list = wNodeDefinitionService.selectWNodeDefinitionList(wNodeDefinition);
        ExcelUtil<WNodeDefinition> util = new ExcelUtil<WNodeDefinition>(WNodeDefinition.class);
        util.exportExcel(response, list, "WNodeDefinition数据");
    }

    /**
     * 获取WNodeDefinition详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wnodedefinition:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wNodeDefinitionService.selectWNodeDefinitionById(id));
    }

    /**
     * 新增WNodeDefinition
     */
    @PreAuthorize("@ss.hasPermi('crm:wnodedefinition:add')")
    @Log(title = "WNodeDefinition", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WNodeDefinition wNodeDefinition)
    {
        return toAjax(wNodeDefinitionService.insertWNodeDefinition(wNodeDefinition));
    }

    /**
     * 修改WNodeDefinition
     */
    @PreAuthorize("@ss.hasPermi('crm:wnodedefinition:edit')")
    @Log(title = "WNodeDefinition", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WNodeDefinition wNodeDefinition)
    {
        return toAjax(wNodeDefinitionService.updateWNodeDefinition(wNodeDefinition));
    }

    /**
     * 删除WNodeDefinition
     */
    @PreAuthorize("@ss.hasPermi('crm:wnodedefinition:remove')")
    @Log(title = "WNodeDefinition", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wNodeDefinitionService.deleteWNodeDefinitionByIds(ids));
    }
}
