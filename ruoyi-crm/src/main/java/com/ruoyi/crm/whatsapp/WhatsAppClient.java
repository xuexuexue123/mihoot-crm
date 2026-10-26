package com.ruoyi.crm.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppPhoneInfo;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppSendResult;

/**
 * Meta WhatsApp Business Platform Cloud API 客户端。
 *
 * <p>Base URL: https://graph.facebook.com/v22.0
 * <p>认证方式：Bearer {access_token}
 * <p>参考 WecomTencentApiClient 模式，使用 RestTemplate 封装 HTTP 调用。
 */
@Component
public class WhatsAppClient
{
    private static final Logger log = LoggerFactory.getLogger(WhatsAppClient.class);

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    public WhatsAppClient(@Value("${whatsapp.api-base-url}") String apiBaseUrl)
    {
        this.apiBaseUrl = apiBaseUrl;
        this.restTemplate = new RestTemplate();
    }

    // ==================== 发送消息 ====================

    public WhatsAppSendResult sendTextMessage(String phoneNumberId, String waId, String text,
                                              String accessToken, boolean previewUrl)
    {
        JSONObject body = new JSONObject();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", waId);
        body.put("type", "text");
        JSONObject textObj = new JSONObject();
        textObj.put("body", text);
        textObj.put("preview_url", previewUrl);
        body.put("text", textObj);

        return sendRequest(phoneNumberId, accessToken, body, waId);
    }

    public WhatsAppSendResult sendImageMessage(String phoneNumberId, String waId, String mediaId,
                                               String caption, String accessToken)
    {
        return sendImageMessage(phoneNumberId, waId, mediaId, null, caption, accessToken);
    }

    public WhatsAppSendResult sendImageMessage(String phoneNumberId, String waId, String mediaId,
                                               String mediaUrl, String caption, String accessToken)
    {
        JSONObject body = new JSONObject();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", waId);
        body.put("type", "image");
        JSONObject imageObj = new JSONObject();
        putMediaIdOrLink(imageObj, mediaId, mediaUrl, "sendImageMessage", phoneNumberId, waId);
        if (caption != null && !caption.isEmpty())
        {
            imageObj.put("caption", caption);
        }
        body.put("image", imageObj);

        return sendRequest(phoneNumberId, accessToken, body, waId);
    }

    public WhatsAppSendResult sendDocumentMessage(String phoneNumberId, String waId, String mediaId,
                                                  String filename, String caption, String accessToken)
    {
        return sendDocumentMessage(phoneNumberId, waId, mediaId, null, filename, caption, accessToken);
    }

    public WhatsAppSendResult sendDocumentMessage(String phoneNumberId, String waId, String mediaId,
                                                  String mediaUrl, String filename, String caption,
                                                  String accessToken)
    {
        JSONObject body = new JSONObject();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", waId);
        body.put("type", "document");
        JSONObject docObj = new JSONObject();
        putMediaIdOrLink(docObj, mediaId, mediaUrl, "sendDocumentMessage", phoneNumberId, waId);
        if (filename != null && !filename.isEmpty())
        {
            docObj.put("filename", filename);
        }
        if (caption != null && !caption.isEmpty())
        {
            docObj.put("caption", caption);
        }
        body.put("document", docObj);

        return sendRequest(phoneNumberId, accessToken, body, waId);
    }

    /**
     * Meta 要求 image/document 必须有 id（已上传 media_id）或 link（公开 HTTPS URL）之一。
     * 优先 mediaId。
     */
    private void putMediaIdOrLink(JSONObject mediaObj, String mediaId, String mediaUrl,
                                  String method, String phoneNumberId, String waId)
    {
        boolean hasId = mediaId != null && !mediaId.isEmpty();
        boolean hasLink = mediaUrl != null && !mediaUrl.isEmpty();
        if (hasId)
        {
            mediaObj.put("id", mediaId);
            if (hasLink)
            {
                log.debug("{} using mediaId (mediaUrl also provided but ignored) phoneNumberId={} waId={}",
                        method, phoneNumberId, waId);
            }
            return;
        }
        if (hasLink)
        {
            mediaObj.put("link", mediaUrl);
            log.info("{} using mediaUrl as link phoneNumberId={} waId={}", method, phoneNumberId, waId);
            return;
        }
        log.warn("{} missing both mediaId and mediaUrl phoneNumberId={} waId={}",
                method, phoneNumberId, waId);
    }

