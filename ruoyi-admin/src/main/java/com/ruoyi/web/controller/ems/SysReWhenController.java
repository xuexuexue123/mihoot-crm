package com.ruoyi.web.controller.ems;

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
import com.ruoyi.ems.domain.SysReWhen;
import com.ruoyi.ems.service.ISysReWhenService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WhenRuleController
 * 
 * @author luohd
 * @date 2023-04-02
 */
@RestController
@RequestMapping("/ems/WhenRule")
public class SysReWhenController extends BaseController
{
    @Autowired
    private ISysReWhenService sysReWhenService;

    /**
     * 查询WhenRule列表
     */
    @PreAuthorize("@ss.hasPermi('ems:WhenRule:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReWhen sysReWhen)
    {
        startPage();
        List<SysReWhen> list = sysReWhenService.selectSysReWhenList(sysReWhen);
        return getDataTable(list);
    }

    /**
     * 导出WhenRule列表
     */
    @PreAuthorize("@ss.hasPermi('ems:WhenRule:export')")
    @Log(title = "WhenRule", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReWhen sysReWhen)
    {
        List<SysReWhen> list = sysReWhenService.selectSysReWhenList(sysReWhen);
        ExcelUtil<SysReWhen> util = new ExcelUtil<SysReWhen>(SysReWhen.class);
        util.exportExcel(response, list, "WhenRule数据");
    }

    /**
     * 获取WhenRule详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:WhenRule:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReWhenService.selectSysReWhenByPyId(pyId));
    }

    /**
     * 新增WhenRule
     */
    @PreAuthorize("@ss.hasPermi('ems:WhenRule:add')")
    @Log(title = "WhenRule", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReWhen sysReWhen)
    {
        return toAjax(sysReWhenService.insertSysReWhen(sysReWhen));
    }

    /**
     * 修改WhenRule
     */
    @PreAuthorize("@ss.hasPermi('ems:WhenRule:edit')")
    @Log(title = "WhenRule", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReWhen sysReWhen)
    {
        return toAjax(sysReWhenService.updateSysReWhen(sysReWhen));
    }

    /**
     * 删除WhenRule
     */
    @PreAuthorize("@ss.hasPermi('ems:WhenRule:remove')")
    @Log(title = "WhenRule", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReWhenService.deleteSysReWhenByPyIds(pyIds));
    }
}
