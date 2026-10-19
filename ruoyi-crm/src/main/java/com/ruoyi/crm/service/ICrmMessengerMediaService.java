package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.messenger.MessengerMediaFetchResult;

public interface ICrmMessengerMediaService
{
    /**
     * 按消息 id 代理拉取媒体（校验租户归属后用 Page Token 请求 Meta URL）。
     */
    MessengerMediaFetchResult fetchByMessageId(String tenantId, Long messageId);

    /**
     * 按媒体 URL 代理（须为该租户某条消息已落库的 media_url）。
     */
    MessengerMediaFetchResult fetchByMediaUrl(String tenantId, String mediaUrl);

    /**
     * 返回消息落库的原始 media_url（用于 OSS 直链 302）；无则 null。
     */
    String resolveStoredMediaUrl(String tenantId, Long messageId);
}
