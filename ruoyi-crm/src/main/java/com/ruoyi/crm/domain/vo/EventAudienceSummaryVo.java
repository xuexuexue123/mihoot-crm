package com.ruoyi.crm.domain.vo;

/**
 * 活动受众汇总指标
 */
public class EventAudienceSummaryVo
{
    private int totalUnique;
    private int h5RegistrationCount;
    private int h5ReleasedLeadCount;
    private int websiteRegistrationCount;
    private int websiteReleasedLeadCount;
    private int wecomContactCount;
    private int mpTouchCount;
    private int contactKnownCount;
    private int registrationPendingCount;

    public int getTotalUnique()
    {
        return totalUnique;
    }

    public void setTotalUnique(int totalUnique)
    {
        this.totalUnique = totalUnique;
    }

    public int getH5RegistrationCount()
    {
        return h5RegistrationCount;
    }

    public void setH5RegistrationCount(int h5RegistrationCount)
    {
        this.h5RegistrationCount = h5RegistrationCount;
    }

    public int getH5ReleasedLeadCount()
    {
        return h5ReleasedLeadCount;
    }

    public void setH5ReleasedLeadCount(int h5ReleasedLeadCount)
    {
        this.h5ReleasedLeadCount = h5ReleasedLeadCount;
    }

    public int getWebsiteRegistrationCount()
    {
        return websiteRegistrationCount;
    }

    public void setWebsiteRegistrationCount(int websiteRegistrationCount)
    {
        this.websiteRegistrationCount = websiteRegistrationCount;
    }

    public int getWebsiteReleasedLeadCount()
    {
        return websiteReleasedLeadCount;
    }

    public void setWebsiteReleasedLeadCount(int websiteReleasedLeadCount)
    {
        this.websiteReleasedLeadCount = websiteReleasedLeadCount;
    }

    public int getWecomContactCount()
    {
        return wecomContactCount;
    }

    public void setWecomContactCount(int wecomContactCount)
    {
        this.wecomContactCount = wecomContactCount;
    }

    public int getMpTouchCount()
    {
        return mpTouchCount;
    }

    public void setMpTouchCount(int mpTouchCount)
    {
        this.mpTouchCount = mpTouchCount;
    }

    public int getContactKnownCount()
    {
        return contactKnownCount;
    }

    public void setContactKnownCount(int contactKnownCount)
    {
        this.contactKnownCount = contactKnownCount;
    }

    public int getRegistrationPendingCount()
    {
        return registrationPendingCount;
    }

    public void setRegistrationPendingCount(int registrationPendingCount)
    {
        this.registrationPendingCount = registrationPendingCount;
    }
}
