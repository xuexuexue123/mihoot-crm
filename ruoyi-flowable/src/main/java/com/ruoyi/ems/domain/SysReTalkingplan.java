package com.ruoyi.ems.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 沟通计划对象 sys_re_talkingplan
 * 
 * @author luohd
 * @date 2023-05-19
 */
public class SysReTalkingplan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String planId;

    /** 项目Id */
    @Excel(name = "项目Id")
    private String projectId;

    /** 利益干系人 */
    @Excel(name = "利益干系人")
    private String counterParty;

    /** 所需信息 */
    @Excel(name = "所需信息")
    private String requiredInfo;

    /** 频率 */
    @Excel(name = "频率")
    private String frequency;

    /** 沟通方法 */
    @Excel(name = "沟通方法")
    private String preferWay;

    /** 详细方法 */
    @Excel(name = "详细方法")
    private String detailInfo;

    /** 责任人 */
    @Excel(name = "责任人")
    private String owner;

    public void setPlanId(String planId) 
    {
        this.planId = planId;
    }

    public String getPlanId() 
    {
        return planId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setCounterParty(String counterParty) 
    {
        this.counterParty = counterParty;
    }

    public String getCounterParty() 
    {
        return counterParty;
    }
    public void setRequiredInfo(String requiredInfo) 
    {
        this.requiredInfo = requiredInfo;
    }

    public String getRequiredInfo() 
    {
        return requiredInfo;
    }
    public void setFrequency(String frequency) 
    {
        this.frequency = frequency;
    }

    public String getFrequency() 
    {
        return frequency;
    }
    public void setPreferWay(String preferWay) 
    {
        this.preferWay = preferWay;
    }

    public String getPreferWay() 
    {
        return preferWay;
    }
    public void setDetailInfo(String detailInfo) 
    {
        this.detailInfo = detailInfo;
    }

    public String getDetailInfo() 
    {
        return detailInfo;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("planId", getPlanId())
            .append("projectId", getProjectId())
            .append("counterParty", getCounterParty())
            .append("requiredInfo", getRequiredInfo())
            .append("frequency", getFrequency())
            .append("preferWay", getPreferWay())
            .append("detailInfo", getDetailInfo())
            .append("owner", getOwner())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
