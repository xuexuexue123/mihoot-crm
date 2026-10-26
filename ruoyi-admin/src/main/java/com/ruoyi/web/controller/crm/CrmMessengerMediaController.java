package com.ruoyi.web.controller.crm;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.messenger.MessengerMediaFetchResult;
import com.ruoyi.crm.service.ICrmMessengerMediaService;

/**
 * Messenger 媒体代理：用 Page Token 拉取 Meta 临时 URL，供 Inbox 预览。
 */
@RestController
@RequestMapping("/crm/messenger")
public class CrmMessengerMediaController
{
    private static final Logger log = LoggerFactory.getLogger(CrmMessengerMediaController.class);

    @Autowired
    private ICrmMessengerMediaService mediaService;

    @PreAuthorize("@ss.hasPermi('crm:messenger:conversations')")
    @GetMapping("/media")
    public ResponseEntity<?> getMedia(
            @RequestHeader(value = "tenant", required = false) String tenant,
            @RequestParam(value = "messageId", required = false) Long messageId,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "download", required = false, defaultValue = "false") boolean download)
    {
        if (tenant == null || tenant.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Missing tenant header");
        }
        try
        {
            MessengerMediaFetchResult result;
            if (messageId != null)
            {
                // OSS 等公网 URL：302 直链，避免服务端再拉一次破坏签名
                String stored = mediaService.resolveStoredMediaUrl(tenant, messageId);
                if (StringUtils.hasText(stored)
                        && (stored.startsWith("https://") || stored.startsWith("http://"))
                        && !com.ruoyi.crm.messenger.MessengerClient.isMetaMediaHost(stored))
                {
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .header(HttpHeaders.LOCATION, stored)
                            .cacheControl(org.springframework.http.CacheControl.noStore())
                            .build();
                }
                result = mediaService.fetchByMessageId(tenant, messageId);
            }
            else if (StringUtils.hasText(url))
            {
                result = mediaService.fetchByMediaUrl(tenant, url);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("messageId or url is required");
            }
            if (!result.isSuccess())
            {
                log.error("GET /crm/messenger/media failed tenant={} err={}", tenant, result.getErrorMessage());
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(result.getErrorMessage() != null ? result.getErrorMessage() : "download failed");
            }

            MediaType mediaType;
            try
            {
                mediaType = MediaType.parseMediaType(
                        result.getContentType() != null ? result.getContentType()
                                : MediaType.APPLICATION_OCTET_STREAM_VALUE);
            }
            catch (Exception e)
            {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setCacheControl("private, max-age=3600");
            headers.setContentLength(result.getBody().length);

            boolean forceAttachment = download || isLikelyDocument(mediaType);
            if (StringUtils.hasText(result.getFilename()) || forceAttachment)
            {
                String name = StringUtils.hasText(result.getFilename())
                        ? sanitizeFilename(result.getFilename())
                        : "messenger-media";
                ContentDisposition.Builder cd = forceAttachment
                        ? ContentDisposition.attachment()
                        : ContentDisposition.inline();
                headers.setContentDisposition(cd.filename(name, StandardCharsets.UTF_8).build());
            }

            return new ResponseEntity<>(result.getBody(), headers, HttpStatus.OK);
        }
        catch (ServiceException e)
        {
            HttpStatus status = e.getCode() != null && e.getCode() == 404
                    ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage());
        }
    }

    private static boolean isLikelyDocument(MediaType mediaType)
    {
        if (mediaType == null)
        {
            return true;
        }
        String type = mediaType.getType();
        return !"image".equalsIgnoreCase(type) && !"audio".equalsIgnoreCase(type) && !"video".equalsIgnoreCase(type);
    }

    private static String sanitizeFilename(String filename)
    {
        String s = filename.replaceAll("[\\\\/\\r\\n\\t\\0]", "_").trim();
        return s.isEmpty() ? "file" : s;
    }
}
