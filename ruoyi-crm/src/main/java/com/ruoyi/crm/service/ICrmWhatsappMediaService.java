package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.dto.whatsapp.WhatsAppMediaFetchResult;

/**
 * WhatsApp 入站/出站媒体代理（按租户校验 media_id 归属后从 Meta 拉取）。
 */
public interface ICrmWhatsappMediaService
{
    /**
     * @param tenantId 租户
     * @param mediaId  Meta media id（须已出现在本租户 crm_whatsapp_message）
     */
    WhatsAppMediaFetchResult fetchMedia(String tenantId, String mediaId);
}
