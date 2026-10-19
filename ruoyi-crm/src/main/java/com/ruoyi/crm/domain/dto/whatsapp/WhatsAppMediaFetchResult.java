package com.ruoyi.crm.domain.dto.whatsapp;

/**
 * WhatsApp Cloud API 媒体下载结果（二进制 + MIME + 可选文件名）。
 */
public class WhatsAppMediaFetchResult
{
    private final boolean success;
    private final String contentType;
    private final byte[] body;
    private final String filename;
    private final String errorMessage;

    private WhatsAppMediaFetchResult(boolean success, String contentType, byte[] body,
                                     String filename, String errorMessage)
    {
        this.success = success;
        this.contentType = contentType;
        this.body = body;
        this.filename = filename;
        this.errorMessage = errorMessage;
    }

    public static WhatsAppMediaFetchResult success(String contentType, byte[] body)
    {
        return success(contentType, body, null);
    }

    public static WhatsAppMediaFetchResult success(String contentType, byte[] body, String filename)
    {
        return new WhatsAppMediaFetchResult(true, contentType, body, filename, null);
    }

    public static WhatsAppMediaFetchResult failure(String errorMessage)
    {
        return new WhatsAppMediaFetchResult(false, null, null, null, errorMessage);
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getContentType()
    {
        return contentType;
    }

    public byte[] getBody()
    {
        return body;
    }

    public String getFilename()
    {
        return filename;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }
}
