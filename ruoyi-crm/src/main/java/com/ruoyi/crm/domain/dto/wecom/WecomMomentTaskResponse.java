package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询朋友圈发表任务响应（#2 get_moment_task）。
 */
public class WecomMomentTaskResponse
{
    private Integer errcode;
    private String errmsg;

    @JsonProperty("next_cursor")
    private String nextCursor;

    @JsonProperty("task_list")
    private List<WecomMomentTaskItem> taskList;

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

    public String getNextCursor()
    {
        return nextCursor;
    }

    public void setNextCursor(String nextCursor)
    {
        this.nextCursor = nextCursor;
    }

    public List<WecomMomentTaskItem> getTaskList()
    {
        return taskList;
    }

    public void setTaskList(List<WecomMomentTaskItem> taskList)
    {
        this.taskList = taskList;
    }
}
