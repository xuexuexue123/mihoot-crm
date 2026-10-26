package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.service.support.AudienceIdentityUnionRow;
import com.ruoyi.crm.service.support.AudienceSourceRow;
import com.ruoyi.crm.service.support.ContactNameRow;

/**
 * 活动受众数据源查询
 */
public interface EventAudienceMapper
{
    List<AudienceSourceRow> selectH5Sources(@Param("eventId") String eventId);

    List<AudienceSourceRow> selectWebsiteSources(@Param("eventId") String eventId);

    List<AudienceSourceRow> selectWecomSources(@Param("eventId") String eventId);

    List<AudienceSourceRow> selectMpSources(@Param("eventId") String eventId);

    List<ContactNameRow> selectContactNames(@Param("contactIds") List<String> contactIds);

    List<AudienceIdentityUnionRow> selectUnionidByOpenids(@Param("openids") List<String> openids);

    List<AudienceIdentityUnionRow> selectUnionidByExternalUserids(@Param("externalUserids") List<String> externalUserids);
}
