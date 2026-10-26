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
import com.ruoyi.crm.domain.CrmResearch;
import com.ruoyi.crm.service.ICrmResearchService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 研报Controller
 * 
 * @author luohd
 * @date 2024-10-03
 */
@RestController
@RequestMapping("/crm/research")
public class CrmResearchController extends BaseController
{
    @Autowired
    private ICrmResearchService crmResearchService;

    /**
     * 查询研报列表
     */
    @PreAuthorize("@ss.hasPermi('crm:research:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmResearch crmResearch)
    {
        startPage();
        List<CrmResearch> list = crmResearchService.selectCrmResearchList(crmResearch);
        return getDataTable(list);
    }

    /**
     * 导出研报列表
     */
    @PreAuthorize("@ss.hasPermi('crm:research:export')")
    @Log(title = "研报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmResearch crmResearch)
    {
        List<CrmResearch> list = crmResearchService.selectCrmResearchList(crmResearch);
        ExcelUtil<CrmResearch> util = new ExcelUtil<CrmResearch>(CrmResearch.class);
        util.exportExcel(response, list, "研报数据");
    }

    /**
     * 获取研报详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:research:query')")
    @GetMapping(value = "/{researchId}")
    public AjaxResult getInfo(@PathVariable("researchId") String researchId)
    {
        return success(crmResearchService.selectCrmResearchByResearchId(researchId));
    }

    /**
     * 新增研报
     */
    @PreAuthorize("@ss.hasPermi('crm:research:add')")
    @Log(title = "研报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmResearch crmResearch)
    {
        return toAjax(crmResearchService.insertCrmResearch(crmResearch));
    }

    /**
     * 修改研报
     */
    @PreAuthorize("@ss.hasPermi('crm:research:edit')")
    @Log(title = "研报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmResearch crmResearch)
    {
        return toAjax(crmResearchService.updateCrmResearch(crmResearch));
    }

    /**
     * 删除研报
     */
    @PreAuthorize("@ss.hasPermi('crm:research:remove')")
    @Log(title = "研报", businessType = BusinessType.DELETE)
	@DeleteMapping("/{researchIds}")
    public AjaxResult remove(@PathVariable String[] researchIds)
    {
        return toAjax(crmResearchService.deleteCrmResearchByResearchIds(researchIds));
    }
}
