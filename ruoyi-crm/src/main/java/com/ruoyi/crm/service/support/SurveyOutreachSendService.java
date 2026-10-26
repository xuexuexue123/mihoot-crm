package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.domain.dto.SurveyOutreachSendRequest;
import com.ruoyi.crm.domain.vo.SurveyOutreachTaskVo;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmSurveysService;

/**
 * 侧边栏 / Inbox 问卷发送确认（wecom_sidebar、whatsapp_inbox）
 */
@Service
public class SurveyOutreachSendService
{
    @Autowired
    private ICrmSurveysService crmSurveysService;

    @Autowired
    private ICrmContactService crmContactService;

    @Autowired
    private SurveyOutreachLogService surveyOutreachLogService;

    public SurveyOutreachTaskVo confirmSend(String surveyId, SurveyOutreachSendRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getContactId()))
        {
            throw new ServiceException("contactId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(request.getChannel()))
        {
            throw new ServiceException("channel 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!isAllowedSidebarChannel(request.getChannel()))
        {
            throw new ServiceException("channel 必须为 wecom_sidebar 或 whatsapp_inbox", HttpStatus.BAD_REQUEST);
        }

        loadIssuableSurvey(surveyId);
        ensureContactExists(request.getContactId());

        String recipientType = StringUtils.hasText(request.getRecipientType())
                ? request.getRecipientType()
                : SurveyOutreachLogService.RECIPIENT_TYPE_CONTACT;
        String recipientId = StringUtils.hasText(request.getRecipientId())
                ? request.getRecipientId()
                : request.getContactId();

        return surveyOutreachLogService.recordSentUpsert(
                surveyId,
                request.getChannel(),
                recipientType,
                recipientId,
                null);
    }

    private void ensureContactExists(String contactId)
    {
        CrmContact contact = crmContactService.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            throw new ServiceException("联系人不存在", HttpStatus.BAD_REQUEST);
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

    private static boolean isAllowedSidebarChannel(String channel)
    {
        return SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR.equals(channel)
                || SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX.equals(channel);
    }
}
