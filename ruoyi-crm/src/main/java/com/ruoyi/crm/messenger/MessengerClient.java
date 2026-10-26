package com.ruoyi.crm.messenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.messenger.MessengerMediaFetchResult;
import com.ruoyi.crm.domain.dto.messenger.MessengerPageInfo;
import com.ruoyi.crm.domain.dto.messenger.MessengerSendResult;

/**
 * Meta Messenger Platform Graph API 客户端。
 *
 * <p>Base URL: {@code messenger.api-base-url}（默认 https://graph.facebook.com/v25.0）
 * <p>认证方式：Bearer {page_access_token}
 */
@Component
public class MessengerClient
{
    private static final Logger log = LoggerFactory.getLogger(MessengerClient.class);

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    public MessengerClient(@Value("${messenger.api-base-url:https://graph.facebook.com/v25.0}") String apiBaseUrl)
    {
        this.apiBaseUrl = apiBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    public MessengerPageInfo getPageInfo(String pageId, String accessToken)
    {
        try
        {
            String url = apiBaseUrl + "/" + pageId + "?fields=name,id";
            JSONObject response = getJson(url, accessToken);

            if (response.containsKey("error"))
            {
                log.error("Failed to get page info for {}: {}", pageId, response.getJSONObject("error"));
                return null;
            }

            MessengerPageInfo info = new MessengerPageInfo();
            info.setPageId(response.getString("id") != null ? response.getString("id") : pageId);
            info.setPageName(response.getString("name"));
            return info;
        }
        catch (Exception e)
        {
            log.error("Failed to get page info for {}: {}", pageId, e.getMessage());
            return null;
        }
    }

    public MessengerSendResult sendTextMessage(String pageId, String psid, String text,
                                               String accessToken)
    {
        return sendTextMessage(pageId, psid, text, "RESPONSE", null, accessToken);
    }

    public MessengerSendResult sendTextMessage(String pageId, String psid, String text,
                                               String messagingType, String tag, String accessToken)
    {
        JSONObject body = baseSendBody(psid, messagingType, tag);
        JSONObject message = new JSONObject();
        message.put("text", text);
        body.put("message", message);
        return postMessages(pageId, psid, accessToken, body);
    }

    /**
     * 发送附件（image / file / audio / video）。{@code attachmentType} 为 Graph attachment.type。
     */
    public MessengerSendResult sendAttachmentMessage(String pageId, String psid, String attachmentType,
                                                     String mediaUrl, String filename, String caption,
                                                     String messagingType, String tag, String accessToken)
    {
        JSONObject body = baseSendBody(psid, messagingType, tag);
        JSONObject message = new JSONObject();
        JSONObject attachment = new JSONObject();
        attachment.put("type", attachmentType);
        JSONObject payload = new JSONObject();
        payload.put("url", mediaUrl);
        payload.put("is_reusable", true);
        if (StringUtils.hasText(filename) && "file".equals(attachmentType))
        {
            // Graph 不强制 filename；部分场景可忽略
        }
        attachment.put("payload", payload);
        message.put("attachment", attachment);
        if (StringUtils.hasText(caption) && ("image".equals(attachmentType) || "video".equals(attachmentType)))
        {
            // Messenger image/video 不支持独立 caption 字段于 attachment；文本需另发。忽略 caption 落库即可。
        }
        body.put("message", message);
        return postMessages(pageId, psid, accessToken, body);
    }

    /** Best-effort mark_seen；失败由调用方忽略。 */
    public boolean markSeen(String pageId, String psid, String accessToken)
    {
        JSONObject body = new JSONObject();
        JSONObject recipient = new JSONObject();
        recipient.put("id", psid);
        body.put("recipient", recipient);
        body.put("sender_action", "mark_seen");
        String url = apiBaseUrl + "/" + pageId + "/messages";
        try
        {
            JSONObject response = postJson(url, accessToken, body);
            if (response.containsKey("error"))
            {
                log.warn("Messenger mark_seen failed pageId={} psid={} err={}",
                        pageId, psid, response.getJSONObject("error"));
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            log.warn("Messenger mark_seen error pageId={} psid={}: {}", pageId, psid, e.getMessage());
            return false;
        }
    }

    /** 拉取媒体：Meta CDN 需 Page Token；OSS 等公网 URL 不要带 Bearer（会破坏签名）。 */
    public MessengerMediaFetchResult downloadMediaUrl(String mediaUrl, String accessToken)
    {
        if (!StringUtils.hasText(mediaUrl))
        {
            return MessengerMediaFetchResult.failure("mediaUrl is required");
        }
        try
        {
            // 必须用 URI.create，避免 RestTemplate 对已签名 query 二次 encode → SignatureDoesNotMatch
            java.net.URI uri = java.net.URI.create(mediaUrl.trim());
            HttpHeaders headers = new HttpHeaders();
            if (isMetaMediaHost(mediaUrl) && StringUtils.hasText(accessToken))
            {
                headers.setBearerAuth(accessToken);
            }
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    uri, HttpMethod.GET, request, byte[].class);
            byte[] body = response.getBody();
            if (body == null || body.length == 0)
            {
                return MessengerMediaFetchResult.failure("empty media body");
            }
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (response.getHeaders().getContentType() != null)
            {
                contentType = response.getHeaders().getContentType().toString();
            }
            return MessengerMediaFetchResult.success(contentType, body);
        }
        catch (HttpStatusCodeException e)
        {
            return MessengerMediaFetchResult.failure("HTTP " + e.getStatusCode() + ": "
                    + e.getResponseBodyAsString());
        }
        catch (Exception e)
        {
            return MessengerMediaFetchResult.failure(e.getMessage());
        }
    }

    /** Meta Graph / CDN 主机才需要 Page Access Token。 */
    public static boolean isMetaMediaHost(String mediaUrl)
    {
        try
        {
            String host = java.net.URI.create(mediaUrl.trim()).getHost();
            if (host == null)
            {
                return false;
            }
            String h = host.toLowerCase();
            return h.equals("graph.facebook.com")
                    || h.endsWith(".facebook.com")
                    || h.endsWith(".fbcdn.net")
                    || h.endsWith(".fbsbx.com")
                    || h.contains("facebook.com")
                    || h.contains("fbcdn.net");
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static JSONObject baseSendBody(String psid, String messagingType, String tag)
    {
        JSONObject body = new JSONObject();
        JSONObject recipient = new JSONObject();
        recipient.put("id", psid);
        body.put("recipient", recipient);
        String type = StringUtils.hasText(messagingType) ? messagingType : "RESPONSE";
        body.put("messaging_type", type);
        if ("MESSAGE_TAG".equals(type) && StringUtils.hasText(tag))
        {
            body.put("tag", tag);
        }
        return body;
    }

    private MessengerSendResult postMessages(String pageId, String psid, String accessToken, JSONObject body)
    {
        String url = apiBaseUrl + "/" + pageId + "/messages";
        log.info("Messenger Graph send start pageId={} psid={} url={} messagingType={}",
                pageId, psid, url, body.getString("messaging_type"));
        try
        {
            JSONObject response = postJson(url, accessToken, body);
            if (response.containsKey("error"))
            {
                JSONObject err = response.getJSONObject("error");
                String msg = err != null ? err.getString("message") : "unknown error";
                log.error("Messenger Graph send failed pageId={} psid={} error={}", pageId, psid, msg);
                return MessengerSendResult.failure(psid, msg);
            }
            String mid = response.getString("message_id");
            if (mid == null)
            {
                mid = response.getString("mid");
            }
            log.info("Messenger Graph send ok pageId={} psid={} messageId={}", pageId, psid, mid);
            return MessengerSendResult.success(mid, psid);
        }
        catch (HttpStatusCodeException e)
        {
            String errBody = e.getResponseBodyAsString();
            log.error("Messenger Graph send HTTP error pageId={} psid={} status={} body={}",
                    pageId, psid, e.getStatusCode(), errBody);
            return MessengerSendResult.failure(psid, errBody != null ? errBody : e.getMessage());
        }
        catch (Exception e)
        {
            log.error("Messenger Graph send error pageId={} psid={}: {}", pageId, psid, e.getMessage());
            return MessengerSendResult.failure(psid, e.getMessage());
        }
    }

    private JSONObject postJson(String url, String accessToken, JSONObject body)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(body.toJSONString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return JSON.parseObject(response.getBody());
    }

    private JSONObject getJson(String url, String accessToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, request, String.class);
        return JSON.parseObject(response.getBody());
    }
}
