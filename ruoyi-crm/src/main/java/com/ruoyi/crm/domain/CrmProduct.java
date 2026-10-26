package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 产品对象 crm_product（产品中心主档）
 *
 * @author luohd
 * @date 2024-02-06
 */
public class CrmProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_ARCHIVED = "archived";

    /** id */
    private String productId;

    /** 名称 */
    @Excel(name = "名称")
    private String productName;

    /** 拼音 */
    @Excel(name = "拼音")
    private String productPinyin;

    /** 通用名 */
    @Excel(name = "通用名")
    private String productLabel;

    /** 编码 */
    @Excel(name = "编码")
    private String productCode;

    /** 本位码 */
    @Excel(name = "本位码")
    private String productLocalCode;

    /** 物品图片（兼容旧字段，等同 coverImage） */
    @Excel(name = "物品图片")
    private String avatar;

    /** 物品类别（legacy 字典值） */
    @Excel(name = "物品类别")
    private String category;

    /** 分类ID */
    private String categoryId;

    /** 状态 draft/active/inactive/archived */
    @Excel(name = "状态")
    private String status;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 规格 */
    @Excel(name = "规格")
    private String spec;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 条形码 */
    @Excel(name = "条形码")
    private String barcode;

    /** 生产厂家 */
    @Excel(name = "生产厂家")
    private String producer;

    /** 采购类型 */
    @Excel(name = "采购类型")
    private String purchaseCategory;

    /** 批准文号 */
    @Excel(name = "批准文号")
    private String approvedCode;

    /** 供应商 */
    @Excel(name = "供应商")
    private String provider;

    /** 最小库存 */
    @Excel(name = "最小库存")
    private String minCount;

    /** 出库售价 / 标准售价（元） */
    @Excel(name = "出库售价")
    private BigDecimal outboundPrice;

    /** 自主设立 */
    @Excel(name = "自主设立")
    private String isOwn;

    /** 自主定价 */
    @Excel(name = "自主定价")
    private String isOwnPrice;

    /** 描述（兼容旧字段） */
    @Excel(name = "描述")
    private String description;

    /** 仓库ID */
    @Excel(name = "仓库ID")
    private String invId;

    /** 仓库名 */
    @Excel(name = "仓库名")
    private String invName;

    /** 企业微信商品图册 product_id（legacy） */
    private String wecomProductId;

    /** 搜索关键词 */
    private String searchKeywords;

    /** 短描述 */
    private String shortDescription;

    /** 销售卖点 */
    private String sellingPoints;

    /** 详细介绍 Markdown */
    private String detailMd;

    /** 封面图 */
    private String coverImage;

    /** 发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishedTime;

    /** 停售时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date disabledTime;

    /** 行业扩展属性 jsonb 字符串 */
    private String industryAttributes;

    /** 逻辑删除 0正常 1已删 */
    private String delFlag;

    /** 乐观锁版本 */
    private Integer version;

    /** 查询：关键词（非表字段） */
    private String keyword;

    /** 查询：更新起止（非表字段） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedFrom;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTo;

    /** 聚合：分类名称 */
    private String categoryName;

    /** 聚合：媒体列表 */
    private List<CrmProductMedia> mediaList;

    /** 聚合：企微渠道状态 */
    private String wecomSyncStatus;

    /** 聚合：企微 externalId（优先 binding） */
    private String wecomExternalId;

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public String getProductId()
    {
        return productId;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductPinyin(String productPinyin)
    {
        this.productPinyin = productPinyin;
    }

    public String getProductPinyin()
    {
        return productPinyin;
    }

    public void setProductLabel(String productLabel)
    {
        this.productLabel = productLabel;
    }

    public String getProductLabel()
    {
        return productLabel;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductLocalCode(String productLocalCode)
    {
        this.productLocalCode = productLocalCode;
    }

    public String getProductLocalCode()
    {
        return productLocalCode;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }

    public String getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setSpec(String spec)
    {
        this.spec = spec;
    }

    public String getSpec()
    {
        return spec;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getModel()
    {
        return model;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBarcode(String barcode)
    {
        this.barcode = barcode;
    }

    public String getBarcode()
    {
        return barcode;
    }

    public void setProducer(String producer)
    {
        this.producer = producer;
    }

    public String getProducer()
    {
        return producer;
    }

    public void setPurchaseCategory(String purchaseCategory)
    {
        this.purchaseCategory = purchaseCategory;
    }

    public String getPurchaseCategory()
    {
        return purchaseCategory;
    }

    public void setApprovedCode(String approvedCode)
    {
        this.approvedCode = approvedCode;
    }

    public String getApprovedCode()
    {
        return approvedCode;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setMinCount(String minCount)
    {
        this.minCount = minCount;
    }

    public String getMinCount()
    {
        return minCount;
    }

    public void setOutboundPrice(BigDecimal outboundPrice)
    {
        this.outboundPrice = outboundPrice;
    }

    public BigDecimal getOutboundPrice()
    {
        return outboundPrice;
    }

    /** 标准售价别名（与 outboundPrice 同源） */
    public BigDecimal getStandardPrice()
    {
        return outboundPrice;
    }

    public void setStandardPrice(BigDecimal standardPrice)
    {
        this.outboundPrice = standardPrice;
    }

    public void setIsOwn(String isOwn)
    {
        this.isOwn = isOwn;
    }

    public String getIsOwn()
    {
        return isOwn;
    }

    public void setIsOwnPrice(String isOwnPrice)
    {
        this.isOwnPrice = isOwnPrice;
    }

    public String getIsOwnPrice()
    {
        return isOwnPrice;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setInvId(String invId)
    {
        this.invId = invId;
    }

    public String getInvId()
    {
        return invId;
    }

    public void setInvName(String invName)
    {
        this.invName = invName;
    }

    public String getInvName()
    {
        return invName;
    }

    public void setWecomProductId(String wecomProductId)
    {
        this.wecomProductId = wecomProductId;
    }

    public String getWecomProductId()
    {
        return wecomProductId;
    }

    public String getSearchKeywords()
    {
        return searchKeywords;
    }

    public void setSearchKeywords(Object searchKeywords)
    {
        this.searchKeywords = coerceToText(searchKeywords, ",");
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getSellingPoints()
    {
        return sellingPoints;
    }

    public void setSellingPoints(Object sellingPoints)
    {
        this.sellingPoints = coerceToText(sellingPoints, "\n");
    }

    public String getDetailMd()
    {
        return detailMd;
    }

    public void setDetailMd(String detailMd)
    {
        this.detailMd = detailMd;
    }

    public String getCoverImage()
    {
        return coverImage;
    }

    public void setCoverImage(String coverImage)
    {
        this.coverImage = coverImage;
    }

    public Date getPublishedTime()
    {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime)
    {
        this.publishedTime = publishedTime;
    }

    public Date getDisabledTime()
    {
        return disabledTime;
    }

    public void setDisabledTime(Date disabledTime)
    {
        this.disabledTime = disabledTime;
    }

    public String getIndustryAttributes()
    {
        return industryAttributes;
    }

    public void setIndustryAttributes(String industryAttributes)
    {
        this.industryAttributes = industryAttributes;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    public Date getUpdatedFrom()
    {
        return updatedFrom;
    }

    public void setUpdatedFrom(Date updatedFrom)
    {
        this.updatedFrom = updatedFrom;
    }

    public Date getUpdatedTo()
    {
        return updatedTo;
    }

    public void setUpdatedTo(Date updatedTo)
    {
        this.updatedTo = updatedTo;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public List<CrmProductMedia> getMediaList()
    {
        return mediaList;
    }

    public void setMediaList(List<CrmProductMedia> mediaList)
    {
        this.mediaList = mediaList;
    }

    /** 前端字段名 media → mediaList */
    @com.fasterxml.jackson.annotation.JsonProperty("media")
    public void setMedia(List<CrmProductMedia> media)
    {
        this.mediaList = media;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("media")
    public List<CrmProductMedia> getMedia()
    {
        return mediaList;
    }

    /**
     * 兼容前端传 string 或 string[]。
     */
    private static String coerceToText(Object value, String joiner)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        if (value instanceof Iterable)
        {
            StringBuilder sb = new StringBuilder();
            for (Object item : (Iterable<?>) value)
            {
                if (item == null)
                {
                    continue;
                }
                String text = String.valueOf(item).trim();
                if (text.isEmpty())
                {
                    continue;
                }
                if (sb.length() > 0)
                {
                    sb.append(joiner);
                }
                sb.append(text);
            }
            return sb.toString();
        }
        return String.valueOf(value);
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("productCode", getProductCode())
            .append("categoryId", getCategoryId())
            .append("status", getStatus())
            .append("outboundPrice", getOutboundPrice())
            .append("delFlag", getDelFlag())
            .append("version", getVersion())
            .append("wecomProductId", getWecomProductId())
            .toString();
    }
}
