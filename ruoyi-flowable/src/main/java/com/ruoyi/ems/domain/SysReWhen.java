package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * WhenRule对象 sys_re_when
 * 
 * @author luohd
 * @date 2023-04-02
 */
public class SysReWhen extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** 业务Id */
    @Excel(name = "业务Id")
    private String caseTypeId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 结果属性 */
    @Excel(name = "结果属性")
    private String resultProperty;

    /** 规则表达式 */
    private String whenExpression;

    /** 表达式关系 */
    private String whenRelationship;

    private String whenLogicString;

    public void setPyId(String pyId) 
    {
        this.pyId = pyId;
    }

    public String getPyId() 
    {
        return pyId;
    }
    public void setCaseTypeId(String caseTypeId) 
    {
        this.caseTypeId = caseTypeId;
    }

    public String getCaseTypeId() 
    {
        return caseTypeId;
    }
    public void setPyLabel(String pyLabel) 
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setPyName(String pyName) 
    {
        this.pyName = pyName;
    }

    public String getPyName() 
    {
        return pyName;
    }
    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }
    public void setResultProperty(String resultProperty) 
    {
        this.resultProperty = resultProperty;
    }

    public String getResultProperty() 
    {
        return resultProperty;
    }
    public void setWhenExpression(String whenExpression) 
    {
        this.whenExpression = whenExpression;
    }

    public String getWhenExpression() 
    {
        return whenExpression;
    }
    public void setWhenRelationship(String whenRelationship) 
    {
        this.whenRelationship = whenRelationship;
    }

    public String getWhenRelationship() 
    {
        return whenRelationship;
    }

    public String getWhenLogicString() {
        return whenLogicString;
    }

    public void setWhenLogicString(String whenLogicString) {
        this.whenLogicString = whenLogicString;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("caseTypeId", getCaseTypeId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("version", getVersion())
            .append("resultProperty", getResultProperty())
            .append("whenExpression", getWhenExpression())
            .append("whenRelationship", getWhenRelationship())
                .append("whenLogicString", getWhenLogicString())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
