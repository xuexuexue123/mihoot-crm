package com.ruoyi.crm.domain.vo;

import java.util.Map;

/**
 * React Flow 边结构
 */
public class WorkflowEdgeVO
{
    private String id;

    private String source;

    private String target;

    private String sourceHandle;

    private String targetHandle;

    private String type;

    private String label;

    private Map<String, Object> data;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getSourceHandle()
    {
        return sourceHandle;
    }

    public void setSourceHandle(String sourceHandle)
    {
        this.sourceHandle = sourceHandle;
    }

    public String getTargetHandle()
    {
        return targetHandle;
    }

    public void setTargetHandle(String targetHandle)
    {
        this.targetHandle = targetHandle;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
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
