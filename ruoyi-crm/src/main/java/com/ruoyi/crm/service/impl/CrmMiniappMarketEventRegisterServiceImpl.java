package com.ruoyi.crm.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmMarketEvent;
import com.ruoyi.crm.domain.dto.MiniappMarketEventRegisterRequest;
import com.ruoyi.crm.domain.vo.MiniappMarketEventRegisterResultVo;
import com.ruoyi.crm.domain.vo.SubjectRegistrationRowVo;
import com.ruoyi.crm.enums.EventRegistrationSource;
import com.ruoyi.crm.enums.EventRegistrationStatus;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmMarketEventService;
import com.ruoyi.crm.service.ICrmMiniappMarketEventRegisterService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.DataTableTenantSupport;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.service.support.MiniappLeadCustomFieldSupport;
import com.ruoyi.crm.service.support.PhoneNormalizer;
import com.ruoyi.crm.service.support.RegistrationFormSnapshotSupport;

/**
 * 小程序七库市场活动报名：原子双写 Lead/Contact + registration + Activity。
 * <p>
 * 不走营销 {@code registerInternal}（无场次/容量/票券）。
 */
@Service
public class CrmMiniappMarketEventRegisterServiceImpl implements ICrmMiniappMarketEventRegisterService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMiniappMarketEventRegisterServiceImpl.class);

    public static final String DEFAULT_SOURCE = "wechat_miniapp";
    public static final String DEFAULT_LEAD_TYPE = "business";

    @Autowired
    private ICrmMarketEventService marketEventService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MiniappMarketEventRegisterResultVo register(String tenantId, MiniappMarketEventRegisterRequest request)
    {
        String tenant = DataTableTenantSupport.requireTenantId(tenantId);
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getMarketEventId()))
        {
            throw new ServiceException("marketEventId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (request.getSlots() == null || request.getSlots().isEmpty())
        {
            throw new ServiceException("slots 不能为空", HttpStatus.BAD_REQUEST);
        }

        String marketEventId = request.getMarketEventId().trim();
        CrmMarketEvent marketEvent = marketEventService.selectCrmMarketEventById(marketEventId);
        if (marketEvent == null)
        {
            throw new ServiceException("市场活动不存在", HttpStatus.NOT_FOUND);
        }

        String normalized = normalizePhoneRequired(request.getMobilePhone());
        String audienceKey = "mobile:" + normalized;
        String displayName = resolveDisplayName(request, normalized);
        String formDataJson = JSON.toJSONString(request.getSlots());
        String operator = resolveUsername();

        String contactId = findContactId(normalized);
        String leadId = null;
        String identityResolution;

        if (StringUtils.isNotBlank(contactId))
        {
            identityResolution = RESOLUTION_CONTACT_KNOWN;
        }
        else
        {
            CrmLead existing = crmLeadMapper.selectActiveUnconvertedByMobilePhone(normalized);
            if (existing != null)
            {
                applyLeadUpdate(existing, request, displayName, normalized);
                int rows = crmLeadService.updateCrmLead(existing);
                if (rows <= 0)
                {
                    throw new ServiceException("线索更新失败", HttpStatus.ERROR);
                }
                leadId = existing.getLeadId();
                identityResolution = RESOLUTION_LEAD_UPDATED;
                activityEventProducer.produceLeadDuplicated(leadId, "mobilePhone", "wechat_miniapp");
            }
            else
            {
                CrmLead created = buildNewLead(request, displayName, normalized);
                int rows = crmLeadService.insertCrmLead(created);
                if (rows <= 0)
                {
                    throw new ServiceException("线索创建失败", HttpStatus.ERROR);
                }
                leadId = created.getLeadId();
                identityResolution = RESOLUTION_LEAD_CREATED;
            }
        }

        CrmEventRegistration existing = registrationMapper.selectActiveBySlot(
                tenant, marketEventId, SESSION_ID_MARKET_EVENT, audienceKey);
        if (existing != null)
        {
            patchExistingRegistration(tenant, existing, displayName, normalized, formDataJson, operator);
            log.info("market-event register resolution={} registrationId={} marketEventId={} mobile={}",
                    RESOLUTION_REGISTRATION_UPDATED, existing.getRegistrationId(), marketEventId,
                    maskMobile(normalized));
            return MiniappMarketEventRegisterResultVo.of(leadId, contactId, existing.getRegistrationId(),
                    RESOLUTION_REGISTRATION_UPDATED, audienceKey, marketEventId,
                    EventRegistrationSource.MARKET_EVENT.getCode());
        }

        String registrationId = UUID.randomUUID().toString();
        CrmEventRegistration registration = new CrmEventRegistration();
        registration.setRegistrationId(registrationId);
        registration.setEventId(marketEventId);
        registration.setSessionId(SESSION_ID_MARKET_EVENT);
        registration.setTenantId(tenant);
        registration.setLeadId(leadId);
        registration.setContactId(contactId);
        registration.setAudienceKey(audienceKey);
        registration.setDisplayName(displayName);
        registration.setMobilePhone(normalized);
        registration.setRegistrationStatus(EventRegistrationStatus.CONFIRMED.getCode());
        registration.setRegistrationSource(EventRegistrationSource.MARKET_EVENT.getCode());
        registration.setFormData(formDataJson);
        registration.setRegisteredAt(DateUtils.getNowDate());
        registration.setCreateBy(operator);
        registration.setCreateTime(DateUtils.getNowDate());
        registrationMapper.insert(registration);

        writeActivity(marketEvent, registration);

        log.info("market-event register resolution={} registrationId={} marketEventId={} mobile={}",
                identityResolution, registrationId, marketEventId, maskMobile(normalized));
        return MiniappMarketEventRegisterResultVo.of(leadId, contactId, registrationId,
                identityResolution, audienceKey, marketEventId,
                EventRegistrationSource.MARKET_EVENT.getCode());
    }

    @Override
    public List<SubjectRegistrationRowVo> listByLeadId(String tenantId, String leadId, String registrationSource)
    {
        String tenant = DataTableTenantSupport.requireTenantId(tenantId);
        if (StringUtils.isBlank(leadId))
        {
            throw new ServiceException("leadId 不能为空", HttpStatus.BAD_REQUEST);
        }
        List<SubjectRegistrationRowVo> rows = registrationMapper.selectByLeadId(
                tenant, leadId.trim(), blankToNull(registrationSource));
        populateFormData(rows);
        return rows;
    }

    @Override
    public List<SubjectRegistrationRowVo> listByContactId(String tenantId, String contactId,
            String registrationSource)
    {
        String tenant = DataTableTenantSupport.requireTenantId(tenantId);
        if (StringUtils.isBlank(contactId))
        {
            throw new ServiceException("contactId 不能为空", HttpStatus.BAD_REQUEST);
        }
        List<SubjectRegistrationRowVo> rows = registrationMapper.selectByContactId(
                tenant, contactId.trim(), blankToNull(registrationSource));
        populateFormData(rows);
        return rows;
    }

    private void patchExistingRegistration(String tenant, CrmEventRegistration existing, String displayName,
            String normalized, String formDataJson, String operator)
    {
        CrmEventRegistration patch = new CrmEventRegistration();
        patch.setTenantId(tenant);
        patch.setRegistrationId(existing.getRegistrationId());
        patch.setDisplayName(displayName);
        patch.setMobilePhone(normalized);
        patch.setFormData(formDataJson);
        patch.setUpdateBy(operator);
        patch.setUpdateTime(DateUtils.getNowDate());
        registrationMapper.update(patch);
        existing.setDisplayName(displayName);
        existing.setMobilePhone(normalized);
        existing.setFormData(formDataJson);
    }

    private void writeActivity(CrmMarketEvent marketEvent, CrmEventRegistration registration)
    {
        String contactId = registration.getContactId();
        String leadId = registration.getLeadId();
        String subjectType;
        String subjectId;
        if (StringUtils.isNotBlank(contactId))
        {
            subjectType = "Contact";
            subjectId = contactId;
        }
        else if (StringUtils.isNotBlank(leadId))
        {
            subjectType = "Lead";
            subjectId = leadId;
        }
        else
        {
            return;
        }
        String eventName = marketEvent.getTitleCn();
        activityEventProducer.produceEventRegistrationSubmitted(
                subjectType, subjectId, CHANNEL_WECHAT_MINIAPP,
                registration.getEventId(), eventName,
                registration.getRegistrationId(), registration.getSessionId(),
                registration.getRegistrationSource(), registration.getRegistrationStatus(),
                registration.getRegisteredAt(), null, null);
    }

    private void applyLeadUpdate(CrmLead existing, MiniappMarketEventRegisterRequest request,
            String displayName, String normalized)
    {
        if (StringUtils.isBlank(existing.getLeadName()) && StringUtils.isNotBlank(displayName))
        {
            existing.setLeadName(displayName);
        }
        if (StringUtils.isBlank(existing.getMobilePhone()))
        {
            existing.setMobilePhone(normalized);
        }
        mergeBlank(existing, request);
        if (StringUtils.isBlank(existing.getLeadSource()))
        {
            existing.setLeadSource(DEFAULT_SOURCE);
        }
        existing.setCustomField(mergeCustomFields(existing.getCustomField(), request));
        existing.setUpdateTime(DateUtils.getNowDate());
        existing.setUpdateBy(resolveUsername());
    }

    private CrmLead buildNewLead(MiniappMarketEventRegisterRequest request, String displayName,
            String normalized)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(UUID.randomUUID().toString());
        lead.setMobilePhone(normalized);
        lead.setLeadName(displayName);
        lead.setLeadSource(DEFAULT_SOURCE);
        lead.setLeadType(DEFAULT_LEAD_TYPE);
        lead.setStage(LeadDefaults.DEFAULT_STAGE);
        lead.setStatus(AudienceLeadVisibility.STATUS_OPEN);
        mergeBlank(lead, request);
        lead.setCustomField(mergeCustomFields(null, request));
        lead.setCreateTime(DateUtils.getNowDate());
        lead.setCreateBy(resolveUsername());
        return lead;
    }

    private static void mergeBlank(CrmLead lead, MiniappMarketEventRegisterRequest request)
    {
        if (StringUtils.isBlank(lead.getWorkEmail()) && StringUtils.isNotBlank(request.getEmail()))
        {
            lead.setWorkEmail(request.getEmail().trim());
        }
        if (StringUtils.isBlank(lead.getFullName()) && StringUtils.isNotBlank(request.getCompanyName()))
        {
            lead.setFullName(request.getCompanyName().trim());
        }
        if (StringUtils.isBlank(lead.getWebsite()) && StringUtils.isNotBlank(request.getWebsite()))
        {
            lead.setWebsite(request.getWebsite().trim());
        }
        if (StringUtils.isBlank(lead.getIndustry()) && StringUtils.isNotBlank(request.getBusinessField()))
        {
            lead.setIndustry(request.getBusinessField().trim());
        }
    }

    private static String mergeCustomFields(String existing, MiniappMarketEventRegisterRequest request)
    {
        Map<String, Object> profile = new HashMap<>();
        putIfPresent(profile, "gender", request.getGender());
        putIfPresent(profile, "address", request.getAddress());
        putIfPresent(profile, "appointment_date", request.getAppointmentDate());
        putIfPresent(profile, "registration_type", request.getRegistrationType());
        putIfPresent(profile, "type_label", request.getTypeLabel());
        putIfPresent(profile, "idempotency_key", request.getIdempotencyKey());
        putIfPresent(profile, "event_title", request.getEventTitle());
        return MiniappLeadCustomFieldSupport.mergeProfile(existing, profile.isEmpty() ? null : profile);
    }

    private static void putIfPresent(Map<String, Object> map, String key, String value)
    {
        if (StringUtils.isNotBlank(value))
        {
            map.put(key, value.trim());
        }
    }

    private static void populateFormData(List<SubjectRegistrationRowVo> rows)
    {
        if (rows == null)
        {
            return;
        }
        for (SubjectRegistrationRowVo row : rows)
        {
            Map<String, Object> formData = RegistrationFormSnapshotSupport.parseFormDataJson(row.getFormDataJson());
            if (formData != null)
            {
                row.setFormData(formData);
                if (StringUtils.isBlank(row.getWorkEmail()) && formData.get("email") != null)
                {
                    row.setWorkEmail(String.valueOf(formData.get("email")));
                }
            }
            if (EventRegistrationSource.MARKET_EVENT.getCode().equals(row.getRegistrationSource())
                    && SESSION_ID_MARKET_EVENT.equals(row.getSessionId()))
            {
                row.setSessionName(null);
            }
        }
    }

    private String findContactId(String normalizedPhone)
    {
        try
        {
            return identityService.findContactByIdentity("mobile_phone", normalizedPhone);
        }
        catch (Exception e)
        {
            log.warn("findContactByIdentity failed for market-event register: {}", e.getMessage());
            return null;
        }
    }

    private static String resolveDisplayName(MiniappMarketEventRegisterRequest request, String mobile)
    {
        if (StringUtils.isNotBlank(request.getContactName()))
        {
            return request.getContactName().trim();
        }
        if (StringUtils.isNotBlank(request.getLeadName()))
        {
            return request.getLeadName().trim();
        }
        if (mobile == null || mobile.length() < 4)
        {
            return "小程序用户";
        }
        return "用户" + mobile.substring(mobile.length() - 4);
    }

    private static String normalizePhoneRequired(String mobilePhone)
    {
        if (StringUtils.isBlank(mobilePhone))
        {
            throw new ServiceException("mobilePhone 不能为空", HttpStatus.BAD_REQUEST);
        }
        String normalized = PhoneNormalizer.normalize(mobilePhone);
        if (StringUtils.isBlank(normalized))
        {
            normalized = mobilePhone.trim();
        }
        return normalized;
    }

    private static String blankToNull(String value)
    {
        return StringUtils.isBlank(value) ? null : value.trim();
    }

    private static String maskMobile(String mobile)
    {
        if (mobile == null || mobile.length() < 7)
        {
            return "***";
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    private static String resolveUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "miniapp_bff";
        }
    }
}
