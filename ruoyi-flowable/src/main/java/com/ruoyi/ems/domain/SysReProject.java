package com.ruoyi.ems.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目对象 sys_re_project
 *
 * @author luohd
 * @date 2023-05-17
 */
public class SysReProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String pyId;

    /** 业务Id */
    @Excel(name = "业务Id")
    private String caseApplicationId;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 头像地址 */
    @Excel(name = "头像地址")
    private String avatar;

    /** 项目类型 */
    @Excel(name = "项目类型")
    private String category;

    /** 实例ID */
    @Excel(name = "实例ID")
    private String pyCaseId;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 是否归档 */
    @Excel(name = "是否归档")
    private Long archived;

    /** 项目经理 */
    @Excel(name = "项目经理")
    private String owner;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String customerName;

    /** 合同号 */
    @Excel(name = "合同号")
    private String contractId;

    /** 成本估算 */
    @Excel(name = "成本估算")
    private BigDecimal costEstimation;

    /** 实际成本 */
    @Excel(name = "实际成本")
    private BigDecimal realCost;

    /** 成本绩效指数 */
    @Excel(name = "成本绩效指数")
    private BigDecimal cpi;

    /** 预算成本 */
    @Excel(name = "预算成本")
    private BigDecimal pv;

    /** 实际成本 */
    @Excel(name = "实际成本")
    private BigDecimal ac;

    /** 挣值 */
    @Excel(name = "挣值")
    private BigDecimal ev;

    /** 进度绩效指数 */
    @Excel(name = "进度绩效指数")
    private BigDecimal spi;

    /** 预估预算成本 */
    @Excel(name = "预估预算成本")
    private BigDecimal bac;

    /** 完工估算 */
    @Excel(name = "完工估算")
    private BigDecimal eac;

    /** 完工尚需估算 */
    @Excel(name = "完工尚需估算")
    private BigDecimal etc;

    /** 工时估算 */
    @Excel(name = "工时估算")
    private BigDecimal durationEstimation;

    /** 实际工时 */
    @Excel(name = "实际工时")
    private BigDecimal realDuration;


    /** $column.columnComment */
    private String stage;

    public void setPyId(String pyId)
    {
        this.pyId = pyId;
    }

    public String getPyId()
    {
        return pyId;
    }
    public void setCaseApplicationId(String caseApplicationId)
    {
        this.caseApplicationId = caseApplicationId;
    }

    public String getCaseApplicationId()
    {
        return caseApplicationId;
    }
    public void setPyName(String pyName)
    {
        this.pyName = pyName;
    }

    public String getPyName()
    {
        return pyName;
    }
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getAvatar()
    {
        return avatar;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }
    public void setPyCaseId(String pyCaseId)
    {
        this.pyCaseId = pyCaseId;
    }

    public String getPyCaseId()
    {
        return pyCaseId;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }
    public void setArchived(Long archived)
    {
        this.archived = archived;
    }

    public Long getArchived()
    {
        return archived;
    }
    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    public String getOwner()
    {
        return owner;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerName()
    {
        return customerName;
    }
    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public String getContractId()
    {
        return contractId;
    }
    public void setCostEstimation(BigDecimal costEstimation)
    {
        this.costEstimation = costEstimation;
    }

    public BigDecimal getCostEstimation()
    {
        return costEstimation;
    }
    public void setRealCost(BigDecimal realCost)
    {
        this.realCost = realCost;
    }

    public BigDecimal getRealCost()
    {
        return realCost;
    }
    public void setCpi(BigDecimal cpi)
    {
        this.cpi = cpi;
    }

    public BigDecimal getCpi()
    {
        return cpi;
    }
    public void setPv(BigDecimal pv)
    {
        this.pv = pv;
    }

    public BigDecimal getPv()
    {
        return pv;
    }
    public void setAc(BigDecimal ac)
    {
        this.ac = ac;
    }

    public BigDecimal getAc()
    {
        return ac;
    }
    public void setEv(BigDecimal ev)
    {
        this.ev = ev;
    }

    public BigDecimal getEv()
    {
        return ev;
    }
    public void setSpi(BigDecimal spi)
    {
        this.spi = spi;
    }

    public BigDecimal getSpi()
    {
        return spi;
    }
    public void setBac(BigDecimal bac)
    {
        this.bac = bac;
    }

    public BigDecimal getBac()
    {
        return bac;
    }
    public void setEac(BigDecimal eac)
    {
        this.eac = eac;
    }

    public BigDecimal getEac()
    {
        return eac;
    }
    public void setEtc(BigDecimal etc)
    {
        this.etc = etc;
    }

    public BigDecimal getEtc()
    {
        return etc;
    }
    public void setDurationEstimation(BigDecimal durationEstimation)
    {
        this.durationEstimation = durationEstimation;
    }

    public BigDecimal getDurationEstimation()
    {
        return durationEstimation;
    }
    public void setRealDuration(BigDecimal realDuration)
    {
        this.realDuration = realDuration;
    }

    public BigDecimal getRealDuration()
    {
        return realDuration;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getStage()
    {
        return stage;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("pyId", getPyId())
                .append("caseApplicationId", getCaseApplicationId())
                .append("pyName", getPyName())
                .append("avatar", getAvatar())
                .append("category", getCategory())
                .append("pyCaseId", getPyCaseId())
                .append("description", getDescription())
                .append("archived", getArchived())
                .append("owner", getOwner())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("status", getStatus())
                .append("customerName", getCustomerName())
                .append("contractId", getContractId())
                .append("costEstimation", getCostEstimation())
                .append("realCost", getRealCost())
                .append("cpi", getCpi())
                .append("pv", getPv())
                .append("ac", getAc())
                .append("ev", getEv())
                .append("spi", getSpi())
                .append("bac", getBac())
                .append("eac", getEac())
                .append("etc", getEtc())
                .append("durationEstimation", getDurationEstimation())
                .append("realDuration", getRealDuration())
                .toString();
    }
}
