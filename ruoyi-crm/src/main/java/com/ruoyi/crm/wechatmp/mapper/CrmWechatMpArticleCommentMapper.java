package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpArticleComment;
import com.ruoyi.crm.wechatmp.dto.WechatMpArticleCommentQuery;

public interface CrmWechatMpArticleCommentMapper
{
    int upsert(CrmWechatMpArticleComment row);

    int batchUpsert(@Param("list") List<CrmWechatMpArticleComment> list);

    CrmWechatMpArticleComment selectById(@Param("id") Long id);

    List<CrmWechatMpArticleComment> selectList(WechatMpArticleCommentQuery query);

    int updateReply(@Param("id") Long id, @Param("replyContent") String replyContent,
            @Param("replyTime") java.util.Date replyTime, @Param("status") String status);

    int clearReply(@Param("id") Long id);

    int updateCommentType(@Param("id") Long id, @Param("commentType") int commentType);

    int markRead(@Param("id") Long id);

    /**
     * 侧栏回写联系人 / 线索。touch* 为 true 时才更新对应列（允许写 null 清空）。
     */
    int updateCrmLinks(@Param("id") Long id,
            @Param("touchContact") boolean touchContact, @Param("contactId") String contactId,
            @Param("touchLead") boolean touchLead, @Param("leadId") String leadId);

    int deleteById(@Param("id") Long id);

    int markReviewRequired(@Param("id") Long id, @Param("ruleId") Long ruleId);

    /** P2：待自动回复的 pending 且未标记待审的留言 */
    List<CrmWechatMpArticleComment> selectPendingForAutoReply(@Param("limit") int limit);
}
