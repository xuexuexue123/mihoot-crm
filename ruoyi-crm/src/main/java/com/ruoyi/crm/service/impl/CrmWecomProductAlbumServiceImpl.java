package com.ruoyi.crm.service.impl;

import java.io.File;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.domain.CrmProductChannelBinding;
import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomSyncedProductItem;
import com.ruoyi.crm.domain.vo.ProductWecomPreviewVo;
import com.ruoyi.crm.mapper.CrmProductChannelBindingMapper;
import com.ruoyi.crm.mapper.CrmProductMapper;
import com.ruoyi.crm.service.ICrmWecomProductAlbumService;
import com.ruoyi.crm.wecom.WecomMediaUploadResult;
import com.ruoyi.crm.wecom.WecomTencentApiClient;

@Service
public class CrmWecomProductAlbumServiceImpl implements ICrmWecomProductAlbumService
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomProductAlbumServiceImpl.class);

    /** 商品图册 price 上限：5000000 分（50000 元） */
    private static final int MAX_PRICE = 5000000;

    /** description 最大字数 */
    private static final int MAX_DESCRIPTION_LENGTH = 300;

    @Autowired
    private WecomTencentApiClient wecomTencentApiClient;

    @Autowired
    private CrmProductMapper crmProductMapper;

    @Autowired
    private CrmProductChannelBindingMapper bindingMapper;

    @Override
    public ProductWecomPreviewVo previewProductSync(String productId)
    {
        if (StringUtils.isBlank(productId))
        {
            throw new ServiceException("productId 不能为空", HttpStatus.BAD_REQUEST);
        }
        CrmProduct product = crmProductMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            throw new ServiceException("产品不存在: " + productId, HttpStatus.BAD_REQUEST);
        }

        CrmProductChannelBinding binding = bindingMapper.selectByProductAndChannel(
            productId, CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
        String existingWecomId = binding != null && StringUtils.isNotBlank(binding.getExternalId())
            ? binding.getExternalId()
            : product.getWecomProductId();
        String syncStatus = binding != null ? binding.getSyncStatus() : (
            StringUtils.isNotBlank(existingWecomId) ? CrmProductChannelBinding.SYNC_SYNCED : null);

        String cover = StringUtils.isNotBlank(product.getCoverImage()) ? product.getCoverImage() : product.getAvatar();
        String desc = StringUtils.isNotBlank(product.getShortDescription())
            ? product.getShortDescription()
            : product.getDescription();
        if (StringUtils.isBlank(desc))
        {
            desc = product.getProductName();
        }

        ProductWecomPreviewVo vo = new ProductWecomPreviewVo();
        vo.setProductId(productId);
        vo.setProductName(product.getProductName());
        vo.setProductCode(product.getProductCode());
        vo.setDescription(desc);
        vo.setCoverImage(cover);
        vo.setStandardPriceYuan(product.getOutboundPrice());
        vo.setWecomProductId(existingWecomId);
        vo.setSyncStatus(syncStatus);
        vo.setMode(StringUtils.isNotBlank(existingWecomId) ? "update" : "create");

        List<String> blockers = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (CrmProduct.STATUS_ARCHIVED.equals(product.getStatus()) || "1".equals(product.getDelFlag()))
        {
            blockers.add("已归档产品不能同步到企微");
        }
        if (StringUtils.isBlank(product.getProductName()))
        {
            blockers.add("产品名称不能为空");
        }
        if (StringUtils.isBlank(cover) && StringUtils.isBlank(existingWecomId))
        {
            blockers.add("首次同步企微需要封面图");
        }
        else if (StringUtils.isBlank(cover))
        {
            warnings.add("当前无封面，更新时可能沿用企微已有图片");
        }
        if (product.getOutboundPrice() == null)
        {
            blockers.add("产品未设置标准售价");
        }
        else
        {
            try
            {
                int priceFen = product.getOutboundPrice().movePointRight(2)
                    .setScale(0, RoundingMode.HALF_UP).intValueExact();
                vo.setPriceFen(priceFen);
                if (priceFen > MAX_PRICE)
                {
                    blockers.add("价格超过企微上限 50000 元");
                }
                if (priceFen <= 0)
                {
                    warnings.add("价格为 0 或负数，请确认是否符合业务预期");
                }
            }
            catch (ArithmeticException e)
            {
                blockers.add("标准售价无法转换为企微分单位");
            }
        }
        if (StringUtils.isNotBlank(desc) && desc.length() > MAX_DESCRIPTION_LENGTH)
        {
            warnings.add("描述超过企微 " + MAX_DESCRIPTION_LENGTH + " 字限制，同步时将截断");
        }
        if (CrmProductChannelBinding.SYNC_DIRTY.equals(syncStatus))
        {
            warnings.add("本地内容相对上次同步有变更");
        }
        if (CrmProductChannelBinding.SYNC_FAILED.equals(syncStatus))
        {
            warnings.add("上次同步失败，可重试");
        }

        vo.setBlockers(blockers);
        vo.setWarnings(warnings);
        vo.setCanSync(blockers.isEmpty());
        return vo;
    }

    @Override
    public WecomProductAlbumResponse syncProductFromMaster(String accessToken, String productId)
    {
        CrmProduct product = crmProductMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            throw new ServiceException("产品不存在: " + productId, HttpStatus.BAD_REQUEST);
        }
        if (CrmProduct.STATUS_ARCHIVED.equals(product.getStatus()) || "1".equals(product.getDelFlag()))
        {
            throw new ServiceException("已归档产品不能同步到企微", HttpStatus.BAD_REQUEST);
        }
        String cover = StringUtils.isNotBlank(product.getCoverImage()) ? product.getCoverImage() : product.getAvatar();
        if (StringUtils.isBlank(cover))
        {
            throw new ServiceException("首次同步企微需要封面图", HttpStatus.BAD_REQUEST);
        }
        if (product.getOutboundPrice() == null)
        {
            throw new ServiceException("产品未设置标准售价，无法同步企微", HttpStatus.BAD_REQUEST);
        }
        // 主档存元 → 企微要分，只在服务层换算一次
        int priceFen = product.getOutboundPrice().movePointRight(2).setScale(0, RoundingMode.HALF_UP).intValueExact();
        if (priceFen > MAX_PRICE)
        {
            throw new ServiceException("价格超过上限 50000 元", HttpStatus.BAD_REQUEST);
        }

        String desc = StringUtils.isNotBlank(product.getShortDescription())
            ? product.getShortDescription()
            : product.getDescription();

        WecomProductAlbumRequest request = new WecomProductAlbumRequest();
        request.setProductId(productId);
        request.setProductName(product.getProductName());
        request.setDescription(desc);
        request.setPrice(priceFen);
        request.setAvatar(cover);
        return syncToWecom(accessToken, request);
    }

    @Override
    public WecomProductAlbumResponse syncToWecom(String accessToken, WecomProductAlbumRequest request)
    {
        String productId = request.getProductId();
        if (productId == null || productId.isEmpty())
        {
            WecomProductAlbumResponse resp = new WecomProductAlbumResponse();
            resp.setErrcode(-1);
            resp.setErrmsg("productId 不能为空");
            return resp;
        }

        CrmProduct product = crmProductMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            WecomProductAlbumResponse resp = new WecomProductAlbumResponse();
            resp.setErrcode(-1);
            resp.setErrmsg("产品不存在: " + productId);
            return resp;
        }

        CrmProductChannelBinding binding = bindingMapper.selectByProductAndChannel(
            productId, CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
        String existingWecomId = binding != null && StringUtils.isNotBlank(binding.getExternalId())
            ? binding.getExternalId()
            : product.getWecomProductId();

        JSONArray attachments = null;
        String avatar = request.getAvatar();
        if (avatar != null && !avatar.isEmpty())
        {
            String mediaId = uploadImageFromUrl(accessToken, avatar);
            if (mediaId != null && !mediaId.isEmpty())
            {
                JSONObject att = new JSONObject();
                att.put("type", "image");
                JSONObject image = new JSONObject();
                image.put("media_id", mediaId);
                att.put("image", image);
                attachments = new JSONArray();
                attachments.add(att);
                log.info("媒体上传成功: media_id={}, source={}", mediaId, avatar);
            }
            else
            {
                log.warn("图片上传失败或media_id为空, avatar={}", avatar);
            }
        }

        JSONObject body = new JSONObject();

        String desc = request.getProductName() != null ? request.getProductName() : "";
        if (request.getDescription() != null && !request.getDescription().isEmpty())
        {
            desc = desc + " " + request.getDescription();
        }
        if (desc.length() > MAX_DESCRIPTION_LENGTH)
        {
            desc = desc.substring(0, MAX_DESCRIPTION_LENGTH);
        }
        body.put("description", desc);

        Integer price = request.getPrice();
        if (price != null)
        {
            if (price > MAX_PRICE)
            {
                WecomProductAlbumResponse resp = new WecomProductAlbumResponse();
                resp.setErrcode(-1);
                resp.setErrmsg("价格超过上限 50000 元");
                return resp;
            }
            body.put("price", price);
        }

        String productSn = product.getProductCode() != null && !product.getProductCode().isEmpty()
                ? product.getProductCode()
                : productId;
        productSn = productSn.replaceAll("[^a-zA-Z0-9]", "");
        if (productSn.length() > 128)
        {
            productSn = productSn.substring(0, 128);
        }
        if (!productSn.isEmpty())
        {
            body.put("product_sn", productSn);
        }

        if (attachments != null && !attachments.isEmpty())
        {
            body.put("attachments", attachments);
        }

        boolean isUpdate = StringUtils.isNotBlank(existingWecomId);
        JSONObject wecomResp;

        if (isUpdate)
        {
            body.put("product_id", existingWecomId);
            log.info("更新企微商品图册: product_id={}, body={}", existingWecomId, body.toJSONString());
            wecomResp = wecomTencentApiClient.updateProductAlbum(accessToken, body);
        }
        else
        {
            if (attachments == null || attachments.isEmpty())
            {
                WecomProductAlbumResponse resp = new WecomProductAlbumResponse();
                resp.setErrcode(-1);
                resp.setErrmsg("创建商品图册时至少需要一张图片");
                return resp;
            }
            log.info("创建企微商品图册: body={}", body.toJSONString());
            wecomResp = wecomTencentApiClient.addProductAlbum(accessToken, body);
        }

        log.info("企微商品图册响应: errcode={}, errmsg={}, product_id={}",
                wecomResp.getIntValue("errcode"), wecomResp.getString("errmsg"),
                wecomResp.getString("product_id"));

        WecomProductAlbumResponse resp = new WecomProductAlbumResponse();
        resp.setErrcode(wecomResp.getIntValue("errcode"));
        resp.setErrmsg(wecomResp.getString("errmsg"));

        String syncHash = buildSyncHash(request.getProductName(), request.getDescription(),
            request.getPrice(), request.getAvatar());
        String username = safeUsername();

        if (resp.getErrcode() != null && resp.getErrcode() == 0)
        {
            String wecomProductId = wecomResp.getString("product_id");
            if (!isUpdate && wecomProductId != null)
            {
                resp.setProductId(wecomProductId);
                crmProductMapper.updateWecomProductId(productId, wecomProductId);
                upsertBindingSuccess(productId, wecomProductId, syncHash, username);
                log.info("商品图册同步成功: crmProductId={}, wecomProductId={}", productId, wecomProductId);
            }
            else
            {
                String id = StringUtils.isNotBlank(wecomProductId) ? wecomProductId : existingWecomId;
                resp.setProductId(id);
                crmProductMapper.updateWecomProductId(productId, id);
                upsertBindingSuccess(productId, id, syncHash, username);
                log.info("商品图册更新成功: wecomProductId={}", id);
            }
        }
        else
        {
            markBindingFailed(productId, existingWecomId, resp.getErrmsg(), username);
            log.warn("企业微信商品图册同步失败: errcode={}, errmsg={}",
                    resp.getErrcode(), resp.getErrmsg());
        }

        return resp;
    }

    @Override
    public void deleteProductChannel(String accessToken, String productId)
    {
        CrmProduct product = crmProductMapper.selectCrmProductByProductId(productId);
        if (product == null)
        {
            throw new ServiceException("产品不存在: " + productId, HttpStatus.BAD_REQUEST);
        }
        CrmProductChannelBinding binding = bindingMapper.selectByProductAndChannel(
            productId, CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
        String wecomId = binding != null && StringUtils.isNotBlank(binding.getExternalId())
            ? binding.getExternalId()
            : product.getWecomProductId();
        if (StringUtils.isBlank(wecomId))
        {
            throw new ServiceException("产品尚未同步到企微", HttpStatus.BAD_REQUEST);
        }
        deleteFromWecom(accessToken, wecomId);
    }

    @Override
    public void deleteFromWecom(String accessToken, String wecomProductId)
    {
        JSONObject body = new JSONObject();
        body.put("product_id", wecomProductId);

        JSONObject wecomResp = wecomTencentApiClient.deleteProductAlbum(accessToken, body);

        int errcode = wecomResp.getIntValue("errcode");
        if (errcode != 0)
        {
            log.warn("企业微信删除商品图册失败: errcode={}, errmsg={}",
                    errcode, wecomResp.getString("errmsg"));
            throw new ServiceException("企业微信删除商品图册失败: " + wecomResp.getString("errmsg"));
        }

        crmProductMapper.clearWecomProductIdByWecomProductId(wecomProductId);
        bindingMapper.logicalDeleteByExternalId(
            CrmProductChannelBinding.CHANNEL_WECOM_ALBUM, wecomProductId, safeUsername());
    }

    @Override
    public List<WecomSyncedProductItem> listSynced()
    {
        List<CrmProduct> products = crmProductMapper.selectSyncedToWecom();
        List<WecomSyncedProductItem> result = new ArrayList<>();
        for (CrmProduct p : products)
        {
            WecomSyncedProductItem item = new WecomSyncedProductItem();
            item.setProductId(p.getProductId());
            String wecomId = StringUtils.isNotBlank(p.getWecomExternalId())
                ? p.getWecomExternalId() : p.getWecomProductId();
            item.setWecomProductId(wecomId);
            result.add(item);
        }
        return result;
    }

    private void upsertBindingSuccess(String productId, String externalId, String syncHash, String username)
    {
        Date now = new Date();
        CrmProductChannelBinding existing = bindingMapper.selectByProductAndChannel(
            productId, CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
        if (existing == null)
        {
            CrmProductChannelBinding row = new CrmProductChannelBinding();
            row.setBindingId(IdUtils.fastUUID());
            row.setProductId(productId);
            row.setChannelType(CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
            row.setExternalId(externalId);
            row.setSyncStatus(CrmProductChannelBinding.SYNC_SYNCED);
            row.setLastSyncHash(syncHash);
            row.setLastSyncTime(now);
            row.setLastSyncBy(username);
            row.setLastError("");
            row.setCreateBy(username);
            row.setCreateTime(now);
            row.setUpdateBy(username);
            row.setUpdateTime(now);
            row.setDelFlag("0");
            bindingMapper.insert(row);
        }
        else
        {
            existing.setExternalId(externalId);
            existing.setSyncStatus(CrmProductChannelBinding.SYNC_SYNCED);
            existing.setLastSyncHash(syncHash);
            existing.setLastSyncTime(now);
            existing.setLastSyncBy(username);
            existing.setLastError("");
            existing.setUpdateBy(username);
            existing.setUpdateTime(now);
            bindingMapper.update(existing);
        }
    }

    private void markBindingFailed(String productId, String externalId, String error, String username)
    {
        Date now = new Date();
        CrmProductChannelBinding existing = bindingMapper.selectByProductAndChannel(
            productId, CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
        String err = error == null ? "" : (error.length() > 1000 ? error.substring(0, 1000) : error);
        if (existing == null)
        {
            CrmProductChannelBinding row = new CrmProductChannelBinding();
            row.setBindingId(IdUtils.fastUUID());
            row.setProductId(productId);
            row.setChannelType(CrmProductChannelBinding.CHANNEL_WECOM_ALBUM);
            row.setExternalId(externalId);
            row.setSyncStatus(CrmProductChannelBinding.SYNC_FAILED);
            row.setLastError(err);
            row.setCreateBy(username);
            row.setCreateTime(now);
            row.setUpdateBy(username);
            row.setUpdateTime(now);
            row.setDelFlag("0");
            bindingMapper.insert(row);
        }
        else
        {
            existing.setSyncStatus(CrmProductChannelBinding.SYNC_FAILED);
            existing.setLastError(err);
            existing.setUpdateBy(username);
            existing.setUpdateTime(now);
            bindingMapper.update(existing);
        }
    }

    private static String buildSyncHash(String name, String description, Integer price, String avatar)
    {
        String raw = StringUtils.defaultString(name) + "|"
            + StringUtils.defaultString(description) + "|"
            + (price == null ? "" : price) + "|"
            + StringUtils.defaultString(avatar);
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest)
            {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            return Integer.toHexString(raw.hashCode());
        }
    }

    private static String safeUsername()
    {
        try
        {
            return SecurityUtils.getUsername();
        }
        catch (Exception e)
        {
            return "system";
        }
    }

    private String uploadImageFromUrl(String accessToken, String imagePath)
    {
        File sourceFile = null;
        File tempFile = null;
        boolean isTemp = false;
        try
        {
            if (imagePath.startsWith(Constants.RESOURCE_PREFIX + "/"))
            {
                String relativePath = imagePath.substring(Constants.RESOURCE_PREFIX.length());
                String localPath = RuoYiConfig.getProfile() + relativePath;
                sourceFile = new File(localPath);
                if (!sourceFile.exists() || !sourceFile.isFile())
                {
                    log.warn("本地图片文件不存在: {}", localPath);
                    return null;
                }
            }
            else if (imagePath.startsWith("http://") || imagePath.startsWith("https://"))
            {
                String suffix = ".jpg";
                try
                {
                    String path = new URL(imagePath).getPath();
                    String ext = FilenameUtils.getExtension(path);
                    if (ext != null && !ext.isEmpty())
                    {
                        suffix = "." + ext.toLowerCase();
                    }
                }
                catch (Exception ignored)
                {
                }

                tempFile = File.createTempFile("wecom_album_dl_", suffix);
                try (InputStream in = new URL(imagePath).openStream())
                {
                    Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                sourceFile = tempFile;
                isTemp = true;
            }
            else
            {
                log.warn("不支持的图片路径格式: {}", imagePath);
                return null;
            }

            WecomMediaUploadResult uploadResult =
                    wecomTencentApiClient.uploadAttachment(accessToken, "image", sourceFile, 2);

            if (uploadResult.getErrcode() != 0)
            {
                log.warn("企业微信上传图片失败: errcode={}, errmsg={}",
                        uploadResult.getErrcode(), uploadResult.getErrmsg());
                return null;
            }

            return uploadResult.getMediaId();
        }
        catch (Exception e)
        {
            log.error("上传图片失败: path={}", imagePath, e);
            return null;
        }
        finally
        {
            if (isTemp && tempFile != null && tempFile.exists())
            {
                tempFile.delete();
            }
        }
    }
}
