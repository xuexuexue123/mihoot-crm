package com.ruoyi.crm.wechatmp.domain;

import java.util.Date;

public class CrmWechatMpFan
{
    private Long id;
    private String appId;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatarUrl;
    private Boolean subscribe;
    private Date subscribeTime;
    private Date unsubscribeTime;
    private Integer sex;
    private String country;
    private String province;
    private String city;
    private String remark;
    private String tagIds;
    private String subscribeScene;
    private String contactId;
    private Date lastSyncAt;
    private Date createTime;
    private Date updateTime;
    private String contactName;
    private Date lastActivityAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public String getOpenId() { return openId; }
    public void setOpenId(String openId) { this.openId = openId; }
    public String getUnionId() { return unionId; }
    public void setUnionId(String unionId) { this.unionId = unionId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Boolean getSubscribe() { return subscribe; }
    public void setSubscribe(Boolean subscribe) { this.subscribe = subscribe; }
    public Date getSubscribeTime() { return subscribeTime; }
    public void setSubscribeTime(Date subscribeTime) { this.subscribeTime = subscribeTime; }
    public Date getUnsubscribeTime() { return unsubscribeTime; }
    public void setUnsubscribeTime(Date unsubscribeTime) { this.unsubscribeTime = unsubscribeTime; }
    public Integer getSex() { return sex; }
    public void setSex(Integer sex) { this.sex = sex; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getTagIds() { return tagIds; }
    public void setTagIds(String tagIds) { this.tagIds = tagIds; }
    public String getSubscribeScene() { return subscribeScene; }
    public void setSubscribeScene(String subscribeScene) { this.subscribeScene = subscribeScene; }
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }
    public Date getLastSyncAt() { return lastSyncAt; }
    public void setLastSyncAt(Date lastSyncAt) { this.lastSyncAt = lastSyncAt; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public Date getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(Date lastActivityAt) { this.lastActivityAt = lastActivityAt; }
}
