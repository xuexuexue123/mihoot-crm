package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmSurveyOutreachLog;
import com.ruoyi.crm.domain.vo.SurveyOutreachTaskVo;

public interface CrmSurveyOutreachLogMapper
{
    int insert(CrmSurveyOutreachLog row);

    int upsert(CrmSurveyOutreachLog row);

    CrmSurveyOutreachLog selectBySurveyRecipientChannel(@Param("surveyId") String surveyId,
            @Param("channel") String channel, @Param("recipientId") String recipientId);

    List<SurveyOutreachTaskVo> selectTasksBySurveyId(@Param("surveyId") String surveyId,
            @Param("recipientId") String recipientId, @Param("channel") String channel);

    long countSentBySurveyAndChannel(@Param("surveyId") String surveyId, @Param("channel") String channel);
}
