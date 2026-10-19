package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据转换对象 sys_re_datatransform
 * 
 * @author luohd
 * @date 2023-03-09
 */
public class SysReDatatransform extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private Long pyId;

    /** case编码 */
    @Excel(name = "case编码")
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

    /** 来源 */
    @Excel(name = "来源")
    private String source;

    public void setPyId(Long pyId) 
    {
        this.pyId = pyId;
    }

    public Long getPyId() 
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
    public void setTarget(String target) 
    {
        this.target = target;
    }

    public String getTarget() 
    {
        return target;
    }
    public void setSource(String source) 
    {
        this.source = source;
    }

    public String getSource() 
    {
        return source;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("caseTypeId", getCaseTypeId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("target", getTarget())
            .append("source", getSource())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
