package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.domain.CrmProductCategory;
import com.ruoyi.crm.domain.CrmProductChannelBinding;
import com.ruoyi.crm.domain.CrmProductMedia;
import com.ruoyi.crm.domain.dto.ProductBatchStatusRequest;
import com.ruoyi.crm.domain.dto.ProductCategoryMoveRequest;
import com.ruoyi.crm.domain.vo.ProductBatchItemResultVo;
import com.ruoyi.crm.domain.vo.ProductCategoryTreeVo;
import com.ruoyi.crm.domain.vo.ProductReferenceSummaryVo;
import com.ruoyi.crm.domain.vo.ProductSellableVo;
import com.ruoyi.crm.knowledge.CrmKnowledgeIngestService;
import com.ruoyi.crm.mapper.CrmProductCategoryMapper;
import com.ruoyi.crm.mapper.CrmProductChannelBindingMapper;
import com.ruoyi.crm.mapper.CrmProductMapper;
import com.ruoyi.crm.mapper.CrmProductMediaMapper;
import com.ruoyi.crm.service.ICrmProductCenterService;

/**
 * 产品中心应用服务实现
 */
@Service
public class CrmProductCenterServiceImpl implements ICrmProductCenterService
{
    @Autowired
    private CrmProductMapper productMapper;

    @Autowired
    private CrmProductCategoryMapper categoryMapper;

    @Autowired
    private CrmProductMediaMapper mediaMapper;

    @Autowired
    private CrmProductChannelBindingMapper bindingMapper;

    @Autowired
    private CrmKnowledgeIngestService knowledgeIngestService;

    @Override
    public List<CrmProduct> listProducts(CrmProduct query)
    {
        if (query == null)
        {
            query = new CrmProduct();
        }
        return productMapper.selectCrmProductList(query);
    }

    @Override
    public CrmProduct getProductDetail(String productId)
    {
        CrmProduct product = requireProduct(productId);
        product.setMediaList(mediaMapper.selectByProductId(productId));
        enrichCompatFields(product);
        return product;
    }

    @Override
    public List<ProductSellableVo> listSellable(CrmProduct query)
    {
        if (query == null)
        {
            query = new CrmProduct();
        }
        List<CrmProduct> rows = productMapper.selectSellableProductList(query);
        List<ProductSellableVo> result = new ArrayList<>();
        for (CrmProduct p : rows)
        {
            ProductSellableVo vo = new ProductSellableVo();
            vo.setProductId(p.getProductId());
            vo.setSellableItemId(p.getProductId());
            vo.setProductCode(p.getProductCode());
            vo.setProductName(p.getProductName());
            vo.setCategoryId(p.getCategoryId());
            vo.setCategoryName(p.getCategoryName());
            vo.setUnit(p.getUnit());
            vo.setSpec(p.getSpec());
            vo.setStandardPrice(p.getOutboundPrice());
            String shortDesc = StringUtils.isNotBlank(p.getShortDescription()) ? p.getShortDescription() : p.getDescription();
            vo.setShortDescription(shortDesc);
            String cover = StringUtils.isNotBlank(p.getCoverImage()) ? p.getCoverImage() : p.getAvatar();
            vo.setCoverImage(cover);
            vo.setHasVariants(false);
            result.add(vo);
        }
        return result;
    }

