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
import com.ruoyi.crm.domain.CrmDocrelation;
import com.ruoyi.crm.service.ICrmDocrelationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 文档关系Controller
 * 
 * @author luohd
 * @date 2024-02-02
 */
@RestController
@RequestMapping("/crm/docrelation")
public class CrmDocrelationController extends BaseController
{
    @Autowired
    private ICrmDocrelationService crmDocrelationService;

    /**
     * 查询文档关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:docrelation:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmDocrelation crmDocrelation)
    {
        startPage();
        List<CrmDocrelation> list = crmDocrelationService.selectCrmDocrelationList(crmDocrelation);
        return getDataTable(list);
    }

    /**
     * 导出文档关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:docrelation:export')")
    @Log(title = "文档关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmDocrelation crmDocrelation)
    {
        List<CrmDocrelation> list = crmDocrelationService.selectCrmDocrelationList(crmDocrelation);
        ExcelUtil<CrmDocrelation> util = new ExcelUtil<CrmDocrelation>(CrmDocrelation.class);
        util.exportExcel(response, list, "文档关系数据");
    }

    /**
     * 获取文档关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:docrelation:query')")
    @GetMapping(value = "/{relateId}")
    public AjaxResult getInfo(@PathVariable("relateId") String relateId)
    {
        return success(crmDocrelationService.selectCrmDocrelationByRelateId(relateId));
    }

    /**
     * 新增文档关系
     */
    @PreAuthorize("@ss.hasPermi('crm:docrelation:add')")
    @Log(title = "文档关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmDocrelation crmDocrelation)
    {
        return toAjax(crmDocrelationService.insertCrmDocrelation(crmDocrelation));
    }

    /**
     * 修改文档关系
     */
    @PreAuthorize("@ss.hasPermi('crm:docrelation:edit')")
    @Log(title = "文档关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmDocrelation crmDocrelation)
    {
        return toAjax(crmDocrelationService.updateCrmDocrelation(crmDocrelation));
    }

    /**
     * 删除文档关系
     */
    @PreAuthorize("@ss.hasPermi('crm:docrelation:remove')")
    @Log(title = "文档关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relateIds}")
    public AjaxResult remove(@PathVariable String[] relateIds)
    {
        return toAjax(crmDocrelationService.deleteCrmDocrelationByRelateIds(relateIds));
    }
}
