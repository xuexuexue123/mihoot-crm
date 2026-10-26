package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmProductCategory;

public interface CrmProductCategoryMapper
{
    CrmProductCategory selectById(String categoryId);

    List<CrmProductCategory> selectAllActive();

    List<CrmProductCategory> selectList(CrmProductCategory query);

    int countByCode(@Param("categoryCode") String categoryCode, @Param("excludeId") String excludeId);

    int countChildren(@Param("parentId") String parentId);

    int insert(CrmProductCategory row);

    int update(CrmProductCategory row);

    int logicalDelete(@Param("categoryId") String categoryId, @Param("updateBy") String updateBy);
}
