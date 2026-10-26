package com.ruoyi.crm.service.followup;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmFollowupRule;
import com.ruoyi.crm.domain.CrmFollowupRuleEnrollment;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.service.ICrmTaskService;

/**
 * 根据规则模板创建 CRM 任务。缺 owner / related id 时抛出 SkipException。
 */
@Component
public class FollowupRuleTaskFactory
{
    @Autowired
    private ICrmTaskService crmTaskService;

    @Autowired
    private CrmOptyMapper crmOptyMapper;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    public String createTask(CrmFollowupRule rule, CrmFollowupRuleEnrollment enrollment)
    {
        SubjectSnapshot subject = loadSubject(enrollment.getSubjectType(), enrollment.getSubjectId());
        if (subject == null)
        {
            throw new FollowupRuleSkipException(FollowupRuleConstants.SKIP_SUBJECT_DELETED);
        }

        JSONObject payload = parsePayload(enrollment.getTriggerPayload());
        JSONObject template = parsePayload(rule.getTaskTemplate());
        if (template == null)
        {
            throw new FollowupRuleSkipException(FollowupRuleConstants.SKIP_TEMPLATE_RENDER_FAILED);
        }

        String relatedId = FollowupRuleTemplateRenderer.render(
                template.getString("relatedToid"), payload, enrollment.getSubjectId());
        if (StringUtils.isBlank(relatedId))
        {
            relatedId = enrollment.getSubjectId();
        }
        if (StringUtils.isBlank(relatedId))
        {
            throw new FollowupRuleSkipException(FollowupRuleConstants.SKIP_MISSING_RELATED_ID);
        }

        String assignee = resolveAssignee(template.getString("assigneeMode"), subject, payload);
        if (StringUtils.isBlank(assignee))
        {
            throw new FollowupRuleSkipException(FollowupRuleConstants.SKIP_MISSING_ASSIGNEE);
        }

        String subjectText = FollowupRuleTemplateRenderer.render(
                template.getString("taskSubject"), payload, enrollment.getSubjectId());
        if (StringUtils.isBlank(subjectText))
        {
            subjectText = "回访：" + enrollment.getSubjectId();
        }

        String relatedTo = FollowupRuleTemplateRenderer.render(
                template.getString("relatedTo"), payload, enrollment.getSubjectId());
        if (StringUtils.isBlank(relatedTo))
        {
            relatedTo = subject.displayName;
        }

        String relatedType = FollowupRuleRelatedTypeMapper.toUiRelatedType(
                firstNonBlank(template.getString("relatedTotype"), enrollment.getSubjectType()));
        String taskType = FollowupRuleRelatedTypeMapper.toUiTaskType(template.getString("taskType"));

        CrmTask task = new CrmTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskSubject(subjectText);
        task.setTaskType(taskType);
        task.setAssignedTo(assignee);
        if (subject.userId != null)
        {
            task.setUserId(subject.userId);
            task.setAssignedToid(String.valueOf(subject.userId));
        }
        if (subject.deptId != null)
        {
            task.setDeptId(subject.deptId);
        }
        task.setRelatedTotype(relatedType);
        task.setRelatedToid(relatedId);
        task.setRelatedTo(relatedTo);
        task.setDueDate(resolveDueDate(template, enrollment));
        task.setStartDate(DateUtils.getNowDate());
        task.setStatusDetail(FollowupRuleConstants.TASK_STATUS);
        task.setPriority(FollowupRuleConstants.TASK_PRIORITY);
        task.setCreateBy(FollowupRuleConstants.CREATE_BY);
        task.setCreateTime(DateUtils.getNowDate());
        crmTaskService.insertCrmTask(task);
        return task.getTaskId();
    }

    SubjectSnapshot loadSubject(String subjectType, String subjectId)
    {
        if (StringUtils.isBlank(subjectId))
        {
            return null;
        }
        if (FollowupRuleConstants.SUBJECT_OPPORTUNITY.equals(subjectType) || "销售机会".equals(subjectType))
        {
            CrmOpty opty = crmOptyMapper.selectCrmOptyByOptyId(subjectId);
            if (opty == null)
            {
                return null;
            }
            return new SubjectSnapshot(opty.getName(), opty.getOwner(), opty.getUserId(), opty.getDeptId());
        }
        if (FollowupRuleConstants.SUBJECT_LEAD.equals(subjectType) || "业务线索".equals(subjectType))
        {
            CrmLead lead = crmLeadMapper.selectCrmLeadByLeadId(subjectId);
            if (lead == null)
            {
                return null;
            }
            return new SubjectSnapshot(lead.getLeadName(), lead.getOwner(), lead.getUserId(), lead.getDeptId());
        }
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(subjectId);
        if (contact == null)
        {
            return null;
        }
        return new SubjectSnapshot(contact.getName(), contact.getOwner(), contact.getUserId(), contact.getDeptId());
    }

    private String resolveAssignee(String assigneeMode, SubjectSnapshot subject, JSONObject payload)
    {
        if (subject != null && StringUtils.isNotEmpty(subject.owner))
        {
            return subject.owner;
        }
        if (payload == null)
        {
            return null;
        }
        String fromOpty = FollowupRuleTemplateRenderer.lookup(payload, "opportunity.owner");
        if (StringUtils.isNotBlank(fromOpty))
        {
            return fromOpty;
        }
        String fromLead = FollowupRuleTemplateRenderer.lookup(payload, "lead.owner");
        if (StringUtils.isNotBlank(fromLead))
        {
            return fromLead;
        }
        return FollowupRuleTemplateRenderer.lookup(payload, "contact.owner");
    }

    private Date resolveDueDate(JSONObject template, CrmFollowupRuleEnrollment enrollment)
    {
        String mode = template.getString("dueDateMode");
        Date now = DateUtils.getNowDate();
        if ("offset_hours".equals(mode))
        {
            int hours = template.getIntValue("dueDateOffsetHours", 4);
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.HOUR_OF_DAY, hours);
            return cal.getTime();
        }
        if ("offset_business_days".equals(mode))
        {
            int days = template.getIntValue("dueDateOffsetDays", 2);
            return FollowupRuleDelayCalculator.toDate(
                    FollowupRuleDelayCalculator.addBusinessDays(now.toInstant(), days));
        }
        if ("offset_days".equals(mode))
        {
            int days = template.getIntValue("dueDateOffsetDays", 0);
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_MONTH, days);
            return cal.getTime();
        }
        if ("from_field".equals(mode) && enrollment.getNextRunAt() != null)
        {
            return enrollment.getNextRunAt();
        }
        return enrollment.getNextRunAt() != null ? enrollment.getNextRunAt() : now;
    }

    private static JSONObject parsePayload(String json)
    {
        if (StringUtils.isBlank(json))
        {
            return new JSONObject();
        }
        try
        {
            return JSON.parseObject(json);
        }
        catch (Exception ex)
        {
            return new JSONObject();
        }
    }

    private static String firstNonBlank(String a, String b)
    {
        return StringUtils.isNotBlank(a) ? a : b;
    }

    static final class SubjectSnapshot
    {
        final String displayName;
        final String owner;
        final Long userId;
        final Long deptId;

        SubjectSnapshot(String displayName, String owner, Long userId, Long deptId)
        {
            this.displayName = displayName;
            this.owner = owner;
            this.userId = userId;
            this.deptId = deptId;
        }
    }

    public static class FollowupRuleSkipException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        private final String skipReason;

        public FollowupRuleSkipException(String skipReason)
        {
            super(skipReason);
            this.skipReason = skipReason;
        }

        public String getSkipReason()
        {
            return skipReason;
        }
    }
}
