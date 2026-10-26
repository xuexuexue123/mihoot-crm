package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmProductMedia;

public interface CrmProductMediaMapper
{
    List<CrmProductMedia> selectByProductId(String productId);

    int insert(CrmProductMedia row);

    int update(CrmProductMedia row);

    int logicalDeleteByProductId(@Param("productId") String productId, @Param("updateBy") String updateBy);

    int logicalDelete(@Param("mediaId") String mediaId, @Param("updateBy") String updateBy);

    int clearCoverByProductId(@Param("productId") String productId);
}
