package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * owner记录对象 crm_ownerhistory
 * 
 * @author luohd
 * @date 2024-01-20
 */
public class CrmOwnerhistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String ohId;

    /** 目标类别 */
    @Excel(name = "目标类别")
    private String ohCategory;

    /** 目标编号 */
    @Excel(name = "目标编号")
    private String targetId;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 变更原因 */
    @Excel(name = "变更原因")
    private String changeReason;

    public void setOhId(String ohId) 
    {
        this.ohId = ohId;
    }

    public String getOhId() 
    {
        return ohId;
    }
    public void setOhCategory(String ohCategory) 
    {
        this.ohCategory = ohCategory;
    }

    public String getOhCategory() 
    {
        return ohCategory;
    }
    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setChangeReason(String changeReason) 
    {
        this.changeReason = changeReason;
    }

    public String getChangeReason() 
    {
        return changeReason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ohId", getOhId())
            .append("ohCategory", getOhCategory())
            .append("targetId", getTargetId())
            .append("owner", getOwner())
            .append("changeReason", getChangeReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
