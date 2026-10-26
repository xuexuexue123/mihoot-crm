package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * activity对象 sys_re_activity
 * 
 * @author luohd
 * @date 2023-03-13
 */
public class SysReActivity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** 应用Id */
    @Excel(name = "应用Id")
    private String caseTypeId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 目标 */
    @Excel(name = "目标")
    private String target;

    public String getPyId() {
        return pyId;
    }

    public void setPyId(String pyId) {
        this.pyId = pyId;
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
    public void setTarget(String target) 
    {
        this.target = target;
    }

    public String getTarget() 
    {
        return target;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("caseTypeId", getCaseTypeId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("target", getTarget())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
