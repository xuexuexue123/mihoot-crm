package com.ruoyi.crm.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 群组对象 crm_household
 * 
 * @author luohd
 * @date 2024-01-16
 */
public class CrmHousehold extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String householdId;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 成员数 */
    @Excel(name = "成员数")
    private Long memberCount;

    /** 区域 */
    @Excel(name = "区域")
    private String territory;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String owner;

    /** 客户经理 */
    @Excel(name = "客户经理")
    private String headOfHousehold;

    /** 电话 */
    @Excel(name = "电话")
    private String workPhone;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    private Long userId;

    private Long deptId;
    /** 群组成员信息 */
    private List<CrmHouseholdmember> crmHouseholdmemberList;

    public void setHouseholdId(String householdId) 
    {
        this.householdId = householdId;
    }

    public String getHouseholdId() 
    {
        return householdId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setMemberCount(Long memberCount) 
    {
        this.memberCount = memberCount;
    }

    public Long getMemberCount() 
    {
        return memberCount;
    }
    public void setTerritory(String territory) 
    {
        this.territory = territory;
    }

    public String getTerritory() 
    {
        return territory;
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
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public List<CrmHouseholdmember> getCrmHouseholdmemberList()
    {
        return crmHouseholdmemberList;
    }

    public void setCrmHouseholdmemberList(List<CrmHouseholdmember> crmHouseholdmemberList)
    {
        this.crmHouseholdmemberList = crmHouseholdmemberList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("householdId", getHouseholdId())
            .append("name", getName())
            .append("memberCount", getMemberCount())
            .append("territory", getTerritory())
            .append("owner", getOwner())
            .append("headOfHousehold", getHeadOfHousehold())
            .append("workPhone", getWorkPhone())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("crmHouseholdmemberList", getCrmHouseholdmemberList())
            .toString();
    }
}
