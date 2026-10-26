package com.ruoyi.crm.service.followup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.mapper.CrmFollowupRuleMapper;

/**
 * 为当前租户写入四条系统预置规则（幂等）。
 */
@Component
public class FollowupRuleSystemSeeder
{
    @Autowired
    private CrmFollowupRuleMapper ruleMapper;

    public synchronized void ensure(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        seed(tenantId, FollowupRuleConstants.SYS_OPPORTUNITY_WON, "赢单后回访",
                "商机赢单后 2 个工作日创建售后回访任务", 10,
                FollowupRuleConstants.TRIGGER_OPPORTUNITY_WON,
                FollowupRuleConstants.DELAY_BUSINESS_DAYS, 2, FollowupRuleConstants.UNIT_DAYS, null,
                "{\"taskSubject\":\"赢单回访：{{opportunity.name}}\",\"taskType\":\"售后回访\",\"relatedTotype\":\"销售机会\",\"relatedToid\":\"{{opportunity.optyId}}\",\"relatedTo\":\"{{opportunity.name}}\",\"assigneeMode\":\"record_owner\",\"dueDateMode\":\"offset_business_days\",\"dueDateOffsetDays\":2,\"writePulse\":true}",
                "{\"strategy\":\"once_per_record\"}", null);
        seed(tenantId, FollowupRuleConstants.SYS_LEAD_ASSIGNED, "新线索首次跟进",
                "线索分配负责人后 4 小时创建首次跟进任务", 20,
                FollowupRuleConstants.TRIGGER_LEAD_ASSIGNED,
                FollowupRuleConstants.DELAY_FIXED, 4, FollowupRuleConstants.UNIT_HOURS, null,
                "{\"taskSubject\":\"首次跟进：{{lead.leadName}}\",\"taskType\":\"支持类活动\",\"relatedTotype\":\"业务线索\",\"relatedToid\":\"{{lead.leadId}}\",\"relatedTo\":\"{{lead.leadName}}\",\"assigneeMode\":\"record_owner\",\"dueDateMode\":\"offset_days\",\"dueDateOffsetDays\":0}",
                "{\"strategy\":\"window_days\",\"windowDays\":3}", "LEAD_FOLLOWUP");
        seed(tenantId, FollowupRuleConstants.SYS_PATIENT_RECALL, "复诊到期提醒",
                "患者复诊到期日 09:00 创建售后回访任务", 30,
                FollowupRuleConstants.TRIGGER_PATIENT_RECALL_DUE,
                FollowupRuleConstants.DELAY_UNTIL_FIELD, null, null, "recallSchedule.nextDueAt",
                "{\"taskSubject\":\"复诊回访：{{contact.name}}（{{opportunity.treatmentItemCode}}）\",\"taskType\":\"售后回访\",\"relatedTotype\":\"联系人\",\"relatedToid\":\"{{contact.contactId}}\",\"relatedTo\":\"{{contact.name}}\",\"assigneeMode\":\"record_owner\",\"dueDateMode\":\"from_field\",\"dueDateField\":\"recallSchedule.nextDueAt\",\"writePulse\":false}",
                "{\"strategy\":\"window_days\",\"windowDays\":7}", "PATIENT_RECALL");
        upgradePatientRecallField(tenantId);
        seed(tenantId, FollowupRuleConstants.SYS_APPOINTMENT_NOSHOW, "预约未到跟进",
                "预约未到诊后 2 小时创建跟进任务", 40,
                FollowupRuleConstants.TRIGGER_APPOINTMENT_NOSHOW,
                FollowupRuleConstants.DELAY_FIXED, 2, FollowupRuleConstants.UNIT_HOURS, null,
                "{\"taskSubject\":\"未到诊跟进：{{contact.name}}\",\"taskType\":\"支持类活动\",\"relatedTotype\":\"联系人\",\"relatedToid\":\"{{contact.contactId}}\",\"relatedTo\":\"{{contact.name}}\",\"assigneeMode\":\"record_owner\",\"dueDateMode\":\"offset_hours\",\"dueDateOffsetHours\":4}",
                "{\"strategy\":\"window_days\",\"windowDays\":1}", null);
    }

    private void seed(String tenantId, String ruleId, String name, String description, int priority,
            String triggerType, String delayMode, Integer delayValue, String delayUnit, String delayField,
            String taskTemplate, String dedupeConfig, String catalogKey)
    {
        if (ruleMapper.selectEntityById(tenantId, ruleId) != null)
        {
            return;
        }
        CrmFollowupRule row = new CrmFollowupRule();
        row.setRuleId(ruleId);
        row.setTenantId(tenantId);
        row.setName(name);
        row.setDescription(description);
        row.setEnabled(FollowupRuleConstants.ENABLED);
        row.setIsSystem(FollowupRuleConstants.IS_SYSTEM);
        row.setPriority(priority);
        row.setTriggerType(triggerType);
        row.setTriggerConfig("{}");
        row.setDelayMode(delayMode);
        row.setDelayValue(delayValue);
        row.setDelayUnit(delayUnit);
        row.setDelayField(delayField);
        row.setTaskTemplate(taskTemplate);
        row.setDedupeConfig(dedupeConfig);
        row.setCatalogKey(catalogKey);
        row.setDelFlag("0");
        row.setCreateBy("system");
        row.setCreateTime(DateUtils.getNowDate());
        row.setUpdateBy("system");
        row.setUpdateTime(DateUtils.getNowDate());
        try
        {
            ruleMapper.insertCrmFollowupRule(row);
        }
        catch (Exception ignored)
        {
            // concurrent seed
        }
    }

    private void upgradePatientRecallField(String tenantId)
    {
        CrmFollowupRule existing = ruleMapper.selectEntityById(tenantId, FollowupRuleConstants.SYS_PATIENT_RECALL);
        if (existing == null)
        {
            return;
        }
        String delayField = existing.getDelayField();
        if (delayField != null && delayField.contains("recallSchedule.nextDueAt"))
        {
            return;
        }
        existing.setDelayField("recallSchedule.nextDueAt");
        existing.setTaskTemplate(
                "{\"taskSubject\":\"复诊回访：{{contact.name}}（{{opportunity.treatmentItemCode}}）\",\"taskType\":\"售后回访\",\"relatedTotype\":\"联系人\",\"relatedToid\":\"{{contact.contactId}}\",\"relatedTo\":\"{{contact.name}}\",\"assigneeMode\":\"record_owner\",\"dueDateMode\":\"from_field\",\"dueDateField\":\"recallSchedule.nextDueAt\",\"writePulse\":false}");
        existing.setUpdateBy("system");
        existing.setUpdateTime(DateUtils.getNowDate());
        try
        {
            ruleMapper.updateCrmFollowupRule(existing);
        }
        catch (Exception ignored)
        {
        }
    }
}
