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
import com.ruoyi.crm.domain.CrmContract;
import com.ruoyi.crm.service.ICrmContractService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 合同Controller
 * 
 * @author luohd
 * @date 2024-02-15
 */
@RestController
@RequestMapping("/crm/contract")
public class CrmContractController extends BaseController
{
    @Autowired
    private ICrmContractService crmContractService;

    /**
     * 查询合同列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmContract crmContract)
    {
        startPage();
        List<CrmContract> list = crmContractService.selectCrmContractList(crmContract);
        return getDataTable(list);
    }

    /**
     * 查询本周新签订的合同列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmContract crmContract)
    {
        startPage();
        List<CrmContract> list = crmContractService.selectCrmContractListWeekly(crmContract);
        return getDataTable(list);
    }

    /**
     * 查询到期的合同列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:list')")
    @GetMapping("/listdue")
    public TableDataInfo listdue(CrmContract crmContract)
    {
        startPage();
        List<CrmContract> list = crmContractService.selectCrmContractDueList(crmContract);
        return getDataTable(list);
    }

    /**
     * 导出合同列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:export')")
    @Log(title = "合同", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmContract crmContract)
    {
        List<CrmContract> list = crmContractService.selectCrmContractList(crmContract);
        ExcelUtil<CrmContract> util = new ExcelUtil<CrmContract>(CrmContract.class);
        util.exportExcel(response, list, "合同数据");
    }

    /**
     * 获取合同详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:query')")
    @GetMapping(value = "/{contractId}")
    public AjaxResult getInfo(@PathVariable("contractId") String contractId)
    {
        return success(crmContractService.selectCrmContractByContractId(contractId));
    }

    /**
     * 新增合同
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:add')")
    @Log(title = "合同", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmContract crmContract)
    {
        return toAjax(crmContractService.insertCrmContract(crmContract));
    }

    /**
     * 修改合同
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:edit')")
    @Log(title = "合同", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmContract crmContract)
    {
        return toAjax(crmContractService.updateCrmContract(crmContract));
    }

    /**
     * 删除合同
     */
    @PreAuthorize("@ss.hasPermi('crm:contract:remove')")
    @Log(title = "合同", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contractIds}")
    public AjaxResult remove(@PathVariable String[] contractIds)
    {
        return toAjax(crmContractService.deleteCrmContractByContractIds(contractIds));
    }
}
