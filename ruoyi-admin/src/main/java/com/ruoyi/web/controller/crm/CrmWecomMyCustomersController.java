package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.wecom.WecomMyCustomersDetailRequest;
import com.ruoyi.crm.service.ICrmWecomMyCustomersService;

/**
 * 销售「我的企微客户」— 列表/详情/同步（强制当前用户 snUserid）
 */
@RestController
@RequestMapping("/crm/wecom/my-customers")
public class CrmWecomMyCustomersController extends BaseController
{
    @Autowired
    private ICrmWecomMyCustomersService myCustomersService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:my-customers:list')")
    @GetMapping("/list")
    public AjaxResult list()
    {
        return AjaxResult.success(myCustomersService.listMyCustomers());
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:my-customers:list')")
    @PostMapping("/details")
    public AjaxResult details(@RequestBody WecomMyCustomersDetailRequest request)
    {
        return AjaxResult.success(myCustomersService.getCustomerDetails(request));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:my-customers:sync')")
    @Log(title = "同步我的企微客户", businessType = BusinessType.UPDATE)
    @PostMapping("/import-mine")
    public AjaxResult importMine()
    {
        return AjaxResult.success(myCustomersService.importMine());
    }
}
