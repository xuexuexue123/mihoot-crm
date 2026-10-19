package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.crm.domain.CrmAccountrelationEmail;
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
import com.ruoyi.crm.domain.CrmAccountrelation;
import com.ruoyi.crm.service.ICrmAccountrelationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 客户关系Controller
 * 
 * @author luohd
 * @date 2024-05-19
 */
@RestController
@RequestMapping("/crm/accountrelation")
public class CrmAccountrelationController extends BaseController
{
    @Autowired
    private ICrmAccountrelationService crmAccountrelationService;

    /**
     * 查询客户关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmAccountrelation crmAccountrelation)
    {
        startPage();
        List<CrmAccountrelation> list = crmAccountrelationService.selectCrmAccountrelationList(crmAccountrelation);
        return getDataTable(list);
    }

    /**
     * 导出客户关系列表
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:export')")
    @Log(title = "客户关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmAccountrelation crmAccountrelation)
    {
        List<CrmAccountrelation> list = crmAccountrelationService.selectCrmAccountrelationList(crmAccountrelation);
        ExcelUtil<CrmAccountrelation> util = new ExcelUtil<CrmAccountrelation>(CrmAccountrelation.class);
        util.exportExcel(response, list, "客户关系数据");
    }

    /**
     * 获取客户关系详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:query')")
    @GetMapping(value = "/{relateId}")
    public AjaxResult getInfo(@PathVariable("relateId") String relateId)
    {
        return success(crmAccountrelationService.selectCrmAccountrelationByRelateId(relateId));
    }

    /**
     * 新增客户关系
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:add')")
    @Log(title = "客户关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmAccountrelation crmAccountrelation)
    {
        return toAjax(crmAccountrelationService.insertCrmAccountrelation(crmAccountrelation));
    }

    /**
     * 修改客户关系
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:edit')")
    @Log(title = "客户关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmAccountrelationEmail crmAccountrelation) throws Exception {
        return toAjax(crmAccountrelationService.updateCrmAccountrelation(crmAccountrelation));
    }

    /**
     * 删除客户关系
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:remove')")
    @Log(title = "客户关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete")
    public AjaxResult removeByAccountId(@RequestBody CrmAccountrelation crmAccountrelation)
    {
        return toAjax(crmAccountrelationService.deleteCrmAccountrelationByAccountId(crmAccountrelation));
    }

    /**
     * 删除客户关系
     */
    @PreAuthorize("@ss.hasPermi('crm:accountrelation:remove')")
    @Log(title = "客户关系", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relateIds}")
    public AjaxResult remove(@PathVariable String[] relateIds)
    {
        return toAjax(crmAccountrelationService.deleteCrmAccountrelationByRelateIds(relateIds));
    }
}
