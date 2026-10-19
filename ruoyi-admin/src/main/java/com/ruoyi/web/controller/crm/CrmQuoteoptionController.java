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
import com.ruoyi.crm.domain.CrmQuoteoption;
import com.ruoyi.crm.service.ICrmQuoteoptionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报价产品Controller
 * 
 * @author luohd
 * @date 2024-02-14
 */
@RestController
@RequestMapping("/crm/quoteoption")
public class CrmQuoteoptionController extends BaseController
{
    @Autowired
    private ICrmQuoteoptionService crmQuoteoptionService;

    /**
     * 查询报价产品列表
     */
    @PreAuthorize("@ss.hasPermi('crm:quoteoption:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmQuoteoption crmQuoteoption)
    {
        startPage();
        List<CrmQuoteoption> list = crmQuoteoptionService.selectCrmQuoteoptionList(crmQuoteoption);
        return getDataTable(list);
    }

    /**
     * 导出报价产品列表
     */
    @PreAuthorize("@ss.hasPermi('crm:quoteoption:export')")
    @Log(title = "报价产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmQuoteoption crmQuoteoption)
    {
        List<CrmQuoteoption> list = crmQuoteoptionService.selectCrmQuoteoptionList(crmQuoteoption);
        ExcelUtil<CrmQuoteoption> util = new ExcelUtil<CrmQuoteoption>(CrmQuoteoption.class);
        util.exportExcel(response, list, "报价产品数据");
    }

    /**
     * 获取报价产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:quoteoption:query')")
    @GetMapping(value = "/{optionId}")
    public AjaxResult getInfo(@PathVariable("optionId") String optionId)
    {
        return success(crmQuoteoptionService.selectCrmQuoteoptionByOptionId(optionId));
    }

    /**
     * 新增报价产品
     */
    @PreAuthorize("@ss.hasPermi('crm:quoteoption:add')")
    @Log(title = "报价产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmQuoteoption crmQuoteoption)
    {
        return toAjax(crmQuoteoptionService.insertCrmQuoteoption(crmQuoteoption));
    }

    /**
     * 修改报价产品
     */
    @PreAuthorize("@ss.hasPermi('crm:quoteoption:edit')")
    @Log(title = "报价产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmQuoteoption crmQuoteoption)
    {
        return toAjax(crmQuoteoptionService.updateCrmQuoteoption(crmQuoteoption));
    }

    /**
     * 删除报价产品
     */
    @PreAuthorize("@ss.hasPermi('crm:quoteoption:remove')")
    @Log(title = "报价产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{optionIds}")
    public AjaxResult remove(@PathVariable String[] optionIds)
    {
        return toAjax(crmQuoteoptionService.deleteCrmQuoteoptionByOptionIds(optionIds));
    }
}
