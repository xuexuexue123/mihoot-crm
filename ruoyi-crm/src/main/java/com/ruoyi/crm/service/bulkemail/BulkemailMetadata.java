package com.ruoyi.crm.service.bulkemail;

import com.ruoyi.crm.domain.dto.BulkemailAudienceFilter;

/**
 * crm_bulkemail.metadata JSON 结构
 */
public class BulkemailMetadata
{
    private Boolean personalizationEnabled;
    private BulkemailAudienceFilter audienceFilter;

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
