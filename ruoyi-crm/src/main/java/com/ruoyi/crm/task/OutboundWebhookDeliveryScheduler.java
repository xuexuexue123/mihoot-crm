package com.ruoyi.crm.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmOutboundWebhookDelivery;
import com.ruoyi.crm.mapper.CrmOutboundWebhookDeliveryMapper;
import com.ruoyi.crm.service.webhook.OutboundWebhookDeliveryWorker;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

@Component
public class OutboundWebhookDeliveryScheduler
{
    private static final Logger log = LoggerFactory.getLogger(OutboundWebhookDeliveryScheduler.class);

    private final Set<String> missingSchemaWarnedTenants = ConcurrentHashMap.newKeySet();

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${crm.outbound-webhook.scheduler-enabled:true}")
    private boolean schedulerEnabled;

    @Value("${crm.outbound-webhook.allow-http:false}")
    private boolean allowHttp;

    @Value("${crm.outbound-webhook.claim-limit:50}")
    private int claimLimit;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private CrmOutboundWebhookDeliveryMapper deliveryMapper;

    @Autowired
    private OutboundWebhookDeliveryWorker deliveryWorker;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Scheduled(fixedDelay = 30000)
    public void processDueDeliveries()
    {
        if (!schedulerEnabled)
        {
            return;
        }
        MasterTenant query = new MasterTenant();
        List<MasterTenant> tenants = masterTenantService.selectMasterTenantList(query);
        for (MasterTenant tenant : tenants)
        {
            if (!isActiveTenant(tenant))
            {
                continue;
            }
            try
            {
                ensureTenantDataSourceReady(tenant);
                DynamicDataSourceContextHolder.setDataSourceKey(tenant.getTenant());
                TransactionTemplate tx = new TransactionTemplate(transactionManager);
                tx.execute(status -> {
                    List<CrmOutboundWebhookDelivery> due = deliveryMapper.selectDue(tenant.getTenant(),
                            new Date(), claimLimit);
                    missingSchemaWarnedTenants.remove(tenant.getTenant());
                    for (CrmOutboundWebhookDelivery row : due)
                    {
                        try
                        {
                            deliveryWorker.deliver(row, allowHttp);
                        }
                        catch (Exception ex)
                        {
                            log.warn("outbound webhook due {} failed: {}", row.getDeliveryId(), ex.getMessage());
                        }
                    }
                    return null;
                });
            }
            catch (Exception ex)
            {
                if (isMissingSchema(ex))
                {
                    if (missingSchemaWarnedTenants.add(tenant.getTenant()))
                    {
                        log.warn("Outbound webhook scheduler skipped tenant {}: tables missing; "
                                + "run scripts/sql/seed-crm-outbound-webhook-ddl.sql", tenant.getTenant());
                    }
                }
                else
                {
                    log.warn("Outbound webhook scheduler tenant {}: {}", tenant.getTenant(), ex.getMessage());
                }
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    private boolean isActiveTenant(MasterTenant tenant)
    {
        if (tenant == null || StringUtils.isBlank(tenant.getTenant()))
        {
            return false;
        }
        if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus()))
        {
            return false;
        }
        return tenant.getExpirationDate() == null || !tenant.getExpirationDate().before(DateUtils.getNowDate());
    }

    private void ensureTenantDataSourceReady(MasterTenant tenant)
    {
        String tenantId = tenant.getTenant();
        if (!dynamicRoutingDataSource.existDataSource(tenantId))
        {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("driverClassName", driverClassName);
            map.put("url", tenant.getUrl());
            map.put("username", tenant.getUsername());
            map.put("password", tenant.getPassword());
            dynamicRoutingDataSource.addDataSource(tenantId, map);
        }
    }

    private static boolean isMissingSchema(Throwable ex)
    {
        Throwable current = ex;
        while (current != null)
        {
            String message = current.getMessage();
            if (message != null && message.contains("crm_outbound_webhook") && message.contains("does not exist"))
            {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
