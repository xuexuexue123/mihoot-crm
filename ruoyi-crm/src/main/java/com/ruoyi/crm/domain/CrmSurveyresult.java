package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 调查结论对象 crm_surveyresult
 * 
 * @author luohd
 * @date 2024-05-18
 */
public class CrmSurveyresult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String resultId;

    /** 模版编号 */
    @Excel(name = "模版编号")
    private String templateId;

    /** 模版名称 */
    @Excel(name = "模版名称")
    private String templateName;

    /** 归属人ID */
    @Excel(name = "归属人ID")
    private String relateId;

    /** 归属类型 */
    @Excel(name = "归属类型")
    private String relateType;

    /** 归属名称 */
    @Excel(name = "归属名称")
    private String relateName;

    /** 调查名称 */
    @Excel(name = "调查名称")
    private String surveyName;

    /** 归属类型 */
    @Excel(name = "归属类型")
    private String surveyAttachment;

    /** 调查结论 */
    @Excel(name = "调查结论")
    private String surveyResult;

    public void setResultId(String resultId) 
    {
        this.resultId = resultId;
    }

    public String getResultId() 
    {
        return resultId;
    }
    public void setTemplateId(String templateId) 
    {
        this.templateId = templateId;
    }

    public String getTemplateId() 
    {
        return templateId;
    }
    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
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
    public void setSurveyName(String surveyName) 
    {
        this.surveyName = surveyName;
    }

    public String getSurveyName() 
    {
        return surveyName;
    }
    public void setSurveyAttachment(String surveyAttachment) 
    {
        this.surveyAttachment = surveyAttachment;
    }

    public String getSurveyAttachment() 
    {
        return surveyAttachment;
    }
    public void setSurveyResult(String surveyResult) 
    {
        this.surveyResult = surveyResult;
    }

    public String getSurveyResult() 
    {
        return surveyResult;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("resultId", getResultId())
            .append("templateId", getTemplateId())
            .append("templateName", getTemplateName())
            .append("relateId", getRelateId())
            .append("relateType", getRelateType())
            .append("relateName", getRelateName())
            .append("surveyName", getSurveyName())
            .append("surveyAttachment", getSurveyAttachment())
            .append("surveyResult", getSurveyResult())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
