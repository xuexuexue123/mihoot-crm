package com.ruoyi.web.controller.crm;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.ActivityEventDTO;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.event.MessengerActivityEventTypes;
import com.ruoyi.crm.mapper.CrmMessengerConversationMapper;
import com.ruoyi.crm.messenger.MessengerIdentityResolutionSupport;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.support.IdentityNormalizer;

@RestController
@RequestMapping("/crm/messenger")
public class CrmMessengerContactController extends BaseController
{
    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private CrmMessengerConversationMapper conversationMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @PostMapping("/contact/link")
    public AjaxResult linkContact(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        String psid = (String) body.get("psid");
        String contactId = (String) body.get("contactId");

        if (psid == null || psid.isEmpty())
        {
            return AjaxResult.error(400, "psid is required");
        }
        if (contactId == null || contactId.isEmpty())
        {
            return AjaxResult.error(400, "contactId is required");
        }

        try
        {
            identityService.bindIdentity(contactId, IdentityNormalizer.TYPE_MESSENGER_PSID, psid,
                    MessengerIdentityResolutionSupport.SOURCE_MESSENGER_SIDEBAR);
            conversationMapper.updateContactId(tenant, psid, contactId);

            try
            {
                ActivityEventDTO dto = new ActivityEventDTO();
                dto.setChannel("messenger");
                dto.setChannelEventId("messenger:linked:" + psid + ":" + contactId);
                dto.setEventType(MessengerActivityEventTypes.LINKED);
                dto.setEventName(MessengerActivityEventTypes.LINKED_NAME);
                dto.setEventCategory(MessengerActivityEventTypes.LINKED_CATEGORY);
                dto.setImportance(MessengerActivityEventTypes.LINKED_IMPORTANCE);
                dto.setSource(MessengerIdentityResolutionSupport.SOURCE_MESSENGER_SIDEBAR);
                dto.setCreateBy("messenger-sidebar");
                dto.setStartedDate(new Date());
                dto.setSubjectType("Contact");
                dto.setSubjectId(contactId);
                dto.setMessageData(MessengerActivityEventTypes.LINKED_NAME);
                activityEventProducer.produce(dto);
            }
            catch (Exception ignored)
            {
                // activity 非关键路径
            }

            return AjaxResult.success("Messenger contact linked");
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : 400, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @DeleteMapping("/contact/link")
    public AjaxResult unlinkContact(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return AjaxResult.error(400, "Missing tenant header");
        }
        String psid = (String) body.get("psid");
        String contactId = (String) body.get("contactId");

        if (psid == null || psid.isEmpty())
        {
            return AjaxResult.error(400, "psid is required");
        }
        if (contactId == null || contactId.isEmpty())
        {
            return AjaxResult.error(400, "contactId is required");
        }

        identityService.unbindIdentity(contactId, IdentityNormalizer.TYPE_MESSENGER_PSID, psid);
        conversationMapper.updateContactId(tenant, psid, null);
        return AjaxResult.success("Messenger contact unlinked");
    }
}
