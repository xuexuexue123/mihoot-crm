package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 转化历史对象 crm_conversion_history
 * 
 * @author luohd
 * @date 2025-04-29
 */
public class CrmConversionHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String id;

    /** 编号 */
    @Excel(name = "编号")
    private String leadId;

    /** 线索名 */
    @Excel(name = "线索名")
    private String leadName;

    /** 转化类型 */
    @Excel(name = "转化类型")
    private String conversionType;

    /** 编号 */
    @Excel(name = "编号")
    private String targetId;

    /** 名称 */
    @Excel(name = "名称")
    private String targetName;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String convertedBy;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setLeadId(String leadId) 
    {
        this.leadId = leadId;
    }

    public String getLeadId() 
    {
        return leadId;
    }
    public void setLeadName(String leadName) 
    {
        this.leadName = leadName;
    }

    public String getLeadName() 
    {
        return leadName;
    }
    public void setConversionType(String conversionType) 
    {
        this.conversionType = conversionType;
    }

    public String getConversionType() 
    {
        return conversionType;
    }
    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }
    public void setTargetName(String targetName) 
    {
        this.targetName = targetName;
    }

    public String getTargetName() 
    {
        return targetName;
    }
    public void setConvertedBy(String convertedBy) 
    {
        this.convertedBy = convertedBy;
    }

    public String getConvertedBy() 
    {
        return convertedBy;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("leadId", getLeadId())
            .append("leadName", getLeadName())
            .append("conversionType", getConversionType())
            .append("targetId", getTargetId())
            .append("targetName", getTargetName())
            .append("convertedBy", getConvertedBy())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .toString();
    }
}
