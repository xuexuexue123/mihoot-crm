package com.ruoyi.crm.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 政府招商/审批机构 crm_government_agency
 */
public class CrmGovernmentAgency extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String nameCn;
    private String nameVn;
    private String nameEn;
    private String abbreviation;
    private String level;
    private String category;
    private String province;
    private String address;
    private String coreResponsibilities;
    private List<String> approvalItems = new ArrayList<>();
    private String oneStopServices;
    private String contactPhone;
    private String email;
    private String website;
    private String workingHours;
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

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getCoreResponsibilities()
    {
        return coreResponsibilities;
    }

    public void setCoreResponsibilities(String coreResponsibilities)
    {
        this.coreResponsibilities = coreResponsibilities;
    }

    public List<String> getApprovalItems()
    {
        return approvalItems;
    }

    public void setApprovalItems(List<String> approvalItems)
    {
        this.approvalItems = approvalItems != null ? approvalItems : new ArrayList<>();
    }

    public String getOneStopServices()
    {
        return oneStopServices;
    }

    public void setOneStopServices(String oneStopServices)
    {
        this.oneStopServices = oneStopServices;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getWorkingHours()
    {
        return workingHours;
    }

    public void setWorkingHours(String workingHours)
    {
        this.workingHours = workingHours;
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
