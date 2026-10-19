package com.ruoyi.crm.service.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmSurveyOutreachLog;
import com.ruoyi.crm.domain.vo.SurveyOutreachTaskVo;
import com.ruoyi.crm.mapper.CrmSurveyOutreachLogMapper;

@Service
public class SurveyOutreachLogService
{
    public static final String CHANNEL_WECOM_MASS = "wecom_mass";
    public static final String CHANNEL_WECOM_SIDEBAR = "wecom_sidebar";
    public static final String CHANNEL_WHATSAPP_INBOX = "whatsapp_inbox";
    public static final String CHANNEL_MP_TEMPLATE = "wechat_mp_template";
    public static final String CHANNEL_MP_BATCH = "wechat_mp_batch";

    public static final String RECIPIENT_TYPE_CONTACT = "contact";

    @Autowired
    private CrmSurveyOutreachLogMapper outreachLogMapper;

    public void recordSent(String surveyId, String channel, String recipientType,
            String recipientId, String taskId)
    {
        recordSentUpsert(surveyId, channel, recipientType, recipientId, taskId);
    }

    public SurveyOutreachTaskVo recordSentUpsert(String surveyId, String channel, String recipientType,
            String recipientId, String taskId)
    {
        if (!StringUtils.hasText(surveyId) || !StringUtils.hasText(recipientId))
        {
            throw new IllegalArgumentException("surveyId and recipientId are required");
        }
        if (!StringUtils.hasText(channel))
        {
            throw new IllegalArgumentException("channel is required");
        }
        if (!StringUtils.hasText(recipientType))
        {
            recipientType = RECIPIENT_TYPE_CONTACT;
        }
        CrmSurveyOutreachLog row = new CrmSurveyOutreachLog();
        row.setSurveyId(surveyId);
        row.setChannel(channel);
        row.setRecipientType(recipientType);
        row.setRecipientId(recipientId);
        row.setTaskId(taskId);
        row.setStatus("sent");
        row.setSentAt(DateUtils.getNowDate());
        outreachLogMapper.upsert(row);
        CrmSurveyOutreachLog persisted = outreachLogMapper.selectBySurveyRecipientChannel(
                surveyId, channel, recipientId);
        return toTaskVo(persisted != null ? persisted : row);
    }

    private static SurveyOutreachTaskVo toTaskVo(CrmSurveyOutreachLog row)
    {
        SurveyOutreachTaskVo vo = new SurveyOutreachTaskVo();
        vo.setId(row.getId());
        vo.setSurveyId(row.getSurveyId());
        vo.setChannel(row.getChannel());
        vo.setRecipientType(row.getRecipientType());
        vo.setRecipientId(row.getRecipientId());
        vo.setStatus(row.getStatus());
        vo.setSentAt(row.getSentAt());
        return vo;
    }
}
