package com.ruoyi.crm.wechatmp.dto;

/**
 * 永久素材上传结果
 */
public class WechatMpMaterialUploadResult
{
    private String mediaId;

    private String url;

    public WechatMpMaterialUploadResult()
    {
    }

    public WechatMpMaterialUploadResult(String mediaId, String url)
    {
        this.mediaId = mediaId;
        this.url = url;
    }

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
