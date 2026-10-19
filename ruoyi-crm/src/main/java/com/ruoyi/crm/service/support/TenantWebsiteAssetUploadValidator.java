package com.ruoyi.crm.service.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmTenantWebsiteAsset;
import com.ruoyi.tenant.config.TenantWebsiteProperties;

/**
 * 网站素材上传校验：扩展名、MIME、魔数、大小
 */
@Component
public class TenantWebsiteAssetUploadValidator
{
    private static final int PAYLOAD_TOO_LARGE = 413;

    private static final String[] IMAGE_EXTENSIONS = { "png", "jpg", "jpeg", "gif", "webp" };
    private static final String[] VIDEO_EXTENSIONS = { "mp4" };

    private static final Set<String> IMAGE_MIME_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "image/png", "image/jpeg", "image/gif", "image/webp")));
    private static final Set<String> VIDEO_MIME_TYPES = Collections.unmodifiableSet(
            Collections.singleton("video/mp4"));

    @Autowired
    private TenantWebsiteProperties tenantWebsiteProperties;

    public DetectedMedia detectAndValidate(MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("上传文件不能为空", HttpStatus.BAD_REQUEST);
        }

        String originalName = file.getOriginalFilename();
        if (StringUtils.isBlank(originalName))
        {
            throw new ServiceException("文件名不能为空", HttpStatus.BAD_REQUEST);
        }

        String extension = normalizeExtension(FilenameUtils.getExtension(originalName));
        DetectedMedia media = detectMediaType(extension);
        if (media == null)
        {
            throw new ServiceException("不支持的文件类型", HttpStatus.BAD_REQUEST);
        }

        validateSize(file.getSize(), media);
        validateMimeType(file.getContentType(), media);
        validateMagicBytes(file, media);

        return media;
    }

    public String[] allowedExtensionsFor(DetectedMedia media)
    {
        return media == DetectedMedia.IMAGE ? IMAGE_EXTENSIONS : VIDEO_EXTENSIONS;
    }

    private DetectedMedia detectMediaType(String extension)
    {
        if (Arrays.asList(IMAGE_EXTENSIONS).contains(extension))
        {
            return DetectedMedia.IMAGE;
        }
        if (Arrays.asList(VIDEO_EXTENSIONS).contains(extension))
        {
            return DetectedMedia.VIDEO;
        }
        return null;
    }

    private void validateSize(long size, DetectedMedia media)
    {
        long maxBytes = media == DetectedMedia.IMAGE
                ? tenantWebsiteProperties.getAsset().getImageMaxBytes()
                : tenantWebsiteProperties.getAsset().getVideoMaxBytes();
        if (size > maxBytes)
        {
            throw new ServiceException("文件大小超过限制", PAYLOAD_TOO_LARGE);
        }
    }

    private void validateMimeType(String contentType, DetectedMedia media)
    {
        if (StringUtils.isBlank(contentType))
        {
            throw new ServiceException("无法识别文件 MIME 类型", HttpStatus.BAD_REQUEST);
        }
        String normalized = contentType.split(";")[0].trim().toLowerCase(Locale.ROOT);
        Set<String> allowed = media == DetectedMedia.IMAGE ? IMAGE_MIME_TYPES : VIDEO_MIME_TYPES;
        if (!allowed.contains(normalized))
        {
            throw new ServiceException("不支持的 MIME 类型", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateMagicBytes(MultipartFile file, DetectedMedia media)
    {
        byte[] header = readHeader(file, 12);
        if (media == DetectedMedia.IMAGE)
        {
            if (isPng(header) || isJpeg(header) || isGif(header) || isWebp(header))
            {
                return;
            }
        }
        else if (isMp4(header))
        {
            return;
        }
        throw new ServiceException("文件内容与类型不匹配", HttpStatus.BAD_REQUEST);
    }

    private byte[] readHeader(MultipartFile file, int length)
    {
        byte[] buffer = new byte[length];
        try (InputStream in = file.getInputStream())
        {
            int read = in.read(buffer);
            if (read <= 0)
            {
                throw new ServiceException("无法读取文件内容", HttpStatus.BAD_REQUEST);
            }
            if (read < length)
            {
                return Arrays.copyOf(buffer, read);
            }
            return buffer;
        }
        catch (IOException ex)
        {
            throw new ServiceException("无法读取文件内容", HttpStatus.BAD_REQUEST);
        }
    }

    private static boolean isPng(byte[] header)
    {
        return header.length >= 8
                && header[0] == (byte) 0x89
                && header[1] == 0x50
                && header[2] == 0x4E
                && header[3] == 0x47;
    }

    private static boolean isJpeg(byte[] header)
    {
        return header.length >= 3
                && header[0] == (byte) 0xFF
                && header[1] == (byte) 0xD8
                && header[2] == (byte) 0xFF;
    }

    private static boolean isGif(byte[] header)
    {
        return header.length >= 6
                && header[0] == 'G'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == '8';
    }

    private static boolean isWebp(byte[] header)
    {
        return header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P';
    }

    private static boolean isMp4(byte[] header)
    {
        return header.length >= 8
                && header[4] == 'f'
                && header[5] == 't'
                && header[6] == 'y'
                && header[7] == 'p';
    }

    private static String normalizeExtension(String extension)
    {
        if (extension == null)
        {
            return "";
        }
        return extension.trim().toLowerCase(Locale.ROOT);
    }

    public enum DetectedMedia
    {
        IMAGE(CrmTenantWebsiteAsset.MEDIA_IMAGE),
        VIDEO(CrmTenantWebsiteAsset.MEDIA_VIDEO);

        private final String mediaType;

        DetectedMedia(String mediaType)
        {
            this.mediaType = mediaType;
        }

        public String getMediaType()
        {
            return mediaType;
        }
    }
}
