package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 尽职调查对象 crm_dd
 * 
 * @author luohd
 * @date 2024-05-01
 */
public class CrmDd extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private String ddId;

    /** 编码 */
    @Excel(name = "编码")
    private String ddCode;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String relateId;

    /** 类型 */
    @Excel(name = "类型")
    private String category;

    /** 问题组 */
    @Excel(name = "问题组")
    private String groupName;

    /** 关键点 */
    @Excel(name = "关键点")
    private String keyPoints;

    /** 问题 */
    @Excel(name = "问题")
    private String question;

    /** 排序 */
    @Excel(name = "排序")
    private Long questionOrder;

    /** 字段类型 */
    @Excel(name = "字段类型")
    private String fieldType;

    /** 字段配置 */
    @Excel(name = "字段配置")
    private String fieldConfig;

    /** 回答 */
    @Excel(name = "回答")
    private String answer;

    /** 附件名 */
    @Excel(name = "附件名")
    private String answerFilename;

    /** 附件ID */
    @Excel(name = "附件ID")
    private String answerCorresid;

    /** 备注 */
    @Excel(name = "备注")
    private String description;

    /** 场景 */
    @Excel(name = "场景")
    private String scenario;

    public void setDdId(String ddId) 
    {
        this.ddId = ddId;
    }

    public String getDdId() 
    {
        return ddId;
    }
    public void setDdCode(String ddCode) 
    {
        this.ddCode = ddCode;
    }

    public String getDdCode() 
    {
        return ddCode;
    }
    public void setRelateId(String relateId) 
    {
        this.relateId = relateId;
    }

    public String getRelateId() 
    {
        return relateId;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setGroupName(String groupName) 
    {
        this.groupName = groupName;
    }

    public String getGroupName() 
    {
        return groupName;
    }
    public void setKeyPoints(String keyPoints) 
    {
        this.keyPoints = keyPoints;
    }

    public String getKeyPoints() 
    {
        return keyPoints;
    }
    public void setQuestion(String question) 
    {
        this.question = question;
    }

    public String getQuestion() 
    {
        return question;
    }
    public void setQuestionOrder(Long questionOrder) 
    {
        this.questionOrder = questionOrder;
    }

    public Long getQuestionOrder() 
    {
        return questionOrder;
    }
    public void setFieldType(String fieldType) 
    {
        this.fieldType = fieldType;
    }

    public String getFieldType() 
    {
        return fieldType;
    }
    public void setFieldConfig(String fieldConfig) 
    {
        this.fieldConfig = fieldConfig;
    }

    public String getFieldConfig() 
    {
        return fieldConfig;
    }
    public void setAnswer(String answer) 
    {
        this.answer = answer;
    }

    public String getAnswer() 
    {
        return answer;
    }
    public void setAnswerFilename(String answerFilename) 
    {
        this.answerFilename = answerFilename;
    }

    public String getAnswerFilename() 
    {
        return answerFilename;
    }
    public void setAnswerCorresid(String answerCorresid) 
    {
        this.answerCorresid = answerCorresid;
    }

    public String getAnswerCorresid() 
    {
        return answerCorresid;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setScenario(String scenario)
    {
        this.scenario = scenario;
    }

    public String getScenario()
    {
        return scenario;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ddId", getDdId())
            .append("ddCode", getDdCode())
            .append("relateId", getRelateId())
            .append("category", getCategory())
            .append("groupName", getGroupName())
            .append("keyPoints", getKeyPoints())
            .append("question", getQuestion())
            .append("questionOrder", getQuestionOrder())
            .append("fieldType", getFieldType())
            .append("fieldConfig", getFieldConfig())
            .append("answer", getAnswer())
            .append("answerFilename", getAnswerFilename())
            .append("answerCorresid", getAnswerCorresid())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