    public WhatsAppSendResult sendTemplateMessage(String phoneNumberId, String waId,
                                                   String templateName, String language,
                                                   JSONArray components, String accessToken)
    {
        JSONObject body = new JSONObject();
        body.put("messaging_product", "whatsapp");
        body.put("recipient_type", "individual");
        body.put("to", waId);
        body.put("type", "template");
        JSONObject templateObj = new JSONObject();
        templateObj.put("name", templateName);
        JSONObject langObj = new JSONObject();
        langObj.put("code", language);
        templateObj.put("language", langObj);
        if (components != null && !components.isEmpty())
        {
            templateObj.put("components", components);
        }
        else
        {
            log.warn("sendTemplateMessage components empty/null template={} lang={} waId={} "
                            + "(media HEADER templates require header parameter with image.id or image.link)",
                    templateName, language, waId);
        }
        body.put("template", templateObj);

        log.info("WhatsApp sendTemplate phoneNumberId={} waId={} template={} lang={} componentsSummary={}",
                phoneNumberId, waId, templateName, language, summarizeTemplateComponents(components));

        return sendRequest(phoneNumberId, accessToken, body, waId);
    }

    private WhatsAppSendResult sendRequest(String phoneNumberId, String accessToken,
                                           JSONObject body, String waId)
    {
        String msgType = body != null ? body.getString("type") : null;
        String url = apiBaseUrl + "/" + phoneNumberId + "/messages";
        log.info("WhatsApp Graph send start type={} phoneNumberId={} waId={} url={} payload={}",
                msgType, phoneNumberId, waId, url, sanitizePayloadForLog(body));
        try
        {
            JSONObject response = postJson(url, accessToken, body);

            if (response.containsKey("messages"))
            {
                JSONArray messages = response.getJSONArray("messages");
                if (messages != null && !messages.isEmpty())
                {
                    String messageId = messages.getJSONObject(0).getString("id");
                    log.info("WhatsApp Graph send ok type={} waId={} messageId={}", msgType, waId, messageId);
                    return WhatsAppSendResult.success(messageId, waId);
                }
            }
            if (response.containsKey("error"))
            {
                return failureFromMetaError(waId, msgType, body, response.getJSONObject("error"), null);
            }
            log.error("WhatsApp Graph unexpected response type={} waId={} body={}",
                    msgType, waId, truncate(response.toJSONString(), 2000));
            return WhatsAppSendResult.failure(waId, "Unknown Meta API response: " + response.toJSONString());
        }
        catch (HttpStatusCodeException e)
        {
            String responseBody = e.getResponseBodyAsString();
            JSONObject errorObj = extractMetaError(responseBody);
            if (errorObj != null)
            {
                return failureFromMetaError(waId, msgType, body, errorObj, e.getStatusCode().value());
            }
            log.error("WhatsApp Graph HTTP error type={} waId={} status={} response={} requestPayload={}",
                    msgType, waId, e.getStatusCode().value(), truncate(responseBody, 2000),
                    sanitizePayloadForLog(body));
            return WhatsAppSendResult.failure(waId,
                    "Meta API error: " + e.getStatusCode().value() + " " + e.getStatusText()
                            + ": " + truncate(responseBody, 1500));
        }
        catch (Exception e)
        {
            log.error("WhatsApp Graph send failed type={} waId={} requestPayload={}",
                    msgType, waId, sanitizePayloadForLog(body), e);
            return WhatsAppSendResult.failure(waId, e.getMessage());
        }
    }

