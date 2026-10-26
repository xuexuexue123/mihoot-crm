package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 工单对象 crm_ticket
 * 
 * @author luohd
 * @date 2025-04-08
 */
public class CrmTicket extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String id;

    /** 客户编号 */
    @Excel(name = "客户编号")
    private String accountId;

    /** 客户名 */
    @Excel(name = "客户名")
    private String accountName;

    /** 工单类别 */
    @Excel(name = "工单类别")
    private String category;

    /** 名称 */
    @Excel(name = "名称")
    private String subject;

    /** 来源 */
    @Excel(name = "来源")
    private String source;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 描述 */
    @Excel(name = "描述")
    private String resolutionNote;

    /** 优先级 */
    @Excel(name = "优先级")
    private String priority;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 关闭者 */
    @Excel(name = "关闭者")
    private String closeBy;

    /** 关闭时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "关闭时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date closeTime;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** SLA 预定时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "SLA 预定时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date slaDueDate;

    /** 是否符合SLA */
    @Excel(name = "是否符合SLA")
    private String slaComplianceStatus;

    /** 逾期时间 */
    @Excel(name = "逾期时间")
    private String overdueBy;

    /** 工单来源地区 */
    @Excel(name = "工单来源地区")
    private String region;

    /** 来源渠道Email, Phone, Chat */
    @Excel(name = "来源渠道Email, Phone, Chat")
    private String channel;

    /** 工单来源 (自助提交，客服人员) */
    @Excel(name = "工单来源 (自助提交，客服人员)")
    private String sourceType;

    /**  */
    @Excel(name = "")
    private Long customerSatisfaction;

    /** 首次响应时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "首次响应时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date firstResponseTime;

    /** 工单解决时间 */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    @Excel(name = "工单解决时间", width = 30, dateFormat = "yyyy-MM-dd hh:mm:ss")
    private Date resolutionTime;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setAccountId(String accountId) 
    {
        this.accountId = accountId;
    }

    public String getAccountId() 
    {
        return accountId;
    }
    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setSubject(String subject) 
    {
        this.subject = subject;
    }

    public String getSubject() 
    {
        return subject;
    }
    public void setSource(String source) 
    {
        this.source = source;
    }

    public String getSource() 
    {
        return source;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setResolutionNote(String resolutionNote) 
    {
        this.resolutionNote = resolutionNote;
    }

    public String getResolutionNote() 
    {
        return resolutionNote;
    }
    public void setPriority(String priority) 
    {
        this.priority = priority;
    }

    public String getPriority() 
    {
        return priority;
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
    public void setCloseBy(String closeBy) 
    {
        this.closeBy = closeBy;
    }

    public String getCloseBy() 
    {
        return closeBy;
    }
    public void setCloseTime(Date closeTime) 
    {
        this.closeTime = closeTime;
    }

    public Date getCloseTime() 
    {
        return closeTime;
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
    public void setSlaDueDate(Date slaDueDate)
    {
        this.slaDueDate = slaDueDate;
    }

    public Date getSlaDueDate()
    {
        return slaDueDate;
    }
    public void setSlaComplianceStatus(String slaComplianceStatus)
    {
        this.slaComplianceStatus = slaComplianceStatus;
    }

    public String getSlaComplianceStatus()
    {
        return slaComplianceStatus;
    }
    public void setOverdueBy(String overdueBy)
    {
        this.overdueBy = overdueBy;
    }

    public String getOverdueBy()
    {
        return overdueBy;
    }
    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getRegion()
    {
        return region;
    }
    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getChannel()
    {
        return channel;
    }
    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getSourceType()
    {
        return sourceType;
    }
    public void setCustomerSatisfaction(Long customerSatisfaction)
    {
        this.customerSatisfaction = customerSatisfaction;
    }

    public Long getCustomerSatisfaction()
    {
        return customerSatisfaction;
    }
    public void setFirstResponseTime(Date firstResponseTime)
    {
        this.firstResponseTime = firstResponseTime;
    }

    public Date getFirstResponseTime()
    {
        return firstResponseTime;
    }
    public void setResolutionTime(Date resolutionTime)
    {
        this.resolutionTime = resolutionTime;
    }

    public Date getResolutionTime()
    {
        return resolutionTime;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("accountId", getAccountId())
            .append("accountName", getAccountName())
            .append("category", getCategory())
            .append("subject", getSubject())
            .append("source", getSource())
            .append("description", getDescription())
            .append("resolutionNote", getResolutionNote())
            .append("priority", getPriority())
            .append("owner", getOwner())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("closeBy", getCloseBy())
            .append("closeTime", getCloseTime())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .toString();
    }
}
