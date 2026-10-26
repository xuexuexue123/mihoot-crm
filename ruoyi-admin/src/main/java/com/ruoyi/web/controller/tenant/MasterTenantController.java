package com.ruoyi.web.controller.tenant;

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
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.service.IMasterTenantService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 租户管理Controller
 *
 * @author luohd
 * @date 2024-01-05
 */
@RestController
@RequestMapping("/tenant/tenant")
public class MasterTenantController extends BaseController
{
    @Autowired
    private IMasterTenantService masterTenantService;

    /**
     * 查询租户管理列表
     */
    @PreAuthorize("@ss.hasPermi('tenant:tenant:list')")
    @GetMapping("/list")
    public TableDataInfo list(MasterTenant masterTenant)
    {
        startPage();
        List<MasterTenant> list = masterTenantService.selectMasterTenantList(masterTenant);
        return getDataTable(list);
    }

    /**
     * 导出租户管理列表
     */
    @PreAuthorize("@ss.hasPermi('tenant:tenant:export')")
    @Log(title = "租户管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MasterTenant masterTenant)
    {
        List<MasterTenant> list = masterTenantService.selectMasterTenantList(masterTenant);
        ExcelUtil<MasterTenant> util = new ExcelUtil<MasterTenant>(MasterTenant.class);
        util.exportExcel(response, list, "租户管理数据");
    }

    /**
     * 获取租户管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('tenant:tenant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(masterTenantService.selectMasterTenantById(id));
    }

    /**
     * 新增租户管理
     */
    @PreAuthorize("@ss.hasPermi('tenant:tenant:add')")
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MasterTenant masterTenant)
    {
        return toAjax(masterTenantService.insertMasterTenant(masterTenant));
    }

    /**
     * 修改租户管理
     */
    @PreAuthorize("@ss.hasPermi('tenant:tenant:edit')")
    @Log(title = "租户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MasterTenant masterTenant)
    {
        return toAjax(masterTenantService.updateMasterTenant(masterTenant));
    }

    /**
     * 删除租户管理
     */
    @PreAuthorize("@ss.hasPermi('tenant:tenant:remove')")
    @Log(title = "租户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(masterTenantService.deleteMasterTenantByIds(ids));
    }
}
