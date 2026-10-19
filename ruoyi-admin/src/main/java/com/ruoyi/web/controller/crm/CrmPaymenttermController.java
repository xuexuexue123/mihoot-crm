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
import com.ruoyi.crm.domain.CrmPaymentterm;
import com.ruoyi.crm.service.ICrmPaymenttermService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 收款Controller
 * 
 * @author luohd
 * @date 2024-02-15
 */
@RestController
@RequestMapping("/crm/paymentterm")
public class CrmPaymenttermController extends BaseController
{
    @Autowired
    private ICrmPaymenttermService crmPaymenttermService;

    /**
     * 查询收款列表
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmPaymentterm crmPaymentterm)
    {
        startPage();
        List<CrmPaymentterm> list = crmPaymenttermService.selectCrmPaymenttermList(crmPaymentterm);
        return getDataTable(list);
    }

    /**
     * 查询本周收款列表
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmPaymentterm crmPaymentterm)
    {
        startPage();
        List<CrmPaymentterm> list = crmPaymenttermService.selectCrmPaymenttermListWeekly(crmPaymentterm);
        return getDataTable(list);
    }

    /**
     * 导出收款列表
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:export')")
    @Log(title = "收款", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmPaymentterm crmPaymentterm)
    {
        List<CrmPaymentterm> list = crmPaymenttermService.selectCrmPaymenttermList(crmPaymentterm);
        ExcelUtil<CrmPaymentterm> util = new ExcelUtil<CrmPaymentterm>(CrmPaymentterm.class);
        util.exportExcel(response, list, "收款数据");
    }

    /**
     * 获取收款详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:query')")
    @GetMapping(value = "/{termId}")
    public AjaxResult getInfo(@PathVariable("termId") String termId)
    {
        return success(crmPaymenttermService.selectCrmPaymenttermByTermId(termId));
    }

    /**
     * 新增收款
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:add')")
    @Log(title = "收款", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmPaymentterm crmPaymentterm)
    {
        return toAjax(crmPaymenttermService.insertCrmPaymentterm(crmPaymentterm));
    }

    /**
     * 修改收款
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:edit')")
    @Log(title = "收款", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmPaymentterm crmPaymentterm)
    {
        return toAjax(crmPaymenttermService.updateCrmPaymentterm(crmPaymentterm));
    }

    /**
     * 删除收款
     */
    @PreAuthorize("@ss.hasPermi('crm:paymentterm:remove')")
    @Log(title = "收款", businessType = BusinessType.DELETE)
	@DeleteMapping("/{termIds}")
    public AjaxResult remove(@PathVariable String[] termIds)
    {
        return toAjax(crmPaymenttermService.deleteCrmPaymenttermByTermIds(termIds));
    }
}
