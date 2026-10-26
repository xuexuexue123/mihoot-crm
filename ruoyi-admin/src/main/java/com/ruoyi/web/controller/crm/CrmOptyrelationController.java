package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.crm.domain.CrmOptyrelationEmail;
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
import com.ruoyi.crm.domain.CrmOptyrelation;
import com.ruoyi.crm.service.ICrmOptyrelationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 机会关系Controller
 * 
 * @author luohd
 * @date 2024-02-07
 */
@RestController
@RequestMapping("/crm/optyrelation")
public class CrmOptyrelationController extends BaseController
{
    @Autowired
    private ICrmOptyrelationService crmOptyrelationService;

    /**
     * 查询机会关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:optyrelation:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOptyrelation crmOptyrelation)
    {
        startPage();
        List<CrmOptyrelation> list = crmOptyrelationService.selectCrmOptyrelationList(crmOptyrelation);
        return getDataTable(list);
    }

    /**
     * 导出机会关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:optyrelation:export')")
    @Log(title = "机会关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOptyrelation crmOptyrelation)
    {
        List<CrmOptyrelation> list = crmOptyrelationService.selectCrmOptyrelationList(crmOptyrelation);
        ExcelUtil<CrmOptyrelation> util = new ExcelUtil<CrmOptyrelation>(CrmOptyrelation.class);
        util.exportExcel(response, list, "机会关系数据");
    }

    /**
     * 获取机会关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:optyrelation:query')")
    @GetMapping(value = "/{relateId}")
    public AjaxResult getInfo(@PathVariable("relateId") String relateId)
    {
        return success(crmOptyrelationService.selectCrmOptyrelationByRelateId(relateId));
    }

    /**
     * 新增机会关系
     */
    @PreAuthorize("@ss.hasPermi('crm:optyrelation:add')")
    @Log(title = "机会关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOptyrelation crmOptyrelation)
    {
        return toAjax(crmOptyrelationService.insertCrmOptyrelation(crmOptyrelation));
    }

    /**
     * 修改机会关系
     */
    @PreAuthorize("@ss.hasPermi('crm:optyrelation:edit')")
    @Log(title = "机会关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOptyrelationEmail crmOptyrelation) throws Exception {
        return toAjax(crmOptyrelationService.updateCrmOptyrelation(crmOptyrelation));
    }

    /**
     * 删除机会关系
     */
    @PreAuthorize("@ss.hasPermi('crm:optyrelation:remove')")
    @Log(title = "机会关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relateIds}")
    public AjaxResult remove(@PathVariable String[] relateIds)
    {
        return toAjax(crmOptyrelationService.deleteCrmOptyrelationByRelateIds(relateIds));
    }
}
