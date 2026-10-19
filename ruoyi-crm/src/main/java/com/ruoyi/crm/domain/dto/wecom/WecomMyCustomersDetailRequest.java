package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * POST /crm/wecom/my-customers/details 请求体
 */
public class WecomMyCustomersDetailRequest
{
    private List<String> externalUseridList;

    public List<String> getExternalUseridList()
    {
        return externalUseridList;
    }

    public void setExternalUseridList(List<String> externalUseridList)
    {
        this.externalUseridList = externalUseridList;
    }
}
