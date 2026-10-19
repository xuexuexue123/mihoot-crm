package com.ruoyi.crm.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.domain.dto.PatientClvRecomputeRequest;
import com.ruoyi.crm.mapper.CrmPatientValueMapper;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.service.support.PatientWorkflowAutomationLauncher;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import com.ruoyi.framework.datasource.DynamicRoutingDataSource;
import com.ruoyi.tenant.domain.MasterTenant;
import com.ruoyi.tenant.domain.enums.TenantStatus;
import com.ruoyi.tenant.service.IMasterTenantService;

@Component("patientClvRecomputeTask")
public class PatientClvRecomputeTask
{
    private static final Logger log = LoggerFactory.getLogger(PatientClvRecomputeTask.class);

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Autowired
    private IMasterTenantService masterTenantService;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired
    private IPatientClvService patientClvService;

    @Autowired
    private CrmPatientValueMapper patientValueMapper;

    @Autowired
    private PatientWorkflowAutomationLauncher automationLauncher;

    public void runDaily()
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
                PatientClvRecomputeRequest req = new PatientClvRecomputeRequest();
                req.setScope("all");
                req.setTenantOnly(true);
                patientClvService.recompute(req);
                scanReactivation(tenant.getTenant());
            }
            catch (Exception e)
            {
                log.warn("Patient CLV daily recompute tenant {}: {}", tenant.getTenant(), e.getMessage());
            }
            finally
            {
                DynamicDataSourceContextHolder.clearDataSourceKey();
            }
        }
    }

    private void scanReactivation(String tenantId)
    {
        CrmPatientValue query = new CrmPatientValue();
        query.setDaysSinceLastVisitGte(180);
        List<CrmPatientValue> rows = patientValueMapper.selectCrmPatientValueList(query);
        for (CrmPatientValue row : rows)
        {
            if ("VIP".equals(row.getTier()) || "GOLD".equals(row.getTier()))
            {
                automationLauncher.triggerReactivation(row, tenantId);
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
}
