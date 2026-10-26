package com.ruoyi.crm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWhatsappConfig;
import com.ruoyi.crm.domain.CrmWhatsappMessage;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;
import com.ruoyi.crm.mapper.CrmWhatsappConfigMapper;
import com.ruoyi.crm.mapper.CrmWhatsappMessageMapper;
import com.ruoyi.crm.service.ICrmWhatsappMediaService;
import com.ruoyi.crm.service.support.TokenEncryptor;
import com.ruoyi.crm.whatsapp.WhatsAppClient;

@Service
public class CrmWhatsappMediaServiceImpl implements ICrmWhatsappMediaService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappMediaServiceImpl.class);

    @Autowired
    private CrmWhatsappConfigMapper configMapper;

    @Autowired
    private CrmWhatsappMessageMapper messageMapper;

    @Autowired
    private WhatsAppClient whatsappClient;

    @Autowired
    private TokenEncryptor tokenEncryptor;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public WhatsAppMediaFetchResult fetchMedia(String tenantId, String mediaId)
    {
        if (!StringUtils.hasText(tenantId))
        {
            throw new ServiceException("Missing tenant", 400);
        }
        if (!StringUtils.hasText(mediaId) || mediaId.contains("/") || mediaId.contains(".."))
        {
            throw new ServiceException("Invalid mediaId", 400);
        }

        CrmWhatsappMessage owned = messageMapper.selectByMediaId(tenantId, mediaId.trim());
        if (owned == null)
        {
            log.warn("WhatsApp media not found for tenant={} mediaId={}", tenantId, mediaId);
            throw new ServiceException("Media not found", 404);
        }

        if (CrmWhatsappInboundMediaPersistService.isDurableMediaUrl(owned.getMediaUrl()))
        {
            WhatsAppMediaFetchResult fromOss = fetchFromDurableUrl(owned);
            if (fromOss.isSuccess())
            {
                return fromOss;
            }
            log.warn("WhatsApp media durable URL fetch failed tenant={} mediaId={} err={}; falling back to Meta",
                    tenantId, mediaId, fromOss.getErrorMessage());
        }

        CrmWhatsappConfig config = configMapper.selectByTenantId(tenantId);
        if (config == null || config.getAccessTokenEnc() == null)
        {
            throw new ServiceException("WhatsApp configuration not found", 400);
        }
        String accessToken = tokenEncryptor.decrypt(config.getAccessTokenEnc());

        WhatsAppMediaFetchResult result = whatsappClient.downloadMedia(mediaId.trim(), accessToken);
        if (!result.isSuccess())
        {
            return result;
        }
        String mime = result.getContentType();
        if (!StringUtils.hasText(mime) && StringUtils.hasText(owned.getMediaMimeType()))
        {
            mime = owned.getMediaMimeType();
        }
        return WhatsAppMediaFetchResult.success(mime, result.getBody(), owned.getFilename());
    }

    private WhatsAppMediaFetchResult fetchFromDurableUrl(CrmWhatsappMessage owned)
    {
        String url = owned.getMediaUrl().trim();
        try
        {
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, byte[].class);
            byte[] body = response.getBody();
            if (body == null || body.length == 0)
            {
                return WhatsAppMediaFetchResult.failure("empty body from durable media URL");
            }
            String mime = null;
            if (response.getHeaders().getContentType() != null)
            {
                mime = response.getHeaders().getContentType().toString();
            }
            if (!StringUtils.hasText(mime))
            {
                mime = owned.getMediaMimeType();
            }
            log.info("WhatsApp media served from durable URL tenant={} mediaId={} bytes={}",
                    owned.getTenantId(), owned.getMediaId(), body.length);
            return WhatsAppMediaFetchResult.success(mime, body, owned.getFilename());
        }
        catch (Exception e)
        {
            return WhatsAppMediaFetchResult.failure("durable URL fetch error: " + e.getMessage());
        }
    }
}
