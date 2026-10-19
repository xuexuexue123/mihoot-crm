package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 产品渠道映射 crm_product_channel_binding
 */
public class CrmProductChannelBinding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    public static final String CHANNEL_WECOM_ALBUM = "wecom_album";
    public static final String SYNC_SYNCED = "synced";
    public static final String SYNC_DIRTY = "dirty";
    public static final String SYNC_FAILED = "failed";

    private String bindingId;
    private String productId;
    private String channelType;
    private String externalId;
    private String syncStatus;
    private String lastSyncHash;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;
    private String lastSyncBy;
    private String lastError;
    private String delFlag;

    public String getBindingId()
    {
        return bindingId;
    }

    public void setBindingId(String bindingId)
    {
        this.bindingId = bindingId;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getChannelType()
    {
        return channelType;
    }

    public void setChannelType(String channelType)
    {
        this.channelType = channelType;
    }

    public String getExternalId()
    {
        return externalId;
    }

    public void setExternalId(String externalId)
    {
        this.externalId = externalId;
    }

    public String getSyncStatus()
    {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
    }

    public String getLastSyncHash()
    {
        return lastSyncHash;
    }

    public void setLastSyncHash(String lastSyncHash)
    {
        this.lastSyncHash = lastSyncHash;
    }

    public Date getLastSyncTime()
    {
        return lastSyncTime;
    }

    public void setLastSyncTime(Date lastSyncTime)
    {
        this.lastSyncTime = lastSyncTime;
    }

    public String getLastSyncBy()
    {
        return lastSyncBy;
    }

    public void setLastSyncBy(String lastSyncBy)
    {
        this.lastSyncBy = lastSyncBy;
    }

    public String getLastError()
    {
        return lastError;
    }

    public void setLastError(String lastError)
    {
        this.lastError = lastError;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
