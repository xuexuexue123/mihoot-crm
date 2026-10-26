package com.ruoyi.crm.domain.dto.wecom;

/**
 * 朋友圈地理位置。
 */
public class WecomMomentLocation
{
    private String latitude;
    private String longitude;
    private String name;

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
