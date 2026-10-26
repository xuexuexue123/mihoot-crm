package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.crm.domain.CrmCollectEmail;
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
import com.ruoyi.crm.domain.CrmCollect;
import com.ruoyi.crm.service.ICrmCollectService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据收集Controller
 * 
 * @author luohd
 * @date 2024-07-04
 */
@RestController
@RequestMapping("/crm/collect")
public class CrmCollectController extends BaseController
{
    @Autowired
    private ICrmCollectService crmCollectService;

    /**
     * 查询数据收集列表
     */
    @PreAuthorize("@ss.hasPermi('crm:collect:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmCollect crmCollect)
    {
        startPage();
        List<CrmCollect> list = crmCollectService.selectCrmCollectList(crmCollect);
        return getDataTable(list);
    }

    /**
     * 导出数据收集列表
     */
    @PreAuthorize("@ss.hasPermi('crm:collect:export')")
    @Log(title = "数据收集", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmCollect crmCollect)
    {
        List<CrmCollect> list = crmCollectService.selectCrmCollectList(crmCollect);
        ExcelUtil<CrmCollect> util = new ExcelUtil<CrmCollect>(CrmCollect.class);
        util.exportExcel(response, list, "数据收集数据");
    }

    /**
     * 获取数据收集详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:collect:query')")
    @GetMapping(value = "/{collectId}")
    public AjaxResult getInfo(@PathVariable("collectId") String collectId)
    {
        return success(crmCollectService.selectCrmCollectByCollectId(collectId));
    }

    /**
     * 新增数据收集
     */
    @PreAuthorize("@ss.hasPermi('crm:collect:add')")
    @Log(title = "数据收集", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmCollectEmail crmCollect) throws Exception {
        return toAjax(crmCollectService.insertCrmCollect(crmCollect));
    }

    /**
     * 修改数据收集
     */
    @PreAuthorize("@ss.hasPermi('crm:collect:edit')")
    @Log(title = "数据收集", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmCollect crmCollect)
    {
        return toAjax(crmCollectService.updateCrmCollect(crmCollect));
    }

    /**
     * 删除数据收集
     */
    @PreAuthorize("@ss.hasPermi('crm:collect:remove')")
    @Log(title = "数据收集", businessType = BusinessType.DELETE)
	@DeleteMapping("/{collectIds}")
    public AjaxResult remove(@PathVariable String[] collectIds)
    {
        return toAjax(crmCollectService.deleteCrmCollectByCollectIds(collectIds));
    }
}
