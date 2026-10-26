package com.ruoyi.crm.wechatmp.service;

import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftAddRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftSyncRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpDraftUpdateRequest;

public interface IWechatMpArticleDraftService
{
    /** 分页查询草稿列表（仅从本地 DB 读取，不触发增量同步） */
    TableDataInfo listDrafts(String appId, int offset, int count);

    /** 获取草稿详情 */
    AjaxResult getDraftDetail(String appId, String mediaId, boolean localOnly);

    /** 新增草稿 */
    AjaxResult createDraft(String appId, WechatMpDraftAddRequest request, boolean localOnly);

    /** 修改草稿 */
    AjaxResult updateDraft(String appId, String mediaId, WechatMpDraftUpdateRequest request, boolean localOnly);

    /** 删除草稿 */
    AjaxResult deleteDraft(String appId, String mediaId, boolean localOnly);

    /** 将本地草稿同步到微信公众号。body 可选，非空时 body 中的字段覆盖 DB 再推送。 */
    AjaxResult syncDraft(String appId, String mediaId, WechatMpDraftSyncRequest body);

    /** 上传图文消息内的图片到微信服务器，返回图片 URL */
    AjaxResult uploadImage(String appId, MultipartFile file);

    /** 发布草稿：调用微信 freepublish/submit 将指定草稿提交发布 */
    AjaxResult publishDraft(String appId, String mediaId);

    /** 更新草稿的发布状态 */
    AjaxResult updatePublished(String appId, String mediaId, boolean published);

    /** 查询发布任务状态并更新本地 publish_task 记录（含 article_id、article_url） */
    AjaxResult queryPublishStatus(String appId, String publishId);

    /** 刷新所有发布中（publish_status=1）的任务状态 */
    AjaxResult refreshAllPendingStatus(String appId);

    /** 显式触发增量同步：从微信拉取新草稿并合并到本地 DB */
    AjaxResult fetchDraftsFromWechat(String appId);
}