    private WhatsAppSendResult failureFromMetaError(String waId, String msgType, JSONObject requestBody,
                                                    JSONObject error, Integer httpStatus)
    {
        int errorCode = error.getIntValue("code");
        String errorMsg = error.getString("message");
        String errorType = error.getString("type");
        String fbtraceId = error.getString("fbtrace_id");
        JSONObject errorData = error.getJSONObject("error_data");
        String details = errorData != null ? errorData.getString("details") : null;

        log.error("Meta WhatsApp API error httpStatus={} type={} waId={} code={} errorType={} message={} "
                        + "details={} fbtrace_id={} requestPayload={}",
                httpStatus, msgType, waId, errorCode, errorType, errorMsg, details, fbtraceId,
                sanitizePayloadForLog(requestBody));

        StringBuilder sb = new StringBuilder();
        sb.append("Meta API error");
        if (httpStatus != null)
        {
            sb.append(" ").append(httpStatus);
        }
        sb.append(" [").append(errorCode).append("]: ").append(errorMsg);
        if (details != null && !details.isEmpty())
        {
            sb.append(" | details=").append(details);
        }
        if (fbtraceId != null && !fbtraceId.isEmpty())
        {
            sb.append(" | fbtrace_id=").append(fbtraceId);
        }
        return WhatsAppSendResult.failure(waId, sb.toString());
    }

    private static JSONObject extractMetaError(String responseBody)
    {
        if (responseBody == null || responseBody.isEmpty())
        {
            return null;
        }
        try
        {
            JSONObject parsed = JSON.parseObject(responseBody);
            if (parsed != null && parsed.containsKey("error"))
            {
                return parsed.getJSONObject("error");
            }
        }
        catch (Exception ignored)
        {
            // not JSON
        }
        return null;
    }

