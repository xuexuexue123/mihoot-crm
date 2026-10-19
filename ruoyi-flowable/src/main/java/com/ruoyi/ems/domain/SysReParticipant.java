package com.ruoyi.ems.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目干系人对象 sys_re_participant
 * 
 * @author luohd
 * @date 2023-05-19
 */
public class SysReParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private String partId;

    /** 业务Id */
    @Excel(name = "业务Id")
    private String projectId;

    /** 姓名 */
    @Excel(name = "姓名")
    private String partName;

    /** 团队/客户 */
    @Excel(name = "团队/客户")
    private String category;

    /** 角色 */
    @Excel(name = "角色")
    private String role;

    /** 部门 */
    @Excel(name = "部门")
    private String department;

    /** 职务 */
    @Excel(name = "职务")
    private String title;

    /** 职责 */
    @Excel(name = "职责")
    private String responsibility;

    /** 电话 */
    @Excel(name = "电话")
    private String mobilePhone;

    /** 经理 */
    @Excel(name = "经理")
    private String manager;

    /** 进入日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "进入日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date entryDate;

    /** 离开日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "离开日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date leaveDate;

    /** 投入频度 */
    @Excel(name = "投入频度")
    private BigDecimal effort;

    /** 是否可用 */
    @Excel(name = "是否可用")
    private Long status;

    /** Email */
    @Excel(name = "Email")
    private String email;

    /** 签到码 */
//    @Excel(name = "签到码")
    private String code;


    public void setPartId(String partId) 
    {
        this.partId = partId;
    }

    public String getPartId() 
    {
        return partId;
    }
    public void setProjectId(String projectId) 
    {
        this.projectId = projectId;
    }

    public String getProjectId() 
    {
        return projectId;
    }
    public void setPartName(String partName) 
    {
        this.partName = partName;
    }

    public String getPartName() 
    {
        return partName;
    }
    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }
    public void setRole(String role) 
    {
        this.role = role;
    }

    public String getRole() 
    {
        return role;
    }
    public void setDepartment(String department) 
    {
        this.department = department;
    }

    public String getDepartment() 
    {
        return department;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setResponsibility(String responsibility) 
    {
        this.responsibility = responsibility;
    }

    public String getResponsibility() 
    {
        return responsibility;
    }
    public void setMobilePhone(String mobilePhone) 
    {
        this.mobilePhone = mobilePhone;
    }

    public String getMobilePhone() 
    {
        return mobilePhone;
    }
    public void setManager(String manager) 
    {
        this.manager = manager;
    }

    public String getManager() 
    {
        return manager;
    }
    public void setEntryDate(Date entryDate) 
    {
        this.entryDate = entryDate;
    }

    public Date getEntryDate() 
    {
        return entryDate;
    }
    public void setLeaveDate(Date leaveDate) 
    {
        this.leaveDate = leaveDate;
    }

    public Date getLeaveDate() 
    {
        return leaveDate;
    }
    public void setEffort(BigDecimal effort) 
    {
        this.effort = effort;
    }

    public BigDecimal getEffort() 
    {
        return effort;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail()
    {
        return email;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("partId", getPartId())
            .append("projectId", getProjectId())
            .append("partName", getPartName())
            .append("category", getCategory())
            .append("role", getRole())
            .append("department", getDepartment())
            .append("title", getTitle())
            .append("responsibility", getResponsibility())
            .append("mobilePhone", getMobilePhone())
            .append("manager", getManager())
            .append("entryDate", getEntryDate())
            .append("leaveDate", getLeaveDate())
            .append("effort", getEffort())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("email", getEmail())
            .append("code", getCode())
            .toString();
    }
}
