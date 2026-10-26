package com.ruoyi.crm.domain.dto.wecom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 朋友圈图片附件。
 */
public class WecomMomentImage
{
    @JsonProperty("media_id")
    private String mediaId;

    @JsonProperty("media_id")
    public String getMediaId()
    {
        return mediaId;
    }

    public void setMediaId(String mediaId)
    {
        this.mediaId = mediaId;
    }
}
