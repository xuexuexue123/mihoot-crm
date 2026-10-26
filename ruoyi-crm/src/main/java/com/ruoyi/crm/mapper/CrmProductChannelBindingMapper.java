package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmProductChannelBinding;

public interface CrmProductChannelBindingMapper
{
    CrmProductChannelBinding selectByProductAndChannel(@Param("productId") String productId,
                                                       @Param("channelType") String channelType);

    CrmProductChannelBinding selectByExternalId(@Param("channelType") String channelType,
                                                @Param("externalId") String externalId);

    int insert(CrmProductChannelBinding row);

    int update(CrmProductChannelBinding row);

    int markDirtyByProductId(@Param("productId") String productId, @Param("updateBy") String updateBy);

    int logicalDelete(@Param("bindingId") String bindingId, @Param("updateBy") String updateBy);

    int logicalDeleteByExternalId(@Param("channelType") String channelType,
                                  @Param("externalId") String externalId,
                                  @Param("updateBy") String updateBy);
}
