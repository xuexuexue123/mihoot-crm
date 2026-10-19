package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 群组成员对象 crm_householdmember
 * 
 * @author luohd
 * @date 2024-02-02
 */
public class CrmHouseholdmember extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String memberId;

    /** 群组编号 */
    @Excel(name = "群组编号")
    private String householdId;

    /** 群组名称 */
    @Excel(name = "群组名称")
    private String householdName;

    /** 联系人编号 */
    @Excel(name = "联系人编号")
    private String contactId;

    /** 成员名称 */
    @Excel(name = "成员名称")
    private String name;

    /** 角色 */
    @Excel(name = "角色")
    private String role;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String headOfHousehold;

    /** 电话 */
    @Excel(name = "电话")
    private String workPhone;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 是否活跃 （Y是 N否） */
    @Excel(name = "是否活跃 ", readConverterExp = "Y=是,N=否")
    private String isActive;

    public void setMemberId(String memberId) 
    {
        this.memberId = memberId;
    }

    public String getMemberId() 
    {
        return memberId;
    }
    public void setHouseholdId(String householdId) 
    {
        this.householdId = householdId;
    }

    public String getHouseholdId() 
    {
        return householdId;
    }
    public void setHouseholdName(String householdName) 
    {
        this.householdName = householdName;
    }

    public String getHouseholdName() 
    {
        return householdName;
    }
    public void setContactId(String contactId) 
    {
        this.contactId = contactId;
    }

    public String getContactId() 
    {
        return contactId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setRole(String role) 
    {
        this.role = role;
    }

    public String getRole() 
    {
        return role;
    }
    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }
    public void setHeadOfHousehold(String headOfHousehold) 
    {
        this.headOfHousehold = headOfHousehold;
    }

    public String getHeadOfHousehold() 
    {
        return headOfHousehold;
    }
    public void setWorkPhone(String workPhone) 
    {
        this.workPhone = workPhone;
    }

    public String getWorkPhone() 
    {
        return workPhone;
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
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setIsActive(String isActive) 
    {
        this.isActive = isActive;
    }

    public String getIsActive() 
    {
        return isActive;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("memberId", getMemberId())
            .append("householdId", getHouseholdId())
            .append("householdName", getHouseholdName())
            .append("contactId", getContactId())
            .append("name", getName())
            .append("role", getRole())
            .append("owner", getOwner())
            .append("headOfHousehold", getHeadOfHousehold())
            .append("workPhone", getWorkPhone())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("description", getDescription())
            .append("isActive", getIsActive())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
