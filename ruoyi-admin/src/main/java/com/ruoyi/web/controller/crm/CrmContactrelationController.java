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
import com.ruoyi.crm.domain.CrmContactrelation;
import com.ruoyi.crm.service.ICrmContactrelationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 关联关系Controller
 * 
 * @author luohd
 * @date 2024-01-25
 */
@RestController
@RequestMapping("/crm/contactrelation")
public class CrmContactrelationController extends BaseController
{
    @Autowired
    private ICrmContactrelationService crmContactrelationService;

    /**
     * 查询关联关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmContactrelation crmContactrelation)
    {
        startPage();
        List<CrmContactrelation> list = crmContactrelationService.selectCrmContactrelationList(crmContactrelation);
        return getDataTable(list);
    }

    /**
     * 导出关联关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:export')")
    @Log(title = "关联关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmContactrelation crmContactrelation)
    {
        List<CrmContactrelation> list = crmContactrelationService.selectCrmContactrelationList(crmContactrelation);
        ExcelUtil<CrmContactrelation> util = new ExcelUtil<CrmContactrelation>(CrmContactrelation.class);
        util.exportExcel(response, list, "关联关系数据");
    }

    /**
     * 获取关联关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:query')")
    @GetMapping(value = "/{relateId}")
    public AjaxResult getInfo(@PathVariable("relateId") String relateId)
    {
        return success(crmContactrelationService.selectCrmContactrelationByRelateId(relateId));
    }

    /**
     * 新增关联关系
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:add')")
    @Log(title = "关联关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmContactrelation crmContactrelation)
    {
        return toAjax(crmContactrelationService.insertCrmContactrelation(crmContactrelation));
    }

    /**
     * 修改关联关系
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:edit')")
    @Log(title = "关联关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmContactrelation crmContactrelation)
    {
        return toAjax(crmContactrelationService.updateCrmContactrelation(crmContactrelation));
    }

    /**
     * 删除关联关系
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:remove')")
    @Log(title = "关联关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relateIds}")
    public AjaxResult remove(@PathVariable String[] relateIds)
    {
        return toAjax(crmContactrelationService.deleteCrmContactrelationByRelateIds(relateIds));
    }

    /**
     * 删除关联关系
     */
    @PreAuthorize("@ss.hasPermi('crm:contactrelation:remove')")
    @Log(title = "关联关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/deletebycontactid")
    public AjaxResult removeByContactId(@RequestBody CrmContactrelation crmContactrelation)
    {
        return toAjax(crmContactrelationService.deleteCrmContactrelationByContactId(crmContactrelation));
    }
}
