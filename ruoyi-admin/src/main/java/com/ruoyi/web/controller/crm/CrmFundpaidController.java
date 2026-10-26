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
import com.ruoyi.crm.domain.CrmFundpaid;
import com.ruoyi.crm.service.ICrmFundpaidService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 基金投资Controller
 * 
 * @author luohd
 * @date 2024-07-09
 */
@RestController
@RequestMapping("/crm/fundpaid")
public class CrmFundpaidController extends BaseController
{
    @Autowired
    private ICrmFundpaidService crmFundpaidService;

    /**
     * 查询基金投资列表
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmFundpaid crmFundpaid)
    {
        startPage();
        List<CrmFundpaid> list = crmFundpaidService.selectCrmFundpaidList(crmFundpaid);
        return getDataTable(list);
    }

    /**
     * 导出基金投资列表
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:export')")
    @Log(title = "基金投资", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmFundpaid crmFundpaid)
    {
        List<CrmFundpaid> list = crmFundpaidService.selectCrmFundpaidList(crmFundpaid);
        ExcelUtil<CrmFundpaid> util = new ExcelUtil<CrmFundpaid>(CrmFundpaid.class);
        util.exportExcel(response, list, "基金投资数据");
    }

    /**
     * 获取基金投资详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:query')")
    @GetMapping(value = "/{paidId}")
    public AjaxResult getInfo(@PathVariable("paidId") String paidId)
    {
        return success(crmFundpaidService.selectCrmFundpaidByPaidId(paidId));
    }

    /**
     * 获取基金投资详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:query')")
    @GetMapping(value = "/getlist/{interestId}")
    public TableDataInfo getListInfo(@PathVariable("interestId") String interestId)
    {
        startPage();
        List<CrmFundpaid> list = crmFundpaidService.selectCrmFundpaidByInterestId(interestId);
        return getDataTable(list);

    }

    /**
     * 新增基金投资
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:add')")
    @Log(title = "基金投资", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmFundpaid crmFundpaid)
    {
        return toAjax(crmFundpaidService.insertCrmFundpaid(crmFundpaid));
    }

    /**
     * 修改基金投资
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:edit')")
    @Log(title = "基金投资", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmFundpaid crmFundpaid)
    {
        return toAjax(crmFundpaidService.updateCrmFundpaid(crmFundpaid));
    }

    /**
     * 删除基金投资
     */
    @PreAuthorize("@ss.hasPermi('crm:fundpaid:remove')")
    @Log(title = "基金投资", businessType = BusinessType.DELETE)
	@DeleteMapping("/{paidIds}")
    public AjaxResult remove(@PathVariable String[] paidIds)
    {
        return toAjax(crmFundpaidService.deleteCrmFundpaidByPaidIds(paidIds));
    }
}
