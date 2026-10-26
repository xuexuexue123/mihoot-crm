package com.ruoyi.crm.domain.dto;

/**
 * 邮件 AI 分析情感子对象
 */
public class EmailAnalysisSentimentDto
{
    private String label;
    private Integer score;
    private String description;

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public Integer getScore()
    {
        return score;
    }

    public void setScore(Integer score)
    {
        this.score = score;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
