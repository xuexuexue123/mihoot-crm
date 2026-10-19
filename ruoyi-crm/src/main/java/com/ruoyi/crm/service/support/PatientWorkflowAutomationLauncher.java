package com.ruoyi.crm.service.support;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmPatientAutomationLog;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.domain.CrmWorkflowAutomation;
import com.ruoyi.crm.enums.WorkflowAutomationKey;
import com.ruoyi.crm.mapper.CrmPatientAutomationLogMapper;
import com.ruoyi.crm.mapper.CrmPatientValueMapper;
import com.ruoyi.crm.mapper.CrmWorkflowAutomationMapper;
import com.ruoyi.flowable.service.IFlowDefinitionService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Component
public class PatientWorkflowAutomationLauncher
{
    private static final String ENABLED = "1";

    @Autowired
    private CrmWorkflowAutomationMapper workflowAutomationMapper;

    @Autowired
    private CrmPatientValueMapper patientValueMapper;

    @Autowired
    private CrmPatientAutomationLogMapper automationLogMapper;

    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    public void triggerRecall(CrmPatientValue patient, String tenantId)
    {
        if (patient == null || patient.getNextRecallDueAt() == null)
        {
            return;
        }
        launch(tenantId, WorkflowAutomationKey.PATIENT_RECALL.getKey(), patient.getContactId(),
                patient.getNextRecallDueAt(), buildRecallPayload(patient));
    }

    public void triggerReactivation(CrmPatientValue patient, String tenantId)
    {
        if (patient == null)
        {
            return;
        }
        launch(tenantId, WorkflowAutomationKey.PATIENT_REACTIVATION.getKey(), patient.getContactId(),
                new Date(), buildRecallPayload(patient));
    }

    public void triggerUpsell(String contactId, String accountId)
    {
        CrmPatientValue patient = patientValueMapper.selectByContactId(contactId);
        Map<String, Object> payload = new HashMap<>();
        payload.put("contactId", contactId);
        payload.put("accountId", accountId);
        if (patient != null)
        {
            payload.put("tier", patient.getTier());
            payload.put("netClv", patient.getNetClv());
        }
        launch(DynamicDataSourceContextHolder.getDataSourceKey(), WorkflowAutomationKey.PATIENT_UPSELL.getKey(), contactId, new Date(), payload);
    }

    private Map<String, Object> buildRecallPayload(CrmPatientValue patient)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("contactId", patient.getContactId());
        payload.put("accountId", patient.getAccountId());
        payload.put("tier", patient.getTier());
        payload.put("netClv", patient.getNetClv());
        payload.put("nextRecallDueAt", patient.getNextRecallDueAt());
        return payload;
    }

    private void launch(String tenantId, String automationKey, String contactId, Date dueDate, Map<String, Object> payload)
    {
        if (StringUtils.isEmpty(contactId))
        {
            return;
        }
        CrmWorkflowAutomation automation = workflowAutomationMapper.selectEntityByTenantAndKey(tenantId, automationKey);
        if (automation == null || !ENABLED.equals(automation.getEnabled()) || StringUtils.isEmpty(automation.getWorkflowId()))
        {
            return;
        }
        java.sql.Date due = new java.sql.Date(dueDate.getTime());
        if (WorkflowAutomationKey.PATIENT_RECALL.getKey().equals(automationKey)
                && automationLogMapper.existsByContactKeyDue(contactId, automationKey, due) > 0)
        {
            return;
        }
        flowDefinitionService.startProcessInstanceById(automation.getWorkflowId(), null, payload);
        if (WorkflowAutomationKey.PATIENT_RECALL.getKey().equals(automationKey))
        {
            CrmPatientAutomationLog log = new CrmPatientAutomationLog();
            log.setLogId(IdUtils.fastUUID());
            log.setContactId(contactId);
            log.setAutomationKey(automationKey);
            log.setDueDate(due);
            log.setTriggeredAt(new Date());
            log.setWorkflowId(automation.getWorkflowId());
            automationLogMapper.insertIfNotExists(log);
        }
    }
}
