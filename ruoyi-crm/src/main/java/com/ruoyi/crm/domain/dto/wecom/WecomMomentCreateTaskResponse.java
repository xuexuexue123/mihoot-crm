package com.ruoyi.crm.domain.dto.wecom;

/**
 * 创建朋友圈发表任务响应（#6 add_moment_task）。
 */
public class WecomMomentCreateTaskResponse
{
    private Integer errcode;
    private String errmsg;
    private String jobid;

    /** 仅 #7 get_moment_task_result 响应使用 */
    private Integer status;
    private String type;

    public Integer getErrcode()
    {
        return errcode;
    }

    public void setErrcode(Integer errcode)
    {
        this.errcode = errcode;
    }

    public String getErrmsg()
    {
        return errmsg;
    }

    public void setErrmsg(String errmsg)
    {
        this.errmsg = errmsg;
    }

    public String getJobid()
    {
        return jobid;
    }

    public void setJobid(String jobid)
    {
        this.jobid = jobid;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
