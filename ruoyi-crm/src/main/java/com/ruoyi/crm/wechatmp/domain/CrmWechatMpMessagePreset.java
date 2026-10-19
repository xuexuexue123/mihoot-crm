package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 公众号模板消息预设
 */
public class CrmWechatMpMessagePreset
{
    private Long id;
    private String presetName;
    private String description;
    private String appId;
    private String wechatTemplateId;
    private String urlTemplate;
    @JsonIgnore
    private String dataJsonRaw;
    private String tags;
    /** 0=启用 1=停用 */
    private String status;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    /** 列表/详情查询：keyword 模糊 preset_name、description */
    private String keyword;
    /** 列表查询：tags 包含 */
    private String tag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPresetName() { return presetName; }
    public void setPresetName(String presetName) { this.presetName = presetName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getWechatTemplateId() { return wechatTemplateId; }
    public void setWechatTemplateId(String wechatTemplateId) { this.wechatTemplateId = wechatTemplateId; }
    public String getUrlTemplate() { return urlTemplate; }
    public void setUrlTemplate(String urlTemplate) { this.urlTemplate = urlTemplate; }

    @JsonProperty("dataJson")
    public JSONObject getDataJson()
    {
        if (dataJsonRaw == null || dataJsonRaw.isEmpty())
        {
            return null;
        }
        return JSONObject.parseObject(dataJsonRaw);
    }

    @JsonProperty("dataJson")
    public void setDataJson(JSONObject dataJson)
    {
        this.dataJsonRaw = dataJson == null ? null : dataJson.toJSONString();
    }

    @JsonIgnore
    public String getDataJsonRaw() { return dataJsonRaw; }

    @JsonIgnore
    public void setDataJsonRaw(String dataJsonRaw) { this.dataJsonRaw = dataJsonRaw; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
