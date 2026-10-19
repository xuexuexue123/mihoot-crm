package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * activity项对象 sys_re_actitem
 * 
 * @author luohd
 * @date 2023-03-14
 */
public class SysReActitem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** actID */
    @Excel(name = "actID")
    private String actId;

    /** 父节点 */
    @Excel(name = "父节点")
    private String parentId;

    /** 排序 */
    @Excel(name = "排序")
    private Long pySort;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 操作 */
    @Excel(name = "操作")
    private String action;

    /** 页名 */
    @Excel(name = "页名")
    private String stepPage;

    /** 属性名 */
    @Excel(name = "属性名")
    private String propertyName;

    /** 属性名 */
    @Excel(name = "属性名")
    private String propertyValue;


    /** 应用Id */
    @Excel(name = "应用Id")
    private String caseTypeId;

    public void setPyId(String pyId) 
    {
        this.pyId = pyId;
    }

    public String getPyId() 
    {
        return pyId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getParentId() 
    {
        return parentId;
    }
    public void setPySort(Long pySort) 
    {
        this.pySort = pySort;
    }

    public Long getPySort() 
    {
        return pySort;
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
    public void setAction(String action) 
    {
        this.action = action;
    }

    public String getAction() 
    {
        return action;
    }
    public void setStepPage(String stepPage) 
    {
        this.stepPage = stepPage;
    }

    public String getStepPage() 
    {
        return stepPage;
    }
    public void setPropertyName(String propertyName) 
    {
        this.propertyName = propertyName;
    }

    public String getPropertyName() 
    {
        return propertyName;
    }
    public void setPropertyValue(String propertyValue) 
    {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() 
    {
        return propertyValue;
    }

    public String getCaseTypeId() {
        return caseTypeId;
    }

    public void setCaseTypeId(String caseTypeId) {
        this.caseTypeId = caseTypeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("actId", getActId())
            .append("parentId", getParentId())
            .append("pySort", getPySort())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("action", getAction())
            .append("stepPage", getStepPage())
            .append("propertyName", getPropertyName())
            .append("propertyValue", getPropertyValue())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
