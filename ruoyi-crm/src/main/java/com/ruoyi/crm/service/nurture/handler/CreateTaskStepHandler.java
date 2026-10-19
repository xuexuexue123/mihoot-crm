package com.ruoyi.crm.service.nurture.handler;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventNurtureEnrollment;
import com.ruoyi.crm.domain.CrmEventNurtureStep;
import com.ruoyi.crm.domain.CrmTask;
import com.ruoyi.crm.domain.dto.EventNurtureAudienceSnapshot;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmTaskService;
import com.ruoyi.crm.service.nurture.support.EventNurtureJsonSupport;
import com.ruoyi.crm.service.nurture.support.EventNurtureStepResult;
import com.ruoyi.crm.service.nurture.support.EventNurtureTaskDefaults;
import com.ruoyi.crm.service.nurture.support.EventNurtureTemplateSupport;

@Component
public class CreateTaskStepHandler
{
    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private ICrmTaskService crmTaskService;

    @Autowired
    private ICrmLeadService crmLeadService;

    public EventNurtureStepResult execute(CrmEventNurtureEnrollment enrollment, CrmEventNurtureStep step,
            EventNurtureAudienceSnapshot audience)
    {
        JSONObject action = EventNurtureJsonSupport.parseJsonObject(step.getActionJson());
        String subjectTemplate = action.getString("subjectTemplate");
        if (StringUtils.isBlank(subjectTemplate))
        {
            return EventNurtureStepResult.failed("subjectTemplate required", false);
        }

        CrmEvent event = crmEventMapper.selectCrmEventByEventId(enrollment.getEventId());
        Map<String, String> vars = EventNurtureTemplateSupport.buildVars(event, enrollment, audience);
        String subject = EventNurtureTemplateSupport.render(subjectTemplate, vars);
        String description = EventNurtureTemplateSupport.render(
                action.getString("descriptionTemplate"), vars);

        String assignTo = action.getString("assignTo");
        String owner = resolveOwner(assignTo, event, enrollment, audience);
        if ("lead_owner".equals(assignTo) && StringUtils.isBlank(owner))
        {
            return EventNurtureStepResult.skipped("no_lead_owner");
        }

        int dueInHours = action.getIntValue("dueInHours", 24);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, dueInHours);
        Date dueDate = cal.getTime();

        CrmTask task = new CrmTask();
        task.setTaskId(UUID.randomUUID().toString());
        task.setTaskSubject(subject);
        task.setTaskType(resolveTaskType(action));
        task.setComments(description);
        task.setAssignedTo(owner);
        task.setDueDate(dueDate);
        task.setStartDate(DateUtils.getNowDate());
        task.setStatusDetail(EventNurtureTaskDefaults.STATUS);
        task.setPriority(EventNurtureTaskDefaults.PRIORITY);
        task.setCreateBy(EventNurtureTaskDefaults.CREATE_BY);
        task.setCreateTime(DateUtils.getNowDate());

        if (StringUtils.isNotEmpty(enrollment.getLeadId()))
        {
            task.setRelatedTotype("Lead");
            task.setRelatedTo(enrollment.getLeadId());
            task.setRelatedToid(enrollment.getLeadId());
        }
        else
        {
            task.setRelatedTotype("Event");
            task.setRelatedTo(enrollment.getEventId());
            task.setRelatedToid(enrollment.getEventId());
        }

        crmTaskService.insertCrmTask(task);
        return EventNurtureStepResult.sent(task.getTaskId());
    }

    private String resolveOwner(String assignTo, CrmEvent event, CrmEventNurtureEnrollment enrollment,
            EventNurtureAudienceSnapshot audience)
    {
        if ("event_owner".equals(assignTo) && event != null)
        {
            return event.getOwner();
        }
        if ("lead_owner".equals(assignTo))
        {
            String leadId = enrollment.getLeadId();
            if (StringUtils.isNotEmpty(leadId))
            {
                CrmLead lead = crmLeadService.selectCrmLeadByLeadId(leadId);
                if (lead != null && StringUtils.isNotEmpty(lead.getOwner()))
                {
                    return lead.getOwner();
                }
            }
            return null;
        }
        if (StringUtils.isNotEmpty(assignTo) && !"lead_owner".equals(assignTo)
                && !"event_owner".equals(assignTo))
        {
            return assignTo;
        }
        return event != null ? event.getOwner() : null;
    }

    private String resolveTaskType(JSONObject action)
    {
        String taskType = action.getString("taskType");
        if (StringUtils.isNotBlank(taskType))
        {
            return taskType.trim();
        }
        return EventNurtureTaskDefaults.TASK_TYPE;
    }
}
