package com.ruoyi.crm.wechatmp.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleComment;
import com.ruoyi.crm.wechatmp.dto.WechatMpArticleCommentQuery;

public interface IWechatMpArticleCommentService
{
    /**
     * 留言列表（内部先解析可见 appId，再启动 PageHelper，避免污染 config 查询）。
     */
    List<CrmWechatMpArticleComment> selectList(WechatMpArticleCommentQuery query, int pageNum, int pageSize);

    CrmWechatMpArticleComment selectById(Long id);

    Map<String, Object> toApiMap(CrmWechatMpArticleComment row);

    void reply(Long id, String content);

    void openComment(String appId, String msgDataId, Integer index);

    void closeComment(String appId, String msgDataId, Integer index);

    void markelect(Long id);

    void unmarkelect(Long id);

    void deleteComment(Long id);

    void deleteReply(Long id);

    void markRead(Long id);

    /**
     * 侧栏回写 contactId / leadId。touch* 为 true 表示该字段参与更新（可写 null 清空）。
     */
    Map<String, Object> patchCrmLinks(Long id, boolean touchContact, String contactId,
            boolean touchLead, String leadId);
}
