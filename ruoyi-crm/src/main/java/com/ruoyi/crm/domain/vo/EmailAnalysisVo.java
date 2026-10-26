package com.ruoyi.crm.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.crm.domain.dto.EmailAnalysisResultDto;

/**
 * 邮件 AI 分析 VO
 */
public class EmailAnalysisVo
{
    private String analysisId;
    private String messageExternalId;
    private String provider;
    private String externalName;
    private String locale;
    private String bodyHash;
    private EmailAnalysisResultDto analysis;
    private String model;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date updateTime;

    public String getAnalysisId()
    {
        return analysisId;
    }

    public void setAnalysisId(String analysisId)
    {
        this.analysisId = analysisId;
    }

    public String getMessageExternalId()
    {
        return messageExternalId;
    }

    public void setMessageExternalId(String messageExternalId)
    {
        this.messageExternalId = messageExternalId;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getExternalName()
    {
        return externalName;
    }

    public void setExternalName(String externalName)
    {
        this.externalName = externalName;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public String getBodyHash()
    {
        return bodyHash;
    }

    public void setBodyHash(String bodyHash)
    {
        this.bodyHash = bodyHash;
    }

    public EmailAnalysisResultDto getAnalysis()
    {
        return analysis;
    }

    public void setAnalysis(EmailAnalysisResultDto analysis)
    {
        this.analysis = analysis;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
}
