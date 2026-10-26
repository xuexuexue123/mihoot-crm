package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 朋友圈可见客户标签列表。
 */
public class WecomMomentExternalContactList
{
    @JsonProperty("tag_list")
    private List<String> tagList;

    public List<String> getTagList()
    {
        return tagList;
    }

    public void setTagList(List<String> tagList)
    {
        this.tagList = tagList;
    }
}
