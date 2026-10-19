package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 阶段记录对象 crm_stagehistory
 * 
 * @author luohd
 * @date 2024-02-07
 */
public class CrmStagehistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String shId;

    /** 阶段 */
    @Excel(name = "阶段")
    private String stage;

    /** 机会编号 */
    @Excel(name = "机会编号")
    private String optyId;

    /** 名称 */
    @Excel(name = "名称")
    private String optyName;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal optyAmount;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 持续时长 */
    @Excel(name = "持续时长")
    private Long duration;

    public void setShId(String shId) 
    {
        this.shId = shId;
    }

    public String getShId() 
    {
        return shId;
    }
    public void setStage(String stage) 
    {
        this.stage = stage;
    }

    public String getStage() 
    {
        return stage;
    }
    public void setOptyId(String optyId) 
    {
        this.optyId = optyId;
    }

    public String getOptyId() 
    {
        return optyId;
    }
    public void setOptyName(String optyName) 
    {
        this.optyName = optyName;
    }

    public String getOptyName() 
    {
        return optyName;
    }
    public void setOptyAmount(BigDecimal optyAmount) 
    {
        this.optyAmount = optyAmount;
    }

    public BigDecimal getOptyAmount() 
    {
        return optyAmount;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("shId", getShId())
            .append("stage", getStage())
            .append("optyId", getOptyId())
            .append("optyName", getOptyName())
            .append("optyAmount", getOptyAmount())
            .append("owner", getOwner())
            .append("duration", getDuration())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
