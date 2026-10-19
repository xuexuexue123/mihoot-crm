package com.ruoyi.crm.service.wecom.contactgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.mapper.CrmContactMapper;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.mapper.CrmWecomContactGroupMapper;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;
import com.ruoyi.crm.service.support.TenantDataSourceSupport;
import com.ruoyi.crm.wecom.WecomAccessTokenService;

/**
 * 处理 change_external_chat：补绑 chat_id / dismiss。
 */
@Service
public class WecomContactGroupCallbackHandler
{
    private static final Logger log = LoggerFactory.getLogger(WecomContactGroupCallbackHandler.class);

    private static final Pattern EVENT = Pattern.compile("<Event><!\\[CDATA\\[(.*?)\\]\\]></Event>|<Event>(.*?)</Event>");
    private static final Pattern CHANGE_TYPE = Pattern.compile(
            "<ChangeType><!\\[CDATA\\[(.*?)\\]\\]></ChangeType>|<ChangeType>(.*?)</ChangeType>");
    private static final Pattern CHAT_ID = Pattern.compile(
            "<ChatId><!\\[CDATA\\[(.*?)\\]\\]></ChatId>|<ChatId>(.*?)</ChatId>");
    private static final Pattern OWNER = Pattern.compile(
            "<Owner><!\\[CDATA\\[(.*?)\\]\\]></Owner>|<Owner>(.*?)</Owner>");
    private static final Pattern NAME = Pattern.compile(
            "<Name><!\\[CDATA\\[(.*?)\\]\\]></Name>|<Name>(.*?)</Name>");
    private static final Pattern MEM_USER = Pattern.compile(
            "<MemUser(?:List)?>([\\s\\S]*?)</MemUser(?:List)?>");
    private static final Pattern ITEM = Pattern.compile("<Item>([\\s\\S]*?)</Item>");
    private static final Pattern USER_ID = Pattern.compile(
            "<UserID><!\\[CDATA\\[(.*?)\\]\\]></UserID>|<UserID>(.*?)</UserID>");
    private static final Pattern TYPE = Pattern.compile(
            "<Type><!\\[CDATA\\[(.*?)\\]\\]></Type>|<Type>(.*?)</Type>");

    @Autowired
    private WecomContactGroupService contactGroupService;

    @Autowired
    private CrmWecomContactGroupMapper contactGroupMapper;

    @Autowired
    private CrmContactMapper crmContactMapper;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private ICrmWecomGroupChatService groupChatService;

    public void handleFromCallbackXml(String plainXml)
    {
        if (StringUtils.isBlank(plainXml))
        {
            return;
        }
        try
        {
            String event = extract(EVENT, plainXml);
            if (!"change_external_chat".equals(event))
            {
                return;
            }
            String changeType = extract(CHANGE_TYPE, plainXml);
            String chatId = extract(CHAT_ID, plainXml);
            if (StringUtils.isBlank(chatId))
            {
                return;
            }
            if ("dismiss".equals(changeType))
            {
                contactGroupService.markDismissedByChatId(chatId);
                return;
            }
            if ("create".equals(changeType))
            {
                handleCreate(chatId, plainXml);
                return;
            }
            if ("update".equals(changeType))
            {
                handleUpdate(chatId, plainXml);
            }
        }
        catch (Exception e)
        {
            log.warn("contact group callback handler failed: {}", e.getMessage());
        }
    }

    private void handleCreate(String chatId, String plainXml)
    {
        String owner = extract(OWNER, plainXml);
        String name = extract(NAME, plainXml);
        List<String> externals = extractExternalUserids(plainXml);
        boolean enriched = false;

        if (StringUtils.isBlank(owner) || externals.isEmpty())
        {
            GroupChatEnrichment enrichment = enrichFromGroupChatGet(chatId);
            if (enrichment != null)
            {
                enriched = true;
                if (StringUtils.isBlank(owner) && StringUtils.isNotBlank(enrichment.owner))
                {
                    owner = enrichment.owner;
                }
                if (StringUtils.isBlank(name) && StringUtils.isNotBlank(enrichment.name))
                {
                    name = enrichment.name;
                }
                if (externals.isEmpty() && enrichment.externals != null && !enrichment.externals.isEmpty())
                {
                    externals = enrichment.externals;
                }
            }
        }

        log.info(
                "contact-group create callback: changeType=create chatId={} owner={} externalUserids={} enriched={} groupName={}",
                chatId, owner, externals, enriched, name);
        contactGroupService.bindFromCallback(owner, chatId, externals, name);
    }

