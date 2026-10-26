package com.ruoyi.crm.wechatmp.dto;

import java.util.List;

/**
 * 图文留言列表查询
 */
public class WechatMpArticleCommentQuery
{
    private String appId;
    private List<String> appIds;
    private String status;
    private String msgDataId;
    private Integer indexInMsg;
    private String keyword;

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }
    public List<String> getAppIds() { return appIds; }
    public void setAppIds(List<String> appIds) { this.appIds = appIds; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMsgDataId() { return msgDataId; }
    public void setMsgDataId(String msgDataId) { this.msgDataId = msgDataId; }
    public Integer getIndexInMsg() { return indexInMsg; }
    public void setIndexInMsg(Integer indexInMsg) { this.indexInMsg = indexInMsg; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
