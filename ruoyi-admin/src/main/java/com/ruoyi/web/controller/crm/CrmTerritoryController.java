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
import com.ruoyi.crm.domain.CrmTerritory;
import com.ruoyi.crm.service.ICrmTerritoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 销售区域Controller
 * 
 * @author luohd
 * @date 2024-01-19
 */
@RestController
@RequestMapping("/crm/territory")
public class CrmTerritoryController extends BaseController
{
    @Autowired
    private ICrmTerritoryService crmTerritoryService;

    /**
     * 查询销售区域列表
     */
    @PreAuthorize("@ss.hasPermi('crm:territory:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmTerritory crmTerritory)
    {
        startPage();
        List<CrmTerritory> list = crmTerritoryService.selectCrmTerritoryList(crmTerritory);
        return getDataTable(list);
    }

    /**
     * 导出销售区域列表
     */
    @PreAuthorize("@ss.hasPermi('crm:territory:export')")
    @Log(title = "销售区域", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmTerritory crmTerritory)
    {
        List<CrmTerritory> list = crmTerritoryService.selectCrmTerritoryList(crmTerritory);
        ExcelUtil<CrmTerritory> util = new ExcelUtil<CrmTerritory>(CrmTerritory.class);
        util.exportExcel(response, list, "销售区域数据");
    }

    /**
     * 获取销售区域详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:territory:query')")
    @GetMapping(value = "/{territoryId}")
    public AjaxResult getInfo(@PathVariable("territoryId") String territoryId)
    {
        return success(crmTerritoryService.selectCrmTerritoryByTerritoryId(territoryId));
    }

    /**
     * 新增销售区域
     */
    @PreAuthorize("@ss.hasPermi('crm:territory:add')")
    @Log(title = "销售区域", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmTerritory crmTerritory)
    {
        return toAjax(crmTerritoryService.insertCrmTerritory(crmTerritory));
    }

    /**
     * 修改销售区域
     */
    @PreAuthorize("@ss.hasPermi('crm:territory:edit')")
    @Log(title = "销售区域", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmTerritory crmTerritory)
    {
        return toAjax(crmTerritoryService.updateCrmTerritory(crmTerritory));
    }

    /**
     * 删除销售区域
     */
    @PreAuthorize("@ss.hasPermi('crm:territory:remove')")
    @Log(title = "销售区域", businessType = BusinessType.DELETE)
	@DeleteMapping("/{territoryIds}")
    public AjaxResult remove(@PathVariable String[] territoryIds)
    {
        return toAjax(crmTerritoryService.deleteCrmTerritoryByTerritoryIds(territoryIds));
    }
}
