package com.ruoyi.crm.wecom;

/**
 * 企业微信素材上传 API 响应
 */
public class WecomMediaUploadResult
{
    private int errcode;
    private String errmsg;
    private String type;
    private String mediaId;
    private Long createdAt;

    public int getErrcode()
    {
        return errcode;
    }

    public void setErrcode(int errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }

    public Long getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }

    public boolean isSuccess()
    {
        return errcode == 0;
    }
}
