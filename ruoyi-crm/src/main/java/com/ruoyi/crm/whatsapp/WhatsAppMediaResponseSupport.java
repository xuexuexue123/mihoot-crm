package com.ruoyi.crm.whatsapp;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

/**
 * WhatsApp 媒体代理响应头辅助（文件名清洗、是否按附件下载）。
 */
public final class WhatsAppMediaResponseSupport
{
    private WhatsAppMediaResponseSupport()
    {
    }

    public static boolean isLikelyDocument(MediaType mediaType, String filename)
    {
        if (StringUtils.hasText(filename))
        {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx")
                    || lower.endsWith(".xls") || lower.endsWith(".xlsx") || lower.endsWith(".ppt")
                    || lower.endsWith(".pptx") || lower.endsWith(".zip") || lower.endsWith(".rar")
                    || lower.endsWith(".txt") || lower.endsWith(".csv"))
            {
                return true;
            }
        }
        if (mediaType == null)
        {
            return false;
        }
        String type = mediaType.getType();
        if ("image".equals(type) || "audio".equals(type) || "video".equals(type))
        {
            return false;
        }
        return MediaType.APPLICATION_PDF.includes(mediaType)
                || MediaType.APPLICATION_OCTET_STREAM.includes(mediaType)
                || "application".equals(type);
    }

    public static String sanitizeFilename(String filename)
    {
        if (filename == null)
        {
            return "file";
        }
        String cleaned = filename.replace("\"", "").replace("\r", "").replace("\n", "").trim();
        if (cleaned.isEmpty())
        {
            return "file";
        }
        int slash = Math.max(cleaned.lastIndexOf('/'), cleaned.lastIndexOf('\\'));
        if (slash >= 0 && slash < cleaned.length() - 1)
        {
            cleaned = cleaned.substring(slash + 1);
        }
        return cleaned;
    }
}
