package com.ruoyi.crm.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 专业服务机构 crm_professional_service
 */
public class CrmProfessionalService extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;

    private String nameCn;
    private String nameVn;
    private String nameEn;
    private String category;
    private String background;
    private String province;
    private String address;
    private List<String> coreServices = new ArrayList<>();
    private String targetClients;
    private String keyCapabilities;
    private String contactPhone;
    private String email;
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

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getBackground()
    {
        return background;
    }

    public void setBackground(String background)
    {
        this.background = background;
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

    public List<String> getCoreServices()
    {
        return coreServices;
    }

    public void setCoreServices(List<String> coreServices)
    {
        this.coreServices = coreServices != null ? coreServices : new ArrayList<>();
    }

    public String getTargetClients()
    {
        return targetClients;
    }

    public void setTargetClients(String targetClients)
    {
        this.targetClients = targetClients;
    }

    public String getKeyCapabilities()
    {
        return keyCapabilities;
    }

    public void setKeyCapabilities(String keyCapabilities)
    {
        this.keyCapabilities = keyCapabilities;
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
