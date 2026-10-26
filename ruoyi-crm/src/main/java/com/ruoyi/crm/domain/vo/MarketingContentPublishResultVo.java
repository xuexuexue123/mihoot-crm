package com.ruoyi.crm.domain.vo;

/**
 * 发布接口响应
 */
public class MarketingContentPublishResultVo
{
    private String contentId;
    private MarketingContentPublishVo publish;

    public MarketingContentPublishResultVo()
    {
    }

    public MarketingContentPublishResultVo(String contentId, MarketingContentPublishVo publish)
    {
        this.contentId = contentId;
        this.publish = publish;
    }

    public String getContentId()
    {
        return contentId;
    }

    public void setContentId(String contentId)
    {
        this.contentId = contentId;
    }

    public MarketingContentPublishVo getPublish()
    {
        return publish;
    }

    public void setPublish(MarketingContentPublishVo publish)
    {
        this.publish = publish;
    }
}
