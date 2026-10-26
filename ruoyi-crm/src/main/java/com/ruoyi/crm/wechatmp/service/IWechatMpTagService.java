package com.ruoyi.crm.wechatmp.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagBatchRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagCreateRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpTagUpdateRequest;

/**
 * 微信公众号标签管理服务接口
 *
 * @author luohd
 * @date 2026-07-17
 */
public interface IWechatMpTagService
{
    /**
     * 获取公众号已创建的标签列表
     */
    AjaxResult listTags(String appId);

    /**
     * 创建标签
     */
    AjaxResult createTag(String appId, WechatMpTagCreateRequest request);

    /**
     * 编辑标签
     */
    AjaxResult updateTag(String appId, WechatMpTagUpdateRequest request);

    /**
     * 删除标签
     */
    AjaxResult deleteTag(String appId, Long tagId);

    /**
     * 批量为用户打标签
     */
    AjaxResult batchTagging(String appId, WechatMpTagBatchRequest request);

    /**
     * 批量为用户取消标签
     */
    AjaxResult batchUntagging(String appId, WechatMpTagBatchRequest request);

    /**
     * 获取用户的标签列表
     */
    AjaxResult getUserTags(String appId, String openId);

    /**
     * 获取标签下粉丝列表
     */
    AjaxResult getTagFans(String appId, Long tagId, String nextOpenId);
}