    @Override
    public ProductReferenceSummaryVo getReferences(String productId)
    {
        CrmProduct product = requireProduct(productId);
        ProductReferenceSummaryVo vo = new ProductReferenceSummaryVo();
        vo.setProductId(productId);
        vo.setQuoteOptionCount(productMapper.countQuoteOptionsByProductId(productId));
        vo.setWecomSyncStatus(product.getWecomSyncStatus());
        vo.setWecomExternalId(product.getWecomExternalId());
        if (vo.getQuoteOptionCount() > 0)
        {
            vo.getWarnings().add("该产品已被 " + vo.getQuoteOptionCount() + " 条报价明细引用，不可物理删除");
        }
        if (StringUtils.isNotBlank(vo.getWecomExternalId()))
        {
            vo.getWarnings().add("该产品已同步到企微商品图册，软删除前请先从企微删除");
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProduct createDraft(CrmProduct body)
    {
        if (body == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        normalizeIncoming(body);
        validateBasics(body, false);
        ensureProductCodeUnique(body.getProductCode(), null);

        String username = safeUsername();
        Date now = DateUtils.getNowDate();
        if (StringUtils.isBlank(body.getProductId()))
        {
            body.setProductId(IdUtils.fastUUID());
        }
        body.setStatus(CrmProduct.STATUS_DRAFT);
        body.setDelFlag("0");
        body.setVersion(1);
        body.setCreateBy(username);
        body.setCreateTime(now);
        body.setUpdateBy(username);
        body.setUpdateTime(now);
        syncCompatFields(body);

        try
        {
            productMapper.insertCrmProduct(body);
        }
        catch (Exception e)
        {
            throwDuplicateCodeIfNeeded(e);
            throw e;
        }

        if (body.getMediaList() != null && !body.getMediaList().isEmpty())
        {
            replaceMediaInternal(body.getProductId(), body.getMediaList(), username, now);
        }

        knowledgeIngestService.onProductChanged(body.getProductId());
        return getProductDetail(body.getProductId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProduct updateProduct(String productId, CrmProduct body)
    {
        if (body == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmProduct existing = requireProduct(productId);
        if (body.getVersion() == null)
        {
            throw new ServiceException("version 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(existing.getVersion(), body.getVersion()))
        {
            throw new ServiceException("数据已被他人更新，请刷新后重试", HttpStatus.CONFLICT);
        }

        normalizeIncoming(body);
        body.setProductId(productId);
        validateBasics(body, false);
        ensureProductCodeUnique(body.getProductCode(), productId);

        String username = safeUsername();
        Date now = DateUtils.getNowDate();
        body.setUpdateBy(username);
        body.setUpdateTime(now);
        // 不允许客户端改状态/删除标记
        body.setStatus(null);
        body.setDelFlag(null);
        body.setCreateBy(null);
        body.setCreateTime(null);
        syncCompatFields(body);

        int rows;
        try
        {
            rows = productMapper.updateCrmProduct(body);
        }
        catch (Exception e)
        {
            throwDuplicateCodeIfNeeded(e);
            throw e;
        }
        if (rows == 0)
        {
            throw new ServiceException("数据已被他人更新，请刷新后重试", HttpStatus.CONFLICT);
        }

        if (body.getMediaList() != null)
        {
            replaceMediaInternal(productId, body.getMediaList(), username, now);
        }

        markWecomDirty(productId);
        knowledgeIngestService.onProductChanged(productId);
        return getProductDetail(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProduct publish(String productId)
    {
        CrmProduct product = requireProduct(productId);
        String status = normalizeStatus(product.getStatus());
        if (!CrmProduct.STATUS_DRAFT.equals(status) && !CrmProduct.STATUS_INACTIVE.equals(status))
        {
            throw new ServiceException("仅草稿或停售产品可发布/恢复为在售", HttpStatus.BAD_REQUEST);
        }
        validateForPublish(product);
        return changeStatus(product, CrmProduct.STATUS_ACTIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProduct disable(String productId)
    {
        CrmProduct product = requireProduct(productId);
        if (!CrmProduct.STATUS_ACTIVE.equals(normalizeStatus(product.getStatus())))
        {
            throw new ServiceException("仅在售产品可停售", HttpStatus.BAD_REQUEST);
        }
        return changeStatus(product, CrmProduct.STATUS_INACTIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProduct reactivate(String productId)
    {
        CrmProduct product = requireProduct(productId);
        if (!CrmProduct.STATUS_INACTIVE.equals(normalizeStatus(product.getStatus())))
        {
            throw new ServiceException("仅停售产品可恢复在售", HttpStatus.BAD_REQUEST);
        }
        validateForPublish(product);
        return changeStatus(product, CrmProduct.STATUS_ACTIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void softDelete(String productId)
    {
        CrmProduct product = requireProduct(productId);
        String status = normalizeStatus(product.getStatus());
        if (CrmProduct.STATUS_ACTIVE.equals(status))
        {
            throw new ServiceException("在售产品请先停售后归档", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(product.getWecomExternalId()))
        {
            throw new ServiceException("请先从企微商品图册删除后再归档本地产品", HttpStatus.BAD_REQUEST);
        }
        String username = safeUsername();
        productMapper.logicalDeleteByProductId(productId, username);
        mediaMapper.logicalDeleteByProductId(productId, username);
        knowledgeIngestService.onProductDeleted(productId);
    }

    @Override
    public List<ProductBatchItemResultVo> batchStatus(ProductBatchStatusRequest request)
    {
        if (request == null || request.getProductIds() == null || request.getProductIds().isEmpty())
        {
            throw new ServiceException("productIds 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getAction()))
        {
            throw new ServiceException("action 不能为空", HttpStatus.BAD_REQUEST);
        }
        List<ProductBatchItemResultVo> results = new ArrayList<>();
        for (String productId : request.getProductIds())
        {
            try
            {
                switch (request.getAction().trim())
                {
                    case "publish":
                        publish(productId);
                        break;
                    case "disable":
                        disable(productId);
                        break;
                    case "reactivate":
                        reactivate(productId);
                        break;
                    case "archive":
                        softDelete(productId);
                        break;
                    default:
                        throw new ServiceException("不支持的 action: " + request.getAction(), HttpStatus.BAD_REQUEST);
                }
                results.add(new ProductBatchItemResultVo(productId, true, "ok"));
            }
            catch (ServiceException e)
            {
                results.add(new ProductBatchItemResultVo(productId, false, e.getMessage()));
            }
            catch (Exception e)
            {
                results.add(new ProductBatchItemResultVo(productId, false, e.getMessage()));
            }
        }
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceMedia(String productId, List<CrmProductMedia> mediaList)
    {
        requireProduct(productId);
        replaceMediaInternal(productId, mediaList, safeUsername(), DateUtils.getNowDate());
        markWecomDirty(productId);
        knowledgeIngestService.onProductChanged(productId);
    }

    @Override
    public List<ProductCategoryTreeVo> categoryTree()
    {
        List<CrmProductCategory> all = categoryMapper.selectAllActive();
        Map<String, ProductCategoryTreeVo> map = new HashMap<>();
        List<ProductCategoryTreeVo> roots = new ArrayList<>();
        for (CrmProductCategory c : all)
        {
            ProductCategoryTreeVo node = new ProductCategoryTreeVo();
            node.setCategoryId(c.getCategoryId());
            node.setParentId(c.getParentId());
            node.setCategoryCode(c.getCategoryCode());
            node.setCategoryName(c.getCategoryName());
            node.setAncestors(c.getAncestors());
            node.setSortOrder(c.getSortOrder());
            node.setStatus(c.getStatus());
            map.put(c.getCategoryId(), node);
        }
        for (ProductCategoryTreeVo node : map.values())
        {
            if (StringUtils.isBlank(node.getParentId()) || !map.containsKey(node.getParentId()))
            {
                roots.add(node);
            }
            else
            {
                map.get(node.getParentId()).getChildren().add(node);
            }
        }
        return roots;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProductCategory createCategory(CrmProductCategory body)
    {
        if (body == null || StringUtils.isBlank(body.getCategoryName()) || StringUtils.isBlank(body.getCategoryCode()))
        {
            throw new ServiceException("分类编码和名称不能为空", HttpStatus.BAD_REQUEST);
        }
        if (categoryMapper.countByCode(body.getCategoryCode().trim(), null) > 0)
        {
            throw new ServiceException("分类编码已存在", HttpStatus.BAD_REQUEST);
        }
        String username = safeUsername();
        Date now = DateUtils.getNowDate();
        body.setCategoryId(IdUtils.fastUUID());
        body.setCategoryCode(body.getCategoryCode().trim());
        body.setCategoryName(body.getCategoryName().trim());
        body.setSortOrder(body.getSortOrder() == null ? 0 : body.getSortOrder());
        body.setStatus(StringUtils.isBlank(body.getStatus()) ? "1" : body.getStatus());
        body.setDelFlag("0");
        body.setCreateBy(username);
        body.setCreateTime(now);
        body.setUpdateBy(username);
        body.setUpdateTime(now);
        fillAncestors(body);
        categoryMapper.insert(body);
        return categoryMapper.selectById(body.getCategoryId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrmProductCategory updateCategory(String categoryId, CrmProductCategory body)
    {
        CrmProductCategory existing = requireCategory(categoryId);
        if (body == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(body.getCategoryCode()))
        {
            if (categoryMapper.countByCode(body.getCategoryCode().trim(), categoryId) > 0)
            {
                throw new ServiceException("分类编码已存在", HttpStatus.BAD_REQUEST);
            }
            existing.setCategoryCode(body.getCategoryCode().trim());
        }
        if (StringUtils.isNotBlank(body.getCategoryName()))
        {
            existing.setCategoryName(body.getCategoryName().trim());
        }
        if (body.getSortOrder() != null)
        {
            existing.setSortOrder(body.getSortOrder());
        }
        if (StringUtils.isNotBlank(body.getStatus()))
        {
            existing.setStatus(body.getStatus());
        }
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        categoryMapper.update(existing);
        return categoryMapper.selectById(categoryId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveCategory(String categoryId, ProductCategoryMoveRequest request)
    {
        CrmProductCategory existing = requireCategory(categoryId);
        String newParentId = request == null ? null : request.getParentId();
        if (StringUtils.isNotBlank(newParentId))
        {
            if (categoryId.equals(newParentId))
            {
                throw new ServiceException("不能将分类移动到自身下", HttpStatus.BAD_REQUEST);
            }
            CrmProductCategory parent = requireCategory(newParentId);
            String ancestors = StringUtils.defaultString(parent.getAncestors());
            if (("," + ancestors + "," + parent.getCategoryId() + ",").contains("," + categoryId + ","))
            {
                throw new ServiceException("不能将分类移动到其子分类下", HttpStatus.BAD_REQUEST);
            }
            existing.setParentId(newParentId);
        }
        else
        {
            existing.setParentId(null);
        }
        if (request != null && request.getSortOrder() != null)
        {
            existing.setSortOrder(request.getSortOrder());
        }
        fillAncestors(existing);
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        categoryMapper.update(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableCategory(String categoryId)
    {
        CrmProductCategory existing = requireCategory(categoryId);
        existing.setStatus("1");
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        categoryMapper.update(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableCategory(String categoryId)
    {
        CrmProductCategory existing = requireCategory(categoryId);
        existing.setStatus("0");
        existing.setUpdateBy(safeUsername());
        existing.setUpdateTime(DateUtils.getNowDate());
        categoryMapper.update(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(String categoryId)
    {
        requireCategory(categoryId);
        if (categoryMapper.countChildren(categoryId) > 0)
        {
            throw new ServiceException("存在子分类，无法删除", HttpStatus.BAD_REQUEST);
        }
        if (productMapper.countByCategoryId(categoryId) > 0)
        {
            throw new ServiceException("分类下仍有产品，无法删除", HttpStatus.BAD_REQUEST);
        }
        categoryMapper.logicalDelete(categoryId, safeUsername());
    }

    @Override
    public void markWecomDirty(String productId)
    {
        if (StringUtils.isBlank(productId))
        {
            return;
        }
        bindingMapper.markDirtyByProductId(productId, safeUsername());
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    private CrmProduct changeStatus(CrmProduct product, String targetStatus)
    {
        String username = safeUsername();
        Date now = DateUtils.getNowDate();
        CrmProduct patch = new CrmProduct();
        patch.setProductId(product.getProductId());
        patch.setVersion(product.getVersion());
        patch.setStatus(targetStatus);
        patch.setUpdateBy(username);
        patch.setUpdateTime(now);
        if (CrmProduct.STATUS_ACTIVE.equals(targetStatus))
        {
            patch.setPublishedTime(now);
            patch.setDisabledTime(null);
        }
        else if (CrmProduct.STATUS_INACTIVE.equals(targetStatus))
        {
            patch.setDisabledTime(now);
        }
        int rows = productMapper.updateCrmProduct(patch);
        if (rows == 0)
        {
            throw new ServiceException("数据已被他人更新，请刷新后重试", HttpStatus.CONFLICT);
        }
        markWecomDirty(product.getProductId());
        knowledgeIngestService.onProductChanged(product.getProductId());
        return getProductDetail(product.getProductId());
    }

    private void replaceMediaInternal(String productId, List<CrmProductMedia> mediaList, String username, Date now)
    {
        mediaMapper.logicalDeleteByProductId(productId, username);
        if (mediaList == null || mediaList.isEmpty())
        {
            return;
        }
        boolean coverSet = false;
        int order = 0;
        String coverUrl = null;
        for (CrmProductMedia media : mediaList)
        {
            if (media == null || StringUtils.isBlank(media.getUrl()))
            {
                continue;
            }
            media.setMediaId(IdUtils.fastUUID());
            media.setProductId(productId);
            media.setMediaType(StringUtils.isBlank(media.getMediaType()) ? "image" : media.getMediaType());
            media.setSortOrder(media.getSortOrder() == null ? order : media.getSortOrder());
            boolean isCover = "1".equals(media.getIsCover()) || (!coverSet && order == 0);
            media.setIsCover(isCover ? "1" : "0");
            if (isCover)
            {
                coverSet = true;
                coverUrl = media.getUrl();
            }
            media.setDelFlag("0");
            media.setCreateBy(username);
            media.setCreateTime(now);
            media.setUpdateBy(username);
            media.setUpdateTime(now);
            mediaMapper.insert(media);
            order++;
        }
        if (StringUtils.isNotBlank(coverUrl))
        {
            CrmProduct coverPatch = new CrmProduct();
            coverPatch.setProductId(productId);
            coverPatch.setCoverImage(coverUrl);
            coverPatch.setAvatar(coverUrl);
            coverPatch.setUpdateBy(username);
            coverPatch.setUpdateTime(now);
            // 不带 version，避免媒体单独保存时因 version 冲突失败；直接更新封面缓存
            productMapper.updateCrmProduct(coverPatch);
        }
    }

    private void validateBasics(CrmProduct body, boolean forPublish)
    {
        if (StringUtils.isBlank(body.getProductName()))
        {
            throw new ServiceException("产品名称不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(body.getProductCode()))
        {
            throw new ServiceException("产品编码不能为空", HttpStatus.BAD_REQUEST);
        }
        if (body.getOutboundPrice() != null && body.getOutboundPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("标准售价不能为负数", HttpStatus.BAD_REQUEST);
        }
        if (body.getOutboundPrice() != null)
        {
            body.setOutboundPrice(body.getOutboundPrice().setScale(2, RoundingMode.HALF_UP));
        }
        if (forPublish)
        {
            validateForPublish(body);
        }
    }

    private void validateForPublish(CrmProduct product)
    {
        if (StringUtils.isBlank(product.getProductName()) || StringUtils.isBlank(product.getProductCode()))
        {
            throw new ServiceException("发布前须填写产品名称和编码", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(product.getCategoryId()))
        {
            throw new ServiceException("发布前须选择有效分类", HttpStatus.BAD_REQUEST);
        }
        CrmProductCategory category = categoryMapper.selectById(product.getCategoryId());
        if (category == null)
        {
            throw new ServiceException("分类不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        if (!"1".equals(category.getStatus()))
        {
            throw new ServiceException("分类已停用，无法发布产品", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(product.getUnit()))
        {
            throw new ServiceException("发布前须填写单位", HttpStatus.BAD_REQUEST);
        }
        if (product.getOutboundPrice() == null)
        {
            throw new ServiceException("发布前须填写标准售价", HttpStatus.BAD_REQUEST);
        }
        if (product.getOutboundPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("标准售价不能为负数", HttpStatus.BAD_REQUEST);
        }
    }

    private void ensureProductCodeUnique(String productCode, String excludeProductId)
    {
        if (StringUtils.isBlank(productCode))
        {
            return;
        }
        if (productMapper.countByProductCode(productCode.trim(), excludeProductId) > 0)
        {
            throw new ServiceException("产品编码已存在: " + productCode.trim(), HttpStatus.BAD_REQUEST);
        }
    }

    private void throwDuplicateCodeIfNeeded(Exception e)
    {
        String msg = e.getMessage() == null ? "" : e.getMessage();
        if (msg.contains("uk_crm_product_code_active") || msg.contains("duplicate key"))
        {
            throw new ServiceException("产品编码已存在", HttpStatus.BAD_REQUEST);
        }
    }

    private void normalizeIncoming(CrmProduct body)
    {
        if (body.getStandardPrice() != null && body.getOutboundPrice() == null)
        {
            body.setOutboundPrice(body.getStandardPrice());
        }
        if (StringUtils.isNotBlank(body.getCoverImage()) && StringUtils.isBlank(body.getAvatar()))
        {
            body.setAvatar(body.getCoverImage());
        }
        if (StringUtils.isNotBlank(body.getAvatar()) && StringUtils.isBlank(body.getCoverImage()))
        {
            body.setCoverImage(body.getAvatar());
        }
        if (StringUtils.isNotBlank(body.getShortDescription()) && StringUtils.isBlank(body.getDescription()))
        {
            body.setDescription(body.getShortDescription());
        }
        if (StringUtils.isNotBlank(body.getDescription()) && StringUtils.isBlank(body.getShortDescription()))
        {
            body.setShortDescription(body.getDescription());
        }
        if (StringUtils.isNotBlank(body.getProductCode()))
        {
            body.setProductCode(body.getProductCode().trim());
        }
        if (StringUtils.isNotBlank(body.getProductName()))
        {
            body.setProductName(body.getProductName().trim());
        }
    }

    private void syncCompatFields(CrmProduct body)
    {
        if (StringUtils.isNotBlank(body.getCoverImage()))
        {
            body.setAvatar(body.getCoverImage());
        }
        else if (StringUtils.isNotBlank(body.getAvatar()))
        {
            body.setCoverImage(body.getAvatar());
        }
        if (StringUtils.isNotBlank(body.getShortDescription()))
        {
            body.setDescription(body.getShortDescription());
        }
        else if (StringUtils.isNotBlank(body.getDescription()))
        {
            body.setShortDescription(body.getDescription());
        }
    }

    private void enrichCompatFields(CrmProduct product)
    {
        if (product == null)
        {
            return;
        }
        if (StringUtils.isBlank(product.getCoverImage()) && StringUtils.isNotBlank(product.getAvatar()))
        {
            product.setCoverImage(product.getAvatar());
        }
        if (StringUtils.isBlank(product.getAvatar()) && StringUtils.isNotBlank(product.getCoverImage()))
        {
            product.setAvatar(product.getCoverImage());
        }
        if (StringUtils.isBlank(product.getShortDescription()) && StringUtils.isNotBlank(product.getDescription()))
        {
            product.setShortDescription(product.getDescription());
        }
        if (StringUtils.isBlank(product.getDescription()) && StringUtils.isNotBlank(product.getShortDescription()))
        {
            product.setDescription(product.getShortDescription());
        }
        product.setStatus(normalizeStatus(product.getStatus()));
    }

    private String normalizeStatus(String status)
    {
        if (StringUtils.isBlank(status))
        {
            return CrmProduct.STATUS_ACTIVE;
        }
        String trimmed = status.trim();
        if ("0".equals(trimmed) || "停用".equals(trimmed))
        {
            return CrmProduct.STATUS_INACTIVE;
        }
        if ("1".equals(trimmed) || "已启用".equals(trimmed) || "启用".equals(trimmed))
        {
            return CrmProduct.STATUS_ACTIVE;
        }
        if (CrmProduct.STATUS_DRAFT.equals(trimmed)
            || CrmProduct.STATUS_ACTIVE.equals(trimmed)
            || CrmProduct.STATUS_INACTIVE.equals(trimmed)
            || CrmProduct.STATUS_ARCHIVED.equals(trimmed))
        {
            return trimmed;
        }
        return CrmProduct.STATUS_ACTIVE;
    }

    private CrmProduct requireProduct(String productId)
    {
        if (StringUtils.isBlank(productId))
        {
            throw new ServiceException("productId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmProduct product = productMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            throw new ServiceException("产品不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        enrichCompatFields(product);
        return product;
    }

    private CrmProductCategory requireCategory(String categoryId)
    {
        if (StringUtils.isBlank(categoryId))
        {
            throw new ServiceException("categoryId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmProductCategory category = categoryMapper.selectById(categoryId);
        if (category == null)
        {
            throw new ServiceException("分类不存在或已删除", HttpStatus.BAD_REQUEST);
        }
        return category;
    }

    private void fillAncestors(CrmProductCategory body)
    {
        if (StringUtils.isBlank(body.getParentId()))
        {
            body.setParentId(null);
            body.setAncestors("");
            return;
        }
        CrmProductCategory parent = requireCategory(body.getParentId());
        String ancestors = StringUtils.isBlank(parent.getAncestors())
            ? parent.getCategoryId()
            : parent.getAncestors() + "," + parent.getCategoryId();
        body.setAncestors(ancestors);
    }

    private static String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "system";
        }
    }
}
