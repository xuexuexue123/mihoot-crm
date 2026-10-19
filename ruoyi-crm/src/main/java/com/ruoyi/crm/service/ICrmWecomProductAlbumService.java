package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomSyncedProductItem;

/**
 * 企业微信商品图册同步服务。
 */
public interface ICrmWecomProductAlbumService
{
    /**
     * 兼容旧接口：将 CRM 产品同步到企业微信商品图册。
     * 新调用方请使用 {@link #syncProductFromMaster(String, String)}，由服务端读取主档。
     */
    WecomProductAlbumResponse syncToWecom(String accessToken, WecomProductAlbumRequest request);

    /**
     * 产品中心人工同步：从主档读取名称/描述/价格/封面，换算为分后调用企微。
     */
    WecomProductAlbumResponse syncProductFromMaster(String accessToken, String productId);

    /**
     * 同步预览：返回主档将提交到企微的字段，不调用企微 API。
     */
    com.ruoyi.crm.domain.vo.ProductWecomPreviewVo previewProductSync(String productId);

    /**
     * 从企业微信删除商品图册，并清除本地映射。
     * 注意：参数是企微商品图册 product_id。
     */
    void deleteFromWecom(String accessToken, String wecomProductId);

    /**
     * 按 CRM productId 从企微删除并清除渠道绑定。
     */
    void deleteProductChannel(String accessToken, String productId);

    /**
     * 查询已同步到企微商品图册的产品映射列表。
     */
    List<WecomSyncedProductItem> listSynced();
}
