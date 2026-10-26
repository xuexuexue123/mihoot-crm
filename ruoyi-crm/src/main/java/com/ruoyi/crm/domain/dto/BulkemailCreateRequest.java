package com.ruoyi.crm.domain.dto;

import com.ruoyi.crm.domain.CrmBulkemailOption;

/**
 * 创建邮件活动请求（P1 发送生命周期 + P2 个性化）
 */
public class BulkemailCreateRequest extends CrmBulkemailOption
{
    private String sendMode;
    private Boolean personalizationEnabled;
    private BulkemailAudienceFilter audienceFilter;

    public String getSendMode()
    {
        return sendMode;
    }

    public void setSendMode(String sendMode)
    {
        this.sendMode = sendMode;
    }

    public Boolean getPersonalizationEnabled()
    {
        return personalizationEnabled;
    }

    public void setPersonalizationEnabled(Boolean personalizationEnabled)
    {
        this.personalizationEnabled = personalizationEnabled;
    }

    public BulkemailAudienceFilter getAudienceFilter()
    {
        return audienceFilter;
    }

    public void setAudienceFilter(BulkemailAudienceFilter audienceFilter)
    {
        this.audienceFilter = audienceFilter;
    }
}
