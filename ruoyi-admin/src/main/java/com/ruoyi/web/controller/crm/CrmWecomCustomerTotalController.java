package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;
import com.ruoyi.crm.service.ICrmDashboardSnapshotService;
import com.ruoyi.crm.service.ICrmWecomCustomerTotalService;

/**
 * 企业微信全企业客户总数统计
 */
@RestController
@RequestMapping("/crm/wecom")
public class CrmWecomCustomerTotalController extends BaseController
{
    @Autowired
    private ICrmDashboardSnapshotService dashboardSnapshotService;

    @Autowired
    private ICrmWecomCustomerTotalService wecomCustomerTotalService;

    /**
     * 获取全企业客户总数统计（优先读今日快照，无快照则实时拉取）
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/customer-total")
    public AjaxResult customerTotal()
    {
        WecomCustomerTotalResponse data = dashboardSnapshotService.getCustomerTotalToday();
        if (data == null)
        {
            data = wecomCustomerTotalService.pullCustomerTotal();
        }
        return AjaxResult.success(data);
    }
}
