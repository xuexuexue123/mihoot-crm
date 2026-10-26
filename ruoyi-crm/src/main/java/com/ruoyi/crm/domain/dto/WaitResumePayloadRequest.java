package com.ruoyi.crm.domain.dto;

/**
 * Wait 节点恢复请求（写入 payload）
 */
public class WaitResumePayloadRequest
{
    /** JSON 字符串 */
    private String payload;

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }
}
