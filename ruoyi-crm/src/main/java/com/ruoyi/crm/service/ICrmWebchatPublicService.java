package com.ruoyi.crm.service;

import java.util.Map;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 公开 Widget API 编排（token → 切库后执行）。
 */
public interface ICrmWebchatPublicService
{
    String resolveAndBindTenant(String websiteToken);

    Map<String, Object> getConfig(String websiteToken, String origin, String referer, String clientIp);

    Map<String, Object> createSession(String websiteToken, String origin, String referer, String clientIp,
                                      String visitorId, String sessionId, String userAgent);

    Map<String, Object> openConversation(String sessionToken, String websiteToken, String origin, String referer);

    Map<String, Object> listMessages(String sessionToken, String websiteToken, Long conversationId,
                                     Long beforeId, Integer pageSize);

    Map<String, Object> postMessage(String sessionToken, String websiteToken, String origin, String referer,
                                    String clientIp, Long conversationId, String content,
                                    String messageType, String clientMessageId);

    Map<String, Object> identify(String sessionToken, String websiteToken, String origin, String referer,
                                 String clientIp, Long conversationId, String displayName,
                                 String mobilePhone, String email, String whatsapp);

    Map<String, Object> closeConversation(String sessionToken, String websiteToken, Long conversationId);

    SseEmitter subscribeConversationEvents(String sessionToken, String websiteToken, Long conversationId);
}
