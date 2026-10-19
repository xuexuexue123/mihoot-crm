package com.ruoyi.crm.domain.dto.wecom;

/**
 * 保存智能机器人配置（Secret 新建必填，更新可省略）
 */
public class WecomAiBotConfigSaveRequest
{
    private String botId;
    /** 新建必填；更新时省略表示保留原 Secret */
    private String botSecret;

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
}
