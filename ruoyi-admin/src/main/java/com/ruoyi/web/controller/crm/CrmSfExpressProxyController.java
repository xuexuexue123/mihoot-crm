package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.sfexpress.SfExpressCancelRequest;
import com.ruoyi.crm.domain.dto.sfexpress.SfExpressTokenRefreshRequest;
import com.ruoyi.crm.service.ICrmSfExpressProxyService;
import com.ruoyi.crm.service.ICrmSfExpressTokenService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;

@RestController
@RequestMapping("/crm/sf-express")
public class CrmSfExpressProxyController extends BaseController
{
    @Autowired
    private ICrmSfExpressProxyService crmSfExpressProxyService;

    @Autowired
    private ICrmSfExpressTokenService crmSfExpressTokenService;

    @PreAuthorize("@ss.hasPermi('crm:sfexpress:execute')")
    @PostMapping("/token/refresh")
    public AjaxResult refreshToken(@RequestBody(required = false) SfExpressTokenRefreshRequest body)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        boolean force = body != null && Boolean.TRUE.equals(body.getForce());
        return AjaxResult.success(crmSfExpressTokenService.refreshToken(tenantId, force));
    }

    @PreAuthorize("@ss.hasPermi('crm:sfexpress:execute')")
    @PostMapping("/orders")
    public AjaxResult createOrder(@RequestBody JSONObject body)
    {
        return AjaxResult.success(crmSfExpressProxyService.createOrder(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:sfexpress:execute')")
    @GetMapping("/routes/{waybillNo}")
    public AjaxResult searchRoutes(@PathVariable("waybillNo") String waybillNo)
    {
        return AjaxResult.success(crmSfExpressProxyService.searchRoutes(waybillNo));
    }

    @PreAuthorize("@ss.hasPermi('crm:sfexpress:execute')")
    @PostMapping("/orders/cancel")
    public AjaxResult cancelOrder(@RequestBody SfExpressCancelRequest body)
    {
        String waybillNo = body != null ? body.getWaybillNo() : null;
        String orderId = body != null ? body.getOrderId() : null;
        if (StringUtils.isBlank(waybillNo) && StringUtils.isBlank(orderId))
        {
            return AjaxResult.error("waybillNo 与 orderId 至少填一项");
        }
        return AjaxResult.success(crmSfExpressProxyService.cancelOrder(waybillNo, orderId));
    }
}
