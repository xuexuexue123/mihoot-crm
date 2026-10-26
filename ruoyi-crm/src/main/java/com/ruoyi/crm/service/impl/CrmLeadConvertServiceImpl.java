package com.ruoyi.crm.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmConversionHistory;
import com.ruoyi.crm.domain.CrmEvent;
import com.ruoyi.crm.domain.CrmEventRegistration;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.dto.LeadConvertCopyOptions;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateRequest;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.LeadConvertLeadSummary;
import com.ruoyi.crm.domain.dto.LeadConvertRequest;
import com.ruoyi.crm.domain.dto.LeadConvertResult;
import com.ruoyi.crm.domain.dto.LeadDedupeMatch;
import com.ruoyi.crm.domain.vo.EventAudienceRowVo;
import com.ruoyi.crm.domain.vo.LeadConvertConfigVo;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmActivityMapper;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.service.webhook.OutboundWebhookHookSupport;
import com.ruoyi.crm.mapper.CrmConversionHistoryMapper;
import com.ruoyi.crm.mapper.CrmEventMapper;
import com.ruoyi.crm.mapper.CrmEventRegistrationMapper;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWecomExternalContactMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadConvertConfigService;
import com.ruoyi.crm.service.ICrmLeadConvertService;
import com.ruoyi.crm.service.ICrmOptyService;
import com.ruoyi.crm.service.IEventAudienceService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.crm.service.support.LeadArrivedStageDetector;
import com.ruoyi.crm.service.support.LeadToContactFieldMapper;
import com.ruoyi.crm.service.impl.LeadDedupeService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatVisitorMapper;
import com.ruoyi.crm.wecom.WecomLeadConvertSyncSupport;
import com.ruoyi.crm.wechatmp.service.MpLeadConvertSyncSupport;
import com.ruoyi.crm.rule.SmartPushEventExecutor;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;

@Service
public class CrmLeadConvertServiceImpl implements ICrmLeadConvertService
{
    private static final Logger log = LoggerFactory.getLogger(CrmLeadConvertServiceImpl.class);

    private static final String STATUS_CONVERTED = "Converted";
    private static final String STAGE_CONVERTING = "转化中";
    private static final String CONVERSION_TYPE_CONTACT = "Contact";
    private static final String SOURCE_LEAD_CONVERT = "lead_convert";

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private CrmActivityMapper crmActivityMapper;

    @Autowired
    private CrmWecomExternalContactMapper wecomExternalContactMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private OutboundWebhookHookSupport outboundWebhookHookSupport;

    @Autowired
    private CrmConversionHistoryMapper conversionHistoryMapper;

    @Autowired
    private ICrmOptyService crmOptyService;

    @Autowired
    private CrmEventMapper crmEventMapper;

    @Autowired
    private CrmEventRegistrationMapper registrationMapper;

    @Autowired
    private ICrmLeadConvertConfigService configService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private LeadToContactFieldMapper fieldMapper;

    @Autowired
    private LeadDedupeService leadDedupeService;

    @Autowired
    private com.ruoyi.crm.service.support.LeadConvertOwnerResolver convertOwnerResolver;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WecomLeadConvertSyncSupport wecomLeadConvertSyncSupport;

    @Autowired
    private MpLeadConvertSyncSupport mpLeadConvertSyncSupport;

    @Autowired
    private IEventAudienceService eventAudienceService;

    @Autowired
    private LeadArrivedStageDetector arrivedStageDetector;

    @Autowired
    private SmartPushEventExecutor smartPushEventExecutor;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    @Autowired
    private CrmWebchatVisitorMapper webchatVisitorMapper;

    @Autowired
    private CrmWebchatConversationMapper webchatConversationMapper;

