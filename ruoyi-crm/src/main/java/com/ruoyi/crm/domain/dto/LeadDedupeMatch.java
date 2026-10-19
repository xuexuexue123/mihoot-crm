package com.ruoyi.crm.domain.dto;

public class LeadDedupeMatch
{
    private boolean duplicate;
    private String entity;
    private String id;
    private String matchedField;

    public static LeadDedupeMatch of(String entity, String id, String matchedField)
    {
        LeadDedupeMatch match = new LeadDedupeMatch();
        match.duplicate = true;
        match.entity = entity;
        match.id = id;
        match.matchedField = matchedField;
        return match;
    }

    public boolean isDuplicate()
    {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate)
    {
        this.duplicate = duplicate;
    }

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMatchedField()
    {
        return matchedField;
    }

    public void setMatchedField(String matchedField)
    {
        this.matchedField = matchedField;
    }
}
