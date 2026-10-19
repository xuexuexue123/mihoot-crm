package com.ruoyi.crm.domain.vo;

/**
 * POST /website/pageview 响应 data
 */
public class WebsitePageviewResultVo
{
    private boolean accepted;
    private boolean bound;

    public WebsitePageviewResultVo()
    {
    }

    public WebsitePageviewResultVo(boolean accepted, boolean bound)
    {
        this.accepted = accepted;
        this.bound = bound;
    }

    public boolean isAccepted()
    {
        return accepted;
    }

    public void setAccepted(boolean accepted)
    {
        this.accepted = accepted;
    }

    public boolean isBound()
    {
        return bound;
    }

    public void setBound(boolean bound)
    {
        this.bound = bound;
    }
}
