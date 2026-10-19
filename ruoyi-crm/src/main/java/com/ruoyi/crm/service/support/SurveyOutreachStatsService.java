package com.ruoyi.crm.service.support;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.crm.domain.vo.SurveyOutreachStatsVo;
import com.ruoyi.crm.domain.vo.SurveyOutreachTaskVo;
import com.ruoyi.crm.mapper.CrmRespondentsMapper;
import com.ruoyi.crm.mapper.CrmSurveyOutreachLogMapper;

@Service
public class SurveyOutreachStatsService
{
    @Autowired
    private CrmSurveyOutreachLogMapper outreachLogMapper;

    @Autowired
    private CrmRespondentsMapper respondentsMapper;

    public List<SurveyOutreachTaskVo> listTasks(String surveyId, String recipientId, String channel)
    {
        return outreachLogMapper.selectTasksBySurveyId(surveyId, recipientId, channel);
    }

    public SurveyOutreachStatsVo getStats(String surveyId)
    {
        long wecomMass = outreachLogMapper.countSentBySurveyAndChannel(
                surveyId, SurveyOutreachLogService.CHANNEL_WECOM_MASS);
        long wecomSidebar = outreachLogMapper.countSentBySurveyAndChannel(
                surveyId, SurveyOutreachLogService.CHANNEL_WECOM_SIDEBAR);
        long whatsappInbox = outreachLogMapper.countSentBySurveyAndChannel(
                surveyId, SurveyOutreachLogService.CHANNEL_WHATSAPP_INBOX);
        long mpTemplate = outreachLogMapper.countSentBySurveyAndChannel(
                surveyId, SurveyOutreachLogService.CHANNEL_MP_TEMPLATE);
        long mpBatch = outreachLogMapper.countSentBySurveyAndChannel(
                surveyId, SurveyOutreachLogService.CHANNEL_MP_BATCH);
        long mpSent = mpTemplate + mpBatch;
        long wecomSent = wecomMass + wecomSidebar;
        long responses = respondentsMapper.countByRelateId(surveyId);
        long totalSent = wecomSent + mpSent + whatsappInbox;

        SurveyOutreachStatsVo vo = new SurveyOutreachStatsVo();
        vo.setWecomSent(wecomSent);
        vo.setMpSent(mpSent);
        vo.setResponses(responses);
        vo.setCompletionRate(totalSent > 0 ? (double) responses / totalSent : 0.0);
        return vo;
    }
}
