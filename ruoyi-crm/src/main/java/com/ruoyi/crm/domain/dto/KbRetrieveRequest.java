package com.ruoyi.crm.domain.dto;

import java.util.List;

/**
 * POST /crm/knowledge/retrieve 请求体
 */
public class KbRetrieveRequest
{
    /** 必填：用户问题 / 检索句 */
    private String query;

    /** 默认 8，上限 20 */
    private Integer topK;

    /** 可选分类过滤；空 = 不限 */
    private List<String> categories;

    /** 可选：余弦相似度阈值，默认 0.2 */
    private Double minScore;

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public Integer getTopK()
    {
        return topK;
    }

    public void setTopK(Integer topK)
    {
        this.topK = topK;
    }

    public List<String> getCategories()
    {
        return categories;
    }

    public void setCategories(List<String> categories)
    {
        this.categories = categories;
    }

    public Double getMinScore()
    {
        return minScore;
    }

    public void setMinScore(Double minScore)
    {
        this.minScore = minScore;
    }
}
