package com.ruoyi.crm.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 越南工业园区入驻企业 crm_tenant_enterprise
 */
public class CrmTenantEnterprise extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String parkId;
    private String parkNameCn;
    private String name;
    private String nameCn;
    private String taxCode;
    private String legalRepresentative;
    private Integer establishedYear;
    private String registeredAddress;
    private String industry;
    private String enterpriseType;
    private String countryOfOrigin;
    private BigDecimal investmentUsdMillion;
    private Integer employeeCount;
    private BigDecimal factoryAreaSqm;
    private String status;
    private String mainProducts;
    private String contactPhone;
    private String contactEmail;
    private String website;
    private String notes;
    /** YYYY-MM-DD or ISO */
    private String createdAt;

    private String delFlag;

    /** 列表查询关键字（非表字段） */
    @JsonIgnore
    private String searchQuery;

    /** 企业类型筛选扩展值（非表字段；FDI/WFOE、合资企业新旧名兼容） */
    @JsonIgnore
    private List<String> enterpriseTypeIn;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getParkId()
    {
        return parkId;
    }

    public void setParkId(String parkId)
    {
        this.parkId = parkId;
    }

    public String getParkNameCn()
    {
        return parkNameCn;
    }

    public void setParkNameCn(String parkNameCn)
    {
        this.parkNameCn = parkNameCn;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNameCn()
    {
        return nameCn;
    }

    public void setNameCn(String nameCn)
    {
        this.nameCn = nameCn;
    }

    public String getTaxCode()
    {
        return taxCode;
    }

    public void setTaxCode(String taxCode)
    {
        this.taxCode = taxCode;
    }

    public String getLegalRepresentative()
    {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative)
    {
        this.legalRepresentative = legalRepresentative;
    }

    public Integer getEstablishedYear()
    {
        return establishedYear;
    }

    public void setEstablishedYear(Integer establishedYear)
    {
        this.establishedYear = establishedYear;
    }

    public String getRegisteredAddress()
    {
        return registeredAddress;
    }

    public void setRegisteredAddress(String registeredAddress)
    {
        this.registeredAddress = registeredAddress;
    }

    public String getIndustry()
    {
        return industry;
    }

    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    public String getEnterpriseType()
    {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType)
    {
        this.enterpriseType = enterpriseType;
    }

    public String getCountryOfOrigin()
    {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin)
    {
        this.countryOfOrigin = countryOfOrigin;
    }

    public BigDecimal getInvestmentUsdMillion()
    {
        return investmentUsdMillion;
    }

    public void setInvestmentUsdMillion(BigDecimal investmentUsdMillion)
    {
        this.investmentUsdMillion = investmentUsdMillion;
    }

    public Integer getEmployeeCount()
    {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount)
    {
        this.employeeCount = employeeCount;
    }

    public BigDecimal getFactoryAreaSqm()
    {
        return factoryAreaSqm;
    }

    public void setFactoryAreaSqm(BigDecimal factoryAreaSqm)
    {
        this.factoryAreaSqm = factoryAreaSqm;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMainProducts()
    {
        return mainProducts;
    }

    public void setMainProducts(String mainProducts)
    {
        this.mainProducts = mainProducts;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt(String createdAt)
    {
        this.createdAt = createdAt;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getSearchQuery()
    {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery)
    {
        this.searchQuery = searchQuery;
    }

    public List<String> getEnterpriseTypeIn()
    {
        return enterpriseTypeIn;
    }

    public void setEnterpriseTypeIn(List<String> enterpriseTypeIn)
    {
        this.enterpriseTypeIn = enterpriseTypeIn;
    }
}
