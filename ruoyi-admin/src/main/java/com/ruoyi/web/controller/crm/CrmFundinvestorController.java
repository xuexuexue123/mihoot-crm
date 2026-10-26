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
import com.ruoyi.crm.domain.CrmFundinvestor;
import com.ruoyi.crm.service.ICrmFundinvestorService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 投资人Controller
 * 
 * @author luohd
 * @date 2024-06-17
 */
@RestController
@RequestMapping("/crm/fundinvestor")
public class CrmFundinvestorController extends BaseController
{
    @Autowired
    private ICrmFundinvestorService crmFundinvestorService;

    /**
     * 查询投资人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:fundinvestor:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmFundinvestor crmFundinvestor)
    {
        startPage();
        List<CrmFundinvestor> list = crmFundinvestorService.selectCrmFundinvestorList(crmFundinvestor);
        return getDataTable(list);
    }

    /**
     * 导出投资人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:fundinvestor:export')")
    @Log(title = "投资人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmFundinvestor crmFundinvestor)
    {
        List<CrmFundinvestor> list = crmFundinvestorService.selectCrmFundinvestorList(crmFundinvestor);
        ExcelUtil<CrmFundinvestor> util = new ExcelUtil<CrmFundinvestor>(CrmFundinvestor.class);
        util.exportExcel(response, list, "投资人数据");
    }

    /**
     * 获取投资人详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:fundinvestor:query')")
    @GetMapping(value = "/{investorId}")
    public AjaxResult getInfo(@PathVariable("investorId") String investorId)
    {
        return success(crmFundinvestorService.selectCrmFundinvestorByInvestorId(investorId));
    }

    /**
     * 新增投资人
     */
    @PreAuthorize("@ss.hasPermi('crm:fundinvestor:add')")
    @Log(title = "投资人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmFundinvestor crmFundinvestor)
    {
        return toAjax(crmFundinvestorService.insertCrmFundinvestor(crmFundinvestor));
    }

    /**
     * 修改投资人
     */
    @PreAuthorize("@ss.hasPermi('crm:fundinvestor:edit')")
    @Log(title = "投资人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmFundinvestor crmFundinvestor)
    {
        return toAjax(crmFundinvestorService.updateCrmFundinvestor(crmFundinvestor));
    }

    /**
     * 删除投资人
     */
    @PreAuthorize("@ss.hasPermi('crm:fundinvestor:remove')")
    @Log(title = "投资人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{investorIds}")
    public AjaxResult remove(@PathVariable String[] investorIds)
    {
        return toAjax(crmFundinvestorService.deleteCrmFundinvestorByInvestorIds(investorIds));
    }
}
