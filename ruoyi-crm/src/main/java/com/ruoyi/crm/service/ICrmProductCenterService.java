package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.domain.CrmProductCategory;
import com.ruoyi.crm.domain.CrmProductMedia;
import com.ruoyi.crm.domain.dto.ProductBatchStatusRequest;
import com.ruoyi.crm.domain.dto.ProductCategoryMoveRequest;
import com.ruoyi.crm.domain.vo.ProductBatchItemResultVo;
import com.ruoyi.crm.domain.vo.ProductCategoryTreeVo;
import com.ruoyi.crm.domain.vo.ProductReferenceSummaryVo;
import com.ruoyi.crm.domain.vo.ProductSellableVo;

/**
 * 产品中心应用服务
 */
public interface ICrmProductCenterService
{
    List<CrmProduct> listProducts(CrmProduct query);

    CrmProduct getProductDetail(String productId);

    List<ProductSellableVo> listSellable(CrmProduct query);

    ProductReferenceSummaryVo getReferences(String productId);

    CrmProduct createDraft(CrmProduct body);

    CrmProduct updateProduct(String productId, CrmProduct body);

    CrmProduct publish(String productId);

    CrmProduct disable(String productId);

    CrmProduct reactivate(String productId);

    void softDelete(String productId);

    List<ProductBatchItemResultVo> batchStatus(ProductBatchStatusRequest request);

    void replaceMedia(String productId, List<CrmProductMedia> mediaList);

    List<ProductCategoryTreeVo> categoryTree();

    CrmProductCategory createCategory(CrmProductCategory body);

    CrmProductCategory updateCategory(String categoryId, CrmProductCategory body);

    void moveCategory(String categoryId, ProductCategoryMoveRequest request);

    void enableCategory(String categoryId);

    void disableCategory(String categoryId);

    void deleteCategory(String categoryId);

    /** 产品内容变更后标记企微 binding dirty（不调外部 API） */
    void markWecomDirty(String productId);
}
