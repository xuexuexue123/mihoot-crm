package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.knowledge.CrmKnowledgeIngestService;
import com.ruoyi.crm.mapper.CrmProductChannelBindingMapper;
import com.ruoyi.crm.mapper.CrmProductMapper;
import com.ruoyi.crm.service.ICrmProductService;

/**
 * 产品Service业务层处理（兼容门面，委托产品中心校验规则）
 *
 * @author luohd
 * @date 2024-02-06
 */
@Service
public class CrmProductServiceImpl implements ICrmProductService
{
    @Autowired
    private CrmProductMapper crmProductMapper;

    @Autowired
    private CrmKnowledgeIngestService knowledgeIngestService;

    @Autowired
    private CrmProductChannelBindingMapper bindingMapper;

    @Override
    public CrmProduct selectCrmProductByProductId(String productId)
    {
        return crmProductMapper.selectCrmProductByProductId(productId);
    }

    @Override
    public List<CrmProduct> selectCrmProductList(CrmProduct crmProduct)
    {
        return crmProductMapper.selectCrmProductList(crmProduct);
    }

    @Override
    public int insertCrmProduct(CrmProduct crmProduct)
    {
        if (crmProduct == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(crmProduct.getProductName()))
        {
            throw new ServiceException("产品名称不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(crmProduct.getProductCode()))
        {
            throw new ServiceException("产品编码不能为空", HttpStatus.BAD_REQUEST);
        }
        if (crmProduct.getOutboundPrice() != null && crmProduct.getOutboundPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("标准售价不能为负数", HttpStatus.BAD_REQUEST);
        }
        if (crmProductMapper.countByProductCode(crmProduct.getProductCode().trim(), null) > 0)
        {
            throw new ServiceException("产品编码已存在: " + crmProduct.getProductCode().trim(), HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(crmProduct.getProductId()))
        {
            crmProduct.setProductId(IdUtils.fastUUID());
        }
        String username = safeUsername();
        Date now = DateUtils.getNowDate();
        if (StringUtils.isBlank(crmProduct.getStatus()))
        {
            crmProduct.setStatus(CrmProduct.STATUS_DRAFT);
        }
        crmProduct.setDelFlag("0");
        if (crmProduct.getVersion() == null)
        {
            crmProduct.setVersion(1);
        }
        crmProduct.setCreateBy(username);
        crmProduct.setCreateTime(now);
        crmProduct.setUpdateBy(username);
        crmProduct.setUpdateTime(now);
        if (StringUtils.isNotBlank(crmProduct.getCoverImage()) && StringUtils.isBlank(crmProduct.getAvatar()))
        {
            crmProduct.setAvatar(crmProduct.getCoverImage());
        }
        if (StringUtils.isNotBlank(crmProduct.getAvatar()) && StringUtils.isBlank(crmProduct.getCoverImage()))
        {
            crmProduct.setCoverImage(crmProduct.getAvatar());
        }
        if (StringUtils.isNotBlank(crmProduct.getShortDescription()) && StringUtils.isBlank(crmProduct.getDescription()))
        {
            crmProduct.setDescription(crmProduct.getShortDescription());
        }
        if (StringUtils.isNotBlank(crmProduct.getDescription()) && StringUtils.isBlank(crmProduct.getShortDescription()))
        {
            crmProduct.setShortDescription(crmProduct.getDescription());
        }

        int rows = crmProductMapper.insertCrmProduct(crmProduct);
        if (rows > 0)
        {
            knowledgeIngestService.onProductChanged(crmProduct.getProductId());
        }
        return rows;
    }

    @Override
    public int updateCrmProduct(CrmProduct crmProduct)
    {
        if (crmProduct == null || StringUtils.isBlank(crmProduct.getProductId()))
        {
            throw new ServiceException("productId 不能为空", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(crmProduct.getProductCode())
            && crmProductMapper.countByProductCode(crmProduct.getProductCode().trim(), crmProduct.getProductId()) > 0)
        {
            throw new ServiceException("产品编码已存在: " + crmProduct.getProductCode().trim(), HttpStatus.BAD_REQUEST);
        }
        if (crmProduct.getOutboundPrice() != null && crmProduct.getOutboundPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("标准售价不能为负数", HttpStatus.BAD_REQUEST);
        }
        crmProduct.setUpdateBy(safeUsername());
        crmProduct.setUpdateTime(DateUtils.getNowDate());
        if (StringUtils.isNotBlank(crmProduct.getCoverImage()))
        {
            crmProduct.setAvatar(crmProduct.getCoverImage());
        }
        if (StringUtils.isNotBlank(crmProduct.getShortDescription()))
        {
            crmProduct.setDescription(crmProduct.getShortDescription());
        }
        int rows = crmProductMapper.updateCrmProduct(crmProduct);
        if (rows > 0)
        {
            bindingMapper.markDirtyByProductId(crmProduct.getProductId(), safeUsername());
            knowledgeIngestService.onProductChanged(crmProduct.getProductId());
        }
        return rows;
    }

    @Override
    public int deleteCrmProductByProductIds(String[] productIds)
    {
        if (productIds == null || productIds.length == 0)
        {
            return 0;
        }
        for (String productId : productIds)
        {
            assertCanSoftDelete(productId);
        }
        int rows = crmProductMapper.logicalDeleteByProductIds(productIds, safeUsername());
        if (rows > 0)
        {
            for (String productId : productIds)
            {
                knowledgeIngestService.onProductDeleted(productId);
            }
        }
        return rows;
    }

    @Override
    public int deleteCrmProductByProductId(String productId)
    {
        assertCanSoftDelete(productId);
        int rows = crmProductMapper.logicalDeleteByProductId(productId, safeUsername());
        if (rows > 0)
        {
            knowledgeIngestService.onProductDeleted(productId);
        }
        return rows;
    }

    private void assertCanSoftDelete(String productId)
    {
        CrmProduct product = crmProductMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            throw new ServiceException("产品不存在或已删除: " + productId, HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isNotBlank(product.getWecomExternalId())
            || StringUtils.isNotBlank(product.getWecomProductId()))
        {
            throw new ServiceException("产品已同步企微，请先从企微删除后再删除本地产品: " + productId, HttpStatus.BAD_REQUEST);
        }
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
