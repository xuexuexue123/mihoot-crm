package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动受众解析出的公众号 openid 列表
 */
public class EventAudienceOpenIdResultVo
{
    private List<String> openIds = new ArrayList<>();
    private int skipCount;

    public List<String> getOpenIds()
    {
        return openIds;
    }

    public void setOpenIds(List<String> openIds)
    {
        this.openIds = openIds;
    }

    public int getSkipCount()
    {
        return skipCount;
    }

    public void setSkipCount(int skipCount)
    {
        this.skipCount = skipCount;
    }
}
