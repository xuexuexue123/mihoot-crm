package com.ruoyi.crm.domain.dto.wecom;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 查询朋友圈互动数据响应（#5 get_moment_comments）。
 */
public class WecomMomentCommentsResponse
{
    private Integer errcode;
    private String errmsg;

    @JsonProperty("comment_list")
    private List<WecomMomentInteractionItem> commentList;

    @JsonProperty("like_list")
    private List<WecomMomentInteractionItem> likeList;

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

    public List<WecomMomentInteractionItem> getCommentList()
    {
        return commentList;
    }

    public void setCommentList(List<WecomMomentInteractionItem> commentList)
    {
        this.commentList = commentList;
    }

    public List<WecomMomentInteractionItem> getLikeList()
    {
        return likeList;
    }

    public void setLikeList(List<WecomMomentInteractionItem> likeList)
    {
        this.likeList = likeList;
    }
}
