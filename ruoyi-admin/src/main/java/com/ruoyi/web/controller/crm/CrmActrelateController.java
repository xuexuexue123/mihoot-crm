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
import com.ruoyi.crm.domain.CrmActrelate;
import com.ruoyi.crm.service.ICrmActrelateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 关联的活动Controller
 * 
 * @author luohd
 * @date 2024-01-22
 */
@RestController
@RequestMapping("/crm/actrelate")
public class CrmActrelateController extends BaseController
{
    @Autowired
    private ICrmActrelateService crmActrelateService;

    /**
     * 查询关联的活动列表
     */
    @PreAuthorize("@ss.hasPermi('crm:actrelate:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmActrelate crmActrelate)
    {
        startPage();
        List<CrmActrelate> list = crmActrelateService.selectCrmActrelateList(crmActrelate);
        return getDataTable(list);
    }

    /**
     * 导出关联的活动列表
     */
    @PreAuthorize("@ss.hasPermi('crm:actrelate:export')")
    @Log(title = "关联的活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmActrelate crmActrelate)
    {
        List<CrmActrelate> list = crmActrelateService.selectCrmActrelateList(crmActrelate);
        ExcelUtil<CrmActrelate> util = new ExcelUtil<CrmActrelate>(CrmActrelate.class);
        util.exportExcel(response, list, "关联的活动数据");
    }

    /**
     * 获取关联的活动详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:actrelate:query')")
    @GetMapping(value = "/{relateId}")
    public AjaxResult getInfo(@PathVariable("relateId") String relateId)
    {
        return success(crmActrelateService.selectCrmActrelateByRelateId(relateId));
    }

    /**
     * 新增关联的活动
     */
    @PreAuthorize("@ss.hasPermi('crm:actrelate:add')")
    @Log(title = "关联的活动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmActrelate crmActrelate)
    {
        return toAjax(crmActrelateService.insertCrmActrelate(crmActrelate));
    }

    /**
     * 修改关联的活动
     */
    @PreAuthorize("@ss.hasPermi('crm:actrelate:edit')")
    @Log(title = "关联的活动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmActrelate crmActrelate)
    {
        return toAjax(crmActrelateService.updateCrmActrelate(crmActrelate));
    }

    /**
     * 删除关联的活动
     */
    @PreAuthorize("@ss.hasPermi('crm:actrelate:remove')")
    @Log(title = "关联的活动", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relateIds}")
    public AjaxResult remove(@PathVariable String[] relateIds)
    {
        return toAjax(crmActrelateService.deleteCrmActrelateByRelateIds(relateIds));
    }
}
