package com.ruoyi.crm.domain.dto.wecom;

/**
 * 朋友圈附件。
 */
public class WecomMomentAttachment
{
    private String msgtype;
    private WecomMomentImage image;
    private WecomMomentVideo video;
    private WecomMomentLink link;

    public String getMsgtype()
    {
        return msgtype;
    }

    public void setMsgtype(String msgtype)
    {
        this.msgtype = msgtype;
    }

    public WecomMomentImage getImage()
    {
        return image;
    }

    public void setImage(WecomMomentImage image)
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
}
