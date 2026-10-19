package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmProduct;

/**
 * 产品Service接口
 * 
 * @author luohd
 * @date 2024-02-06
 */
public interface ICrmProductService 
{
    /**
     * 查询产品
     * 
     * @param productId 产品主键
     * @return 产品
     */
    public CrmProduct selectCrmProductByProductId(String productId);

    /**
     * 查询产品列表
     * 
     * @param crmProduct 产品
     * @return 产品集合
     */
    public List<CrmProduct> selectCrmProductList(CrmProduct crmProduct);

    /**
     * 新增产品
     * 
     * @param crmProduct 产品
     * @return 结果
     */
    public int insertCrmProduct(CrmProduct crmProduct);

    /**
     * 修改产品
     * 
     * @param crmProduct 产品
     * @return 结果
     */
    public int updateCrmProduct(CrmProduct crmProduct);

    /**
     * 批量删除产品
     * 
     * @param productIds 需要删除的产品主键集合
     * @return 结果
     */
    public int deleteCrmProductByProductIds(String[] productIds);

    /**
     * 删除产品信息
     * 
     * @param productId 产品主键
     * @return 结果
     */
    public int deleteCrmProductByProductId(String productId);
}
