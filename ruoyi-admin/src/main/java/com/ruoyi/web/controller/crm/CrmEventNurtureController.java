package com.ruoyi.web.controller.crm;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.dto.EventNurtureSequenceSaveRequest;
import com.ruoyi.crm.domain.dto.EventNurtureStepsSaveRequest;
import com.ruoyi.crm.domain.vo.EventNurtureSequenceVo;
import com.ruoyi.crm.domain.vo.EventNurtureStepVo;
import com.ruoyi.crm.service.nurture.IEventNurtureSequenceService;

/**
 * 活动培育序列配置
 */
@RestController
@RequestMapping("/crm/event")
public class CrmEventNurtureController extends BaseController
{
    @Autowired
    private IEventNurtureSequenceService nurtureSequenceService;

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/nurture/sequence")
    public AjaxResult getSequence(@PathVariable("eventId") String eventId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        EventNurtureSequenceVo vo = nurtureSequenceService.getSequence(tenant, eventId);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PutMapping("/{eventId}/nurture/sequence")
    public AjaxResult saveSequence(@PathVariable("eventId") String eventId,
            @RequestBody(required = false) EventNurtureSequenceSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        EventNurtureSequenceVo vo = nurtureSequenceService.saveSequence(
                tenant, eventId, request, SecurityUtils.getUsername());
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping("/{eventId}/nurture/steps")
    public AjaxResult listSteps(@PathVariable("eventId") String eventId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        List<EventNurtureStepVo> steps = nurtureSequenceService.listSteps(tenant, eventId);
        return success(steps);
    }

    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @PutMapping("/{eventId}/nurture/steps")
    public AjaxResult saveSteps(@PathVariable("eventId") String eventId,
            @RequestBody(required = false) EventNurtureStepsSaveRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        List<EventNurtureStepVo> steps = nurtureSequenceService.replaceSteps(tenant, eventId, request);
        return success(steps);
    }
}
