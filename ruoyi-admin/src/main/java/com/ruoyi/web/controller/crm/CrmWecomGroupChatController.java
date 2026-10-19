package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatDetailResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatListRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatListResponse;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomGroupChatStatsResponse;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmWecomGroupChatService;
import com.ruoyi.crm.service.ICrmWecomStatsQueryService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;

/**
 * 企业微信「群聊」查询代理接口（数据统计、列表、详情）。
 * access_token 由后端自动管理，前端无需手动传入。
 */
@RestController
@RequestMapping("/crm/wecom")
public class CrmWecomGroupChatController extends BaseController
{
    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private ICrmWecomGroupChatService groupChatService;

    @Autowired
    private ICrmWecomStatsQueryService statsQueryService;

    private static final long SECONDS_PER_DAY = 86400L;

    /**
     * 获取群聊数据统计（按自然日聚合）。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/groupchat-stats")
    public AjaxResult getGroupChatStats(@RequestBody WecomGroupChatStatsRequest request)
    {
        if (request.getDayBeginTime() == null || request.getDayEndTime() == null)
        {
            return AjaxResult.error("day_begin_time 和 day_end_time 为必填项");
        }
        long spanSeconds = request.getDayEndTime() - request.getDayBeginTime();
        if (spanSeconds > 30L * SECONDS_PER_DAY)
        {
            return AjaxResult.error("查询时间跨度不能超过30天");
        }

        WecomGroupChatStatsResponse response = statsQueryService.queryGroupchatFromSnapshot(request);
        return AjaxResult.success(response);
    }

    /**
     * 获取客户群列表（cursor 分页）。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @PostMapping("/groupchat/list")
    public AjaxResult listGroupChats(@RequestBody WecomGroupChatListRequest request)
    {
        if (request.getLimit() == null || request.getLimit() < 1 || request.getLimit() > 1000)
        {
            return AjaxResult.error("limit 为必填项，取值范围 1 ~ 1000");
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

        WecomGroupChatListResponse wecomResp;
        try
        {
            wecomResp = groupChatService.listGroupChats(accessToken, request);
        }
        catch (Exception e)
        {
            logger.error("调用企业微信 groupchat/list 失败: corpId={}", cfg.getCorpId(), e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "调用企业微信客户群列表API失败: " + e.getMessage());
        }

        if (wecomResp == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "企业微信未返回响应");
        }
        if (wecomResp.getErrcode() != null && wecomResp.getErrcode() != 0)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "企业微信返回错误: " + wecomResp.getErrmsg());
        }

        return AjaxResult.success()
                .put("group_chat_list", wecomResp.getGroupChatList())
                .put("next_cursor", wecomResp.getNextCursor());
    }

    /**
     * 获取客户群详情。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/groupchat/{chatId}")
    public AjaxResult getGroupChat(@PathVariable String chatId,
                                   @RequestParam(defaultValue = "1") Integer needName)
    {
        if (StringUtils.isEmpty(chatId))
        {
            return AjaxResult.error("chatId 不可为空");
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

        WecomGroupChatDetailResponse wecomResp;
        try
        {
            wecomResp = groupChatService.getGroupChat(accessToken, chatId, needName);
        }
        catch (Exception e)
        {
            logger.error("调用企业微信 groupchat/get 失败: chatId={}", chatId, e);
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "调用企业微信客户群详情API失败: " + e.getMessage());
        }

        if (wecomResp == null)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "企业微信未返回响应");
        }
        if (wecomResp.getErrcode() != null && wecomResp.getErrcode() != 0)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "企业微信返回错误: " + wecomResp.getErrmsg());
        }

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("group_chat", wecomResp.getGroupChat());
        return AjaxResult.success(data);
    }
}
