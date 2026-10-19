package com.ruoyi.crm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.MiniappLeadUpsertRequest;
import com.ruoyi.crm.domain.vo.MiniappLeadByMobileVo;
import com.ruoyi.crm.domain.vo.MiniappLeadUpsertResultVo;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmMiniappLeadService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.LeadDefaults;
import com.ruoyi.crm.service.support.MiniappLeadCustomFieldSupport;
import com.ruoyi.crm.service.support.PhoneNormalizer;

/**
 * 小程序线索幂等查建。
 * <p>
 * 写死规则：
 * 1. 规范化手机号
 * 2. 同号未转化 Lead（open/assigned/initial）→ 更新备注/profile/来源（姓名等仅空时写入）
 * 3. 无 Lead → 新建；leadSource 取 sourceChannel，缺省 wechat_miniapp
 * 4. 更新来源：请求来源非空且现有为空或 wechat_miniapp 时可写入（含升级为 miniapp_appointment）；其它渠道不覆盖
 * 5. 同号 Contact → 关联 contactId，仍更新/创建跟进中 Lead；resolution=linked_contact
 * 6. 重复提交不产生第二条活跃 Lead
 */
@Service
public class CrmMiniappLeadServiceImpl implements ICrmMiniappLeadService
{
    private static final Logger log = LoggerFactory.getLogger(CrmMiniappLeadServiceImpl.class);

    public static final String DEFAULT_SOURCE = "wechat_miniapp";
    public static final String SOURCE_MINIAPP_APPOINTMENT = "miniapp_appointment";
    public static final String DEFAULT_LEAD_TYPE = "business";
    public static final String RESOLUTION_CREATED = "created";
    public static final String RESOLUTION_UPDATED = "updated";
    public static final String RESOLUTION_LINKED_CONTACT = "linked_contact";

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Override
    public MiniappLeadByMobileVo findByMobile(String mobilePhone)
    {
        String normalized = normalizePhoneRequired(mobilePhone);
        CrmLead lead = crmLeadMapper.selectActiveUnconvertedByMobilePhone(normalized);
        if (lead == null)
        {
            return null;
        }
        return toByMobileVo(lead, findContactId(normalized));
    }

    @Override
    public MiniappLeadUpsertResultVo upsert(MiniappLeadUpsertRequest request)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        String normalized = normalizePhoneRequired(request.getMobilePhone());
        String contactId = findContactId(normalized);
        CrmLead existing = crmLeadMapper.selectActiveUnconvertedByMobilePhone(normalized);

        if (existing != null)
        {
            applyUpdate(existing, request, contactId);
            int rows = crmLeadService.updateCrmLead(existing);
            if (rows <= 0)
            {
                throw new ServiceException("线索更新失败", HttpStatus.ERROR);
            }
            String resolution = StringUtils.isNotBlank(contactId)
                    ? RESOLUTION_LINKED_CONTACT : RESOLUTION_UPDATED;
            log.info("miniapp lead upsert resolution={} leadId={} mobile={}",
                    resolution, existing.getLeadId(), maskMobile(normalized));
            activityEventProducer.produceLeadDuplicated(existing.getLeadId(), "mobilePhone", DEFAULT_SOURCE);
            return MiniappLeadUpsertResultVo.of(existing.getLeadId(), contactId, resolution);
        }

