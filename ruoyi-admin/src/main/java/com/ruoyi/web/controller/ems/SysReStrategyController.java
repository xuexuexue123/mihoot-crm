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
import com.ruoyi.ems.domain.SysReStrategy;
import com.ruoyi.ems.service.ISysReStrategyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 策略管理Controller
 * 
 * @author luohd
 * @date 2023-04-01
 */
@RestController
@RequestMapping("/ems/strategy")
public class SysReStrategyController extends BaseController
{
    @Autowired
    private ISysReStrategyService sysReStrategyService;

    /**
     * 查询策略管理列表
     */
    @PreAuthorize("@ss.hasPermi('ems:strategy:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReStrategy sysReStrategy)
    {
        startPage();
        List<SysReStrategy> list = sysReStrategyService.selectSysReStrategyList(sysReStrategy);
        return getDataTable(list);
    }

    /**
     * 导出策略管理列表
     */
    @PreAuthorize("@ss.hasPermi('ems:strategy:export')")
    @Log(title = "策略管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReStrategy sysReStrategy)
    {
        List<SysReStrategy> list = sysReStrategyService.selectSysReStrategyList(sysReStrategy);
        ExcelUtil<SysReStrategy> util = new ExcelUtil<SysReStrategy>(SysReStrategy.class);
        util.exportExcel(response, list, "策略管理数据");
    }

    /**
     * 获取策略管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:strategy:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") String pyId)
    {
        return success(sysReStrategyService.selectSysReStrategyByPyId(pyId));
    }

    /**
     * 新增策略管理
     */
    @PreAuthorize("@ss.hasPermi('ems:strategy:add')")
    @Log(title = "策略管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReStrategy sysReStrategy)
    {
        return toAjax(sysReStrategyService.insertSysReStrategy(sysReStrategy));
    }

    /**
     * 修改策略管理
     */
    @PreAuthorize("@ss.hasPermi('ems:strategy:edit')")
    @Log(title = "策略管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReStrategy sysReStrategy)
    {
        return toAjax(sysReStrategyService.updateSysReStrategy(sysReStrategy));
    }

    /**
     * 删除策略管理
     */
    @PreAuthorize("@ss.hasPermi('ems:strategy:remove')")
    @Log(title = "策略管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable String[] pyIds)
    {
        return toAjax(sysReStrategyService.deleteSysReStrategyByPyIds(pyIds));
    }
}
