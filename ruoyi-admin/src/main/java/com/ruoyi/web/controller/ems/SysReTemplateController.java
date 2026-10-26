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
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目模版Controller
 * 
 * @author luohd
 * @date 2023-05-21
 */
@RestController
@RequestMapping("/ems/template")
public class SysReTemplateController extends BaseController
{
    @Autowired
    private ISysReTemplateService sysReTemplateService;

    /**
     * 查询项目模版列表
     */
    @PreAuthorize("@ss.hasPermi('ems:template:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReTemplate sysReTemplate)
    {
        startPage();
        List<SysReTemplate> list = sysReTemplateService.selectSysReTemplateList(sysReTemplate);
        return getDataTable(list);
    }

    /**
     * 导出项目模版列表
     */
    @PreAuthorize("@ss.hasPermi('ems:template:export')")
    @Log(title = "项目模版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReTemplate sysReTemplate)
    {
        List<SysReTemplate> list = sysReTemplateService.selectSysReTemplateList(sysReTemplate);
        ExcelUtil<SysReTemplate> util = new ExcelUtil<SysReTemplate>(SysReTemplate.class);
        util.exportExcel(response, list, "项目模版数据");
    }

    /**
     * 获取项目模版详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") String templateId)
    {
        return success(sysReTemplateService.selectSysReTemplateByTemplateId(templateId));
    }

    /**
     * 新增项目模版
     */
    @PreAuthorize("@ss.hasPermi('ems:template:add')")
    @Log(title = "项目模版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReTemplate sysReTemplate)
    {
        return toAjax(sysReTemplateService.insertSysReTemplate(sysReTemplate));
    }

    /**
     * 修改项目模版
     */
    @PreAuthorize("@ss.hasPermi('ems:template:edit')")
    @Log(title = "项目模版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReTemplate sysReTemplate)
    {
        return toAjax(sysReTemplateService.updateSysReTemplate(sysReTemplate));
    }

    /**
     * 删除项目模版
     */
    @PreAuthorize("@ss.hasPermi('ems:template:remove')")
    @Log(title = "项目模版", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable String[] templateIds)
    {
        return toAjax(sysReTemplateService.deleteSysReTemplateByTemplateIds(templateIds));
    }
}
