package com.ruoyi.crm.domain.vo;

import java.util.Map;

/**
 * React Flow 节点结构
 */
public class WorkflowNodeVO
{
    private String id;

    private String type;

    private Map<String, Object> position;

    private Map<String, Object> data;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Map<String, Object> getPosition()
    {
        return position;
    }

    public void setPosition(Map<String, Object> position)
    {
        this.position = position;
    }

    public Map<String, Object> getData()
    {
        return data;
    }

    public void setData(Map<String, Object> data)
    {
        this.data = data;
    }
}
