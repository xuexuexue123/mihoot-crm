package com.ruoyi.crm.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 合规制裁主体 crm_sanctioned_entity
 */
public class CrmSanctionedEntity extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String nameCn;
    private String nameVn;
    private String nameEn;
    private List<String> aka = new ArrayList<>();
    private String entityType;
    private String taxCode;
    private String idCardOrPassport;
    private String parentCompanyOrUBO;
    private String riskCategory;
    private String riskReason;
    private String issuingAgency;
    private String riskLevel;
    private String effectiveDate;
    private String expiryDate;
    private String officialNoticeUrl;
    private String matchedEnterpriseCode;
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

    public String getNameCn()
    {
        return nameCn;
    }

    public void setNameCn(String nameCn)
    {
        this.nameCn = nameCn;
    }

    public String getNameVn()
    {
        return nameVn;
    }

    public void setNameVn(String nameVn)
    {
        this.nameVn = nameVn;
    }

    public String getNameEn()
    {
        return nameEn;
    }

    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }

    public List<String> getAka()
    {
        return aka;
    }

    public void setAka(List<String> aka)
    {
        this.aka = aka != null ? aka : new ArrayList<>();
    }

    public String getEntityType()
    {
        return entityType;
    }

    public void setEntityType(String entityType)
    {
        this.entityType = entityType;
    }

    public String getTaxCode()
    {
        return taxCode;
    }

    public void setTaxCode(String taxCode)
    {
        this.taxCode = taxCode;
    }

    public String getIdCardOrPassport()
    {
        return idCardOrPassport;
    }

    public void setIdCardOrPassport(String idCardOrPassport)
    {
        this.idCardOrPassport = idCardOrPassport;
    }

    public String getParentCompanyOrUBO()
    {
        return parentCompanyOrUBO;
    }

    public void setParentCompanyOrUBO(String parentCompanyOrUBO)
    {
        this.parentCompanyOrUBO = parentCompanyOrUBO;
    }

    public String getRiskCategory()
    {
        return riskCategory;
    }

    public void setRiskCategory(String riskCategory)
    {
        this.riskCategory = riskCategory;
    }

    public String getRiskReason()
    {
        return riskReason;
    }

    public void setRiskReason(String riskReason)
    {
        this.riskReason = riskReason;
    }

    public String getIssuingAgency()
    {
        return issuingAgency;
    }

    public void setIssuingAgency(String issuingAgency)
    {
        this.issuingAgency = issuingAgency;
    }

    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getEffectiveDate()
    {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate)
    {
        this.effectiveDate = effectiveDate;
    }

    public String getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public String getOfficialNoticeUrl()
    {
        return officialNoticeUrl;
    }

    public void setOfficialNoticeUrl(String officialNoticeUrl)
    {
        this.officialNoticeUrl = officialNoticeUrl;
    }

    public String getMatchedEnterpriseCode()
    {
        return matchedEnterpriseCode;
    }

    public void setMatchedEnterpriseCode(String matchedEnterpriseCode)
    {
        this.matchedEnterpriseCode = matchedEnterpriseCode;
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
