package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.EventSelfCheckInRequest;
import com.ruoyi.crm.domain.vo.EventSelfCheckInResultVo;
import com.ruoyi.crm.domain.vo.OutreachEventCheckInVo;
import com.ruoyi.crm.service.support.OutreachCheckInTokenService.VerifiedCheckInPayload;
import com.ruoyi.crm.wechatmp.oauth.OutreachMpSessionPayload;

public interface IEventOnsiteSelfCheckInService
{
    VerifiedCheckInPayload verifyAndResolveSession(String checkInToken, String tenantIdForDefaultSession);

    OutreachEventCheckInVo getCheckInPageInfo(String checkInToken, VerifiedCheckInPayload payload, String userAgent,
            OutreachMpSessionPayload mpSession);

    EventSelfCheckInResultVo submitSelfCheckIn(VerifiedCheckInPayload payload, EventSelfCheckInRequest request,
            OutreachMpSessionPayload mpSession);
}
