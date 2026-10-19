package com.ruoyi.ems.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.TreeEntity;

/**
 * 数据转换项对象 sys_re_dtitem
 * 
 * @author luohd
 * @date 2023-03-09
 */
public class SysReDtitem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** dtID */
    private Long dtId;

    /** 排序 */
    @Excel(name = "排序")
    private Long pySort;

    /** 编码 */
    private String parentId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 操作 */
    @Excel(name = "操作")
    private String action;

    /** 目标 */
    @Excel(name = "目标")
    private String target;

    /** 关系 */
    @Excel(name = "关系")
    private String relation;

    /** 来源 */
    @Excel(name = "来源")
    private String source;

    /** case编码 */
    @Excel(name = "case编码")
    private String caseTypeId;


    public void setPyId(String pyId)
    {
        this.pyId = pyId;
    }

    public String getPyId()
    {
        return pyId;
    }

    public void setParentId(String parentId)
    {
        this.parentId = parentId;
    }

    public String getParentId()
    {
        return parentId;
    }

    public void setDtId(Long dtId) 
    {
        this.dtId = dtId;
    }

    public Long getDtId() 
    {
        return dtId;
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
    public void setTarget(String target) 
    {
        this.target = target;
    }

    public String getTarget() 
    {
        return target;
    }
    public void setRelation(String relation) 
    {
        this.relation = relation;
    }

    public String getRelation() 
    {
        return relation;
    }
    public void setSource(String source) 
    {
        this.source = source;
    }

    public String getSource() 
    {
        return source;
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
            .append("dtId", getDtId())
            .append("parentId", getParentId())
            .append("pySort", getPySort())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("action", getAction())
            .append("target", getTarget())
            .append("relation", getRelation())
            .append("source", getSource())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