    private void handleUpdate(String chatId, String plainXml)
    {
        String tenantId = TenantDataSourceSupport.currentTenantKey();
        if (StringUtils.isBlank(tenantId))
        {
            return;
        }
        CrmWecomContactGroup group = contactGroupMapper.selectByChatId(tenantId, chatId);
        if (group == null)
        {
            return;
        }
        CrmContact contact = crmContactMapper.selectCrmContactByContactId(group.getContactId());
        if (contact == null || StringUtils.isBlank(contact.getWecomExternalUserid()))
        {
            return;
        }
        List<String> externals = extractExternalUserids(plainXml);
        if (externals.isEmpty())
        {
            GroupChatEnrichment enrichment = enrichFromGroupChatGet(chatId);
            if (enrichment == null || enrichment.externals == null)
            {
                log.warn(
                        "contact-group update skip dismiss check: chatId={} contactId={} reason=no_member_list_and_get_failed",
                        chatId, group.getContactId());
                return;
            }
            externals = enrichment.externals;
        }
        if (!externals.contains(contact.getWecomExternalUserid()))
        {
            log.info(
                    "contact-group update: customer left chat chatId={} contactId={} externalUserid={}",
                    chatId, group.getContactId(), contact.getWecomExternalUserid());
            contactGroupService.markDismissedByChatId(chatId);
        }
    }

    /**
     * 官方 create 回调常仅有 ChatId；用 groupchat/get 补全 Owner / type=2 成员。
     */
    private GroupChatEnrichment enrichFromGroupChatGet(String chatId)
    {
        try
        {
            CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
            if (cfg == null || StringUtils.isBlank(cfg.getCorpId()))
            {
                log.warn("contact-group groupchat/get skip: wecom config missing chatId={}", chatId);
                return null;
            }
            String accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
            WecomGroupChatDetailResponse resp = groupChatService.getGroupChat(accessToken, chatId, 0);
            if (resp == null)
            {
                log.warn("contact-group groupchat/get empty response chatId={}", chatId);
                return null;
            }
            if (resp.getErrcode() != null && resp.getErrcode() != 0)
            {
                log.warn("contact-group groupchat/get failed chatId={} errcode={} errmsg={}",
                        chatId, resp.getErrcode(), resp.getErrmsg());
                return null;
            }
            WecomGroupChatDetailResponse.WecomGroupChatDetail detail = resp.getGroupChat();
            if (detail == null)
            {
                log.warn("contact-group groupchat/get missing group_chat chatId={}", chatId);
                return null;
            }
            GroupChatEnrichment enrichment = new GroupChatEnrichment();
            enrichment.owner = detail.getOwner();
            enrichment.name = detail.getName();
            enrichment.externals = new ArrayList<>();
            if (detail.getMemberList() != null)
            {
                for (WecomGroupChatDetailResponse.GroupChatMember member : detail.getMemberList())
                {
                    if (member != null && member.getType() != null && member.getType() == 2
                            && StringUtils.isNotBlank(member.getUserid()))
                    {
                        enrichment.externals.add(member.getUserid().trim());
                    }
                }
            }
            return enrichment;
        }
        catch (Exception e)
        {
            log.warn("contact-group groupchat/get exception chatId={}: {}", chatId, e.getMessage());
            return null;
        }
    }

    private List<String> extractExternalUserids(String plainXml)
    {
        List<String> result = new ArrayList<>();
        Matcher itemMatcher = ITEM.matcher(plainXml);
        while (itemMatcher.find())
        {
            String item = itemMatcher.group(1);
            String type = extract(TYPE, item);
            String userId = extract(USER_ID, item);
            if ("2".equals(type) && StringUtils.isNotBlank(userId))
            {
                result.add(userId);
            }
        }
        if (result.isEmpty())
        {
            Matcher mem = MEM_USER.matcher(plainXml);
            if (mem.find())
            {
                Matcher uid = USER_ID.matcher(mem.group(1));
                while (uid.find())
                {
                    String v = firstNonEmpty(uid.group(1), uid.group(2));
                    if (StringUtils.isNotBlank(v))
                    {
                        result.add(v);
                    }
                }
            }
        }
        return result;
    }

    private static String extract(Pattern p, String xml)
    {
        Matcher m = p.matcher(xml);
        if (!m.find())
        {
            return null;
        }
        return firstNonEmpty(m.group(1), m.groupCount() >= 2 ? m.group(2) : null);
    }

    private static String firstNonEmpty(String a, String b)
    {
        if (StringUtils.isNotBlank(a))
        {
            return a.trim();
        }
        if (StringUtils.isNotBlank(b))
        {
            return b.trim();
        }
        return null;
    }

    private static final class GroupChatEnrichment
    {
        String owner;
        String name;
        List<String> externals;
    }
}
