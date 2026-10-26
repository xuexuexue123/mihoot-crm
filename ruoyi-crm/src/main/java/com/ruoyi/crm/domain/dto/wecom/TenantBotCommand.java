package com.ruoyi.crm.domain.dto.wecom;

/**
 * 内部接口返回给 bot-server 的待处理指令
 */
public class TenantBotCommand
{
    private String tenant;
    private String botId;
    /** AES 解密后的明文 */
    private String botSecret;
    /** 内部 CRM JWT（Bearer token） */
    private String crmToken;
    private String status;

    public String getTenant()
    {
        return tenant;
    }

    public void setTenant(String tenant)
    {
        this.tenant = tenant;
    }

    public String getBotId()
    {
        return botId;
    }

    public void setBotId(String botId)
    {
        this.botId = botId;
    }

    public String getBotSecret()
    {
        return botSecret;
    }

    public void setBotSecret(String botSecret)
    {
        this.botSecret = botSecret;
    }

    public String getCrmToken()
    {
        return crmToken;
    }

    public void setCrmToken(String crmToken)
    {
        this.crmToken = crmToken;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
