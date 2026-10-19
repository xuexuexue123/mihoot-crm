package com.ruoyi.crm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWebchatConversation;
import com.ruoyi.crm.domain.CrmWebchatVisitor;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWebchatConversationMapper;
import com.ruoyi.crm.mapper.CrmWebchatVisitorMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmWebchatContactService;
import com.ruoyi.crm.service.ICrmWebchatConversationService;
import com.ruoyi.crm.service.support.IdentityNormalizer;
import com.ruoyi.crm.webchat.WebchatActivityEventTypes;
import com.ruoyi.crm.webchat.WebchatIdentityResolutionSupport;
import com.ruoyi.crm.webchat.realtime.WebchatInboxEventPublisher;
import com.ruoyi.crm.webchat.realtime.WebchatInboxSsePayloads;

@Service
public class CrmWebchatContactServiceImpl implements ICrmWebchatContactService
{
    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private CrmWebchatConversationMapper conversationMapper;

    @Autowired
    private CrmWebchatVisitorMapper visitorMapper;

    @Autowired
    private ICrmWebchatConversationService conversationService;

    @Autowired
    private WebchatIdentityResolutionSupport identityResolutionSupport;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @Autowired(required = false)
    private WebchatInboxEventPublisher inboxEventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> linkContact(String tenantId, Long conversationId, String contactId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId is required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(contactId))
        {
            throw new ServiceException("contactId is required", HttpStatus.BAD_REQUEST);
        }
        CrmWebchatConversation conv = conversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        conversationService.assertOwnedByCurrentUser(conv);

        String cid = contactId.trim();
        identityResolutionSupport.bindVisitorIfNeeded(conv.getVisitorId(), cid,
                WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_INBOX);
        conversationMapper.updateContactLead(conversationId, tenantId, cid, null);
        visitorMapper.updateSubjects(tenantId, conv.getVisitorId(), cid, null);
        conv.setContactId(cid);
        conv.setLeadId(null);

        try
        {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setChannel("webchat");
            dto.setChannelEventId("webchat:linked:" + conv.getVisitorId() + ":" + cid);
            dto.setEventType(WebchatActivityEventTypes.LINKED);
            dto.setEventName(WebchatActivityEventTypes.LINKED_NAME);
            dto.setEventCategory(WebchatActivityEventTypes.LINKED_CATEGORY);
            dto.setImportance(WebchatActivityEventTypes.LINKED_IMPORTANCE);
            dto.setSource(WebchatIdentityResolutionSupport.SOURCE_WEBCHAT_INBOX);
            dto.setCreateBy("webchat-inbox");
            dto.setStartedDate(new Date());
            dto.setSubjectType("Contact");
            dto.setSubjectId(cid);
            dto.setMessageData(WebchatActivityEventTypes.LINKED_NAME);
            activityEventProducer.produce(dto);
        }
        catch (Exception ignored)
        {
        }

        Map<String, Object> data = buildContactLinkResponse(conv, cid, null);
        publishContactLinkSse(tenantId, conv, cid, null);
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> unlinkContact(String tenantId, Long conversationId, String contactId)
    {
        if (conversationId == null)
        {
            throw new ServiceException("conversationId is required", HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasText(contactId))
        {
            throw new ServiceException("contactId is required", HttpStatus.BAD_REQUEST);
        }
        CrmWebchatConversation conv = conversationMapper.selectById(conversationId);
        if (conv == null || !tenantId.equals(conv.getTenantId()))
        {
            throw new ServiceException("Conversation not found", HttpStatus.NOT_FOUND);
        }
        conversationService.assertOwnedByCurrentUser(conv);

        identityService.unbindIdentity(contactId.trim(), IdentityNormalizer.TYPE_WEBCHAT_VISITOR_ID,
                conv.getVisitorId());
        String leadId = conv.getLeadId();
        conversationMapper.updateContactLead(conversationId, tenantId, null, leadId);
        CrmWebchatVisitor visitor = visitorMapper.selectByVisitorId(tenantId, conv.getVisitorId());
        String visitorLeadId = visitor != null && StringUtils.hasText(visitor.getLeadId())
                ? visitor.getLeadId() : leadId;
        visitorMapper.updateSubjects(tenantId, conv.getVisitorId(), null, visitorLeadId);
        conv.setContactId(null);
        conv.setLeadId(leadId);

        Map<String, Object> data = buildContactLinkResponse(conv, null, leadId);
        publishContactLinkSse(tenantId, conv, null, leadId);
        return data;
    }

    private Map<String, Object> buildContactLinkResponse(CrmWebchatConversation conv, String contactId,
            String leadId)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("id", conv.getId());
        data.put("visitorId", conv.getVisitorId());
        data.put("contactId", contactId);
        data.put("leadId", leadId);
        conversationService.enrichContactDisplay(contactId, data);
        conversationService.enrichLeadDisplay(leadId, data);
        return data;
    }

    private void publishContactLinkSse(String tenantId, CrmWebchatConversation conv, String contactId,
            String leadId)
    {
        if (inboxEventPublisher == null || conv == null)
        {
            return;
        }
        Map<String, Object> partial = WebchatInboxSsePayloads.conversationPartialFrom(
                conv.getId(), conv.getUnreadCount(), conv.getStatus(),
                conv.getUserId(), conv.getOwner(), conv.getAssignedAt(), conv.getHostedByAi());
        partial.put("contactId", contactId);
        partial.put("leadId", leadId);
        conversationService.enrichContactDisplay(contactId, partial);
        conversationService.enrichLeadDisplay(leadId, partial);
        inboxEventPublisher.publishConversationUpdated(tenantId,
                WebchatInboxSsePayloads.conversationUpdated(tenantId, partial, true));
    }
}
