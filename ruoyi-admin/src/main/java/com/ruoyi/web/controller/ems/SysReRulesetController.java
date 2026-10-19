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
import com.ruoyi.ems.domain.SysReRuleset;
import com.ruoyi.ems.service.ISysReRulesetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 规则集Controller
 * 
 * @author luohd
 * @date 2023-03-25
 */
@RestController
@RequestMapping("/ems/ruleset")
public class SysReRulesetController extends BaseController
{
    @Autowired
    private ISysReRulesetService sysReRulesetService;

    /**
     * 查询规则集列表
     */
    @PreAuthorize("@ss.hasPermi('ems:ruleset:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReRuleset sysReRuleset)
    {
        startPage();
        List<SysReRuleset> list = sysReRulesetService.selectSysReRulesetList(sysReRuleset);
        return getDataTable(list);
    }

    /**
     * 导出规则集列表
     */
    @PreAuthorize("@ss.hasPermi('ems:ruleset:export')")
    @Log(title = "规则集", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReRuleset sysReRuleset)
    {
        List<SysReRuleset> list = sysReRulesetService.selectSysReRulesetList(sysReRuleset);
        ExcelUtil<SysReRuleset> util = new ExcelUtil<SysReRuleset>(SysReRuleset.class);
        util.exportExcel(response, list, "规则集数据");
    }

    /**
     * 获取规则集详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:ruleset:query')")
    @GetMapping(value = "/{pyId}")
    public AjaxResult getInfo(@PathVariable("pyId") Long pyId)
    {
        return success(sysReRulesetService.selectSysReRulesetByPyId(pyId));
    }

    /**
     * 新增规则集
     */
    @PreAuthorize("@ss.hasPermi('ems:ruleset:add')")
    @Log(title = "规则集", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReRuleset sysReRuleset)
    {
        return toAjax(sysReRulesetService.insertSysReRuleset(sysReRuleset));
    }

    /**
     * 修改规则集
     */
    @PreAuthorize("@ss.hasPermi('ems:ruleset:edit')")
    @Log(title = "规则集", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReRuleset sysReRuleset)
    {
        return toAjax(sysReRulesetService.updateSysReRuleset(sysReRuleset));
    }

    /**
     * 删除规则集
     */
    @PreAuthorize("@ss.hasPermi('ems:ruleset:remove')")
    @Log(title = "规则集", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pyIds}")
    public AjaxResult remove(@PathVariable Long[] pyIds)
    {
        return toAjax(sysReRulesetService.deleteSysReRulesetByPyIds(pyIds));
    }
}
