package com.ruoyi.crm.wechatmp.audience.dto;

/**
 * 粉丝受众标签筛选引用。前端 value 结构：
 * <pre>{@code
 * { "tagId": 123, "tagName": "VIP客户" }
 * }</pre>
 * 后端仅使用 tagId 做匹配；tagName 仅供前端展示。
 *
 * @author luohd
 * @date 2026-07-17
 */
public class FanWechatMpTagRef
{
    private Long tagId;
    private String tagName;

    public Long getTagId()
    {
        return tagId;
    }

    public void setTagId(Long tagId)
    {
        this.tagId = tagId;
    }

    public String getTagName()
    {
        return tagName;
    }

    public void setTagName(String tagName)
    {
        this.tagName = tagName;
    }
}