        CrmLead created = buildNewLead(request, normalized, contactId);
        int rows = crmLeadService.insertCrmLead(created);
        if (rows <= 0)
        {
            throw new ServiceException("线索创建失败", HttpStatus.ERROR);
        }
        String resolution = StringUtils.isNotBlank(contactId)
                ? RESOLUTION_LINKED_CONTACT : RESOLUTION_CREATED;
        log.info("miniapp lead upsert resolution={} leadId={} mobile={}",
                resolution, created.getLeadId(), maskMobile(normalized));
        return MiniappLeadUpsertResultVo.of(created.getLeadId(), contactId, resolution);
    }

    private void applyUpdate(CrmLead existing, MiniappLeadUpsertRequest request, String contactId)
    {
        if (StringUtils.isBlank(existing.getLeadName()) && StringUtils.isNotBlank(request.getLeadName()))
        {
            existing.setLeadName(request.getLeadName().trim());
        }
        if (StringUtils.isNotBlank(request.getRemark()))
        {
            existing.setDescription(mergeRemark(existing.getDescription(), request.getRemark().trim()));
        }
        applyLeadSourceOnUpdate(existing, request.getSourceChannel());
        if (StringUtils.isBlank(existing.getContactId()) && StringUtils.isNotBlank(contactId))
        {
            existing.setContactId(contactId);
        }
        existing.setCustomField(MiniappLeadCustomFieldSupport.mergeProfile(
                existing.getCustomField(), request.getProfileJson()));
        existing.setUpdateTime(DateUtils.getNowDate());
        existing.setUpdateBy(resolveUsername());
    }

    private CrmLead buildNewLead(MiniappLeadUpsertRequest request, String normalized, String contactId)
    {
        CrmLead lead = new CrmLead();
        lead.setLeadId(UUID.randomUUID().toString());
        lead.setMobilePhone(normalized);
        lead.setLeadName(StringUtils.isNotBlank(request.getLeadName())
                ? request.getLeadName().trim()
                : defaultLeadName(normalized));
        lead.setLeadSource(resolveSource(request.getSourceChannel()));
        lead.setLeadType(DEFAULT_LEAD_TYPE);
        lead.setStage(LeadDefaults.DEFAULT_STAGE);
        lead.setStatus(AudienceLeadVisibility.STATUS_OPEN);
        if (StringUtils.isNotBlank(request.getRemark()))
        {
            lead.setDescription(request.getRemark().trim());
        }
        if (StringUtils.isNotBlank(contactId))
        {
            lead.setContactId(contactId);
        }
        lead.setCustomField(MiniappLeadCustomFieldSupport.mergeProfile(null, request.getProfileJson()));
        lead.setCreateTime(DateUtils.getNowDate());
        lead.setCreateBy(resolveUsername());
        return lead;
    }

    private MiniappLeadByMobileVo toByMobileVo(CrmLead lead, String contactId)
    {
        MiniappLeadByMobileVo vo = new MiniappLeadByMobileVo();
        vo.setLeadId(lead.getLeadId());
        vo.setContactId(StringUtils.isNotBlank(lead.getContactId()) ? lead.getContactId() : contactId);
        vo.setMobilePhone(lead.getMobilePhone());
        vo.setLeadName(lead.getLeadName());
        vo.setStage(lead.getStage());
        vo.setStatus(lead.getStatus());
        vo.setLeadSource(lead.getLeadSource());
        return vo;
    }

    private String findContactId(String normalizedPhone)
    {
        try
        {
            return identityService.findContactByIdentity("mobile_phone", normalizedPhone);
        }
        catch (Exception e)
        {
            log.warn("findContactByIdentity failed for miniapp upsert: {}", e.getMessage());
            return null;
        }
    }

    private static String resolveSource(String sourceChannel)
    {
        return StringUtils.isNotBlank(sourceChannel) ? sourceChannel.trim() : DEFAULT_SOURCE;
    }

    /**
     * 更新路径来源策略：请求来源非空时，仅在现有为空或泛化 wechat_miniapp 时写入（可升级为 miniapp_appointment）；
     * 请求未带来源且现有为空时补默认；Capture / wecom 等其它渠道不覆盖。
     */
    static void applyLeadSourceOnUpdate(CrmLead existing, String requestedSource)
    {
        if (StringUtils.isNotBlank(requestedSource))
        {
            String incoming = requestedSource.trim();
            String current = existing.getLeadSource();
            if (StringUtils.isBlank(current) || DEFAULT_SOURCE.equals(current))
            {
                existing.setLeadSource(incoming);
            }
            return;
        }
        if (StringUtils.isBlank(existing.getLeadSource()))
        {
            existing.setLeadSource(DEFAULT_SOURCE);
        }
    }

    private static String mergeRemark(String existing, String incoming)
    {
        if (StringUtils.isBlank(existing))
        {
            return incoming;
        }
        if (existing.contains(incoming))
        {
            return existing;
        }
        return existing + "\n" + incoming;
    }

    private static String defaultLeadName(String mobile)
    {
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
