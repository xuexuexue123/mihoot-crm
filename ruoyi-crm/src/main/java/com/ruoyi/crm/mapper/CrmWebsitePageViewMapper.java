package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWebsitePageView;

/**
 * 官网页面浏览 Mapper
 */
public interface CrmWebsitePageViewMapper
{
    int insertPageView(CrmWebsitePageView pageView);

    /**
     * 同 session + pageUrl 在 withinSeconds 内是否已有记录（相对 viewedAt 或 now）
     */
    int countRecentDuplicate(@Param("sessionId") String sessionId,
            @Param("pageUrl") String pageUrl,
            @Param("viewedAt") Date viewedAt,
            @Param("withinSeconds") int withinSeconds);

    /**
     * 兜底补洞：同 session + pageUrl 且 |viewed_at - candidate| &lt; withinSeconds 则视为已存在
     */
    int countNearDuplicate(@Param("sessionId") String sessionId,
            @Param("pageUrl") String pageUrl,
            @Param("viewedAt") Date viewedAt,
            @Param("withinSeconds") int withinSeconds);

    List<CrmWebsitePageView> selectUnsyncedBySession(@Param("sessionId") String sessionId);

    int markSynced(@Param("id") String id, @Param("activityId") String activityId);
}
