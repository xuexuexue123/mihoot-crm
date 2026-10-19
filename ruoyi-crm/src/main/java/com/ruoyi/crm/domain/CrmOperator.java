package com.ruoyi.crm.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 销售人员对象 crm_operator
 * 
 * @author luohd
 * @date 2024-01-29
 */
public class CrmOperator extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String operatorId;

    /** 人员类型 */
    @Excel(name = "人员类型")
    private String operatorCategory;

    /** 负责区域 */
    @Excel(name = "负责区域")
    private String primaryTerritory;

    /** 用户ID */
    @Excel(name = "用户ID")
    private String operatorIdentity;

    /** 密码 */
    @Excel(name = "密码")
    private String operatorPassword;

    /** 姓名 */
    @Excel(name = "姓名")
    private String operatorName;

    /** 职位 */
    @Excel(name = "职位")
    private String position;

    /** 头像 */
    @Excel(name = "头像")
    private String avatar;

    /** 头衔 */
    @Excel(name = "头衔")
    private String title;

    /** 邮件 */
    @Excel(name = "邮件")
    private String email;

    /** 手机 */
    @Excel(name = "手机")
    private String phone;

    /** 汇报人 */
    @Excel(name = "汇报人")
    private String reportTo;

    /** 汇报人ID */
    @Excel(name = "汇报人ID")
    private String reportToid;

    /** 是否活动 （Y是 N否） */
    @Excel(name = "是否活动 ", readConverterExp = "Y=是,N=否")
    private String isActive;

    public void setOperatorId(String operatorId) 
    {
        this.operatorId = operatorId;
    }

    public String getOperatorId() 
    {
        return operatorId;
    }
    public void setOperatorCategory(String operatorCategory) 
    {
        this.operatorCategory = operatorCategory;
    }

    public String getOperatorCategory() 
    {
        return operatorCategory;
    }
    public void setPrimaryTerritory(String primaryTerritory) 
    {
        this.primaryTerritory = primaryTerritory;
    }

    public String getPrimaryTerritory() 
    {
        return primaryTerritory;
    }
    public void setOperatorIdentity(String operatorIdentity) 
    {
        this.operatorIdentity = operatorIdentity;
    }

    public String getOperatorIdentity() 
    {
        return operatorIdentity;
    }
    public void setOperatorPassword(String operatorPassword) 
    {
        this.operatorPassword = operatorPassword;
    }

    public String getOperatorPassword() 
    {
        return operatorPassword;
    }
    public void setOperatorName(String operatorName) 
    {
        this.operatorName = operatorName;
    }

    public String getOperatorName() 
    {
        return operatorName;
    }
    public void setPosition(String position) 
    {
        this.position = position;
    }

    public String getPosition() 
    {
        return position;
    }
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }
    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }
    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setReportTo(String reportTo) 
    {
        this.reportTo = reportTo;
    }

    public String getReportTo() 
    {
        return reportTo;
    }
    public void setReportToid(String reportToid) 
    {
        this.reportToid = reportToid;
    }

    public String getReportToid() 
    {
        return reportToid;
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
            .append("operatorId", getOperatorId())
            .append("operatorCategory", getOperatorCategory())
            .append("primaryTerritory", getPrimaryTerritory())
            .append("operatorIdentity", getOperatorIdentity())
            .append("operatorPassword", getOperatorPassword())
            .append("operatorName", getOperatorName())
            .append("position", getPosition())
            .append("avatar", getAvatar())
            .append("title", getTitle())
            .append("email", getEmail())
            .append("phone", getPhone())
            .append("reportTo", getReportTo())
            .append("reportToid", getReportToid())
            .append("isActive", getIsActive())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
