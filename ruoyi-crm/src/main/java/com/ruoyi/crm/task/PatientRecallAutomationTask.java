package com.ruoyi.crm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmRecallSchedule;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.followup.FollowupRuleHookSupport;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.service.recall.RecallScheduleService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

@Component("patientRecallAutomationTask")
public class PatientRecallAutomationTask
{
    private static final Logger log = LoggerFactory.getLogger(PatientRecallAutomationTask.class);

    private static final int BATCH = 200;

    private final Set<String> missingSchemaWarnedTenants = ConcurrentHashMap.newKeySet();

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private RecallScheduleService recallScheduleService;

    @Autowired
    private FollowupRuleHookSupport followupRuleHookSupport;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Autowired
    private ICrmContactService contactService;

    public void scanDueRecalls()
    {
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
                List<CrmRecallSchedule> due = recallScheduleService.selectDueScheduled(tenant.getTenant(), BATCH);
                missingSchemaWarnedTenants.remove(tenant.getTenant());
                for (CrmRecallSchedule row : due)
                {
                    CrmContact contact = null;
                    try
                    {
                        contact = contactService.selectCrmContactByContactId(row.getContactId());
                    }
                    catch (Exception ignored)
                    {
                    }
                    followupRuleHookSupport.onPatientRecallDue(row, contact);
                    outboundWebhookHookSupport.onPatientRecallDue(row, contact);
                    recallScheduleService.markFired(row.getScheduleId());
                }
            }
            catch (Exception e)
            {
                if (isMissingSchema(e))
                {
                    if (missingSchemaWarnedTenants.add(tenant.getTenant()))
                    {
                        log.warn("Patient recall scan skipped tenant {}: tables missing; "
                                + "run scripts/sql/seed-crm-recall-policy-ddl.sql", tenant.getTenant());
                    }
                }
                else
                {
                    log.warn("Patient recall scan tenant {}: {}", tenant.getTenant(), e.getMessage());
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
            Map<String, Object> map = new HashMap<>();
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
            if (message != null
                    && (message.contains("crm_recall_schedule") || message.contains("crm_recall_policy"))
                    && message.contains("does not exist"))
            {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
