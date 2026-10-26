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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;
import com.ruoyi.crm.service.ICrmWhatsappMediaService;
import com.ruoyi.crm.whatsapp.WhatsAppMediaResponseSupport;

/**
 * WhatsApp 媒体代理：图片可 inline；文档建议 attachment 下载。
 */
@RestController
@RequestMapping("/crm/whatsapp")
public class CrmWhatsappMediaController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWhatsappMediaController.class);

    @Autowired
    private ICrmWhatsappMediaService mediaService;

    @PreAuthorize("@ss.hasPermi('crm:whatsapp:conversations')")
    @GetMapping("/media/{mediaId}")
    public ResponseEntity<?> getMedia(
            @PathVariable String mediaId,
            @RequestHeader(value = "tenant", required = false) String tenant,
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
            WhatsAppMediaFetchResult result = mediaService.fetchMedia(tenant, mediaId);
            if (!result.isSuccess())
            {
                log.error("GET /crm/whatsapp/media/{} failed tenant={} err={}",
                        mediaId, tenant, result.getErrorMessage());
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

            boolean forceAttachment = download
                    || WhatsAppMediaResponseSupport.isLikelyDocument(mediaType, result.getFilename());
            if (StringUtils.hasText(result.getFilename()) || forceAttachment)
            {
                String name = StringUtils.hasText(result.getFilename())
                        ? WhatsAppMediaResponseSupport.sanitizeFilename(result.getFilename())
                        : ("whatsapp-media-" + mediaId);
                ContentDisposition.Builder cd = forceAttachment
                        ? ContentDisposition.attachment()
                        : ContentDisposition.inline();
                headers.setContentDisposition(cd.filename(name, StandardCharsets.UTF_8).build());
            }

            log.info("GET /crm/whatsapp/media/{} ok tenant={} bytes={} contentType={} filename={}",
                    mediaId, tenant, result.getBody().length, mediaType, result.getFilename());
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
}
