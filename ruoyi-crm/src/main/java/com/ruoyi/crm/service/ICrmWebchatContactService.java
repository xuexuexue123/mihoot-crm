package com.ruoyi.crm.service;

import java.util.Map;

public interface ICrmWebchatContactService
{
    Map<String, Object> linkContact(String tenantId, Long conversationId, String contactId);

    Map<String, Object> unlinkContact(String tenantId, Long conversationId, String contactId);
}
