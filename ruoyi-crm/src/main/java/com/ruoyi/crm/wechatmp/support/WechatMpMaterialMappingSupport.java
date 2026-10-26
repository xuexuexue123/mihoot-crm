package com.ruoyi.crm.wechatmp.support;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMaterial;
import com.ruoyi.crm.wechatmp.dto.WechatMpMaterialDetail;
import com.ruoyi.crm.wechatmp.dto.WechatMpMaterialListItem;
import com.ruoyi.crm.wechatmp.dto.WechatMpMaterialNewsItem;

/**
 * 微信永久素材映射：微信响应 ↔ DTO ↔ 本地实体。
 */
public final class WechatMpMaterialMappingSupport
{
    private WechatMpMaterialMappingSupport()
    {
    }

    public static List<WechatMpMaterialListItem> mapListItems(JSONObject res, String type)
    {
        List<WechatMpMaterialListItem> rows = new ArrayList<>();
        if (res == null)
        {
            return rows;
        }

        JSONArray items = res.getJSONArray("item");
        if (items == null || items.isEmpty())
        {
            return rows;
        }

        for (int i = 0; i < items.size(); i++)
        {
            JSONObject item = items.getJSONObject(i);
            if (item == null)
            {
                continue;
            }
            rows.add(mapWeChatItem(item, type));
        }
        return rows;
    }

    public static WechatMpMaterialListItem mapWeChatItem(JSONObject item, String type)
    {
        WechatMpMaterialListItem row = new WechatMpMaterialListItem();
        row.setMediaId(item.getString("media_id"));
        row.setUpdateTime(toOffsetDateTime(item.getLongValue("update_time")));
        row.setType(type);

        if ("news".equals(type))
        {
            List<WechatMpMaterialNewsItem> newsItems = mapNewsItemsFromContent(item.getJSONObject("content"));
            row.setNewsItems(newsItems);
            if (!newsItems.isEmpty())
            {
                WechatMpMaterialNewsItem first = newsItems.get(0);
                row.setTitle(first.getTitle());
                row.setDigest(first.getDigest());
                row.setThumbUrl(first.getThumbUrl());
                row.setUrl(first.getUrl());
            }
        }
        else
        {
            row.setName(item.getString("name"));
            row.setUrl(item.getString("url"));
        }
        return row;
    }

    public static List<WechatMpMaterialNewsItem> mapNewsItemsFromContent(JSONObject content)
    {
        if (content == null)
        {
            return new ArrayList<>();
        }
        return mapNewsItems(content.getJSONArray("news_item"));
    }

    public static List<WechatMpMaterialNewsItem> mapNewsItems(JSONArray newsItemArr)
    {
        List<WechatMpMaterialNewsItem> list = new ArrayList<>();
        if (newsItemArr == null || newsItemArr.isEmpty())
        {
            return list;
        }
        for (int i = 0; i < newsItemArr.size(); i++)
        {
            JSONObject news = newsItemArr.getJSONObject(i);
            if (news == null)
            {
                continue;
            }
            list.add(mapNewsItem(news));
        }
        return list;
    }

    public static WechatMpMaterialNewsItem mapNewsItem(JSONObject news)
    {
        WechatMpMaterialNewsItem item = new WechatMpMaterialNewsItem();
        item.setTitle(news.getString("title"));
        item.setAuthor(news.getString("author"));
        item.setDigest(news.getString("digest"));
        item.setContent(news.getString("content"));
        item.setContentSourceUrl(news.getString("content_source_url"));
        item.setThumbMediaId(news.getString("thumb_media_id"));
        if (news.containsKey("show_cover_pic"))
        {
            item.setShowCoverPic(news.getInteger("show_cover_pic"));
        }
        item.setUrl(news.getString("url"));
        item.setThumbUrl(news.getString("thumb_url"));
        return item;
    }

    public static long extractTotalCount(JSONObject res)
    {
        if (res == null)
        {
            return 0L;
        }
        return res.getLongValue("total_count");
    }

    public static CrmWechatMpMaterial toEntity(String appId, WechatMpMaterialListItem dto)
    {
        CrmWechatMpMaterial entity = new CrmWechatMpMaterial();
        entity.setAppId(appId);
        entity.setMediaId(dto.getMediaId());
        entity.setType(dto.getType());
        entity.setName(dto.getName());
        entity.setUrl(dto.getUrl());
        entity.setTitle(dto.getTitle());
        entity.setDigest(dto.getDigest());
        entity.setThumbUrl(dto.getThumbUrl());
        entity.setUpdateTime(dto.getUpdateTime());
        if (dto.getNewsItems() != null && !dto.getNewsItems().isEmpty())
        {
            entity.setNewsItemsJson(JSON.toJSONString(dto.getNewsItems()));
        }
        return entity;
    }

    public static List<CrmWechatMpMaterial> toEntityList(String appId, List<WechatMpMaterialListItem> dtos)
    {
        List<CrmWechatMpMaterial> list = new ArrayList<>(dtos.size());
        for (WechatMpMaterialListItem dto : dtos)
        {
            list.add(toEntity(appId, dto));
        }
        return list;
    }

    public static WechatMpMaterialListItem toListItem(CrmWechatMpMaterial entity)
    {
        WechatMpMaterialListItem dto = new WechatMpMaterialListItem();
        dto.setMediaId(entity.getMediaId());
        dto.setName(entity.getName());
        dto.setUrl(entity.getUrl());
        dto.setUpdateTime(entity.getUpdateTime());
        dto.setType(entity.getType());
        dto.setTitle(entity.getTitle());
        dto.setDigest(entity.getDigest());
        dto.setThumbUrl(entity.getThumbUrl());
        dto.setNewsItems(parseNewsItems(entity.getNewsItemsJson()));
        dto.setIsDefault(Boolean.TRUE.equals(entity.getIsDefault()));
        return dto;
    }

    public static List<WechatMpMaterialListItem> toListItems(List<CrmWechatMpMaterial> entities)
    {
        List<WechatMpMaterialListItem> list = new ArrayList<>(entities.size());
        for (CrmWechatMpMaterial entity : entities)
        {
            list.add(toListItem(entity));
        }
        return list;
    }

    public static WechatMpMaterialDetail toDetail(CrmWechatMpMaterial entity)
    {
        WechatMpMaterialDetail detail = new WechatMpMaterialDetail();
        detail.setType(entity.getType());
        detail.setMediaId(entity.getMediaId());
        detail.setContentType(entity.getContentType());
        detail.setTitle(entity.getTitle());
        detail.setDescription(entity.getDescription());
        detail.setDownUrl(entity.getDownUrl());
        detail.setNewsItems(parseNewsItems(entity.getNewsItemsJson()));
        return detail;
    }

    public static List<WechatMpMaterialNewsItem> parseNewsItems(String json)
    {
        if (json == null || json.isEmpty())
        {
            return Collections.emptyList();
        }
        List<WechatMpMaterialNewsItem> list = JSON.parseArray(json, WechatMpMaterialNewsItem.class);
        return list != null ? list : Collections.emptyList();
    }

    public static OffsetDateTime toOffsetDateTime(long unixSeconds)
    {
        if (unixSeconds <= 0L)
        {
            return null;
        }
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(unixSeconds), ZoneId.systemDefault());
    }
}
