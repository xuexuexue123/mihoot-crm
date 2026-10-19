package com.ruoyi.crm.mobile.push;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.google.auth.oauth2.GoogleCredentials;

/**
 * Firebase Cloud Messaging HTTP v1 客户端。
 */
@Component
public class FcmPushClient
{
    private static final Logger log = LoggerFactory.getLogger(FcmPushClient.class);
    private static final String FCM_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

    @Autowired
    private MobilePushProperties properties;

    private volatile GoogleCredentials cachedCredentials;
    private volatile String cachedCredentialsSource;

    /**
     * @return true 发送成功；false 发送失败但 token 仍可能有效
     */
    public boolean sendNotification(String deviceToken, String title, String body,
            Map<String, String> data, int badge, String platform)
    {
        if (!properties.isReady() || !StringUtils.hasText(deviceToken))
        {
            return false;
        }
        String accessToken = obtainAccessToken();
        if (!StringUtils.hasText(accessToken))
        {
            return false;
        }

        String url = "https://fcm.googleapis.com/v1/projects/"
                + properties.getFcm().getProjectId().trim()
                + "/messages:send";

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("token", deviceToken.trim());

        Map<String, String> notification = new LinkedHashMap<>();
        notification.put("title", title != null ? title : "");
        notification.put("body", body != null ? body : "");
        message.put("notification", notification);

        if (data != null && !data.isEmpty())
        {
            message.put("data", data);
        }

        Map<String, Object> apnsPayload = new LinkedHashMap<>();
        Map<String, Object> aps = new LinkedHashMap<>();
        aps.put("badge", badge);
        Map<String, String> alert = new LinkedHashMap<>();
        alert.put("title", title != null ? title : "");
        alert.put("body", body != null ? body : "");
        aps.put("alert", alert);
        apnsPayload.put("aps", aps);
        Map<String, Object> apns = new LinkedHashMap<>();
        apns.put("payload", apnsPayload);
        message.put("apns", apns);

        Map<String, Object> androidNotification = new LinkedHashMap<>();
        androidNotification.put("notification_count", badge);
        Map<String, Object> android = new LinkedHashMap<>();
        android.put("notification", androidNotification);
        message.put("android", android);

        Map<String, Object> envelope = Collections.singletonMap("message", message);
        String jsonBody = JSON.toJSONString(envelope);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10_000)
                .setSocketTimeout(15_000)
                .build();

        try (CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build())
        {
            HttpPost post = new HttpPost(url);
            post.setHeader("Authorization", "Bearer " + accessToken);
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(post))
            {
                int status = response.getStatusLine().getStatusCode();
                String responseBody = response.getEntity() != null
                        ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : "";
                if (status >= 200 && status < 300)
                {
                    return true;
                }
                if (isUnregisteredToken(status, responseBody))
                {
                    throw new FcmUnregisteredTokenException(deviceToken.trim());
                }
                log.warn("FCM send failed status={} platform={} body={}", status, platform, truncate(responseBody));
                return false;
            }
        }
        catch (FcmUnregisteredTokenException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            log.warn("FCM send error platform={}: {}", platform, e.getMessage());
            return false;
        }
    }

    static boolean isUnregisteredToken(int status, String responseBody)
    {
        if (status != 404 && status != 400)
        {
            return false;
        }
        if (!StringUtils.hasText(responseBody))
        {
            return false;
        }
        String upper = responseBody.toUpperCase();
        return upper.contains("UNREGISTERED")
                || upper.contains("NOT_FOUND")
                || upper.contains("INVALID_REGISTRATION")
                || upper.contains("REGISTRATION_TOKEN_NOT_REGISTERED");
    }

    private String obtainAccessToken()
    {
        try
        {
            GoogleCredentials credentials = resolveCredentials();
            credentials.refreshIfExpired();
            if (credentials.getAccessToken() == null)
            {
                return null;
            }
            return credentials.getAccessToken().getTokenValue();
        }
        catch (Exception e)
        {
            log.warn("FCM access token refresh failed: {}", e.getMessage());
            return null;
        }
    }

    private GoogleCredentials resolveCredentials() throws Exception
    {
        String source = properties.getFcm().getCredentials().trim();
        if (!StringUtils.hasText(source))
        {
            throw new IllegalStateException("FCM credentials not configured");
        }
        if (cachedCredentials != null && source.equals(cachedCredentialsSource))
        {
            return cachedCredentials;
        }
        synchronized (this)
        {
            if (cachedCredentials != null && source.equals(cachedCredentialsSource))
            {
                return cachedCredentials;
            }
            GoogleCredentials creds;
            if (source.startsWith("{") && source.contains("\"type\""))
            {
                try (InputStream in = new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)))
                {
                    creds = GoogleCredentials.fromStream(in).createScoped(FCM_SCOPE);
                }
            }
            else
            {
                try (InputStream in = new FileInputStream(source))
                {
                    creds = GoogleCredentials.fromStream(in).createScoped(FCM_SCOPE);
                }
            }
            cachedCredentials = creds;
            cachedCredentialsSource = source;
            return creds;
        }
    }

    private static String truncate(String s)
    {
        if (s == null)
        {
            return "";
        }
        return s.length() > 300 ? s.substring(0, 300) + "…" : s;
    }

    static class FcmUnregisteredTokenException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        private final String pushToken;

        FcmUnregisteredTokenException(String pushToken)
        {
            super("FCM token unregistered");
            this.pushToken = pushToken;
        }

        String getPushToken()
        {
            return pushToken;
        }
    }
}
