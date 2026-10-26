package com.ruoyi.crm.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 知识库切块对象 crm_kb_chunk（embedding 为 pgvector 字面量字符串 "[v1,v2,...]"）
 */
public class CrmKbChunk implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** UUID 主键 */
    private String chunkId;

    /** 所属文档 */
    private String documentId;

    /** 块序号 */
    private Integer chunkIndex;

    /** 块文本 */
    private String chunkText;

    /** token 估算（可空） */
    private Integer tokenEstimate;

    /** jsonb 扩展（JSON 字符串） */
    private String metadata;

    /** 向量字面量 "[v1,v2,...]"，写库时 CAST 为 vector */
    private String embedding;

    private Date createTime;

    public String getChunkId()
    {
        return chunkId;
    }

    public void setChunkId(String chunkId)
    {
        this.chunkId = chunkId;
    }

    public String getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }

    public Integer getChunkIndex()
    {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex)
    {
        this.chunkIndex = chunkIndex;
    }

    public String getChunkText()
    {
        return chunkText;
    }

    public void setChunkText(String chunkText)
    {
        this.chunkText = chunkText;
    }

    public Integer getTokenEstimate()
    {
        return tokenEstimate;
    }

    public void setTokenEstimate(Integer tokenEstimate)
    {
        this.tokenEstimate = tokenEstimate;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }

    public String getEmbedding()
    {
        return embedding;
    }

    public void setEmbedding(String embedding)
    {
        this.embedding = embedding;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
