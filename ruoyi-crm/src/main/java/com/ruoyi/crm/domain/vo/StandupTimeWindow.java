package com.ruoyi.crm.domain.vo;

/**
 * 报告日时间窗口（ISO 边界）。
 */
public class StandupTimeWindow
{
    private String from;
    private String to;

    public StandupTimeWindow()
    {
    }

    public StandupTimeWindow(String from, String to)
    {
        this.from = from;
        this.to = to;
    }

    public String getFrom()
    {
        return from;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getTo()
    {
        return to;
    }

    public void setTo(String to)
    {
        this.to = to;
    }
}
