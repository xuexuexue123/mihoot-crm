package com.ruoyi.web.controller.crm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomSyncedProductItem;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomProductAlbumService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;

/**
 * 企业微信商品图册同步接口。
 * 前端 POST /api/crm/wecom/product-album → Next.js BFF rewrite → 此处 POST /crm/wecom/product-album。
 * access_token 由后端自动管理，前端无需手动传入。
 */
@RestController
@RequestMapping("/crm/wecom")
public class CrmWecomProductAlbumController extends BaseController
{
    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private ICrmWecomProductAlbumService productAlbumService;

    /**
     * 将 CRM 产品同步到企业微信商品图册。
     * 若产品已有 wecomProductId 则更新，否则创建并回写映射。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/product-album")
    public AjaxResult syncProductToWecom(@RequestBody WecomProductAlbumRequest request)
    {
        if (request.getProductId() == null || request.getProductId().isEmpty())
        {
            return AjaxResult.error("productId 不能为空");
        }
        if (request.getProductName() == null || request.getProductName().isEmpty())
        {
            return AjaxResult.error("productName 不能为空");
        }
        if (request.getPrice() == null)
        {
            return AjaxResult.error("price 不能为空");
        }

        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isEmpty(cfg.getCorpId()))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "未找到企业微信配置，请先完成企微自建应用配置");
        }

        String accessToken;
        try
        {
            accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        }
        catch (Exception e)
        {
            logger.error("获取 access_token 失败: corpId={}", cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "获取 access_token 失败: " + e.getMessage());
        }

        WecomProductAlbumResponse wecomResp;
        try
        {
            wecomResp = productAlbumService.syncToWecom(accessToken, request);
        }
        catch (Exception e)
        {
            logger.error("同步商品图册失败: productId={}, corpId={}", request.getProductId(), cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "同步商品图册失败: " + e.getMessage());
        }

        if (wecomResp == null)
        {
            return AjaxResult.error("企业微信未返回响应");
        }
        if (wecomResp.getErrcode() != null && wecomResp.getErrcode() != 0)
        {
            return AjaxResult.error("企业微信返回错误: " + wecomResp.getErrmsg());
        }

        return AjaxResult.success(wecomResp);
    }

    /**
     * 从企业微信删除商品图册，并清除本地映射。
     * 注意：路径参数 productId 是企微商品图册的 product_id，非 CRM 的 productId。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @DeleteMapping("/product-album/{productId}")
    public AjaxResult deleteWecomProductAlbum(@PathVariable String productId)
    {
        if (productId == null || productId.isEmpty())
        {
            return AjaxResult.error("productId 不能为空");
        }

        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isEmpty(cfg.getCorpId()))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "未找到企业微信配置，请先完成企微自建应用配置");
        }

        String accessToken;
        try
        {
            accessToken = wecomAccessTokenService.getAgentAccessToken(cfg);
        }
        catch (Exception e)
        {
            logger.error("获取 access_token 失败: corpId={}", cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "获取 access_token 失败: " + e.getMessage());
        }

        try
        {
            productAlbumService.deleteFromWecom(accessToken, productId);
        }
        catch (Exception e)
        {
            logger.error("删除企微商品图册失败: wecomProductId={}, corpId={}", productId, cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "删除商品图册失败: " + e.getMessage());
        }

        return AjaxResult.success();
    }

    /**
     * 查询已同步到企微商品图册的产品映射列表。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/product-album/list")
    public AjaxResult listSyncedProductAlbums()
    {
        List<WecomSyncedProductItem> rows;
        try
        {
            rows = productAlbumService.listSynced();
        }
        catch (Exception e)
        {
            logger.error("查询已同步商品图册列表失败", e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "查询失败: " + e.getMessage());
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("rows", rows != null ? rows : new java.util.ArrayList<>());

        return AjaxResult.success(data);
    }
}
