package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.ruoyi.crm.domain.dto.CapturePublishRequest;
import com.ruoyi.crm.domain.vo.CaptureSummaryVo;
import com.ruoyi.crm.service.ICrmCaptureService;

@RestController
@RequestMapping("/crm/captures")
public class CrmCaptureController extends BaseController
{
    @Autowired
    private ICrmCaptureService crmCaptureService;

    @PreAuthorize("@ss.hasPermi('crm:captures:list')")
    @GetMapping
    public TableDataInfo list(@RequestParam(value = "templateId", required = false) String templateId,
            @RequestParam(value = "status", required = false) String status)
    {
        startPage();
        List<CaptureSummaryVo> list = crmCaptureService.list(templateId, status);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:captures:query')")
    @GetMapping("/{captureId}")
    public AjaxResult get(@PathVariable String captureId)
    {
        return AjaxResult.success(crmCaptureService.getDetail(captureId));
    }

    @PreAuthorize("@ss.hasPermi('crm:captures:publish')")
    @Log(title = "Captures 发布", businessType = BusinessType.UPDATE)
    @PostMapping("/publish")
    public AjaxResult publish(@RequestBody CapturePublishRequest request)
    {
        return AjaxResult.success(crmCaptureService.publish(request));
    }

    @PreAuthorize("@ss.hasPermi('crm:captures:publish')")
    @Log(title = "Captures 取消发布", businessType = BusinessType.UPDATE)
    @PostMapping("/{captureId}/unpublish")
    public AjaxResult unpublish(@PathVariable String captureId)
    {
        return AjaxResult.success(crmCaptureService.unpublish(captureId));
    }
}
