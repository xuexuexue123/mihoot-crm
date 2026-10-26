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
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.service.ICrmOrgService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 机构Controller
 * 
 * @author luohd
 * @date 2024-01-13
 */
@RestController
@RequestMapping("/crm/organization")
public class CrmOrgController extends BaseController
{
    @Autowired
    private ICrmOrgService crmOrgService;

    /**
     * 查询机构列表
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmOrg crmOrg)
    {
        startPage();
        List<CrmOrg> list = crmOrgService.selectCrmOrgList(crmOrg);
        return getDataTable(list);
    }

    /**
     * 查询本周新建机构列表
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmOrg crmOrg)
    {
        startPage();
        List<CrmOrg> list = crmOrgService.selectCrmOrgListWeekly(crmOrg);
        return getDataTable(list);
    }
    /**
     * 导出机构列表
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:export')")
    @Log(title = "机构", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmOrg crmOrg)
    {
        List<CrmOrg> list = crmOrgService.selectCrmOrgList(crmOrg);
        ExcelUtil<CrmOrg> util = new ExcelUtil<CrmOrg>(CrmOrg.class);
        util.exportExcel(response, list, "机构数据");
    }

    /**
     * 获取机构详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:query')")
    @GetMapping(value = "/{orgId}")
    public AjaxResult getInfo(@PathVariable("orgId") String orgId)
    {
        return success(crmOrgService.selectCrmOrgByOrgId(orgId));
    }

    /**
     * 新增机构
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:add')")
    @Log(title = "机构", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmOrg crmOrg)
    {
        return toAjax(crmOrgService.insertCrmOrg(crmOrg));
    }

    /**
     * 修改机构
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:edit')")
    @Log(title = "机构", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmOrg crmOrg)
    {
        return toAjax(crmOrgService.updateCrmOrg(crmOrg));
    }

    /**
     * 删除机构
     */
    @PreAuthorize("@ss.hasPermi('crm:organization:remove')")
    @Log(title = "机构", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orgIds}")
    public AjaxResult remove(@PathVariable String[] orgIds)
    {
        return toAjax(crmOrgService.deleteCrmOrgByOrgIds(orgIds));
    }
}
