package com.ruoyi.crm.whatsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;

/**
 * 调用 sevencrm {@code POST /internal/whatsapp/media/persist/} 上传入站媒体到租户 OSS。
 */
@Component
public class WhatsAppMediaPersistBffClient
{
    private static final Logger log = LoggerFactory.getLogger(WhatsAppMediaPersistBffClient.class);

    private final WhatsAppMediaPersistProperties properties;
    private final RestTemplate restTemplate = new RestTemplate();

    public WhatsAppMediaPersistBffClient(WhatsAppMediaPersistProperties properties)
    {
        this.properties = properties;
    }

    public boolean isReady()
    {
        return properties.isReady();
    }

    /**
     * @return durable https mediaUrl
     */
    public String upload(String tenantId, byte[] body, String contentType, String filename)
    {
        if (!isReady())
        {
            throw new IllegalStateException("WhatsApp media persist BFF is not configured");
        }
        if (body == null || body.length == 0)
        {
            throw new IllegalArgumentException("empty media body");
        }

        String base = properties.getNextAppUrl().trim().replaceAll("/+$", "");
        String url = base + "/internal/whatsapp/media/persist/";

        String name = StringUtils.hasText(filename) ? filename.trim() : "file";
        MediaType partType;
        try
        {
            partType = StringUtils.hasText(contentType)
                    ? MediaType.parseMediaType(contentType.trim())
                    : MediaType.APPLICATION_OCTET_STREAM;
        }
        catch (Exception e)
        {
            partType = MediaType.APPLICATION_OCTET_STREAM;
        }

        ByteArrayResource resource = new ByteArrayResource(body)
        {
            @Override
            public String getFilename()
            {
                return name;
            }
        };

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(partType);
        HttpEntity<ByteArrayResource> filePart = new HttpEntity<>(resource, partHeaders);

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", filePart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set(RecordTriggerBffSecretValidator.HEADER_NAME, properties.getBffSecret().trim());
        headers.set("tenant", tenantId.trim());

        ResponseEntity<String> response = restTemplate.postForEntity(
                url, new HttpEntity<>(form, headers), String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
        {
            throw new IllegalStateException("BFF media persist HTTP "
                    + response.getStatusCodeValue() + " body="
                    + truncate(response.getBody(), 300));
        }

        JSONObject parsed = JSON.parseObject(response.getBody());
        Integer code = parsed.getInteger("code");
        if (code == null || code != 200)
        {
            throw new IllegalStateException("BFF media persist failed: "
                    + parsed.getString("msg"));
        }
        JSONObject data = parsed.getJSONObject("data");
        String mediaUrl = data != null ? data.getString("mediaUrl") : null;
        if (!StringUtils.hasText(mediaUrl))
        {
            throw new IllegalStateException("BFF media persist returned empty mediaUrl");
        }
        log.info("WhatsApp media persist BFF ok tenant={} bytes={} urlHost={}",
                tenantId, body.length, hostOf(mediaUrl));
        return mediaUrl.trim();
    }

    private static String truncate(String s, int max)
    {
        if (s == null)
        {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max) + "...";
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
}
