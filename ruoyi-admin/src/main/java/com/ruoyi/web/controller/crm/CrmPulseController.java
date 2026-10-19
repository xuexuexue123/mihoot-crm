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
import com.ruoyi.crm.domain.CrmPulse;
import com.ruoyi.crm.service.ICrmPulseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 动态Controller
 * 
 * @author luohd
 * @date 2024-02-24
 */
@RestController
@RequestMapping("/crm/pulse")
public class CrmPulseController extends BaseController
{
    @Autowired
    private ICrmPulseService crmPulseService;

    /**
     * 查询动态列表
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmPulse crmPulse)
    {
        startPage();
        List<CrmPulse> list = crmPulseService.selectCrmPulseList(crmPulse);
        return getDataTable(list);
    }

    /**
     * 查询动态列表 by Ids
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:list')")
    @GetMapping("/listbyids/{pulseIds}/{parentId}")
    public TableDataInfo listbyids(@PathVariable String[] pulseIds, @PathVariable String parentId)
    {
        startPage();
        if (parentId.equals("1")) {
            List<CrmPulse> list = crmPulseService.selectNoteByPulseIds(pulseIds);
            return getDataTable(list);
        }
        List<CrmPulse> list = crmPulseService.selectCrmPulseByPulseIds(pulseIds);
        return getDataTable(list);
    }

    /**
     * 查询本周动态列表
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmPulse crmPulse)
    {
        startPage();
        List<CrmPulse> list = crmPulseService.selectCrmPulseListWeekly(crmPulse);
        return getDataTable(list);
    }
    /**
     * 导出动态列表
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:export')")
    @Log(title = "动态", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmPulse crmPulse)
    {
        List<CrmPulse> list = crmPulseService.selectCrmPulseList(crmPulse);
        ExcelUtil<CrmPulse> util = new ExcelUtil<CrmPulse>(CrmPulse.class);
        util.exportExcel(response, list, "动态数据");
    }

    /**
     * 获取动态详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:query')")
    @GetMapping(value = "/{pulseId}")
    public AjaxResult getInfo(@PathVariable("pulseId") String pulseId)
    {
        return success(crmPulseService.selectCrmPulseByPulseId(pulseId));
    }

    /**
     * 新增动态
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:add')")
    @Log(title = "动态", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmPulse crmPulse)
    {
        return toAjax(crmPulseService.insertCrmPulse(crmPulse));
    }

    /**
     * 修改动态
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:edit')")
    @Log(title = "动态", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmPulse crmPulse)
    {
        return toAjax(crmPulseService.updateCrmPulse(crmPulse));
    }

    /**
     * 删除动态
     */
    @PreAuthorize("@ss.hasPermi('crm:pulse:remove')")
    @Log(title = "动态", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pulseIds}")
    public AjaxResult remove(@PathVariable String[] pulseIds)
    {
        return toAjax(crmPulseService.deleteCrmPulseByPulseIds(pulseIds));
    }
}
