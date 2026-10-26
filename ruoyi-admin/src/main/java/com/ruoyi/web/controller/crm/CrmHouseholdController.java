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
import com.ruoyi.crm.domain.CrmHousehold;
import com.ruoyi.crm.service.ICrmHouseholdService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群组Controller
 * 
 * @author luohd
 * @date 2024-01-16
 */
@RestController
@RequestMapping("/crm/household")
public class CrmHouseholdController extends BaseController
{
    @Autowired
    private ICrmHouseholdService crmHouseholdService;

    /**
     * 查询群组列表
     */
    @PreAuthorize("@ss.hasPermi('crm:household:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmHousehold crmHousehold)
    {
        startPage();
        List<CrmHousehold> list = crmHouseholdService.selectCrmHouseholdList(crmHousehold);
        return getDataTable(list);
    }

    /**
     * 导出群组列表
     */
    @PreAuthorize("@ss.hasPermi('crm:household:export')")
    @Log(title = "群组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmHousehold crmHousehold)
    {
        List<CrmHousehold> list = crmHouseholdService.selectCrmHouseholdList(crmHousehold);
        ExcelUtil<CrmHousehold> util = new ExcelUtil<CrmHousehold>(CrmHousehold.class);
        util.exportExcel(response, list, "群组数据");
    }

    /**
     * 获取群组详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:household:query')")
    @GetMapping(value = "/{householdId}")
    public AjaxResult getInfo(@PathVariable("householdId") String householdId)
    {
        return success(crmHouseholdService.selectCrmHouseholdByHouseholdId(householdId));
    }

    /**
     * 新增群组
     */
    @PreAuthorize("@ss.hasPermi('crm:household:add')")
    @Log(title = "群组", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmHousehold crmHousehold)
    {
        return toAjax(crmHouseholdService.insertCrmHousehold(crmHousehold));
    }

    /**
     * 修改群组
     */
    @PreAuthorize("@ss.hasPermi('crm:household:edit')")
    @Log(title = "群组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmHousehold crmHousehold)
    {
        return toAjax(crmHouseholdService.updateCrmHousehold(crmHousehold));
    }

    /**
     * 删除群组
     */
    @PreAuthorize("@ss.hasPermi('crm:household:remove')")
    @Log(title = "群组", businessType = BusinessType.DELETE)
	@DeleteMapping("/{householdIds}")
    public AjaxResult remove(@PathVariable String[] householdIds)
    {
        return toAjax(crmHouseholdService.deleteCrmHouseholdByHouseholdIds(householdIds));
    }
}
