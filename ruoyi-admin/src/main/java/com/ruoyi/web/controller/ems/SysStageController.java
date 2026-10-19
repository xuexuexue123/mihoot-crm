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
import com.ruoyi.ems.domain.SysStage;
import com.ruoyi.ems.service.ISysStageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * stageController
 * 
 * @author ruoyi
 * @date 2022-12-13
 */
@RestController
@RequestMapping("/ems/stage")
public class SysStageController extends BaseController
{
    @Autowired
    private ISysStageService sysStageService;

    /**
     * 查询stage列表
     */
    @PreAuthorize("@ss.hasPermi('ems:stage:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysStage sysStage)
    {
        startPage();
        List<SysStage> list = sysStageService.selectSysStageList(sysStage);
        return getDataTable(list);
    }

    /**
     * 导出stage列表
     */
    @PreAuthorize("@ss.hasPermi('ems:stage:export')")
    @Log(title = "stage", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysStage sysStage)
    {
        List<SysStage> list = sysStageService.selectSysStageList(sysStage);
        ExcelUtil<SysStage> util = new ExcelUtil<SysStage>(SysStage.class);
        util.exportExcel(response, list, "stage数据");
    }

    /**
     * 获取stage详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:stage:query')")
    @GetMapping(value = "/{stageId}")
    public AjaxResult getInfo(@PathVariable("stageId") Long stageId)
    {
        return success(sysStageService.selectSysStageByStageId(stageId));
    }

    /**
     * 新增stage
     */
    @PreAuthorize("@ss.hasPermi('ems:stage:add')")
    @Log(title = "stage", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysStage sysStage)
    {
//        return toAjax(sysStageService.insertSysStage(sysStage));

        AjaxResult ajax = AjaxResult.success();
        ajax.put("stageId", sysStageService.insertSysStage(sysStage));

        return ajax;

    }

    /**
     * 修改stage
     */
    @PreAuthorize("@ss.hasPermi('ems:stage:edit')")
    @Log(title = "stage", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysStage sysStage)
    {
        return toAjax(sysStageService.updateSysStage(sysStage));
    }

    /**
     * 删除stage
     */
    @PreAuthorize("@ss.hasPermi('ems:stage:remove')")
    @Log(title = "stage", businessType = BusinessType.DELETE)
	@DeleteMapping("/{stageIds}")
    public AjaxResult remove(@PathVariable Long[] stageIds)
    {
        return toAjax(sysStageService.deleteSysStageByStageIds(stageIds));
    }
}
