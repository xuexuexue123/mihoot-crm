package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpPublishTask;

public interface CrmWechatMpPublishTaskMapper
{
    /** 插入发布任务 */
    int insert(CrmWechatMpPublishTask task);

    /** 按 publish_id 更新发布结果（状态、article_id、article_url、fail_idxs） */
    int updateByPublishId(CrmWechatMpPublishTask task);

    /** 按 publish_id 查询 */
    CrmWechatMpPublishTask selectByPublishId(@Param("publishId") String publishId);

    /** 按 msg_data_id 查询（统计 API 关联用） */
    List<CrmWechatMpPublishTask> selectByMsgDataId(@Param("msgDataId") String msgDataId);

    /** 按 media_id 查询历史发布记录 */
    List<CrmWechatMpPublishTask> selectByMediaId(@Param("appId") String appId,
            @Param("mediaId") String mediaId);

    /** 查询所有 publish_status = 1 (发布中) 的任务 */
    List<CrmWechatMpPublishTask> selectAllPending();

    /**
     * 近期发布成功且有 msg_data_id 的任务（留言 sync 选篇）。
     * @param lookbackDays 回溯天数
     * @param limit 每 app 上限
     */
    List<CrmWechatMpPublishTask> selectRecentSuccessWithMsgDataId(
            @Param("appId") String appId,
            @Param("lookbackDays") int lookbackDays,
            @Param("limit") int limit);

    /** 按 appId + msg_data_id 查任务（可带 index） */
    List<CrmWechatMpPublishTask> selectByAppIdAndMsgDataId(
            @Param("appId") String appId,
            @Param("msgDataId") String msgDataId,
            @Param("indexInMsg") Integer indexInMsg);
}
