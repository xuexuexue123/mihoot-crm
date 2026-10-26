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
import com.ruoyi.tenant.domain.MasterRegistration;
import com.ruoyi.tenant.service.IMasterRegistrationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 主注册Controller
 * 
 * @author luohd
 * @date 2024-03-17
 */
@RestController
@RequestMapping("/tenant/registration")
public class MasterRegistrationController extends BaseController
{
    @Autowired
    private IMasterRegistrationService masterRegistrationService;

    /**
     * 查询主注册列表
     */
    @PreAuthorize("@ss.hasPermi('tenant:registration:list')")
    @GetMapping("/list")
    public TableDataInfo list(MasterRegistration masterRegistration)
    {
        startPage();
        List<MasterRegistration> list = masterRegistrationService.selectMasterRegistrationList(masterRegistration);
        return getDataTable(list);
    }

    /**
     * 导出主注册列表
     */
    @PreAuthorize("@ss.hasPermi('tenant:registration:export')")
    @Log(title = "主注册", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MasterRegistration masterRegistration)
    {
        List<MasterRegistration> list = masterRegistrationService.selectMasterRegistrationList(masterRegistration);
        ExcelUtil<MasterRegistration> util = new ExcelUtil<MasterRegistration>(MasterRegistration.class);
        util.exportExcel(response, list, "主注册数据");
    }

    /**
     * 获取主注册详细信息
     */
    @PreAuthorize("@ss.hasPermi('tenant:registration:query')")
    @GetMapping(value = "/{registId}")
    public AjaxResult getInfo(@PathVariable("registId") Long registId)
    {
        return success(masterRegistrationService.selectMasterRegistrationByRegistId(registId));
    }

    /**
     * 新增主注册
     */
    @PreAuthorize("@ss.hasPermi('tenant:registration:add')")
    @Log(title = "主注册", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MasterRegistration masterRegistration)
    {
        return toAjax(masterRegistrationService.insertMasterRegistration(masterRegistration));
    }

    /**
     * 修改主注册
     */
    @PreAuthorize("@ss.hasPermi('tenant:registration:edit')")
    @Log(title = "主注册", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MasterRegistration masterRegistration)
    {
        return toAjax(masterRegistrationService.updateMasterRegistration(masterRegistration));
    }

    /**
     * 删除主注册
     */
    @PreAuthorize("@ss.hasPermi('tenant:registration:remove')")
    @Log(title = "主注册", businessType = BusinessType.DELETE)
	@DeleteMapping("/{registIds}")
    public AjaxResult remove(@PathVariable Long[] registIds)
    {
        return toAjax(masterRegistrationService.deleteMasterRegistrationByRegistIds(registIds));
    }
}
