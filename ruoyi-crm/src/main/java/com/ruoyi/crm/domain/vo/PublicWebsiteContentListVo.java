package com.ruoyi.crm.domain.vo;

import java.util.List;

/**
 * 官网公开内容列表
 */
public class PublicWebsiteContentListVo
{
    private List<PublicWebsiteContentCardVo> items;

    public List<PublicWebsiteContentCardVo> getItems()
    {
        return items;
    }

    public void setItems(List<PublicWebsiteContentCardVo> items)
    {
        this.items = items;
    }
}
