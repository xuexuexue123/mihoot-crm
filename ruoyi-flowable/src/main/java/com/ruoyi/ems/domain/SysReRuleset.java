package com.ruoyi.ems.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 规则集对象 sys_re_ruleset
 * 
 * @author luohd
 * @date 2023-03-25
 */
public class SysReRuleset extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编码 */
    private Long pyId;

    /** 应用Id */
    @Excel(name = "应用Id")
    private String caseApplicationId;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 版本 */
    @Excel(name = "版本")
    private String version;

    /** 需要审批 */
    @Excel(name = "需要审批")
    private Boolean approvalRequired;

    /** 是否锁定 */
    @Excel(name = "是否锁定")
    private Boolean locked;

    /** 解锁密码 */
    private String password;

    /** 生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date effectiveStartTime;

    public void setPyId(Long pyId) 
    {
        this.pyId = pyId;
    }

    public Long getPyId() 
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
    public void setPyLabel(String pyLabel) 
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setPyName(String pyName) 
    {
        this.pyName = pyName;
    }

    public String getPyName() 
    {
        return pyName;
    }
    public void setVersion(String version) 
    {
        this.version = version;
    }

    public String getVersion() 
    {
        return version;
    }
    public void setApprovalRequired(Boolean approvalRequired)
    {
        this.approvalRequired = approvalRequired;
    }

    public Boolean getApprovalRequired()
    {
        return approvalRequired;
    }
    public void setLocked(Boolean locked)
    {
        this.locked = locked;
    }

    public Boolean getLocked()
    {
        return locked;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setEffectiveStartTime(Date effectiveStartTime) 
    {
        this.effectiveStartTime = effectiveStartTime;
    }

    public Date getEffectiveStartTime() 
    {
        return effectiveStartTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pyId", getPyId())
            .append("caseApplicationId", getCaseApplicationId())
            .append("pyLabel", getPyLabel())
            .append("pyName", getPyName())
            .append("version", getVersion())
            .append("approvalRequired", getApprovalRequired())
            .append("locked", getLocked())
            .append("password", getPassword())
            .append("effectiveStartTime", getEffectiveStartTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
