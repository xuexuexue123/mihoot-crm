package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorDataResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomBehaviorMemberAggregation;
import com.ruoyi.crm.service.ICrmWecomStatsQueryService;

/**
 * 企业微信「联系客户统计」行为数据查询接口。
 * 前端 POST /api/crm/wecom/user-behavior-data → Next.js BFF rewrite → 此处 POST /crm/wecom/user-behavior-data。
 * 数据来自本地按成员按天快照库聚合，成员删除后仍可查历史。
 */
@RestController
@RequestMapping("/crm/wecom")
public class CrmWecomBehaviorController extends BaseController
{
    @Autowired
    private ICrmWecomStatsQueryService statsQueryService;

    private static final long SECONDS_PER_DAY = 86400L;

    /**
     * 获取成员联系客户行为数据。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/user-behavior-data")
    public AjaxResult getUserBehaviorData(@RequestBody WecomBehaviorDataRequest request)
    {
        if ((request.getUserid() == null || request.getUserid().isEmpty())
                && (request.getPartyid() == null || request.getPartyid().isEmpty()))
        {
            return AjaxResult.error("userid 与 partyid 不可同时为空");
        }
        if (request.getStartTime() == null || request.getEndTime() == null)
        {
            return AjaxResult.error("start_time 和 end_time 为必填项");
        }
        if (request.getStartTime() > request.getEndTime())
        {
            return AjaxResult.error("start_time 不能大于 end_time");
        }
        long spanSeconds = request.getEndTime() - request.getStartTime();
        if (spanSeconds > 30L * SECONDS_PER_DAY)
        {
            return AjaxResult.error("查询时间跨度不能超过30天");
        }

        WecomBehaviorDataResponse response = statsQueryService.queryBehaviorFromSnapshot(request);
        return AjaxResult.success(response);
    }

    /**
     * 获取成员联系客户行为数据（按成员聚合）。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/user-behavior-data/by-member")
    public AjaxResult getUserBehaviorDataByMember(@RequestBody WecomBehaviorDataRequest request)
    {
        if ((request.getUserid() == null || request.getUserid().isEmpty())
                && (request.getPartyid() == null || request.getPartyid().isEmpty()))
        {
            return AjaxResult.error("userid 与 partyid 不可同时为空");
        }
        if (request.getStartTime() == null || request.getEndTime() == null)
        {
            return AjaxResult.error("start_time 和 end_time 为必填项");
        }
        if (request.getStartTime() > request.getEndTime())
        {
            return AjaxResult.error("start_time 不能大于 end_time");
        }
        long spanSeconds = request.getEndTime() - request.getStartTime();
        if (spanSeconds > 30L * SECONDS_PER_DAY)
        {
            return AjaxResult.error("查询时间跨度不能超过30天");
        }

        WecomBehaviorMemberAggregation response = statsQueryService.queryBehaviorByMember(request);
        return AjaxResult.success(response);
    }
}
