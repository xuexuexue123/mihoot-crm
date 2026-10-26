package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.MiniappMarketEventRegisterRequest;
import com.ruoyi.crm.domain.vo.MiniappMarketEventRegisterResultVo;
import com.ruoyi.crm.domain.vo.SubjectRegistrationRowVo;

/**
 * 小程序七库市场活动报名（Lead/Contact + registration + Activity）
 */
public interface ICrmMiniappMarketEventRegisterService
{
    String SESSION_ID_MARKET_EVENT = "__market_event__";
    String CHANNEL_WECHAT_MINIAPP = "wechat_miniapp";
    String RESOLUTION_LEAD_CREATED = "lead_created";
    String RESOLUTION_LEAD_UPDATED = "lead_updated";
    String RESOLUTION_CONTACT_KNOWN = "contact_known";
    String RESOLUTION_REGISTRATION_UPDATED = "registration_updated";

    MiniappMarketEventRegisterResultVo register(String tenantId, MiniappMarketEventRegisterRequest request);

    List<SubjectRegistrationRowVo> listByLeadId(String tenantId, String leadId, String registrationSource);

    List<SubjectRegistrationRowVo> listByContactId(String tenantId, String contactId, String registrationSource);
}
