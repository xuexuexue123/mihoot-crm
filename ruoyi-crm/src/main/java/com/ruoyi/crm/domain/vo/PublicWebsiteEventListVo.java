package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 官网公开活动列表
 */
public class PublicWebsiteEventListVo
{
    private List<PublicWebsiteEventCardVo> items = new ArrayList<>();

    public List<PublicWebsiteEventCardVo> getItems()
    {
        return items;
    }

    public void setItems(List<PublicWebsiteEventCardVo> items)
    {
        this.items = items != null ? items : new ArrayList<>();
    }
}
