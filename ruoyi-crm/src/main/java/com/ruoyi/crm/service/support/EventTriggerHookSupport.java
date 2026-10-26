package com.ruoyi.crm.service.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmActivity;
import com.ruoyi.crm.domain.dto.EventTriggerDispatchPayload;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmOrgMapper;
import com.ruoyi.crm.mapper.CrmOptyMapper;
import com.ruoyi.crm.service.IEventTriggerDispatchService;

/**
 * crm_activity 插入成功后：富化 subject，事务提交后 dispatch Event Trigger
 */
@Component
public class EventTriggerHookSupport
{
    private static final Logger log = LoggerFactory.getLogger(EventTriggerHookSupport.class);

    private static final DateTimeFormatter ISO_OFFSET =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault());

    @Autowired
    private IEventTriggerDispatchService eventTriggerDispatchService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmOrgMapper crmOrgMapper;

    @Autowired
    private CrmOptyMapper crmOptyMapper;

    @Value("${event-trigger.dispatch-enabled:true}")
    private boolean dispatchEnabled;

    /**
     * 逗号分隔白名单；未配置时用默认两种；显式空字符串表示不过滤。
     */
    @Value("${event-trigger.event-type-whitelist:engagement.survey.completed,engagement.assessment.completed}")
    private String eventTypeWhitelistRaw;

    private Set<String> eventTypeWhitelist = Collections.emptySet();

    private boolean whitelistDisabled;

    @PostConstruct
    void initWhitelist()
    {
        if (eventTypeWhitelistRaw == null)
        {
            eventTypeWhitelist = new HashSet<>(Arrays.asList(
                    "engagement.survey.completed", "engagement.assessment.completed"));
            whitelistDisabled = false;
            return;
        }
        String trimmed = eventTypeWhitelistRaw.trim();
        if (trimmed.isEmpty())
        {
            whitelistDisabled = true;
            eventTypeWhitelist = Collections.emptySet();
            return;
        }
        whitelistDisabled = false;
        eventTypeWhitelist = Arrays.stream(trimmed.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public void afterActivityInserted(CrmActivity activity)
    {
        if (!dispatchEnabled || activity == null)
        {
            return;
        }
        if (StringUtils.isBlank(activity.getEventType())
                || StringUtils.isBlank(activity.getSubjectType())
                || StringUtils.isBlank(activity.getSubjectId()))
        {
            return;
        }
        if (!whitelistDisabled && !eventTypeWhitelist.contains(activity.getEventType().trim()))
        {
            return;
        }

        String tenant = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenant))
        {
            log.debug("event trigger hook skipped: tenant datasource not set");
            return;
        }

        String subjectEntity = EventTriggerUtils.toSubjectEntity(activity.getSubjectType());
        Map<String, Object> subject = loadSubject(activity.getSubjectType(), activity.getSubjectId());

        EventTriggerDispatchPayload payload = new EventTriggerDispatchPayload();
        payload.setPulseId(StringUtils.isNotBlank(activity.getPulseId())
                ? activity.getPulseId() : activity.getActivityId());
        payload.setEventType(activity.getEventType().trim());
        payload.setSubjectType(activity.getSubjectType().trim());
        payload.setSubjectId(activity.getSubjectId().trim());
        payload.setOccurredAt(formatOccurredAt(activity));
        payload.setSubjectEntity(subjectEntity);
        payload.setSubject(subject);

        eventTriggerDispatchService.registerAfterCommit(tenant, payload);
    }

    private Map<String, Object> loadSubject(String subjectType, String subjectId)
    {
        try
        {
            Object record = null;
            String entity = EventTriggerUtils.toSubjectEntity(subjectType);
            if ("lead".equals(entity))
            {
                record = crmLeadMapper.selectCrmLeadByLeadId(subjectId);
            }
            else if ("contact".equals(entity))
            {
                record = crmContactMapper.selectCrmContactByContactId(subjectId);
            }
            else if ("organization".equals(entity))
            {
                record = crmOrgMapper.selectCrmOrgByOrgId(subjectId);
            }
            else if ("opportunity".equals(entity))
            {
                record = crmOptyMapper.selectCrmOptyByOptyId(subjectId);
            }
            if (record == null)
            {
                return null;
            }
            return toSubjectMap(record);
        }
        catch (Exception e)
        {
            log.warn("event trigger subject load failed subjectType={}, subjectId={}: {}",
                    subjectType, subjectId, e.getMessage());
            return null;
        }
    }

    private static Map<String, Object> toSubjectMap(Object record)
    {
        return JSON.parseObject(JSON.toJSONString(record), new TypeReference<Map<String, Object>>() {});
    }

    private static String formatOccurredAt(CrmActivity activity)
    {
        Date when = activity.getStartedDate() != null ? activity.getStartedDate() : activity.getCreateTime();
        if (when == null)
        {
            return Instant.now().toString();
        }
        return ISO_OFFSET.format(when.toInstant());
    }
}
