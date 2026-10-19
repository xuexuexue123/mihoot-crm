package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 朋友圈记录项。
 */
public class WecomMomentItem
{
    @JsonProperty("moment_id")
    private String momentId;

    private String creator;

    @JsonProperty("create_time")
    private Long createTime;

    @JsonProperty("create_type")
    private Integer createType;

    @JsonProperty("visible_type")
    private Integer visibleType;

    private WecomMomentText text;
    private List<WecomMomentImage> image;
    private WecomMomentVideo video;
    private WecomMomentLink link;
    private WecomMomentLocation location;

    public String getMomentId()
    {
        return momentId;
    }

    public void setMomentId(String momentId)
    {
        this.momentId = momentId;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public Long getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Long createTime)
    {
        this.createTime = createTime;
    }

    public Integer getCreateType()
    {
        return createType;
    }

    public void setCreateType(Integer createType)
    {
        this.createType = createType;
    }

    public Integer getVisibleType()
    {
        return visibleType;
    }

    public void setVisibleType(Integer visibleType)
    {
        this.visibleType = visibleType;
    }

    public WecomMomentText getText()
    {
        return text;
    }

    public void setText(WecomMomentText text)
    {
        this.text = text;
    }

    public List<WecomMomentImage> getImage()
    {
        return image;
    }

    public void setImage(List<WecomMomentImage> image)
    {
        this.image = image;
    }

    public WecomMomentVideo getVideo()
    {
        return video;
    }

    public void setVideo(WecomMomentVideo video)
    {
        this.video = video;
    }

    public WecomMomentLink getLink()
    {
        return link;
    }

    public void setLink(WecomMomentLink link)
    {
        this.link = link;
    }

    public WecomMomentLocation getLocation()
    {
        return location;
    }

    public void setLocation(WecomMomentLocation location)
    {
        this.location = location;
    }
}
