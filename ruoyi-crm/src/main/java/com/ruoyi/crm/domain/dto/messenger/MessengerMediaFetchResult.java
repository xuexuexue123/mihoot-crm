package com.ruoyi.crm.domain.dto.messenger;

/**
 * Messenger 媒体代理下载结果。
 */
public class MessengerMediaFetchResult
{
    private final boolean success;
    private final String contentType;
    private final byte[] body;
    private final String filename;
    private final String errorMessage;

    private MessengerMediaFetchResult(boolean success, String contentType, byte[] body,
                                      String filename, String errorMessage)
    {
        this.success = success;
        this.contentType = contentType;
        this.body = body;
        this.filename = filename;
        this.errorMessage = errorMessage;
    }

    public static MessengerMediaFetchResult success(String contentType, byte[] body)
    {
        return success(contentType, body, null);
    }

    public static MessengerMediaFetchResult success(String contentType, byte[] body, String filename)
    {
        return new MessengerMediaFetchResult(true, contentType, body, filename, null);
    }

    public static MessengerMediaFetchResult failure(String errorMessage)
    {
        return new MessengerMediaFetchResult(false, null, null, null, errorMessage);
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
