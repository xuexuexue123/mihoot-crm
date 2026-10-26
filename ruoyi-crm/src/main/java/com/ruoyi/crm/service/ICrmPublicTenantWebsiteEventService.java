package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.PublicWebsiteEventRegisterRequest;
import com.ruoyi.crm.domain.vo.OutreachRegistrationSubmitResultVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventDetailVo;
import com.ruoyi.crm.domain.vo.PublicWebsiteEventListVo;

/**
 * 租户官网公开活动 API
 */
public interface ICrmPublicTenantWebsiteEventService
{
    PublicWebsiteEventListVo listEvents(String slug, Integer limit);

    PublicWebsiteEventDetailVo getEvent(String slug, String eventId);

    OutreachRegistrationSubmitResultVo register(String slug, String eventId,
            PublicWebsiteEventRegisterRequest request);
}
