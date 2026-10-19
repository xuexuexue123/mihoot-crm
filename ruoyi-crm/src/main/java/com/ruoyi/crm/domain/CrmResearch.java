package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 研报对象 crm_research
 * 
 * @author luohd
 * @date 2024-10-03
 */
public class CrmResearch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String researchId;

    /** 类别 */
    @Excel(name = "类别")
    private String category;

    /** 标题 */
    @Excel(name = "标题")
    private String subject;

    /** 发表时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发表时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date releaseDate;

    /** 行业 */
    @Excel(name = "行业")
    private String industry;

    /** 收藏 */
    @Excel(name = "收藏")
    private Long favorite;

    /** AI观点 */
    @Excel(name = "AI观点")
    private String aiSummary;

    /** 目录 */
    @Excel(name = "目录")
    private String catalog;

    /** 关联文件 */
    @Excel(name = "关联文件")
    private String commentFile;

    public void setResearchId(String researchId) 
    {
        this.researchId = researchId;
    }

    public String getResearchId() 
    {
        return researchId;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setReleaseDate(Date releaseDate) 
    {
        this.releaseDate = releaseDate;
    }

    public Date getReleaseDate() 
    {
        return releaseDate;
    }
    public void setIndustry(String industry) 
    {
        this.industry = industry;
    }

    public String getIndustry() 
    {
        return industry;
    }
    public void setFavorite(Long favorite) 
    {
        this.favorite = favorite;
    }

    public Long getFavorite() 
    {
        return favorite;
    }
    public void setAiSummary(String aiSummary) 
    {
        this.aiSummary = aiSummary;
    }

    public String getAiSummary() 
    {
        return aiSummary;
    }
    public void setCatalog(String catalog) 
    {
        this.catalog = catalog;
    }

    public String getCatalog() 
    {
        return catalog;
    }
    public void setCommentFile(String commentFile) 
    {
        this.commentFile = commentFile;
    }

    public String getCommentFile() 
    {
        return commentFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("researchId", getResearchId())
            .append("category", getCategory())
            .append("subject", getSubject())
            .append("releaseDate", getReleaseDate())
            .append("industry", getIndustry())
            .append("favorite", getFavorite())
            .append("aiSummary", getAiSummary())
            .append("catalog", getCatalog())
            .append("commentFile", getCommentFile())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