    @Autowired
    private com.ruoyi.crm.service.IContactServiceSummaryService contactServiceSummaryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LeadConvertResult convert(String leadId, LeadConvertRequest request, String operator)
    {
        if (!StringUtils.hasText(leadId))
        {
            throw new ServiceException("leadId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (request == null)
        {
            request = new LeadConvertRequest();
        }

        CrmLead lead = crmLeadMapper.selectByLeadIdForUpdate(leadId.trim());
        if (lead == null)
        {
            throw new ServiceException("Lead 不存在", HttpStatus.NOT_FOUND);
        }

        LeadConvertResult skipped = buildSkippedIfConverted(lead);
        if (skipped != null)
        {
            if (StringUtils.hasText(skipped.getContactId()))
            {
                scheduleSummaryBootstrapAfterCommit(skipped.getContactId(), false);
            }
            return skipped;
        }

        LeadConvertConfigVo config = configService.getConfig(currentTenantId());
        LeadConvertCopyOptions copyOptions = request.effectiveCopyOptions();
        String conversionType = request.effectiveConversionType();

        String name = firstNonBlank(request.getName(), lead.getContactName(), lead.getLeadName());
        String mobilePhone = firstNonBlank(request.getMobilePhone(), lead.getMobilePhone());
        String orgId = resolveOrgId(request.getOrgId(), lead, request.getEventId(), config);

        validateConvertRequest(conversionType, orgId, name, mobilePhone, request.getOpportunityName(),
                request.getTrigger());

        ResolveContactResult resolved = resolveTargetContact(lead, request, config, mobilePhone, copyOptions, operator);
        boolean merged = resolved.merged;
        String contactId = resolved.contactId;
        String contactName = resolved.contactName;

        bindIdentitiesIfNeeded(copyOptions, contactId, mobilePhone, lead);

        writeBackLead(lead, contactId, contactName, operator);
        insertConversionHistory(lead, contactId, contactName, operator);

        String trigger = StringUtils.hasText(request.getTrigger()) ? request.getTrigger().trim() : "manual";
        activityEventProducer.produceLeadConverted(leadId, contactId, lead.getLeadSource(), trigger, merged,
                lead.getSid(), resolveWecomStateForLead(leadId));

        // Step 11.5: migrate website.page.view / event.registration.submitted /
        // engagement.assessment.completed / lead.created from Lead to Contact
        crmActivityMapper.migrateWebsitePageViewSubject(leadId, contactId);
        visitorSessionService.migrateSubjectLeadToContact(leadId, contactId);
        webchatVisitorMapper.migrateLeadToContact(leadId, contactId);
        webchatConversationMapper.migrateLeadToContact(leadId, contactId);

        String optyId = null;
        if ("withOpportunity".equalsIgnoreCase(conversionType))
        {
            optyId = insertOpportunity(lead, contactId, orgId, request.getOpportunityName(), operator);
        }

        scheduleWecomSyncAfterCommit(leadId, contactId);
        scheduleMpSyncAfterCommit(leadId, contactId);
        scheduleSummaryBootstrapAfterCommit(contactId, true);

        LeadConvertResult result = new LeadConvertResult();
        result.setLeadId(leadId);
        result.setContactId(contactId);
        result.setMerged(merged);
        result.setSkipped(false);
        result.setOpportunityId(optyId);
        smartPushEventExecutor.onEvent("lead_converted", contactId, DynamicDataSourceContextHolder.getDataSourceKey());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LeadConvertResult convertFromWecomCallback(String leadId, String eventId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return null;
        }
        CrmLead lead = crmLeadMapper.selectCrmLeadByLeadId(leadId.trim());
        if (lead == null)
        {
            log.warn("WeCom callback convert skipped: lead not found leadId={}", leadId);
            return null;
        }
        LeadConvertConfigVo config = configService.getConfig(currentTenantId());
        String orgId = resolveOrgId(null, lead, eventId, config);
        if (!StringUtils.hasText(orgId))
        {
            log.warn("WeCom callback convert skipped: missing orgId leadId={} eventId={}", leadId, eventId);
            return null;
        }
        LeadConvertRequest request = new LeadConvertRequest();
        request.setTrigger("wecom_callback");
        request.setEventId(eventId);
        request.setOrgId(orgId);
        return convert(leadId.trim(), request, "wecom-callback");
    }

    @Override
    public LeadConvertEvaluateResult evaluate(String leadId, LeadConvertEvaluateRequest request)
    {
        if (!StringUtils.hasText(leadId))
        {
            throw new ServiceException("leadId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (request == null || !StringUtils.hasText(request.getTrigger()))
        {
            throw new ServiceException("trigger 不能为空", HttpStatus.BAD_REQUEST);
        }

        CrmLead lead = crmLeadMapper.selectCrmLeadByLeadId(leadId.trim());
        if (lead == null)
        {
            throw new ServiceException("Lead 不存在", HttpStatus.NOT_FOUND);
        }

        LeadConvertConfigVo config = configService.getConfig(currentTenantId());
        LeadConvertEvaluateResult result = new LeadConvertEvaluateResult();
        result.setConfig(config);
        result.setLead(toLeadSummary(lead));

        if (isAlreadyConverted(lead))
        {
            result.setEligible(false);
            result.setReason("already_converted");
            return result;
        }

        String trigger = request.getTrigger().trim();
        if ("stage_arrived".equals(trigger) && !Boolean.TRUE.equals(config.getAutoConvertOnFirstVisit()))
        {
            result.setEligible(false);
            result.setReason("disabled");
            return result;
        }
        if ("event_check_in".equals(trigger) && !Boolean.TRUE.equals(config.getAutoConvertOnCheckIn()))
        {
            result.setEligible(false);
            result.setReason("disabled");
            return result;
        }

        if (!hasLeadName(lead))
        {
            result.setEligible(false);
            result.setReason("missing_name");
            return result;
        }
        if (!StringUtils.hasText(lead.getMobilePhone()))
        {
            result.setEligible(false);
            result.setReason("missing_mobile");
            return result;
        }

        String orgId = resolveOrgId(null, lead, request.getEventId(), config);
        if (!StringUtils.hasText(orgId))
        {
            result.setEligible(false);
            result.setReason("missing_org");
            return result;
        }

        result.setEligible(true);
        result.setOrgId(orgId);
        return result;
    }

    /**
     * stage 变更后评估（供 Controller enrich）
     */
    @Override
    public LeadConvertEvaluateResult evaluateStageArrived(CrmLead before, CrmLead after)
    {
        if (before == null || after == null || !StringUtils.hasText(after.getLeadId()))
        {
            return null;
        }
        String oldStage = before.getStage();
        String newStage = after.getStage();
        if (!arrivedStageDetector.stageChangedToArrived(oldStage, newStage))
        {
            return null;
        }
        LeadConvertEvaluateRequest req = new LeadConvertEvaluateRequest();
        req.setTrigger("stage_arrived");
        return evaluate(after.getLeadId(), req);
    }

    /**
     * 签到成功后评估（供 EventCheckInService enrich）
     */
    @Override
    public LeadConvertEvaluateResult evaluateEventCheckIn(String eventId, String registrationId,
            String audienceKey)
    {
        String leadId = resolveLeadIdForCheckIn(eventId, registrationId, audienceKey);
        if (!StringUtils.hasText(leadId))
        {
            return null;
        }
        LeadConvertEvaluateRequest req = new LeadConvertEvaluateRequest();
        req.setTrigger("event_check_in");
        req.setEventId(eventId);
        req.setRegistrationId(registrationId);
        req.setAudienceKey(audienceKey);
        LeadConvertEvaluateResult result = evaluate(leadId, req);
        return result;
    }

    @Override
    public String resolveLeadIdForCheckIn(String eventId, String registrationId, String audienceKey)
    {
        String tenantId = currentTenantId();
        if (StringUtils.hasText(registrationId))
        {
            CrmEventRegistration reg = registrationMapper.selectByRegistrationId(tenantId, registrationId);
            if (reg != null && StringUtils.hasText(reg.getLeadId()))
            {
                return reg.getLeadId();
            }
        }
        if (StringUtils.hasText(eventId) && StringUtils.hasText(audienceKey))
        {
            EventAudienceRowVo row = eventAudienceService.findByAudienceKey(tenantId, eventId, audienceKey);
            if (row != null && StringUtils.hasText(row.getLeadId()))
            {
                return row.getLeadId();
            }
        }
        return null;
    }

    private LeadConvertResult buildSkippedIfConverted(CrmLead lead)
    {
        if (!isAlreadyConverted(lead))
        {
            return null;
        }
        LeadConvertResult result = new LeadConvertResult();
        result.setLeadId(lead.getLeadId());
        result.setContactId(lead.getContactId());
        result.setSkipped(true);
        result.setMerged(false);
        ensureContactSourceLeadId(lead);
        return result;
    }

    private void ensureContactSourceLeadId(CrmLead lead)
    {
        if (!StringUtils.hasText(lead.getContactId()) || !StringUtils.hasText(lead.getLeadId()))
        {
            return;
        }
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(lead.getContactId());
        if (contact == null || StringUtils.hasText(contact.getSourceLeadId()))
        {
            return;
        }
        CrmContact patch = new CrmContact();
        patch.setContactId(contact.getContactId());
        patch.setSourceLeadId(lead.getLeadId());
        crmContactMapper.updateCrmContact(patch);
    }

    private static boolean isAlreadyConverted(CrmLead lead)
    {
        if (StringUtils.hasText(lead.getContactId()))
        {
            return true;
        }
        return "converted".equals(AudienceLeadVisibility.normalizeStatus(lead.getStatus()));
    }

    private static boolean hasLeadName(CrmLead lead)
    {
        return StringUtils.hasText(lead.getLeadName()) || StringUtils.hasText(lead.getContactName());
    }

    private ResolveContactResult resolveTargetContact(CrmLead lead, LeadConvertRequest request,
            LeadConvertConfigVo config, String mobilePhone, LeadConvertCopyOptions copyOptions, String operator)
    {
        String conversionType = request.effectiveConversionType();
        LeadDedupeMatch contactMatch = leadDedupeService.findDuplicateContact(lead, request.getContactId());
        CrmContact existing = contactMatch == null ? null : leadDedupeService.loadContact(contactMatch.getId());

        if (existing != null && "mergeOnly".equalsIgnoreCase(conversionType))
        {
            return mergeIntoExisting(lead, existing, copyOptions);
        }
        if (existing != null)
        {
            if (Boolean.TRUE.equals(config.getMergeOnDuplicatePhone()))
            {
                return mergeIntoExisting(lead, existing, copyOptions);
            }
            throw new ServiceException("手机号已存在 Contact，无法新建", HttpStatus.CONFLICT);
        }
        if ("mergeOnly".equalsIgnoreCase(conversionType))
        {
            throw new ServiceException("未找到可合并的 Contact", HttpStatus.BAD_REQUEST);
        }

        String orgId = resolveOrgId(request.getOrgId(), lead, request.getEventId(), config);
        String name = firstNonBlank(request.getName(), lead.getContactName(), lead.getLeadName());
        CrmContact contact = fieldMapper.mapNewContact(lead, orgId, name, mobilePhone, copyOptions);
        convertOwnerResolver.applyToNewContact(contact, lead, config, operator);
        contact.setContactId(IdUtils.fastUUID());
        contact.setSourceLeadId(lead.getLeadId());
        contact.setCreateTime(DateUtils.getNowDate());
        contact.setCreateBy(resolveCreateBy(operator));
        crmContactMapper.insertCrmContact(contact);
        outboundWebhookHookSupport.onContactCreated(contact);

        ResolveContactResult result = new ResolveContactResult();
        result.contactId = contact.getContactId();
        result.contactName = contact.getName();
        result.merged = false;
        return result;
    }

    private ResolveContactResult mergeIntoExisting(CrmLead lead, CrmContact existing,
            LeadConvertCopyOptions copyOptions)
    {
        CrmContact patch = fieldMapper.mapMergePatch(lead, existing, copyOptions);
        if (!StringUtils.hasText(existing.getSourceLeadId()))
        {
            patch.setSourceLeadId(lead.getLeadId());
        }
        patch.setUpdateTime(DateUtils.getNowDate());
        patch.setUpdateBy(SecurityUtils.getUsername());
        crmContactMapper.updateCrmContact(patch);

        ResolveContactResult result = new ResolveContactResult();
        result.contactId = existing.getContactId();
        result.contactName = firstNonBlank(existing.getName(), lead.getContactName(), lead.getLeadName());
        result.merged = true;
        return result;
    }

    private CrmContact findExistingContact(String contactId, String mobilePhone)
    {
        if (StringUtils.hasText(contactId))
        {
            CrmContact byId = crmContactMapper.selectCrmContactByContactId(contactId.trim());
            if (byId != null)
            {
                return byId;
            }
        }
        if (StringUtils.hasText(mobilePhone))
        {
            String viaIdentity = identityService.findContactByIdentity("mobile_phone", mobilePhone.trim());
            if (StringUtils.hasText(viaIdentity))
            {
                return crmContactMapper.selectCrmContactByContactId(viaIdentity);
            }
            CrmContact probe = new CrmContact();
            probe.setMobilePhone(mobilePhone.trim());
            List<CrmContact> matches = crmContactMapper.checkContactExisting(probe);
            if (matches != null && !matches.isEmpty())
            {
                return matches.get(0);
            }
        }
        return null;
    }

    private void bindIdentitiesIfNeeded(LeadConvertCopyOptions copyOptions, String contactId,
            String mobilePhone, CrmLead lead)
    {
        if (!copyOptions.isMigrateIdentities())
        {
            return;
        }
        if (StringUtils.hasText(mobilePhone))
        {
            try
            {
                identityService.bindIdentity(contactId, "mobile_phone", mobilePhone.trim(), SOURCE_LEAD_CONVERT);
            }
            catch (RuntimeException ex)
            {
                identityService.recordIdentityConflict("mobile_phone", mobilePhone.trim(), contactId,
                        SOURCE_LEAD_CONVERT);
            }
        }
        if (StringUtils.hasText(lead.getWecomExternalUserid()))
        {
            try
            {
                identityService.bindIdentity(contactId, "wecom_external_userid",
                        lead.getWecomExternalUserid().trim(), SOURCE_LEAD_CONVERT);
            }
            catch (RuntimeException ex)
            {
                identityService.recordIdentityConflict("wecom_external_userid",
                        lead.getWecomExternalUserid().trim(), contactId, SOURCE_LEAD_CONVERT);
            }
        }
        if (StringUtils.hasText(lead.getWechatMpOpenid()))
        {
            try
            {
                identityService.bindIdentity(contactId, "wechat_mp_openid",
                        lead.getWechatMpOpenid().trim(), SOURCE_LEAD_CONVERT);
            }
            catch (RuntimeException ex)
            {
                identityService.recordIdentityConflict("wechat_mp_openid",
                        lead.getWechatMpOpenid().trim(), contactId, SOURCE_LEAD_CONVERT);
            }
        }
    }

    private void writeBackLead(CrmLead lead, String contactId, String contactName, String operator)
    {
        CrmLead upd = new CrmLead();
        upd.setLeadId(lead.getLeadId());
        upd.setContactId(contactId);
        upd.setContactName(contactName);
        upd.setConvertTime(DateUtils.getNowDate());
        upd.setStatus(STATUS_CONVERTED);
        upd.setStage(STAGE_CONVERTING);
        upd.setUpdateTime(DateUtils.getNowDate());
        upd.setUpdateBy(operator);
        crmLeadMapper.updateCrmLead(upd);
    }

    private void insertConversionHistory(CrmLead lead, String contactId, String contactName, String operator)
    {
        CrmConversionHistory history = new CrmConversionHistory();
        history.setId(IdUtils.fastUUID());
        history.setLeadId(lead.getLeadId());
        history.setLeadName(firstNonBlank(lead.getLeadName(), lead.getContactName()));
        history.setConversionType(CONVERSION_TYPE_CONTACT);
        history.setTargetId(contactId);
        history.setTargetName(contactName);
        history.setConvertedBy(operator);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null && loginUser.getUser() != null)
        {
            history.setUserId(loginUser.getUser().getUserId());
            history.setDeptId(loginUser.getUser().getDeptId());
        }
        else if (lead.getUserId() != null)
        {
            history.setUserId(lead.getUserId());
            history.setDeptId(lead.getDeptId());
        }
        history.setCreateBy(operator);
        history.setCreateTime(DateUtils.getNowDate());
        conversionHistoryMapper.insertCrmConversionHistory(history);
    }

    private String insertOpportunity(CrmLead lead, String contactId, String orgId, String optyName,
            String operator)
    {
        CrmOpty opty = new CrmOpty();
        opty.setOptyId(IdUtils.fastUUID());
        opty.setLeadId(lead.getLeadId());
        opty.setContactId(contactId);
        opty.setOrgId(orgId);
        opty.setName(optyName.trim());
        opty.setLeadName(firstNonBlank(lead.getLeadName(), lead.getContactName()));
        opty.setContactName(firstNonBlank(lead.getContactName(), lead.getLeadName()));
        opty.setOwner(lead.getOwner());
        opty.setCreateBy(operator);
        opty.setCreateTime(DateUtils.getNowDate());
        crmOptyService.insertCrmOpty(opty);
        return opty.getOptyId();
    }

    private void scheduleWecomSyncAfterCommit(String leadId, String contactId)
    {
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    wecomLeadConvertSyncSupport.syncAfterLeadConverted(leadId, contactId);
                }
            });
        }
        else
        {
            wecomLeadConvertSyncSupport.syncAfterLeadConverted(leadId, contactId);
        }
    }

    private void scheduleMpSyncAfterCommit(String leadId, String contactId)
    {
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    mpLeadConvertSyncSupport.syncAfterLeadConverted(leadId, contactId);
                }
            });
        }
        else
        {
            mpLeadConvertSyncSupport.syncAfterLeadConverted(leadId, contactId);
        }
    }

    /**
     * 事务提交后异步生成 Contact 服务摘要（见 docs/contact-service-summary-plan.md）。
     * force=true：新转化/合并后 bootstrap；force=false：幂等 skipped 时仅在无摘要时补跑。
     */
    private void scheduleSummaryBootstrapAfterCommit(String contactId, boolean force)
    {
        if (!StringUtils.hasText(contactId) || contactServiceSummaryService == null)
        {
            return;
        }
        final String tenantId = DynamicDataSourceContextHolder.getDataSourceKey();
        Runnable job = new Runnable()
        {
            @Override
            public void run()
            {
                contactServiceSummaryService.enqueueBootstrap(tenantId, contactId, "convert", force);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
            {
                @Override
                public void afterCommit()
                {
                    job.run();
                }
            });
        }
        else
        {
            job.run();
        }
    }

    private static void validateConvertRequest(String conversionType, String orgId, String name,
            String mobilePhone, String opportunityName, String trigger)
    {
        if (!"mergeOnly".equalsIgnoreCase(conversionType) && !StringUtils.hasText(orgId))
        {
            throw new ServiceException("orgId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(name))
        {
            throw new ServiceException("姓名不能为空", HttpStatus.BAD_REQUEST);
        }
        boolean wecomCallback = "wecom_callback".equals(trigger);
        if (!wecomCallback && !StringUtils.hasText(mobilePhone))
        {
            throw new ServiceException("手机号不能为空", HttpStatus.BAD_REQUEST);
        }
        if ("withOpportunity".equalsIgnoreCase(conversionType)
                && !StringUtils.hasText(opportunityName))
        {
            throw new ServiceException("withOpportunity 时 opportunityName 必填", HttpStatus.BAD_REQUEST);
        }
    }

    private static String resolveCreateBy(String operator)
    {
        if (StringUtils.hasText(operator))
        {
            return operator.trim();
        }
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception ex)
        {
            return "system";
        }
    }

    private String resolveOrgId(String requestOrgId, CrmLead lead, String eventId, LeadConvertConfigVo config)
    {
        if (StringUtils.hasText(requestOrgId))
        {
            return requestOrgId.trim();
        }
        if (lead != null && StringUtils.hasText(lead.getOrgId()))
        {
            return lead.getOrgId().trim();
        }
        if (StringUtils.hasText(eventId))
        {
            CrmEvent event = crmEventMapper.selectCrmEventByEventId(eventId.trim());
            if (event != null && StringUtils.hasText(event.getDefaultOrgId()))
            {
                return event.getDefaultOrgId().trim();
            }
        }
        if (config != null && StringUtils.hasText(config.getDefaultOrgId()))
        {
            return config.getDefaultOrgId().trim();
        }
        return null;
    }

    private static LeadConvertLeadSummary toLeadSummary(CrmLead lead)
    {
        LeadConvertLeadSummary summary = new LeadConvertLeadSummary();
        summary.setLeadId(lead.getLeadId());
        summary.setLeadName(firstNonBlank(lead.getLeadName(), lead.getContactName()));
        summary.setMobilePhone(lead.getMobilePhone());
        summary.setStage(lead.getStage());
        return summary;
    }

    private static String currentTenantId()
    {
        return TenantDataSourceSupport.currentTenantKey();
    }

    private static String firstNonBlank(String... values)
    {
        if (values == null)
        {
            return null;
        }
        for (String v : values)
        {
            if (StringUtils.hasText(v))
            {
                return v.trim();
            }
        }
        return null;
    }

    private String resolveWecomStateForLead(String leadId)
    {
        if (!StringUtils.hasText(leadId))
        {
            return null;
        }
        CrmWecomExternalContact query = new CrmWecomExternalContact();
        query.setCrmLeadId(leadId.trim());
        List<CrmWecomExternalContact> rows = wecomExternalContactMapper.selectList(query);
        if (rows == null)
        {
            return null;
        }
        for (CrmWecomExternalContact row : rows)
        {
            if (row != null && StringUtils.hasText(row.getState()))
            {
                return row.getState().trim();
            }
        }
        return null;
    }

    private static final class ResolveContactResult
    {
        private String contactId;
        private String contactName;
        private boolean merged;
    }
}
