package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

/**
 * 从企业微信同步外部联系人；userids 为空表示同步全部配置了客户联系的成员。
 */
public class WecomExternalContactImportRequest
{
    /** 可选：仅同步这些成员的 userid */
    private List<String> userids;

    public List<String> getUserids()
    {
        return userids;
    }

    public void setUserids(List<String> userids)
    {
        this.userids = userids;
    }
}
