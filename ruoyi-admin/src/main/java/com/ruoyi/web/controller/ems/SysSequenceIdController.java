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
import com.ruoyi.ems.domain.SysSequenceId;
import com.ruoyi.ems.service.ISysSequenceIdService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * sequenceidController
 * 
 * @author luohd
 * @date 2022-12-05
 */
@RestController
@RequestMapping("/ems/sequenceid")
public class SysSequenceIdController extends BaseController
{
    @Autowired
    private ISysSequenceIdService sysSequenceIdService;

    /**
     * 查询sequenceid列表
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysSequenceId sysSequenceId)
    {
        startPage();
        List<SysSequenceId> list = sysSequenceIdService.selectSysSequenceIdList(sysSequenceId);
        return getDataTable(list);
    }

    /**
     * 导出sequenceid列表
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:export')")
    @Log(title = "sequenceid", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysSequenceId sysSequenceId)
    {
        List<SysSequenceId> list = sysSequenceIdService.selectSysSequenceIdList(sysSequenceId);
        ExcelUtil<SysSequenceId> util = new ExcelUtil<SysSequenceId>(SysSequenceId.class);
        util.exportExcel(response, list, "sequenceid数据");
    }

    /**
     * 获取sequenceid详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:query')")
    @GetMapping(value = "/prefix/{prefix}")
    public AjaxResult getInfo(@PathVariable("prefix") String prefix)
    {
        return success(sysSequenceIdService.selectSysSequenceIdByPrefix(prefix));
    }

    /**
     * 获取sequenceid详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:query')")
    @GetMapping(value = "/{sequenceid}")
    public AjaxResult getInfo(@PathVariable("sequenceid") Long sequenceid)
    {
        return success(sysSequenceIdService.selectSysSequenceIdBySequenceid(sequenceid));
    }

    /**
     * 新增sequenceid
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:add')")
    @Log(title = "sequenceid", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysSequenceId sysSequenceId)
    {
        return toAjax(sysSequenceIdService.insertSysSequenceId(sysSequenceId));
    }

    /**
     * 修改sequenceid
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:edit')")
    @Log(title = "sequenceid", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysSequenceId sysSequenceId)
    {
        return toAjax(sysSequenceIdService.updateSysSequenceId(sysSequenceId));
    }

    /**
     * 删除sequenceid
     */
    @PreAuthorize("@ss.hasPermi('ems:sequenceid:remove')")
    @Log(title = "sequenceid", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sequenceids}")
    public AjaxResult remove(@PathVariable Long[] sequenceids)
    {
        return toAjax(sysSequenceIdService.deleteSysSequenceIdBySequenceids(sequenceids));
    }
}
