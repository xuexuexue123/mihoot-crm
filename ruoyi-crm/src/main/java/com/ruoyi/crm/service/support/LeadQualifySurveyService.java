package com.ruoyi.crm.service.support;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadQualifySurveySubmitRequest;
import com.ruoyi.crm.domain.vo.LeadQualifySurveySubmitResultVo;
import com.ruoyi.crm.domain.vo.LeadQualifySurveyVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.support.OutreachRegistrationTokenService.QualifyVerifiedPayload;
import com.ruoyi.ems.domain.SysReTemplate;
import com.ruoyi.ems.service.ISysReTemplateService;

/**
 * 认定潜客外投公开拉卷 / 提交（调用方须已按 token.tenantId 切换租户数据源）
 */
@Service
public class LeadQualifySurveyService
{
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private ISysReTemplateService templateService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    public QualifyVerifiedPayload verifyToken(String token)
    {
        return outreachRegistrationTokenService.verifyQualify(token);
    }

    public LeadQualifySurveyVo loadSurvey(QualifyVerifiedPayload payload)
    {
        CrmLead lead = requireLead(payload.getLeadId());
        SysReTemplate template = templateService.selectSysReTemplateByTemplateId(payload.getTemplateId());
        if (template == null)
        {
            throw new ServiceException("认定问卷模板不存在", HttpStatus.BAD_REQUEST);
        }

        LeadQualifySurveyVo vo = new LeadQualifySurveyVo();
        vo.setLeadId(lead.getLeadId());
        vo.setDisplayName(maskDisplayName(firstNonBlank(lead.getLeadName(), lead.getFullName())));
        vo.setTemplateId(payload.getTemplateId());
        vo.setTemplateName(template.getTemplateName());
        vo.setSurveyJson(template.getContent());
        vo.setChannel(payload.getChannel());
        vo.setExpiresAt(LeadQualifyOutreachUrlService.formatExpiresAt(payload.getExp()));

        JsonNode answers = parseAnswersObject(lead.getSurveyJson());
        if (answers != null)
        {
            vo.setStatus(STATUS_COMPLETED);
            vo.setAnswers(answers);
        }
        else
        {
            vo.setStatus(STATUS_PENDING);
            vo.setAnswers(null);
        }
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public LeadQualifySurveySubmitResultVo submit(LeadQualifySurveySubmitRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getToken()))
        {
            throw new ServiceException("Missing required parameters", HttpStatus.BAD_REQUEST);
        }
        JsonNode data = request.getData();
        if (data == null || data.isNull() || !data.isObject())
        {
            throw new ServiceException("data 须为对象", HttpStatus.BAD_REQUEST);
        }

        QualifyVerifiedPayload payload = verifyToken(request.getToken());
        CrmLead lead = requireLead(payload.getLeadId());
        if (parseAnswersObject(lead.getSurveyJson()) != null)
        {
            throw new ServiceException("认定问卷已填写", HttpStatus.CONFLICT);
        }

        String surveyJson = LeadQualifySurveyScoreSupport.mergeAnswersWithScores(data);
        CrmLead patch = new CrmLead();
        patch.setLeadId(lead.getLeadId());
        patch.setSurveyJson(surveyJson);
        patch.setUpdateTime(DateUtils.getNowDate());
        int rows = crmLeadMapper.updateCrmLead(patch);
        if (rows <= 0)
        {
            throw new ServiceException("更新失败", HttpStatus.ERROR);
        }

        Date completedAt = DateUtils.getNowDate();
        activityEventProducer.produceAssessmentCompleted(
                lead.getLeadId(),
                firstNonBlank(lead.getLeadName(), lead.getFullName()),
                completedAt,
                OutreachRegistrationTokenService.CHANNEL_QUALIFY_QR,
                payload.getTemplateId());

        LeadQualifySurveySubmitResultVo result = new LeadQualifySurveySubmitResultVo();
        result.setLeadId(lead.getLeadId());
        result.setStatus(STATUS_COMPLETED);
        return result;
    }

    private CrmLead requireLead(String leadId)
    {
        CrmLead lead = crmLeadMapper.selectCrmLeadByLeadId(leadId);
        if (lead == null)
        {
            throw new ServiceException("Lead not found", HttpStatus.NOT_FOUND);
        }
        return lead;
    }

    /**
     * survey_json 非空且可解析为 JSON object 时返回该对象；否则 null（视为未填）。
     */
    static JsonNode parseAnswersObject(String surveyJson)
    {
        if (!StringUtils.hasText(surveyJson))
        {
            return null;
        }
        String trimmed = surveyJson.trim();
        if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed))
        {
            return null;
        }
        try
        {
            JsonNode node = OBJECT_MAPPER.readTree(trimmed);
            if (node != null && node.isObject())
            {
                return node;
            }
            return null;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    static String maskDisplayName(String name)
    {
        if (!StringUtils.hasText(name))
        {
            return "客户";
        }
        String trimmed = name.trim();
        if (trimmed.length() <= 1)
        {
            return trimmed;
        }
        return trimmed.charAt(0) + "*";
    }

    private static String firstNonBlank(String a, String b)
    {
        if (StringUtils.hasText(a))
        {
            return a;
        }
        if (StringUtils.hasText(b))
        {
            return b;
        }
        return null;
    }
}
