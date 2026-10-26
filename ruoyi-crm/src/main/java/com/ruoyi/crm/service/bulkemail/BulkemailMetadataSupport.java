package com.ruoyi.crm.service.bulkemail;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.domain.dto.BulkemailAudienceFilter;
import com.ruoyi.crm.domain.dto.BulkemailCreateRequest;

/**
 * crm_bulkemail.metadata 读写
 */
public final class BulkemailMetadataSupport
{
    private BulkemailMetadataSupport()
    {
    }

    public static BulkemailMetadata parse(String metadataJson)
    {
        if (StringUtils.isBlank(metadataJson))
        {
            return new BulkemailMetadata();
        }
        try
        {
            BulkemailMetadata parsed = JSON.parseObject(metadataJson, BulkemailMetadata.class);
            return parsed != null ? parsed : new BulkemailMetadata();
        }
        catch (Exception ex)
        {
            return new BulkemailMetadata();
        }
    }

    public static String serialize(BulkemailMetadata metadata)
    {
        if (metadata == null)
        {
            return null;
        }
        if (metadata.getPersonalizationEnabled() == null && metadata.getAudienceFilter() == null)
        {
            return null;
        }
        return JSON.toJSONString(metadata);
    }

    public static String buildMetadataJson(BulkemailCreateRequest request)
    {
        BulkemailMetadata metadata = new BulkemailMetadata();
        metadata.setPersonalizationEnabled(request.getPersonalizationEnabled());
        BulkemailAudienceFilter filter = request.getAudienceFilter();
        if (filter == null && StringUtils.isNotBlank(request.getMetadata()))
        {
            BulkemailMetadata existing = parse(request.getMetadata());
            filter = existing.getAudienceFilter();
            if (metadata.getPersonalizationEnabled() == null)
            {
                metadata.setPersonalizationEnabled(existing.getPersonalizationEnabled());
            }
        }
        metadata.setAudienceFilter(filter);
        return serialize(metadata);
    }

    public static boolean isPersonalizationEnabled(CrmBulkemail campaign)
    {
        if (campaign == null)
        {
            return false;
        }
        BulkemailMetadata metadata = parse(campaign.getMetadata());
        return Boolean.TRUE.equals(metadata.getPersonalizationEnabled());
    }

    public static BulkemailAudienceFilter getAudienceFilter(CrmBulkemail campaign)
    {
        if (campaign == null)
        {
            return null;
        }
        return parse(campaign.getMetadata()).getAudienceFilter();
    }
}
