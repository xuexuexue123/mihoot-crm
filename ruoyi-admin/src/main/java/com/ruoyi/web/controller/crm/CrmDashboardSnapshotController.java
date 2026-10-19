package com.ruoyi.web.controller.crm;

import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.DashboardSnapshotResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomCustomerTotalResponse;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmDashboardSnapshotService;
import com.ruoyi.crm.service.ICrmWecomCustomerTotalService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.manager.AsyncManager;

/**
 * 企业仪表盘统计快照 API
 */
@RestController
@RequestMapping("/crm/dashboard")
public class CrmDashboardSnapshotController extends BaseController
{
    @Autowired
    private ICrmDashboardSnapshotService dashboardSnapshotService;

    @Autowired
    private ICrmWecomCustomerTotalService wecomCustomerTotalService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    /**
     * 获取当日仪表盘所有指标快照
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/snapshot")
    public AjaxResult snapshot()
    {
        DashboardSnapshotResponse snapshot = dashboardSnapshotService.getTodaySnapshot();

        // 若 wecom_customer_total 未命中快照，实时拉取 + 异步写入
        if (snapshot.getWecomCustomerTotal() == null)
        {
            WecomCustomerTotalResponse realtime = wecomCustomerTotalService.pullCustomerTotal();
            snapshot.setWecomCustomerTotal(realtime);
            asyncWriteCustomerTotalSnapshot(realtime);
        }

        return AjaxResult.success(snapshot);
    }

    /**
     * 手动触发一次客户总数快照（仅当前租户），同步拉取企微API并写入快照表
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/snapshot/trigger")
    public AjaxResult triggerSnapshot()
    {
        WecomCustomerTotalResponse data = wecomCustomerTotalService.pullCustomerTotal();
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        String corpId = cfg != null ? cfg.getCorpId() : "";
        dashboardSnapshotService.saveCustomerTotalToday(corpId, data, null);
        return AjaxResult.success(data);
    }

    private void asyncWriteCustomerTotalSnapshot(WecomCustomerTotalResponse data)
    {
        if (data == null || data.getTotalStaff() == 0)
        {
            return;
        }

        String tenantKey = TenantDataSourceSupport.currentTenantKey();
        String rawJson = null; // 实时拉取路径不需要保存 rawJson
        AsyncManager.me().execute(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    TenantDataSourceSupport.bindTenantKey(tenantKey);
                    CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
                    String corpId = cfg != null ? cfg.getCorpId() : "";
                    dashboardSnapshotService.saveCustomerTotalToday(corpId, data, rawJson);
                }
                catch (Exception e)
                {
                    logger.warn("Async write customer total snapshot failed: {}", e.getMessage());
                }
                finally
                {
                    DynamicDataSourceContextHolder.clearDataSourceKey();
                }
            }
        });
    }
}
