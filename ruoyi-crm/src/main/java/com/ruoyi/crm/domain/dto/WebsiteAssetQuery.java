package com.ruoyi.crm.domain.dto;

/**
 * 网站素材列表查询
 */
public class WebsiteAssetQuery
{
    private String mediaType;
    private String fileTitle;
    private Boolean includeDeleted;

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getFileTitle()
    {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle)
    {
        this.fileTitle = fileTitle;
    }

    public Boolean getIncludeDeleted()
    {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted)
    {
        this.includeDeleted = includeDeleted;
    }
}
