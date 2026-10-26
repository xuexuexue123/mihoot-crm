package com.ruoyi.crm.wechatmp.dto;

/**
 * 永久素材图文篇章（news_item）
 */
public class WechatMpMaterialNewsItem
{
    private String title;

    private String author;

    private String digest;

    private String content;

    private String contentSourceUrl;

    private String thumbMediaId;

    private Integer showCoverPic;

    private String url;

    private String thumbUrl;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getDigest()
    {
        return digest;
    }

    public void setDigest(String digest)
    {
        this.digest = digest;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContentSourceUrl()
    {
        return contentSourceUrl;
    }

    public void setContentSourceUrl(String contentSourceUrl)
    {
        this.contentSourceUrl = contentSourceUrl;
    }

    public String getThumbMediaId()
    {
        return thumbMediaId;
    }

    public void setThumbMediaId(String thumbMediaId)
    {
        this.thumbMediaId = thumbMediaId;
    }

    public Integer getShowCoverPic()
    {
        return showCoverPic;
    }

    public void setShowCoverPic(Integer showCoverPic)
    {
        this.showCoverPic = showCoverPic;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getThumbUrl()
    {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl)
    {
        this.thumbUrl = thumbUrl;
    }
}
