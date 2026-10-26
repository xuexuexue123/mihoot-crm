package com.ruoyi.crm.domain.dto;

/**
 * Lead→Contact 转化复制选项
 */
public class LeadConvertCopyOptions
{
    private Boolean copyCustomFields = true;
    private Boolean copyTags = true;
    private Boolean copyAttribution = true;
    private Boolean copyWecomIds = true;
    private Boolean migrateIdentities = true;
    private Boolean migrateWecomExternalRow = true;
    private Boolean inheritActivities = false;

    public Boolean getCopyCustomFields()
    {
        return copyCustomFields;
    }

    public void setCopyCustomFields(Boolean copyCustomFields)
    {
        this.copyCustomFields = copyCustomFields;
    }

    public Boolean getCopyTags()
    {
        return copyTags;
    }

    public void setCopyTags(Boolean copyTags)
    {
        this.copyTags = copyTags;
    }

    public Boolean getCopyAttribution()
    {
        return copyAttribution;
    }

    public void setCopyAttribution(Boolean copyAttribution)
    {
        this.copyAttribution = copyAttribution;
    }

    public Boolean getCopyWecomIds()
    {
        return copyWecomIds;
    }

    public void setCopyWecomIds(Boolean copyWecomIds)
    {
        this.copyWecomIds = copyWecomIds;
    }

    public Boolean getMigrateIdentities()
    {
        return migrateIdentities;
    }

    public void setMigrateIdentities(Boolean migrateIdentities)
    {
        this.migrateIdentities = migrateIdentities;
    }

    public Boolean getMigrateWecomExternalRow()
    {
        return migrateWecomExternalRow;
    }

    public void setMigrateWecomExternalRow(Boolean migrateWecomExternalRow)
    {
        this.migrateWecomExternalRow = migrateWecomExternalRow;
    }

    public Boolean getInheritActivities()
    {
        return inheritActivities;
    }

    public void setInheritActivities(Boolean inheritActivities)
    {
        this.inheritActivities = inheritActivities;
    }

    /** 缺省全 true（inheritActivities 默认 false） */
    public static LeadConvertCopyOptions defaults()
    {
        return new LeadConvertCopyOptions();
    }

    public boolean isCopyCustomFields()
    {
        return copyCustomFields == null || copyCustomFields;
    }

    public boolean isCopyTags()
    {
        return copyTags == null || copyTags;
    }

    public boolean isCopyAttribution()
    {
        return copyAttribution == null || copyAttribution;
    }

    public boolean isCopyWecomIds()
    {
        return copyWecomIds == null || copyWecomIds;
    }

    public boolean isMigrateIdentities()
    {
        return migrateIdentities == null || migrateIdentities;
    }

    public boolean isMigrateWecomExternalRow()
    {
        return migrateWecomExternalRow == null || migrateWecomExternalRow;
    }

    public boolean isInheritActivities()
    {
        return inheritActivities != null && inheritActivities;
    }
}
