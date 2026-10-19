package com.ruoyi.crm.domain.vo;

/**
 * 知识检索命中（chunk + 文档来源信息）
 */
public class KbRetrieveHitVo
{
    private String chunkId;
    private String documentId;
    private String title;
    private String category;
    private String sourceType;
    private String sourceId;
    private String chunkText;
    private Double score;

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

    public String getChunkText()
    {
        return chunkText;
    }

    public void setChunkText(String chunkText)
    {
        this.chunkText = chunkText;
    }

    public Double getScore()
    {
        return score;
    }

    public void setScore(Double score)
    {
        this.score = score;
    }
}
