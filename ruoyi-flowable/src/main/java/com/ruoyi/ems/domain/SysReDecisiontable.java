package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 决策表对象 sys_re_decisiontable
 * 
 * @author luohd
 * @date 2023-03-30
 */
public class SysReDecisiontable extends BaseEntity
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

    /** 决策表头 */
    @Excel(name = "决策表头")
    private String decisionTableHeader;

    /** 决策表 */
    @Excel(name = "决策表")
    private String decisionTable;

    /** 决策表规则 */
    @Excel(name = "决策表")
    private String decisionTableRule;

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
    public void setDecisionTableHeader(String decisionTableHeader) 
    {
        this.decisionTableHeader = decisionTableHeader;
    }

    public String getDecisionTableHeader() 
    {
        return decisionTableHeader;
    }
    public void setDecisionTable(String decisionTable) 
    {
        this.decisionTable = decisionTable;
    }

    public String getDecisionTable() 
    {
        return decisionTable;
    }

    public String getDecisionTableRule() {
        return decisionTableRule;
    }

    public void setDecisionTableRule(String decisionTableRule) {
        this.decisionTableRule = decisionTableRule;
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
            .append("decisionTableHeader", getDecisionTableHeader())
            .append("decisionTable", getDecisionTable())
                .append("decisionTableRule", getDecisionTableRule())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
