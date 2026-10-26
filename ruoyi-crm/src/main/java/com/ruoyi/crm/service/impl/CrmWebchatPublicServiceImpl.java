package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmWebchatConfig;
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWebchatMessage;
import com.ruoyi.crm.domain.CrmWebchatVisitor;
import com.ruoyi.crm.domain.CrmWebsiteVisitorSession;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.mapper.CrmWebchatConfigMapper;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatMessageMapper;
import com.ruoyi.crm.mapper.CrmWebchatVisitorMapper;
import com.ruoyi.crm.mapper.CrmWebsiteVisitorSessionMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmWebchatConfigService;
import com.ruoyi.crm.service.ICrmWebchatMessageService;
import com.ruoyi.crm.service.ICrmWebchatPublicService;
import com.ruoyi.crm.service.support.IdentityNormalizer;
import com.ruoyi.crm.service.support.WebsiteVisitorSessionService;
import com.ruoyi.crm.webchat.WebchatActivityEventTypes;
import com.ruoyi.crm.webchat.WebchatIdentityResolutionSupport;
import com.ruoyi.crm.webchat.WebchatOriginValidator;
import com.ruoyi.crm.webchat.WebchatRateLimiter;
import com.ruoyi.crm.webchat.WebchatSessionTokenService;
import com.ruoyi.crm.webchat.WebchatTenantBinder;
import com.ruoyi.crm.webchat.realtime.WebchatInboxSseHub;
import com.ruoyi.crm.webchat.realtime.WebchatInboxSsePayloads;

