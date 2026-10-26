package com.ruoyi.web.controller.ems;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.ems.domain.SysCasetype;
import com.ruoyi.flowable.config.TenantContext;
import com.ruoyi.flowable.service.impl.CaseServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ems/caseinst")
public class CaseInstanceController  extends BaseController {

    @Autowired
    private CaseServiceImpl caseService;


    /**
     * 新增case instance
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:add')")
    @Log(title = "caseinst", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody SysCasetype sysCasetype,
                             @RequestHeader(value = "Tenant", required = true) String tenantId)
    {
//        logger.info("sysCasetype = " + sysCasetype);
        TenantContext.setCurrentTenant(tenantId);
        int caseId = caseService.create(sysCasetype.getCaseTypeId());
        logger.info("caseid = " + caseId);

//        caseService.pay(caseId);

        return toAjax(caseId);
    }


    /**
     * 移动到下一个阶段
     */
    @PreAuthorize("@ss.hasPermi('ems:casetype:add')")
    @Log(title = "caseinst", businessType = BusinessType.INSERT)
    @GetMapping(value = "/{caseId}")
    public AjaxResult moveToNext(@PathVariable("caseId") Integer caseId,
                                 @RequestHeader(value = "Tenant", required = true) String tenantId)
    {

        TenantContext.setCurrentTenant(tenantId);
        logger.info("caseid = " + caseId);
        caseService.next(caseId);

//        caseService.pay(caseId);

        return toAjax(caseId);
    }



    @ApiOperation(value = "根据流程定义id启动流程实例")
    @PostMapping("/start/{procDefId}/{caseId}")
    public AjaxResult start(@ApiParam(value = "流程定义id") @PathVariable(value = "procDefId") String procDefId,
                            @ApiParam(value = "case id") @PathVariable(value = "caseId") String caseId,
                            @ApiParam(value = "变量集合,json对象") @RequestBody Map<String, Object> variables,
                            @RequestHeader(value = "Tenant", required = true) String tenantId) {
        logger.info("###########case instance starting");
        TenantContext.setCurrentTenant(tenantId);
        return caseService.startCaseInstanceById(procDefId, caseId, variables);

    }
}
