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
import com.ruoyi.crm.domain.CrmInterest;
import com.ruoyi.crm.service.ICrmInterestService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 权益变更Controller
 * 
 * @author luohd
 * @date 2024-07-09
 */
@RestController
@RequestMapping("/crm/interest")
public class CrmInterestController extends BaseController
{
    @Autowired
    private ICrmInterestService crmInterestService;

    /**
     * 查询权益变更列表
     */
    @PreAuthorize("@ss.hasPermi('crm:interest:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmInterest crmInterest)
    {
        startPage();
        List<CrmInterest> list = crmInterestService.selectCrmInterestList(crmInterest);
        return getDataTable(list);
    }

    /**
     * 导出权益变更列表
     */
    @PreAuthorize("@ss.hasPermi('crm:interest:export')")
    @Log(title = "权益变更", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmInterest crmInterest)
    {
        List<CrmInterest> list = crmInterestService.selectCrmInterestList(crmInterest);
        ExcelUtil<CrmInterest> util = new ExcelUtil<CrmInterest>(CrmInterest.class);
        util.exportExcel(response, list, "权益变更数据");
    }

    /**
     * 获取权益变更详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:interest:query')")
    @GetMapping(value = "/{interestId}")
    public AjaxResult getInfo(@PathVariable("interestId") String interestId)
    {
        return success(crmInterestService.selectCrmInterestByInterestId(interestId));
    }

    /**
     * 新增权益变更
     */
    @PreAuthorize("@ss.hasPermi('crm:interest:add')")
    @Log(title = "权益变更", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmInterest crmInterest)
    {
        return toAjax(crmInterestService.insertCrmInterest(crmInterest));
    }

    /**
     * 修改权益变更
     */
    @PreAuthorize("@ss.hasPermi('crm:interest:edit')")
    @Log(title = "权益变更", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmInterest crmInterest)
    {
        return toAjax(crmInterestService.updateCrmInterest(crmInterest));
    }

    /**
     * 删除权益变更
     */
    @PreAuthorize("@ss.hasPermi('crm:interest:remove')")
    @Log(title = "权益变更", businessType = BusinessType.DELETE)
	@DeleteMapping("/{interestIds}")
    public AjaxResult remove(@PathVariable String[] interestIds)
    {
        return toAjax(crmInterestService.deleteCrmInterestByInterestIds(interestIds));
    }
}
