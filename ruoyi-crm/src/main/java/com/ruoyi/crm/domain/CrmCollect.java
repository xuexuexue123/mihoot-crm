package com.ruoyi.crm.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 数据收集对象 crm_collect
 *
 * @author luohd
 * @date 2024-07-05
 */
public class CrmCollect extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private String collectId;

    /** 关联类型 */
    @Excel(name = "关联类型")
    private String relateType;

    /** 关联ID */
    @Excel(name = "关联ID")
    private String relateId;

    /** 关联名称 */
    @Excel(name = "关联名称")
    private String relateName;

    /** 收集状态 */
    @Excel(name = "收集状态")
    private String collectStatus;

    /** 报告期 */
    @Excel(name = "报告期")
    private String period;

    /** 类型 */
    @Excel(name = "类型")
    private String category;

    /** 验证码 */
    @Excel(name = "验证码")
    private String validationCode;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 过期日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "过期日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiredDate;

    public void setCollectId(String collectId)
    {
        this.collectId = collectId;
    }

    public String getCollectId()
    {
        return collectId;
    }
    public void setRelateType(String relateType)
    {
        this.relateType = relateType;
    }

    public String getRelateType()
    {
        return relateType;
    }
    public void setRelateId(String relateId)
    {
        this.relateId = relateId;
    }

    public String getRelateId()
    {
        return relateId;
    }
    public void setRelateName(String relateName)
    {
        this.relateName = relateName;
    }

    public String getRelateName()
    {
        return relateName;
    }
    public void setCollectStatus(String collectStatus)
    {
        this.collectStatus = collectStatus;
    }

    public String getCollectStatus()
    {
        return collectStatus;
    }
    public void setPeriod(String period)
    {
        this.period = period;
    }

    public String getPeriod()
    {
        return period;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategory()
    {
        return category;
    }
    public void setValidationCode(String validationCode)
    {
        this.validationCode = validationCode;
    }

    public String getValidationCode()
    {
        return validationCode;
    }
    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
    public void setExpiredDate(Date expiredDate)
    {
        this.expiredDate = expiredDate;
    }

    public Date getExpiredDate()
    {
        return expiredDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("collectId", getCollectId())
                .append("relateType", getRelateType())
                .append("relateId", getRelateId())
                .append("relateName", getRelateName())
                .append("collectStatus", getCollectStatus())
                .append("period", getPeriod())
                .append("category", getCategory())
                .append("validationCode", getValidationCode())
                .append("content", getContent())
                .append("expiredDate", getExpiredDate())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
