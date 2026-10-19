package com.ruoyi.crm.domain.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品引用摘要（删除/归档确认）
 */
public class ProductReferenceSummaryVo
{
    private String productId;
    private long quoteOptionCount;
    private String knowledgeStatus;
    private String wecomSyncStatus;
    private String wecomExternalId;
    private List<String> warnings = new ArrayList<>();

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public long getQuoteOptionCount()
    {
        return quoteOptionCount;
    }

    public void setQuoteOptionCount(long quoteOptionCount)
    {
        this.quoteOptionCount = quoteOptionCount;
    }

    public String getKnowledgeStatus()
    {
        return knowledgeStatus;
    }

    public void setKnowledgeStatus(String knowledgeStatus)
    {
        this.knowledgeStatus = knowledgeStatus;
    }

    public String getWecomSyncStatus()
    {
        return wecomSyncStatus;
    }

    public void setWecomSyncStatus(String wecomSyncStatus)
    {
        this.wecomSyncStatus = wecomSyncStatus;
    }

    public String getWecomExternalId()
    {
        return wecomExternalId;
    }

    public void setWecomExternalId(String wecomExternalId)
    {
        this.wecomExternalId = wecomExternalId;
    }

    public List<String> getWarnings()
    {
        return warnings;
    }

    public void setWarnings(List<String> warnings)
    {
        this.warnings = warnings;
    }
}
