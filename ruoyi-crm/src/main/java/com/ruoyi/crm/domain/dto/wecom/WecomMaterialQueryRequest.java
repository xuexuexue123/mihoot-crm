package com.ruoyi.crm.domain.dto.wecom;

/**
 * 素材列表查询请求 DTO
 */
public class WecomMaterialQueryRequest
{
    private Integer pageNum;
    private Integer pageSize;
    private String mediaType;
    private String status;
    private String fileTitle;
    private Integer attachmentType;

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFileTitle()
    {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle)
    {
        this.fileTitle = fileTitle;
    }

    public Integer getAttachmentType()
    {
        return attachmentType;
    }

    public void setAttachmentType(Integer attachmentType)
    {
        this.attachmentType = attachmentType;
    }
}
