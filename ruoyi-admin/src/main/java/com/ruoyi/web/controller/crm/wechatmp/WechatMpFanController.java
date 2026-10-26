package com.ruoyi.web.controller.crm.wechatmp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpFan;
import com.ruoyi.crm.wechatmp.dto.WechatMpFanSyncRequest;
import com.ruoyi.crm.wechatmp.audience.dto.FanAudiencePreviewRequest;
import com.ruoyi.crm.wechatmp.service.IFanAudienceService;
import com.ruoyi.crm.wechatmp.service.IWechatMpFanService;
import com.ruoyi.crm.wechatmp.service.IWechatMpFanTimelineService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpFanController extends BaseController
{
    @Autowired
    private IWechatMpFanService fanService;

    @Autowired
    private IWechatMpFanTimelineService fanTimelineService;

    @Autowired
    private IFanAudienceService fanAudienceService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:fans') or @ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/fans/audience/fields")
    public AjaxResult audienceFields()
    {
        return AjaxResult.success(fanAudienceService.listFields());
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:fans') or @ss.hasPermi('crm:wechat_mp:message')")
    @PostMapping("/fans/audience/preview")
    public AjaxResult audiencePreview(@RequestBody FanAudiencePreviewRequest req)
    {
        return AjaxResult.success(fanAudienceService.preview(req));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:fans')")
    @GetMapping("/fans")
    public TableDataInfo list(CrmWechatMpFan query)
    {
        startPage();
        List<CrmWechatMpFan> list = fanService.selectFanList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:fans')")
    @GetMapping("/fans/timeline")
    public TableDataInfo timeline(
            @RequestParam("appId") String appId,
            @RequestParam("openId") String openId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "30") int pageSize)
    {
        return fanTimelineService.listTimeline(appId, openId, pageNum, pageSize);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:fans')")
    @Log(title = "公众号粉丝同步", businessType = BusinessType.OTHER)
    @PostMapping("/fans/sync")
    public AjaxResult sync(@RequestBody WechatMpFanSyncRequest req)
    {
        fanService.scheduleSync(req);
        return AjaxResult.success("粉丝同步任务已提交");
    }
}
