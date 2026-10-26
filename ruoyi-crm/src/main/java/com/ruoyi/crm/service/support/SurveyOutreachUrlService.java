package com.ruoyi.crm.service.support;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.domain.dto.SurveyOutreachUrlRenderRequest;
import com.ruoyi.crm.domain.vo.SurveyOutreachUrlVo;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmSurveysService;

/**
 * 问卷 H5 投放链接签发（通用 / 个性化）
 */
@Service
public class SurveyOutreachUrlService
{
    private static final DateTimeFormatter ISO_UTC = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneOffset.UTC);

    @Autowired
    private ICrmSurveysService crmSurveysService;

    @Autowired
    private ICrmContactService crmContactService;

    @Autowired
    private OutreachRegistrationTokenService outreachRegistrationTokenService;

    public SurveyOutreachUrlVo issueGenericUrl(String tenantId, String surveyId)
    {
        ensureSigningConfigured(tenantId);
        CrmSurveys survey = loadIssuableSurvey(surveyId);
        long exp = outreachRegistrationTokenService.computeSurveyExp(survey);
        String surveyUrl = outreachRegistrationTokenService.buildSurveyUrl(tenantId, surveyId, exp);
        return toVo(surveyUrl, exp);
    }

    public SurveyOutreachUrlVo issueRenderUrl(String tenantId, String surveyId,
            SurveyOutreachUrlRenderRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        boolean hasOpenId = StringUtils.hasText(request.getOpenId());
        boolean hasAppId = StringUtils.hasText(request.getAppId());
        boolean hasContactId = StringUtils.hasText(request.getContactId());

        if (hasOpenId && hasAppId)
        {
            return issueMpPersonalizedUrl(tenantId, surveyId,
                    request.getOpenId(), request.getAppId(),
                    request.getContactId(), request.getChannel());
        }
        if (hasContactId)
        {
            return issueWecomSidebarUrl(tenantId, surveyId,
                    request.getContactId(), request.getChannel());
        }
        throw new ServiceException("openId 与 appId，或 contactId 至少提供一组", HttpStatus.BAD_REQUEST);
    }

    public SurveyOutreachUrlVo issuePersonalizedUrl(String tenantId, String surveyId,
            String openId, String appId, String contactId)
    {
        return issueMpPersonalizedUrl(tenantId, surveyId, openId, appId, contactId, null);
    }

    public SurveyOutreachUrlVo issueMpPersonalizedUrl(String tenantId, String surveyId,
            String openId, String appId, String contactId, String channel)
    {
        if (!StringUtils.hasText(openId) || !StringUtils.hasText(appId))
        {
            throw new ServiceException("openId 与 appId 不能为空", HttpStatus.BAD_REQUEST);
        }
        ensureSigningConfigured(tenantId);
        CrmSurveys survey = loadIssuableSurvey(surveyId);
        long exp = outreachRegistrationTokenService.computeSurveyExp(survey);
        String surveyUrl = outreachRegistrationTokenService.buildSurveyUrl(
                tenantId, surveyId, exp, openId, appId, contactId, channel);
        return toVo(surveyUrl, exp);
    }

    public SurveyOutreachUrlVo issueWecomSidebarUrl(String tenantId, String surveyId,
            String contactId, String channel)
    {
        if (!StringUtils.hasText(contactId))
        {
            throw new ServiceException("contactId 不能为空", HttpStatus.BAD_REQUEST);
        }
        ensureContactExists(contactId);
        ensureSigningConfigured(tenantId);
        CrmSurveys survey = loadIssuableSurvey(surveyId);
        long exp = outreachRegistrationTokenService.computeSurveyExp(survey);
        String surveyUrl = outreachRegistrationTokenService.buildSurveyUrl(
                tenantId, surveyId, exp, null, null, contactId, channel);
        return toVo(surveyUrl, exp);
    }

    public SurveyOutreachUrlVo issueUrlForPreset(String tenantId, String surveyId,
            String openId, String appId, String contactId)
    {
        if (StringUtils.hasText(openId) && StringUtils.hasText(appId))
        {
            return issuePersonalizedUrl(tenantId, surveyId, openId, appId, contactId);
        }
        return issueGenericUrl(tenantId, surveyId);
    }

    private void ensureContactExists(String contactId)
    {
        CrmContact contact = crmContactService.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            throw new ServiceException("联系人不存在", HttpStatus.BAD_REQUEST);
        }
    }

    private void ensureSigningConfigured(String tenantId)
    {
        if (!outreachRegistrationTokenService.isSigningConfigured())
        {
            throw new ServiceException("signing secret not configured", HttpStatus.ERROR);
        }
        if (!outreachRegistrationTokenService.hasPublicBaseUrl(tenantId))
        {
            throw new ServiceException("public app url not configured", HttpStatus.ERROR);
        }
    }

    private CrmSurveys loadIssuableSurvey(String surveyId)
    {
        CrmSurveys survey = crmSurveysService.selectCrmSurveysById(surveyId);
        if (survey == null)
        {
            throw new ServiceException("Survey not found", HttpStatus.NOT_FOUND);
        }
        if (!SurveyOutreachSupport.isIssuable(survey))
        {
            throw new ServiceException("Wrong request", HttpStatus.FORBIDDEN);
        }
        return survey;
    }

    private static SurveyOutreachUrlVo toVo(String surveyUrl, long exp)
    {
        SurveyOutreachUrlVo vo = new SurveyOutreachUrlVo();
        vo.setSurveyUrl(surveyUrl);
        vo.setExpiresAt(ISO_UTC.format(Instant.ofEpochSecond(exp)));
        return vo;
    }
}
