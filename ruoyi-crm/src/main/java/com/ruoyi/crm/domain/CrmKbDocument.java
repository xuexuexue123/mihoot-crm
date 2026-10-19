package com.ruoyi.crm.domain;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 企业知识库文档对象 crm_kb_document
 */
public class CrmKbDocument extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_ARCHIVED = "archived";

    public static final String SOURCE_MANUAL = "manual";
    public static final String SOURCE_ORGANIZATION = "organization";
    public static final String SOURCE_PRODUCT = "product";
    public static final String SOURCE_SALES_SCRIPT = "sales_script";
    public static final String SOURCE_MARKETING_CONTENT = "marketing_content";

    public static final String INDEX_PENDING = "pending";
    public static final String INDEX_INDEXED = "indexed";
    public static final String INDEX_FAILED = "failed";
    public static final String INDEX_SKIPPED = "skipped";

    /** UUID 主键 */
    private String documentId;

    /** 标题 */
    private String title;

    /** company_intro/business/service/brand/faq/case/persona/product/marketing/other */
    private String category;

    /** 正文 Markdown */
    private String contentMd;

    /** draft / published / archived */
    private String status;

    /** manual / organization / product / sales_script / marketing_content */
    private String sourceType;

    /** 源主键；manual 可空 */
    private String sourceId;

    /** jsonb 扩展（JSON 字符串） */
    private String metadata;

    /** 正文哈希，未变更跳过重嵌 */
    private String contentHash;

    /** pending / indexed / failed / skipped */
    private String indexStatus;

    /** 最近一次索引失败原因 */
    private String indexError;

    /** 最近一次索引时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date indexTime;

    /** 逻辑删除 0 正常 / 1 已删 */
    private String delFlag;

    /** 列表查询关键字（非表字段）：匹配 title + content_md */
    private String keyword;

    public String getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getContentMd()
    {
        return contentMd;
    }

    public void setContentMd(String contentMd)
    {
        this.contentMd = contentMd;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getSourceId()
    {
        return sourceId;
    }

    public void setSourceId(String sourceId)
    {
        this.sourceId = sourceId;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }

    public String getContentHash()
    {
        return contentHash;
    }

    public void setContentHash(String contentHash)
    {
        this.contentHash = contentHash;
    }

    public String getIndexStatus()
    {
        return indexStatus;
    }

    public void setIndexStatus(String indexStatus)
    {
        this.indexStatus = indexStatus;
    }

    public String getIndexError()
    {
        return indexError;
    }

    public void setIndexError(String indexError)
    {
        this.indexError = indexError;
    }

    public Date getIndexTime()
    {
        return indexTime;
    }

    public void setIndexTime(Date indexTime)
    {
        this.indexTime = indexTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("documentId", getDocumentId())
            .append("title", getTitle())
            .append("category", getCategory())
            .append("status", getStatus())
            .append("sourceType", getSourceType())
            .append("sourceId", getSourceId())
            .append("indexStatus", getIndexStatus())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
