package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmProduct;

/**
 * 产品Mapper接口
 *
 * @author luohd
 * @date 2024-02-06
 */
public interface CrmProductMapper
{
    public CrmProduct selectCrmProductByProductId(String productId);

    public CrmProduct selectCrmProductByProductIdIncludeDeleted(String productId);

    public List<CrmProduct> selectCrmProductList(CrmProduct crmProduct);

    public List<CrmProduct> selectSellableProductList(CrmProduct crmProduct);

    public int insertCrmProduct(CrmProduct crmProduct);

    public int updateCrmProduct(CrmProduct crmProduct);

    public int deleteCrmProductByProductId(String productId);

    public int deleteCrmProductByProductIds(String[] productIds);

    public int logicalDeleteByProductId(@Param("productId") String productId, @Param("updateBy") String updateBy);

    public int logicalDeleteByProductIds(@Param("productIds") String[] productIds, @Param("updateBy") String updateBy);

    public int countByProductCode(@Param("productCode") String productCode, @Param("excludeProductId") String excludeProductId);

    public long countQuoteOptionsByProductId(@Param("productId") String productId);

    public int countByCategoryId(@Param("categoryId") String categoryId);

    public int updateWecomProductId(@Param("productId") String productId, @Param("wecomProductId") String wecomProductId);

    public CrmProduct selectCrmProductByWecomProductId(@Param("wecomProductId") String wecomProductId);

    public int clearWecomProductIdByWecomProductId(@Param("wecomProductId") String wecomProductId);

    public List<CrmProduct> selectSyncedToWecom();
}
