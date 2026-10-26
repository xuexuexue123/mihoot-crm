package com.ruoyi.crm.domain;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 赴越中资企业 crm_chinese_enterprise
 */
public class CrmChineseEnterprise extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String parentCompanyChina;
    private String stockCode;
    private String stockListingStatus;
    private String parentProvinceChina;
    private String vietnamEntityName;
    private String vietnamEntityNameCn;
    private String taxCode;
    private String parkId;
    private String parkNameCn;
    private String provinceVietnam;
    private String industry;
    private String supplyChainRole;
    private String investmentMode;
    private BigDecimal investmentUsdMillion;
    private BigDecimal registeredCapitalUsdM;
    private Integer employeeCount;
    private BigDecimal factoryAreaSqm;
    private Integer establishedYear;
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

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getParentCompanyChina()
    {
        return parentCompanyChina;
    }

    public void setParentCompanyChina(String parentCompanyChina)
    {
        this.parentCompanyChina = parentCompanyChina;
    }

    public String getStockCode()
    {
        return stockCode;
    }

    public void setStockCode(String stockCode)
    {
        this.stockCode = stockCode;
    }

    public String getStockListingStatus()
    {
        return stockListingStatus;
    }

    public void setStockListingStatus(String stockListingStatus)
    {
        this.stockListingStatus = stockListingStatus;
    }

    public String getParentProvinceChina()
    {
        return parentProvinceChina;
    }

    public void setParentProvinceChina(String parentProvinceChina)
    {
        this.parentProvinceChina = parentProvinceChina;
    }

    public String getVietnamEntityName()
    {
        return vietnamEntityName;
    }

    public void setVietnamEntityName(String vietnamEntityName)
    {
        this.vietnamEntityName = vietnamEntityName;
    }

    public String getVietnamEntityNameCn()
    {
        return vietnamEntityNameCn;
    }

    public void setVietnamEntityNameCn(String vietnamEntityNameCn)
    {
        this.vietnamEntityNameCn = vietnamEntityNameCn;
    }

    public String getTaxCode()
    {
        return taxCode;
    }

    public void setTaxCode(String taxCode)
    {
        this.taxCode = taxCode;
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

    public String getProvinceVietnam()
    {
        return provinceVietnam;
    }

    public void setProvinceVietnam(String provinceVietnam)
    {
        this.provinceVietnam = provinceVietnam;
    }

    public String getIndustry()
    {
        return industry;
    }

    public void setIndustry(String industry)
    {
        this.industry = industry;
    }

    public String getSupplyChainRole()
    {
        return supplyChainRole;
    }

    public void setSupplyChainRole(String supplyChainRole)
    {
        this.supplyChainRole = supplyChainRole;
    }

    public String getInvestmentMode()
    {
        return investmentMode;
    }

    public void setInvestmentMode(String investmentMode)
    {
        this.investmentMode = investmentMode;
    }

    public BigDecimal getInvestmentUsdMillion()
    {
        return investmentUsdMillion;
    }

    public void setInvestmentUsdMillion(BigDecimal investmentUsdMillion)
    {
        this.investmentUsdMillion = investmentUsdMillion;
    }

    public BigDecimal getRegisteredCapitalUsdM()
    {
        return registeredCapitalUsdM;
    }

    public void setRegisteredCapitalUsdM(BigDecimal registeredCapitalUsdM)
    {
        this.registeredCapitalUsdM = registeredCapitalUsdM;
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

    public Integer getEstablishedYear()
    {
        return establishedYear;
    }

    public void setEstablishedYear(Integer establishedYear)
    {
        this.establishedYear = establishedYear;
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
}
