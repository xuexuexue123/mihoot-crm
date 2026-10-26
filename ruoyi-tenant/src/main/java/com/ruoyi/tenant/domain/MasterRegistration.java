package com.ruoyi.tenant.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 主注册对象 master_registration
 * 
 * @author luohd
 * @date 2024-03-17
 */
public class MasterRegistration extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long registId;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactName;

    /** 公司名 */
    @Excel(name = "公司名")
    private String companyName;

    /** 手机号 */
    @Excel(name = "手机号")
    private String mobile;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 主机地址 */
    @Excel(name = "主机地址")
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点")
    private String operLocation;

    /** 价格类别 */
    @Excel(name = "价格类别")
    private String plan;

    /** 状态（0正常 1异常） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private Long status;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date operTime;

    public void setRegistId(Long registId) 
    {
        this.registId = registId;
    }

    public Long getRegistId() 
    {
        return registId;
    }
    public void setContactName(String contactName) 
    {
        this.contactName = contactName;
    }

    public String getContactName() 
    {
        return contactName;
    }
    public void setCompanyName(String companyName) 
    {
        this.companyName = companyName;
    }

    public String getCompanyName() 
    {
        return companyName;
    }
    public void setMobile(String mobile) 
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }
    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }
    public void setOperIp(String operIp) 
    {
        this.operIp = operIp;
    }

    public String getOperIp() 
    {
        return operIp;
    }
    public void setOperLocation(String operLocation) 
    {
        this.operLocation = operLocation;
    }

    public String getOperLocation() 
    {
        return operLocation;
    }
    public void setPlan(String plan) 
    {
        this.plan = plan;
    }

    public String getPlan() 
    {
        return plan;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setOperTime(Date operTime) 
    {
        this.operTime = operTime;
    }

    public Date getOperTime() 
    {
        return operTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("registId", getRegistId())
            .append("contactName", getContactName())
            .append("companyName", getCompanyName())
            .append("mobile", getMobile())
            .append("email", getEmail())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("operIp", getOperIp())
            .append("operLocation", getOperLocation())
            .append("plan", getPlan())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("operTime", getOperTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