    /**
     * 日志用摘要：突出模板 header 是否带 media id/link，避免刷整段超长文本。
     */
    static String summarizeTemplateComponents(JSONArray components)
    {
        if (components == null || components.isEmpty())
        {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < components.size(); i++)
        {
            if (i > 0)
            {
                sb.append(", ");
            }
            JSONObject c = components.getJSONObject(i);
            if (c == null)
            {
                sb.append("null");
                continue;
            }
            String type = c.getString("type");
            sb.append("{type=").append(type);
            JSONArray params = c.getJSONArray("parameters");
            if (params != null)
            {
                sb.append(", params=");
                for (int j = 0; j < params.size(); j++)
                {
                    JSONObject p = params.getJSONObject(j);
                    if (p == null)
                    {
                        continue;
                    }
                    String pType = p.getString("type");
                    sb.append(pType);
                    for (String mediaKey : new String[] {"image", "video", "document"})
                    {
                        JSONObject media = p.getJSONObject(mediaKey);
                        if (media != null)
                        {
                            boolean hasId = media.getString("id") != null && !media.getString("id").isEmpty();
                            boolean hasLink = media.getString("link") != null && !media.getString("link").isEmpty();
                            sb.append("(").append(mediaKey)
                                    .append(" id=").append(hasId)
                                    .append(" link=").append(hasLink).append(")");
                        }
                    }
                    if ("text".equals(pType))
                    {
                        String text = p.getString("text");
                        sb.append("(len=").append(text != null ? text.length() : 0).append(")");
                    }
                    if (j < params.size() - 1)
                    {
                        sb.append("+");
                    }
                }
            }
            sb.append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String sanitizePayloadForLog(JSONObject body)
    {
        if (body == null)
        {
            return "null";
        }
        try
        {
            return truncate(body.toJSONString(), 4000);
        }
        catch (Exception e)
        {
            return "<unserializable>";
        }
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
    }

    // ==================== 媒体下载 ====================

    /**
     * Meta 两步下载：先查 media 元数据拿临时 url + mime_type，再带 Bearer 拉二进制。
     */
    public WhatsAppMediaFetchResult downloadMedia(String mediaId, String accessToken)
    {
        if (mediaId == null || mediaId.isEmpty())
        {
            return WhatsAppMediaFetchResult.failure("mediaId is required");
        }
        try
        {
            String metaUrl = apiBaseUrl + "/" + mediaId;
            JSONObject meta = getJson(metaUrl, accessToken);
            if (meta == null)
            {
                return WhatsAppMediaFetchResult.failure("empty media metadata");
            }
            if (meta.containsKey("error"))
            {
                JSONObject error = meta.getJSONObject("error");
                String msg = error != null ? error.getString("message") : "unknown error";
                log.error("WhatsApp media metadata error mediaId={} message={}", mediaId, msg);
                return WhatsAppMediaFetchResult.failure("Meta media metadata error: " + msg);
            }
            String downloadUrl = meta.getString("url");
            if (downloadUrl == null || downloadUrl.isEmpty())
            {
                return WhatsAppMediaFetchResult.failure("media url missing in Meta response");
            }
            String mimeType = meta.getString("mime_type");
            log.info("WhatsApp media download start mediaId={} mimeType={} urlHost={}",
                    mediaId, mimeType, hostOf(downloadUrl));

            ResponseEntity<byte[]> response = getBytes(downloadUrl, accessToken);
            byte[] body = response.getBody();
            if (body == null || body.length == 0)
            {
                return WhatsAppMediaFetchResult.failure("empty media body");
            }
            if (mimeType == null || mimeType.isEmpty())
            {
                MediaType ct = response.getHeaders().getContentType();
                mimeType = ct != null ? ct.toString() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            log.info("WhatsApp media download ok mediaId={} bytes={} contentType={}",
                    mediaId, body.length, mimeType);
            return WhatsAppMediaFetchResult.success(mimeType, body);
        }
        catch (HttpStatusCodeException e)
        {
            log.error("WhatsApp media download HTTP error mediaId={} status={} body={}",
                    mediaId, e.getStatusCode().value(), truncate(e.getResponseBodyAsString(), 500));
            return WhatsAppMediaFetchResult.failure(
                    "Meta media download HTTP " + e.getStatusCode().value() + ": " + e.getStatusText());
        }
        catch (Exception e)
        {
            log.error("Failed to download WhatsApp media {}: {}", mediaId, e.getMessage(), e);
            return WhatsAppMediaFetchResult.failure("Failed to download media: " + e.getMessage());
        }
    }

    public String getMediaUrl(String mediaId, String accessToken)
    {
        try
        {
            String url = apiBaseUrl + "/" + mediaId;
            JSONObject response = getJson(url, accessToken);
            if (response != null && response.containsKey("url"))
            {
                return response.getString("url");
            }
            return null;
        }
        catch (Exception e)
        {
            log.error("Failed to get media URL for {}: {}", mediaId, e.getMessage());
            return null;
        }
    }

    private static String hostOf(String url)
    {
        try
        {
            return java.net.URI.create(url).getHost();
        }
        catch (Exception e)
        {
            return "?";
        }
    }

    // ==================== 模板查询 ====================

    public JSONObject getMessageTemplates(String wabaId, String accessToken,
                                          String status, String language, int limit)
    {
        try
        {
            StringBuilder urlBuilder = new StringBuilder(apiBaseUrl)
                    .append("/").append(wabaId).append("/message_templates")
                    .append("?limit=").append(limit);
            if (status != null && !status.isEmpty())
            {
                urlBuilder.append("&status=").append(status);
            }
            if (language != null && !language.isEmpty())
            {
                urlBuilder.append("&language=").append(language);
            }

            return getJson(urlBuilder.toString(), accessToken);
        }
        catch (Exception e)
        {
            log.error("Failed to get message templates for waba {}: {}", wabaId, e.getMessage());
            throw new RuntimeException("Failed to get message templates", e);
        }
    }

    // ==================== 电话号码信息 ====================

    public WhatsAppPhoneInfo getPhoneNumberInfo(String phoneNumberId, String accessToken)
    {
        try
        {
            String url = apiBaseUrl + "/" + phoneNumberId;
            JSONObject response = getJson(url, accessToken);

            WhatsAppPhoneInfo info = new WhatsAppPhoneInfo();
            info.setPhoneNumberId(phoneNumberId);
            info.setDisplayPhoneNumber(response.getString("display_phone_number"));
            info.setVerifiedName(response.getString("verified_name"));
            info.setCodeVerificationStatus(response.getString("code_verification_status"));
            info.setQualityRating(response.getString("quality_rating"));
            return info;
        }
        catch (Exception e)
        {
            log.error("Failed to get phone number info for {}: {}", phoneNumberId, e.getMessage());
            return null;
        }
    }

    // ==================== HTTP 工具方法 ====================

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

    private ResponseEntity<byte[]> getBytes(String url, String accessToken)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, request, byte[].class);
    }
}
