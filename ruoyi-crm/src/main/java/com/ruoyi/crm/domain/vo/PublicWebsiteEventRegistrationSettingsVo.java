package com.ruoyi.crm.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 官网公开活动报名设置子集
 */
public class PublicWebsiteEventRegistrationSettingsVo
{
    private Boolean registrationEnabled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date registrationDeadline;

    private Boolean waitlistEnabled;

    public Boolean getRegistrationEnabled()
    {
        return registrationEnabled;
    }

    public void setRegistrationEnabled(Boolean registrationEnabled)
    {
        this.registrationEnabled = registrationEnabled;
    }

    public Date getRegistrationDeadline()
    {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(Date registrationDeadline)
    {
        this.registrationDeadline = registrationDeadline;
    }

    public Boolean getWaitlistEnabled()
    {
        return waitlistEnabled;
    }

    public void setWaitlistEnabled(Boolean waitlistEnabled)
    {
        this.waitlistEnabled = waitlistEnabled;
    }
}
