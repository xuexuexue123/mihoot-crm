package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.service.support.MpOutreachSentAtRow;
import com.ruoyi.crm.wechatmp.domain.CrmEventMpOutreachLog;

public interface CrmEventMpOutreachLogMapper
{
    CrmEventMpOutreachLog selectByEventOpenIdTrigger(String eventId, String openId, String triggerType);

    int insertIgnore(CrmEventMpOutreachLog row);

    int updateByEventOpenIdTrigger(CrmEventMpOutreachLog row);

    List<CrmEventMpOutreachLog> selectList(CrmEventMpOutreachLog query);

    List<MpOutreachSentAtRow> selectLatestSentAtByEventAndOpenIds(
            @Param("eventId") String eventId,
            @Param("openIds") List<String> openIds);
}
