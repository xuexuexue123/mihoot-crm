package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 联系方式对象 crm_contactmethod
 * 
 * @author luohd
 * @date 2024-03-16
 */
public class CrmContactmethod extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String methodId;

    /** 类型(电话、微信、手机、email、地址) */
    @Excel(name = "类型(电话、微信、手机、email、地址)")
    private String methodType;

    /** 内容 */
    @Excel(name = "内容")
    private String detail;

    /** 主联系方式 */
    @Excel(name = "主联系方式")
    private Long isPrimary;

    /** 关联类型(联系人、客户、组织) */
    @Excel(name = "关联类型(联系人、客户、组织)")
    private String relateType;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 联系人ID */
    @Excel(name = "联系人ID")
    private String sourceId;

    public void setMethodId(String methodId) 
    {
        this.methodId = methodId;
    }

    public String getMethodId() 
    {
        return methodId;
    }
    public void setMethodType(String methodType) 
    {
        this.methodType = methodType;
    }

    public String getMethodType() 
    {
        return methodType;
    }
    public void setDetail(String detail) 
    {
        this.detail = detail;
    }

    public String getDetail() 
    {
        return detail;
    }
    public void setIsPrimary(Long isPrimary) 
    {
        this.isPrimary = isPrimary;
    }

    public Long getIsPrimary() 
    {
        return isPrimary;
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
    public void setSourceId(String sourceId) 
    {
        this.sourceId = sourceId;
    }

    public String getSourceId() 
    {
        return sourceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("methodId", getMethodId())
            .append("methodType", getMethodType())
            .append("detail", getDetail())
            .append("isPrimary", getIsPrimary())
            .append("relateType", getRelateType())
            .append("relateName", getRelateName())
            .append("sourceId", getSourceId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
