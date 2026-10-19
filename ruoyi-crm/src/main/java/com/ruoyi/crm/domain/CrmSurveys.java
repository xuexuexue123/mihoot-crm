package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 调查对象 crm_surveys
 * 
 * @author luohd
 * @date 2025-03-29
 */
public class CrmSurveys extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String id;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String relateId;

    /** 关联类型 */
    @Excel(name = "关联类型")
    private String relateType;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 名称 */
    @Excel(name = "名称")
    private String title;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 调查问题 */
    @Excel(name = "调查问题")
    private String surveyJson;

    /** 总答卷数 */
    @Excel(name = "总答卷数")
    private Long totalResponses;

    /** 完成率 */
    @Excel(name = "完成率")
    private BigDecimal completionRate;

    /** NPS 评分 */
    @Excel(name = "NPS 评分")
    private BigDecimal npsScore;

    /** 已发送 */
    @Excel(name = "已发送")
    private Long sent;

    /** 限制1个 */
    @Excel(name = "限制1个")
    private Long limitOne;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setRelateType(String relateType) 
    {
        this.relateType = relateType;
    }

    public String getRelateType() 
    {
        return relateType;
    }
    public void setRelateName(String relateName) 
    {
        this.relateName = relateName;
    }

    public String getRelateName() 
    {
        return relateName;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }
    public void setSurveyJson(String surveyJson) 
    {
        this.surveyJson = surveyJson;
    }

    public String getSurveyJson() 
    {
        return surveyJson;
    }
    public void setTotalResponses(Long totalResponses) 
    {
        this.totalResponses = totalResponses;
    }

    public Long getTotalResponses() 
    {
        return totalResponses;
    }
    public void setCompletionRate(BigDecimal completionRate) 
    {
        this.completionRate = completionRate;
    }

    public BigDecimal getCompletionRate() 
    {
        return completionRate;
    }
    public void setNpsScore(BigDecimal npsScore) 
    {
        this.npsScore = npsScore;
    }

    public BigDecimal getNpsScore() 
    {
        return npsScore;
    }

    public void setSent(Long sent)
    {
        this.sent = sent;
    }

    public Long getSent()
    {
        return sent;
    }
    public void setLimitOne(Long limitOne)
    {
        this.limitOne = limitOne;
    }

    public Long getLimitOne()
    {
        return limitOne;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("relateId", getRelateId())
            .append("relateType", getRelateType())
            .append("relateName", getRelateName())
            .append("title", getTitle())
            .append("description", getDescription())
            .append("status", getStatus())
            .append("surveyJson", getSurveyJson())
            .append("totalResponses", getTotalResponses())
            .append("completionRate", getCompletionRate())
            .append("npsScore", getNpsScore())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
