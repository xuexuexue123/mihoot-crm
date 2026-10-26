package com.ruoyi.flowable.controller;

import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.flowable.config.TenantContext;
import com.ruoyi.flowable.service.ISysDeployFormService;
import com.ruoyi.system.domain.SysDeployForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysForm;
import com.ruoyi.flowable.service.ISysFormService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 流程表单Controller
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@RestController
@RequestMapping("/flowable/form")
public class SysFormController extends BaseController {
    @Autowired
    private ISysFormService sysFormService;

    @Autowired
    private ISysDeployFormService sysDeployFormService;

    /**
     * 查询流程表单列表
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysForm sysForm,
                              @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        startPage();
        logger.info("sysForm = " + sysForm);
        List<SysForm> list = sysFormService.selectSysFormList(sysForm);
        logger.info("sysForm = " + sysForm);
        return getDataTable(list);
    }
    /**
     * 查询流程表单
     */
    @PostMapping("/by")
    public AjaxResult getInfoById(SysForm sysForm,
                                  @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        System.out.println("this is called : " + sysForm.getFormId());
        return AjaxResult.success(sysFormService.selectSysFormById(sysForm.getFormId()));
    }
    /**
     * 导出流程表单列表
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:export')")
    @Log(title = "流程表单", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(SysForm sysForm,
                             @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        List<SysForm> list = sysFormService.selectSysFormList(sysForm);
        ExcelUtil<SysForm> util = new ExcelUtil<SysForm>(SysForm.class);
        return util.exportExcel(list, "form");
    }

    /**
     * 获取流程表单详细信息
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:query')")
    @GetMapping(value = "/{formId}")
    public AjaxResult getInfo(@PathVariable("formId") Long formId,
                              @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        return AjaxResult.success(sysFormService.selectSysFormById(formId));
    }


    /**
     * luohd: 获取流程的启动表单
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:query')")
    @GetMapping(value = "/getStarterForm/{procDefId}")
    public AjaxResult getStarterForm(@PathVariable("procDefId") String procDefId,
                                     @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        return AjaxResult.success(sysFormService.selectSysFormByProcDefId(procDefId));
    }


    /**
     * luohd: 获取当前流程节点的表单
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:query')")
    @GetMapping(value = "/getTaskForm/{taskId}")
    public AjaxResult getTaskForm(@PathVariable("taskId") String taskId,
                                  @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        return AjaxResult.success(sysFormService.selectSysFormByTaskId(taskId));
    }

    /**
     * 新增流程表单
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:add')")
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysForm sysForm,
                          @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        System.out.println("procDef id = " + sysForm.getProcDefId());
        String procDefId = sysForm.getProcDefId();
        Long resultId = sysFormService.insertSysForm(sysForm);

        System.out.println("sysFormaa = " + sysForm.getFormId());

//        if (!Objects.isNull(sysForm)&& procDefId != null) {
//            System.out.println("trying to insert relationship");
//            SysDeployForm sysDeployForm = new SysDeployForm();
//            sysDeployForm.setFormId(resultId);
//            sysDeployForm.setDeployId(procDefId);
//            return toAjax(sysDeployFormService.insertSysDeployForm(sysDeployForm));
//        }
        JSONObject json = new JSONObject();
        json.put("id", sysForm.getFormId().intValue());

        return AjaxResult.success(json);
    }

    /**
     * 修改流程表单
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:edit')")
    @Log(title = "流程表单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysForm sysForm,
                           @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        return toAjax(sysFormService.updateSysForm(sysForm));
    }

    /**
     * 删除流程表单
     */
    @PreAuthorize("@ss.hasPermi('flowable:form:remove')")
    @Log(title = "流程表单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{formIds}")
    public AjaxResult remove(@PathVariable Long[] formIds,
                             @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        return toAjax(sysFormService.deleteSysFormByIds(formIds));
    }


    /**
     * 挂载流程表单
     */
    @Log(title = "流程表单", businessType = BusinessType.INSERT)
    @PostMapping("/addDeployForm")
    public AjaxResult addDeployForm(@RequestBody SysDeployForm sysDeployForm,
                                    @RequestHeader(value = "Tenant", required = true) String tenantId) {
        TenantContext.setCurrentTenant(tenantId);
        return toAjax(sysDeployFormService.insertSysDeployForm(sysDeployForm));
    }
}