@Service
public class CrmWebchatPublicServiceImpl implements ICrmWebchatPublicService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWebchatPublicServiceImpl.class);

    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{8,64}$");

    @Autowired
    private WebchatTenantBinder tenantBinder;

    @Autowired
    private CrmWebchatConfigMapper configMapper;

    @Autowired
    private CrmWebchatVisitorMapper visitorMapper;

    @Autowired
    private CrmWebchatConversationMapper conversationMapper;

    @Autowired
    private CrmWebchatMessageMapper messageMapper;

    @Autowired
    private CrmWebsiteVisitorSessionMapper websiteSessionMapper;

    @Autowired
    private ICrmWebchatConfigService configService;

    @Autowired
    private ICrmWebchatMessageService messageService;

    @Autowired
    private WebchatSessionTokenService sessionTokenService;

    @Autowired
    private WebchatRateLimiter rateLimiter;

    @Autowired
    private WebchatIdentityResolutionSupport identityResolutionSupport;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private WebsiteVisitorSessionService visitorSessionService;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired
    private WebchatInboxSseHub sseHub;

    @Override
    public String resolveAndBindTenant(String websiteToken)
    {
        return tenantBinder.resolveAndBind(websiteToken);
    }

    @Override
    public Map<String, Object> getConfig(String websiteToken, String origin, String referer, String clientIp)
    {
        rateLimiter.checkConfig(clientIp);
        String tenantId = resolveAndBindTenant(websiteToken);
        CrmWebchatConfig config = requireEnabledConfig(tenantId, websiteToken);
        assertOriginAllowed(origin, referer, config);
        return configService.getPublicConfig(tenantId);
    }

    /**
     * 公开写接口勿在本方法上加 {@code @Transactional}：须先 token→切租户库，
     * 否则事务会在 bind 前固定主库连接（见 WebsiteOnlineRegisterServiceImpl）。
     */
    @Override
    public Map<String, Object> createSession(String websiteToken, String origin, String referer, String clientIp,
                                             String visitorId, String sessionId, String userAgent)
    {
        rateLimiter.checkSession(clientIp);
        // 须在任意租户库 SQL 之前绑定
        String tenantId = resolveAndBindTenant(websiteToken);
        CrmWebchatConfig config = requireEnabledConfig(tenantId, websiteToken);
        assertOriginAllowed(origin, referer, config);

        String effectiveVisitorId = StringUtils.hasText(visitorId) ? visitorId.trim() : UUID.randomUUID().toString();
        validateIdToken("visitorId", effectiveVisitorId);
        if (StringUtils.hasText(sessionId))
        {
            validateIdToken("sessionId", sessionId.trim());
        }

        Date now = new Date();
        CrmWebchatVisitor visitor = new CrmWebchatVisitor();
        visitor.setTenantId(tenantId);
        visitor.setVisitorId(effectiveVisitorId);
        visitor.setUserAgent(truncate(userAgent, 512));
        visitor.setFirstSeenAt(now);
        visitor.setLastSeenAt(now);
        visitor.setCreateTime(now);
        visitorMapper.upsertVisitor(visitor);

        visitor = visitorMapper.selectByVisitorId(tenantId, effectiveVisitorId);
        stitchIdentity(tenantId, visitor, sessionId);

        visitor = visitorMapper.selectByVisitorId(tenantId, effectiveVisitorId);
        WebchatSessionTokenService.IssuedToken issued =
                sessionTokenService.issue(tenantId, effectiveVisitorId);

        Map<String, Object> data = new HashMap<>();
        data.put("visitorId", effectiveVisitorId);
        data.put("sessionToken", issued.getToken());
        data.put("expiresAt", new Date(issued.getExpiresAtEpochMs()));
        data.put("contactBound", StringUtils.hasText(visitor.getContactId()));
        data.put("leadBound", StringUtils.hasText(visitor.getLeadId()));
        if (StringUtils.hasText(visitor.getDisplayName()))
        {
            data.put("displayName", visitor.getDisplayName());
        }
        return data;
    }

    @Override
    public Map<String, Object> openConversation(String sessionToken, String websiteToken,
                                                String origin, String referer)
    {
        // bindAndVerify 内先切租户库；勿在本方法加 @Transactional
        BoundSession bound = bindAndVerify(sessionToken, websiteToken, origin, referer, true);
        CrmWebchatVisitor visitor = visitorMapper.selectByVisitorId(bound.tenantId, bound.visitorId);
        if (visitor == null)
        {
            throw new ServiceException("visitor not found", HttpStatus.NOT_FOUND);
        }

        CrmWebchatConversation active = conversationMapper.selectActiveByVisitorId(
                bound.tenantId, bound.visitorId);
        boolean created = false;
        if (active == null)
        {
            Date now = new Date();
            CrmWebchatConversation createdConv = new CrmWebchatConversation();
            createdConv.setTenantId(bound.tenantId);
            createdConv.setVisitorId(bound.visitorId);
            createdConv.setProfileName(visitor.getDisplayName());
            createdConv.setContactId(visitor.getContactId());
            createdConv.setLeadId(visitor.getLeadId());
            createdConv.setUnreadCount(0);
            createdConv.setStatus("active");
            createdConv.setCreateTime(now);
            createdConv.setUpdateTime(now);
            conversationMapper.insert(createdConv);
            active = conversationMapper.selectActiveByVisitorId(bound.tenantId, bound.visitorId);
            created = true;
            if (active != null)
            {
                produceConversationStarted(active);
            }
        }

        CrmWebchatConfig config = configMapper.selectByTenantId(bound.tenantId);
        Map<String, Object> data = new HashMap<>();
        data.put("id", active != null ? active.getId() : null);
        data.put("status", active != null ? active.getStatus() : "active");
        data.put("contactBound", active != null && StringUtils.hasText(active.getContactId()));
        data.put("leadBound", active != null && StringUtils.hasText(active.getLeadId()));
        data.put("welcomeMessage", config != null ? config.getWelcomeMessage() : null);
        data.put("created", created);
        return data;
    }

    @Override
    public Map<String, Object> listMessages(String sessionToken, String websiteToken, Long conversationId,
                                            Long beforeId, Integer pageSize)
    {
        BoundSession bound = bindAndVerify(sessionToken, websiteToken, null, null, false);
        CrmWebchatConversation conv = requireOwnedConversation(bound, conversationId);
        int size = pageSize != null && pageSize > 0 ? Math.min(pageSize, 100) : 50;
        List<CrmWebchatMessage> messages = messageMapper.selectByConversationId(conv.getId(), beforeId, size);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (CrmWebchatMessage msg : messages)
        {
            rows.add(WebchatInboxSsePayloads.toMessageRecord(msg));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("rows", rows);
        return data;
    }

    @Override
    public Map<String, Object> postMessage(String sessionToken, String websiteToken, String origin, String referer,
                                           String clientIp, Long conversationId, String content,
                                           String messageType, String clientMessageId)
    {
        // bindAndVerify 内先切租户库；消息落库事务由 messageService 承担
        BoundSession bound = bindAndVerify(sessionToken, websiteToken, origin, referer, true);
        rateLimiter.checkMessage(bound.visitorId);
        requireOwnedConversation(bound, conversationId);
        if (StringUtils.hasText(clientMessageId))
        {
            validateIdToken("clientMessageId", clientMessageId.trim());
        }
        CrmWebchatMessage msg = messageService.insertInbound(
                bound.tenantId, conversationId, bound.visitorId, content, messageType, clientMessageId);
        return WebchatInboxSsePayloads.toMessageRecord(msg);
    }

    @Override
    public Map<String, Object> identify(String sessionToken, String websiteToken, String origin, String referer,
                                        String clientIp, Long conversationId, String displayName,
                                        String mobilePhone, String email, String whatsapp)
    {
        rateLimiter.checkIdentify(clientIp);
        // bindAndVerify 内先切租户库；勿在本方法加 @Transactional
        BoundSession bound = bindAndVerify(sessionToken, websiteToken, origin, referer, true);
        CrmWebchatConversation conv = requireOwnedConversation(bound, conversationId);
        CrmWebchatVisitor visitor = visitorMapper.selectByVisitorId(bound.tenantId, bound.visitorId);
        if (visitor == null)
        {
            throw new ServiceException("visitor not found", HttpStatus.NOT_FOUND);
        }

        CrmWebchatVisitor profile = new CrmWebchatVisitor();
        profile.setTenantId(bound.tenantId);
        profile.setVisitorId(bound.visitorId);
        profile.setDisplayName(truncate(displayName, 256));
        profile.setMobilePhone(truncate(mobilePhone, 64));
        profile.setEmail(truncate(email, 256));
        profile.setWhatsapp(truncate(whatsapp, 64));
        profile.setLastSeenAt(new Date());
        visitorMapper.updateProfile(profile);

        visitor = visitorMapper.selectByVisitorId(bound.tenantId, bound.visitorId);

        boolean leadCreated = false;
        String contactId = StringUtils.hasText(visitor.getContactId()) ? visitor.getContactId() : null;
        String leadId = StringUtils.hasText(visitor.getLeadId()) ? visitor.getLeadId() : null;

        if (!StringUtils.hasText(contactId))
        {
            contactId = resolveContactFromProfile(visitor);
        }
        if (StringUtils.hasText(contactId))
        {
            leadId = null;
            identityResolutionSupport.bindVisitorIfNeeded(bound.visitorId, contactId,
                    WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            bindProfileIdentities(contactId, visitor);
        }
        else if (!StringUtils.hasText(leadId))
        {
            ResolveLeadResult lr = resolveOrCreateLead(visitor);
            leadId = lr.leadId;
            leadCreated = lr.created;
        }

        visitorMapper.updateSubjects(bound.tenantId, bound.visitorId, contactId, leadId);
        conversationMapper.updateContactLead(conv.getId(), bound.tenantId, contactId, leadId);

        if (StringUtils.hasText(displayName))
        {
            CrmWebchatConversation nameUpdate = new CrmWebchatConversation();
            nameUpdate.setId(conv.getId());
            nameUpdate.setProfileName(truncate(displayName, 256));
            nameUpdate.setLastMessageAt(conv.getLastMessageAt());
            nameUpdate.setLastMessagePreview(conv.getLastMessagePreview());
            conversationMapper.updateLastMessage(nameUpdate);
        }

        if (StringUtils.hasText(visitor.getWebsiteSessionId())
                && (StringUtils.hasText(contactId) || StringUtils.hasText(leadId)))
        {
            String subjectType = StringUtils.hasText(contactId) ? "Contact" : "Lead";
            String subjectId = StringUtils.hasText(contactId) ? contactId : leadId;
            try
            {
                visitorSessionService.stitch(visitor.getWebsiteSessionId(), null, subjectType, subjectId);
            }
            catch (Exception e)
            {
                log.warn("Webchat identify session stitch failed: {}", e.getMessage());
            }
        }

        CrmWebchatConversation updated = conversationMapper.selectById(conv.getId());
        produceIdentifiedActivity(updated, leadCreated);

        Map<String, Object> data = new HashMap<>();
        data.put("contactBound", StringUtils.hasText(contactId));
        data.put("leadBound", StringUtils.hasText(leadId));
        data.put("leadCreated", leadCreated);
        return data;
    }

    @Override
    public Map<String, Object> closeConversation(String sessionToken, String websiteToken, Long conversationId)
    {
        // bindAndVerify 内先切租户库；勿在本方法加 @Transactional
        BoundSession bound = bindAndVerify(sessionToken, websiteToken, null, null, false);
        CrmWebchatConversation conv = requireOwnedConversation(bound, conversationId);
        Date now = new Date();
        conversationMapper.closeConversation(conv.getId(), bound.tenantId, now);
        produceClosedActivity(conv);
        Map<String, Object> data = new HashMap<>();
        data.put("id", conv.getId());
        data.put("status", "closed");
        data.put("closedAt", now);
        return data;
    }

    @Override
    public SseEmitter subscribeConversationEvents(String sessionToken, String websiteToken, Long conversationId)
    {
        BoundSession bound = bindAndVerify(sessionToken, websiteToken, null, null, false);
        requireOwnedConversation(bound, conversationId);
        return sseHub.registerWidget(bound.tenantId, conversationId);
    }

    private void stitchIdentity(String tenantId, CrmWebchatVisitor visitor, String sessionId)
    {
        if (visitor == null)
        {
            return;
        }
        // already has contact — may still bind missing identity
        if (StringUtils.hasText(visitor.getContactId()))
        {
            identityResolutionSupport.bindVisitorIfNeeded(visitor.getVisitorId(), visitor.getContactId());
        }
        else
        {
            String viaIdentity = identityResolutionSupport.resolveContactId(visitor.getVisitorId());
            if (StringUtils.hasText(viaIdentity))
            {
                visitorMapper.updateSubjects(tenantId, visitor.getVisitorId(), viaIdentity, null);
                visitor.setContactId(viaIdentity);
                visitor.setLeadId(null);
            }
        }

        if (!StringUtils.hasText(sessionId))
        {
            return;
        }
        String sid = sessionId.trim();
        CrmWebsiteVisitorSession session = new CrmWebsiteVisitorSession();
        session.setId(UUID.randomUUID().toString());
        session.setSessionId(sid);
        websiteSessionMapper.upsertSession(session);

        CrmWebchatVisitor touch = new CrmWebchatVisitor();
        touch.setTenantId(tenantId);
        touch.setVisitorId(visitor.getVisitorId());
        touch.setWebsiteSessionId(sid);
        touch.setLastSeenAt(new Date());
        visitorMapper.updateProfile(touch);

        CrmWebsiteVisitorSession boundSession = websiteSessionMapper.selectBySessionId(sid);
        if (boundSession == null || !boundSession.isBound())
        {
            return;
        }
        // do not overwrite existing contact
        visitor = visitorMapper.selectByVisitorId(tenantId, visitor.getVisitorId());
        if (visitor != null && StringUtils.hasText(visitor.getContactId()))
        {
            identityResolutionSupport.bindVisitorIfNeeded(visitor.getVisitorId(), visitor.getContactId());
            return;
        }
        String subjectType = boundSession.getSubjectType();
        String subjectId = boundSession.getSubjectId();
        if ("Contact".equalsIgnoreCase(subjectType) && StringUtils.hasText(subjectId))
        {
            visitorMapper.updateSubjects(tenantId, visitor.getVisitorId(), subjectId, null);
            identityResolutionSupport.bindVisitorIfNeeded(visitor.getVisitorId(), subjectId);
        }
        else if ("Lead".equalsIgnoreCase(subjectType) && StringUtils.hasText(subjectId))
        {
            if (visitor == null || !StringUtils.hasText(visitor.getContactId()))
            {
                visitorMapper.updateSubjects(tenantId, visitor.getVisitorId(), null, subjectId);
            }
        }
    }

    private String resolveContactFromProfile(CrmWebchatVisitor visitor)
    {
        if (StringUtils.hasText(visitor.getMobilePhone()))
        {
            String phone = IdentityNormalizer.normalize(IdentityNormalizer.TYPE_MOBILE_PHONE, visitor.getMobilePhone());
            String c = identityService.findContactByIdentity(IdentityNormalizer.TYPE_MOBILE_PHONE, phone);
            if (StringUtils.hasText(c))
            {
                return c;
            }
        }
        if (StringUtils.hasText(visitor.getEmail()))
        {
            String em = IdentityNormalizer.normalize(IdentityNormalizer.TYPE_EMAIL, visitor.getEmail());
            String c = identityService.findContactByIdentity(IdentityNormalizer.TYPE_EMAIL, em);
            if (StringUtils.hasText(c))
            {
                return c;
            }
        }
        if (StringUtils.hasText(visitor.getWhatsapp()))
        {
            String wa = IdentityNormalizer.normalize(IdentityNormalizer.TYPE_WHATSAPP_PHONE, visitor.getWhatsapp());
            String c = identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_PHONE, wa);
            if (StringUtils.hasText(c))
            {
                return c;
            }
            c = identityService.findContactByIdentity(IdentityNormalizer.TYPE_WHATSAPP_WA_ID, visitor.getWhatsapp().trim());
            if (StringUtils.hasText(c))
            {
                return c;
            }
        }
        return null;
    }

    private void bindProfileIdentities(String contactId, CrmWebchatVisitor visitor)
    {
        try
        {
            if (StringUtils.hasText(visitor.getMobilePhone()))
            {
                identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_MOBILE_PHONE,
                        visitor.getMobilePhone(), WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            }
            if (StringUtils.hasText(visitor.getEmail()))
            {
                identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_EMAIL,
                        visitor.getEmail(), WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            }
            if (StringUtils.hasText(visitor.getWhatsapp()))
            {
                identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_WHATSAPP_PHONE,
                        visitor.getWhatsapp(), WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            }
        }
        catch (Exception e)
        {
            log.warn("bind profile identities failed: {}", e.getMessage());
        }
    }

    private ResolveLeadResult resolveOrCreateLead(CrmWebchatVisitor visitor)
    {
        String phone = StringUtils.hasText(visitor.getMobilePhone())
                ? IdentityNormalizer.normalize(IdentityNormalizer.TYPE_MOBILE_PHONE, visitor.getMobilePhone())
                : null;
        if (StringUtils.hasText(phone))
        {
            CrmLead initial = crmLeadMapper.selectInitialByMobilePhone(phone);
            if (initial != null && StringUtils.hasText(initial.getLeadId()))
            {
                return new ResolveLeadResult(initial.getLeadId(), false);
            }
        }
        CrmLead lead = new CrmLead();
        lead.setLeadId(UUID.randomUUID().toString());
        lead.setStatus("Initial");
        lead.setLeadSource("webchat");
        lead.setLeadType("business");
        lead.setCreateBy("webchat-widget");
        lead.setCreateTime(DateUtils.getNowDate());
        String name = StringUtils.hasText(visitor.getDisplayName()) ? visitor.getDisplayName().trim() : "Web Chat Visitor";
        lead.setLeadName(name);
        lead.setFullName(name);
        if (StringUtils.hasText(phone))
        {
            lead.setMobilePhone(phone);
        }
        if (StringUtils.hasText(visitor.getEmail()))
        {
            lead.setWorkEmail(IdentityNormalizer.normalize(IdentityNormalizer.TYPE_EMAIL, visitor.getEmail()));
        }
        int rows = crmLeadService.insertCrmLead(lead);
        if (rows <= 0)
        {
            throw new ServiceException("failed to create lead", HttpStatus.ERROR);
        }
        return new ResolveLeadResult(lead.getLeadId(), true);
    }

    private BoundSession bindAndVerify(String sessionToken, String websiteToken,
                                       String origin, String referer, boolean checkOrigin)
    {
        // 先验 session（含 tenantId）；公开 GET 历史/SSE 可能只带 sessionToken 不带 website_token
        WebchatSessionTokenService.SessionClaims claims = sessionTokenService.verify(sessionToken);
        String tenantId;
        CrmWebchatConfig config;
        if (StringUtils.hasText(websiteToken))
        {
            tenantId = resolveAndBindTenant(websiteToken);
            if (!tenantId.equals(claims.getTenantId()))
            {
                throw new ServiceException("sessionToken mismatch", HttpStatus.UNAUTHORIZED);
            }
            config = requireEnabledConfig(tenantId, websiteToken);
        }
        else
        {
            tenantId = claims.getTenantId();
            tenantBinder.bindTenantDataSource(tenantId);
            config = configMapper.selectByTenantId(tenantId);
            if (config == null || config.getEnabled() == null || config.getEnabled() != 1)
            {
                throw new ServiceException("webchat disabled", HttpStatus.FORBIDDEN);
            }
        }
        if (checkOrigin)
        {
            assertOriginAllowed(origin, referer, config);
        }
        return new BoundSession(tenantId, claims.getVisitorId());
    }

    private CrmWebchatConversation requireOwnedConversation(BoundSession bound, Long conversationId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId is required", HttpStatus.BAD_REQUEST);
        }
        CrmWebchatConversation conv = conversationMapper.selectById(conversationId);
        if (conv == null || !bound.tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        if (!bound.visitorId.equals(conv.getVisitorId()))
        {
            throw new ServiceException("conversation mismatch", HttpStatus.FORBIDDEN);
        }
        return conv;
    }

    private CrmWebchatConfig requireEnabledConfig(String tenantId, String websiteToken)
    {
        CrmWebchatConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1)
        {
            throw new ServiceException("webchat disabled", HttpStatus.FORBIDDEN);
        }
        if (!websiteToken.equals(config.getWebsiteToken()))
        {
            // master mapping may lag; still require tenant config token match when present
            CrmWebchatConfig byToken = configMapper.selectByWebsiteToken(websiteToken);
            if (byToken == null || byToken.getEnabled() == null || byToken.getEnabled() != 1)
            {
                throw new ServiceException("invalid token", HttpStatus.FORBIDDEN);
            }
            return byToken;
        }
        return config;
    }

    private static void assertOriginAllowed(String origin, String referer, CrmWebchatConfig config)
    {
        if (!WebchatOriginValidator.isOriginAllowed(origin, referer, config.getAllowedDomains()))
        {
            throw new ServiceException("origin not allowed", HttpStatus.FORBIDDEN);
        }
    }

    private static void validateIdToken(String field, String value)
    {
        if (!ID_PATTERN.matcher(value).matches()
                && !isUuid(value))
        {
            throw new ServiceException(field + " invalid", HttpStatus.BAD_REQUEST);
        }
    }

    private static boolean isUuid(String value)
    {
        try
        {
            UUID.fromString(value);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private void produceConversationStarted(CrmWebchatConversation conv)
    {
        if (conv == null || (!StringUtils.hasText(conv.getContactId()) && !StringUtils.hasText(conv.getLeadId())))
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:started:" + conv.getId());
            dto.setEventType(WebchatActivityEventTypes.CONVERSATION_STARTED);
            dto.setEventName(WebchatActivityEventTypes.CONVERSATION_STARTED_NAME);
            dto.setEventCategory(WebchatActivityEventTypes.CONVERSATION_STARTED_CATEGORY);
            dto.setImportance(WebchatActivityEventTypes.CONVERSATION_STARTED_IMPORTANCE);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            dto.setCreateBy("webchat-widget");
            dto.setStartedDate(new Date());
            fillSubject(dto, conv);
            JSONObject payload = new JSONObject();
            payload.put("conversation_id", conv.getId());
            payload.put("visitor_id", conv.getVisitorId());
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception e)
        {
            log.warn("produceConversationStarted failed: {}", e.getMessage());
        }
    }

    private void produceIdentifiedActivity(CrmWebchatConversation conv, boolean leadCreated)
    {
        if (conv == null || (!StringUtils.hasText(conv.getContactId()) && !StringUtils.hasText(conv.getLeadId())))
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:identified:" + conv.getId() + ":" + System.currentTimeMillis());
            dto.setEventType(WebchatActivityEventTypes.IDENTIFIED);
            dto.setEventName(WebchatActivityEventTypes.IDENTIFIED_NAME);
            dto.setEventCategory(WebchatActivityEventTypes.IDENTIFIED_CATEGORY);
            dto.setImportance(WebchatActivityEventTypes.IDENTIFIED_IMPORTANCE);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            dto.setCreateBy("webchat-widget");
            dto.setStartedDate(new Date());
            fillSubject(dto, conv);
            JSONObject payload = new JSONObject();
            payload.put("conversation_id", conv.getId());
            payload.put("visitor_id", conv.getVisitorId());
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);

            if (leadCreated && StringUtils.hasText(conv.getLeadId()))
            {
                ActivityEventDTO leadDto = new ActivityEventDTO();
                leadDto.setChannel("webchat");
                leadDto.setChannelEventId("webchat:lead:" + conv.getLeadId());
                leadDto.setEventType(WebchatActivityEventTypes.LEAD_CREATED);
                leadDto.setEventName(WebchatActivityEventTypes.LEAD_CREATED_NAME);
                leadDto.setEventCategory(WebchatActivityEventTypes.LEAD_CREATED_CATEGORY);
                leadDto.setImportance(WebchatActivityEventTypes.LEAD_CREATED_IMPORTANCE);
                leadDto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
                leadDto.setCreateBy("webchat-widget");
                leadDto.setStartedDate(new Date());
                leadDto.setSubjectType("Lead");
                leadDto.setSubjectId(conv.getLeadId());
                leadDto.setPayload(payload.toJSONString());
                activityEventProducer.produce(leadDto);
            }
        }
        catch (Exception e)
        {
            log.warn("produceIdentifiedActivity failed: {}", e.getMessage());
        }
    }

    private void produceClosedActivity(CrmWebchatConversation conv)
    {
        if (conv == null || (!StringUtils.hasText(conv.getContactId()) && !StringUtils.hasText(conv.getLeadId())))
        {
            return;
        }
        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:closed:" + conv.getId());
            dto.setEventType(WebchatActivityEventTypes.CONVERSATION_CLOSED);
            dto.setEventName(WebchatActivityEventTypes.CONVERSATION_CLOSED_NAME);
            dto.setEventCategory(WebchatActivityEventTypes.CONVERSATION_CLOSED_CATEGORY);
            dto.setImportance(WebchatActivityEventTypes.CONVERSATION_CLOSED_IMPORTANCE);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_WIDGET);
            dto.setCreateBy("webchat-widget");
            dto.setStartedDate(new Date());
            fillSubject(dto, conv);
            JSONObject payload = new JSONObject();
            payload.put("conversation_id", conv.getId());
            payload.put("visitor_id", conv.getVisitorId());
            dto.setPayload(payload.toJSONString());
            activityEventProducer.produce(dto);
        }
        catch (Exception ignored)
        {
        }
    }

    private static void fillSubject(ActivityEventDTO dto, CrmWebchatConversation conv)
    {
        if (StringUtils.hasText(conv.getContactId()))
        {
            dto.setSubjectType("Contact");
            dto.setSubjectId(conv.getContactId());
        }
        else
        {
            dto.setSubjectType("Lead");
            dto.setSubjectId(conv.getLeadId());
        }
    }

    private static String truncate(String value, int max)
    {
        if (!StringUtils.hasText(value))
        {
            return null;
        }
        String t = value.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }

    private static final class BoundSession
    {
        private final String tenantId;
        private final String visitorId;

        private BoundSession(String tenantId, String visitorId)
        {
            this.tenantId = tenantId;
            this.visitorId = visitorId;
        }
    }

    private static final class ResolveLeadResult
    {
        private final String leadId;
        private final boolean created;

        private ResolveLeadResult(String leadId, boolean created)
        {
            this.leadId = leadId;
            this.created = created;
        }
    }
}
