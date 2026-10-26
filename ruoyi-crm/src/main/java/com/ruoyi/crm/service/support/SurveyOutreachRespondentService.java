package com.ruoyi.crm.service.support;

import java.util.Date;
import java.util.Map;
import com.ruoyi.common.utils.uuid.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmOutreachRespondent;
import com.ruoyi.crm.domain.CrmRespondents;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.enums.EventType;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.event.EventDispatcher;
import com.ruoyi.crm.service.ICrmRespondentsService;
import com.ruoyi.crm.service.ICrmSurveysService;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.SurveyVerifiedPayload;

/**
 * 问卷 H5 公开提交（token 校验、落库、Activity）
 */
@Service
public class SurveyOutreachRespondentService
{
    private static final String STATUS_COMPLETED = "completed";

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Autowired
    private ICrmRespondentsService crmRespondentsService;

    @Autowired
    private ICrmSurveysService crmSurveysService;

    @Autowired
    private EventDispatcher eventDispatcher;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private ObjectMapper objectMapper;

    public int submit(CrmOutreachRespondent request)
    {
        if (StringUtils.isBlank(request.getSurveyToken()))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }

        SurveyVerifiedPayload payload = outreachRegistrationTokenService.verifySurvey(request.getSurveyToken());

        if (StringUtils.isNotBlank(request.getTenantId())
                && !payload.getTenantId().equals(request.getTenantId()))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        if (StringUtils.isNotBlank(request.getRelateId())
                && !payload.getSurveyId().equals(request.getRelateId()))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        if (StringUtils.isNotBlank(request.getContactId())
                && payload.getContactId() != null
                && !payload.getContactId().equals(request.getContactId()))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }

        CrmSurveys survey = crmSurveysService.selectCrmSurveysById(payload.getSurveyId());
        if (survey == null)
        {
            throw new ServiceException("Survey not found", HttpStatus.NOT_FOUND);
        }

        String respondentId = StringUtils.isNotBlank(request.getId())
                ? request.getId()
                : UUID.randomUUID().toString();
        CrmRespondents existing = crmRespondentsService.selectCrmRespondentsById(respondentId);
        boolean wasCompleted = existing != null && STATUS_COMPLETED.equals(existing.getStatus());

        CrmOutreachRespondent toSave = new CrmOutreachRespondent();
        toSave.setId(respondentId);
        toSave.setRelateId(payload.getSurveyId());
        toSave.setRelateName(StringUtils.isNotBlank(request.getRelateName())
                ? request.getRelateName() : survey.getTitle());
        toSave.setSurveyJson(request.getSurveyJson());
        toSave.setStatus(request.getStatus());
        toSave.setDescription(request.getDescription());
        toSave.setSessionId(request.getSessionId());
        toSave.setStartedAt(request.getStartedAt());
        toSave.setMetadata(mergeMetadata(request.getMetadata(), payload.getChannel()));

        if (payload.getContactId() != null)
        {
            toSave.setContactId(payload.getContactId());
        }

        Date now = DateUtils.getNowDate();
        if (STATUS_COMPLETED.equals(request.getStatus()))
        {
            if (existing == null || existing.getCompletedAt() == null)
            {
                toSave.setCompletedAt(now);
            }
            else
            {
                toSave.setCompletedAt(existing.getCompletedAt());
            }
        }
        else if (existing != null)
        {
            toSave.setCompletedAt(existing.getCompletedAt());
        }

        int result;
        if (existing != null)
        {
            toSave.setUpdateTime(now);
            result = crmRespondentsService.updateCrmRespondents(toSave);
        }
        else
        {
            toSave.setCreateTime(now);
            result = crmRespondentsService.insertCrmRespondents(toSave);
        }

        dispatchWorkflowEvent(toSave);

        if (STATUS_COMPLETED.equals(request.getStatus())
                && StringUtils.isNotBlank(toSave.getContactId())
                && !wasCompleted)
        {
            activityEventProducer.produceSurveyCompleted(
                    toSave.getContactId(),
                    survey.getId(),
                    survey.getTitle(),
                    respondentId,
                    payload.getChannel(),
                    toSave.getCompletedAt());
        }

        return result;
    }

    private String mergeMetadata(String clientMetadata, String outreachChannel)
    {
        ObjectNode node;
        try
        {
            if (StringUtils.isNotBlank(clientMetadata))
            {
                JsonNode parsed = objectMapper.readTree(clientMetadata);
                node = parsed.isObject()
                        ? (ObjectNode) parsed.deepCopy()
                        : objectMapper.createObjectNode();
            }
            else
            {
                node = objectMapper.createObjectNode();
            }
        }
        catch (Exception ex)
        {
            node = objectMapper.createObjectNode();
        }
        if (StringUtils.isNotBlank(outreachChannel))
        {
            node.put("outreachChannel", outreachChannel);
        }
        return node.toString();
    }

    private void dispatchWorkflowEvent(CrmOutreachRespondent respondent)
    {
        if (StringUtils.isBlank(respondent.getSurveyJson()))
        {
            return;
        }
        try
        {
            JsonNode rootNode = objectMapper.readTree(respondent.getSurveyJson());
            if (rootNode.isObject())
            {
                Map<String, Object> eventPayload = objectMapper.convertValue(rootNode, Map.class);
                eventDispatcher.dispatch(EventType.SURVEY_SUBMITTED, respondent.getRelateId(), eventPayload);
            }
        }
        catch (Exception ex)
        {
            // workflow dispatch is best-effort
        }
    }
}
