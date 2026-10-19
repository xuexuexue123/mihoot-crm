package com.ruoyi.common.core.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 组织管理对象 sys_organization
 * 
 * @author luohd
 * @date 2024-03-17
 */
public class SysOrganization extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 组织id */
    private String organizationId;

    /** 名称 */
    @Excel(name = "名称")
    private String pyName;

    /** 中文名 */
    @Excel(name = "中文名")
    private String pyLabel;

    /** 网站 */
    @Excel(name = "网站")
    private String website;

    /** 电话 */
    @Excel(name = "电话")
    private String tel;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 社会信用代码 */
    @Excel(name = "社会信用代码")
    private String uuid;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bankName;

    /** 银行账号	 */
    @Excel(name = "银行账号	")
    private String bankAccount;

    /** 发票地址 */
    @Excel(name = "发票地址")
    private String fapiaoDelivery;

    /** Logo */
    @Excel(name = "Logo")
    private String avatar;

    /** 大logo */
    @Excel(name = "大logo")
    private String largerAvatar;

    /** 行业（知识库主档） */
    @Excel(name = "行业")
    private String industry;

    /** 一句话定位 */
    @Excel(name = "定位")
    private String tagline;

    /** 企业简介长文；空则知识库同步用 description */
    private String intro;

    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    public String getIndustry()
    {
        return industry;
    }

    public void setTagline(String tagline)
    {
        this.tagline = tagline;
    }

    public String getTagline()
    {
        return tagline;
    }

    public void setIntro(String intro)
    {
        this.intro = intro;
    }

    public String getIntro()
    {
        return intro;
    }

    public void setOrganizationId(String organizationId) 
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() 
    {
        return organizationId;
    }
    public void setPyName(String pyName) 
    {
        this.pyName = pyName;
    }

    public String getPyName() 
    {
        return pyName;
    }
    public void setPyLabel(String pyLabel) 
    {
        this.pyLabel = pyLabel;
    }

    public String getPyLabel() 
    {
        return pyLabel;
    }
    public void setWebsite(String website) 
    {
        this.website = website;
    }

    public String getWebsite() 
    {
        return website;
    }
    public void setTel(String tel) 
    {
        this.tel = tel;
    }

    public String getTel() 
    {
        return tel;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }
    public void setUuid(String uuid) 
    {
        this.uuid = uuid;
    }

    public String getUuid() 
    {
        return uuid;
    }
    public void setBankName(String bankName) 
    {
        this.bankName = bankName;
    }

    public String getBankName() 
    {
        return bankName;
    }
    public void setBankAccount(String bankAccount) 
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() 
    {
        return bankAccount;
    }
    public void setFapiaoDelivery(String fapiaoDelivery) 
    {
        this.fapiaoDelivery = fapiaoDelivery;
    }

    public String getFapiaoDelivery() 
    {
        return fapiaoDelivery;
    }
    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }
    public void setLargerAvatar(String largerAvatar) 
    {
        this.largerAvatar = largerAvatar;
    }

    public String getLargerAvatar() 
    {
        return largerAvatar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("organizationId", getOrganizationId())
            .append("pyName", getPyName())
            .append("pyLabel", getPyLabel())
            .append("website", getWebsite())
            .append("tel", getTel())
            .append("address", getAddress())
            .append("description", getDescription())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("uuid", getUuid())
            .append("bankName", getBankName())
            .append("bankAccount", getBankAccount())
            .append("fapiaoDelivery", getFapiaoDelivery())
            .append("avatar", getAvatar())
            .append("largerAvatar", getLargerAvatar())
            .toString();
    }
}
