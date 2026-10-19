package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmEventSpeaker;
import com.ruoyi.crm.service.ICrmEventSpeakerService;
import com.ruoyi.common.constant.HttpStatus;

/**
 * 活动演讲者Controller
 * 
 * @author luohd
 * @date 2025-07-20
 */
@RestController
@RequestMapping("/crm/event/{eventId}/speaker")
public class CrmEventSpeakerController extends BaseController
{
    @Autowired
    private ICrmEventSpeakerService speakerService;

    /**
     * 查询活动演讲者列表
     */
    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping
    public AjaxResult list(@PathVariable("eventId") String eventId)
    {
        List<CrmEventSpeaker> list = speakerService.selectSpeakerByEventId(eventId);
        return success(list);
    }

    /**
     * 查询单个演讲者详情
     */
    @PreAuthorize("@ss.hasPermi('crm:event:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("eventId") String eventId, @PathVariable("id") Long id)
    {
        CrmEventSpeaker speaker = speakerService.selectSpeakerById(id);
        if (speaker == null || !eventId.equals(speaker.getEventId()))
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Speaker not found");
        }
        return success(speaker);
    }

    /**
     * 新增演讲者
     */
    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @Log(title = "活动演讲者", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@PathVariable("eventId") String eventId, @RequestBody CrmEventSpeaker speaker)
    {
        speaker.setId(null);
        speaker.setEventId(eventId);
        return toAjax(speakerService.insertSpeaker(speaker));
    }

    /**
     * 修改演讲者
     */
    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @Log(title = "活动演讲者", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/{id}")
    public AjaxResult edit(@PathVariable("eventId") String eventId,
            @PathVariable("id") Long id, @RequestBody CrmEventSpeaker speaker)
    {
        CrmEventSpeaker existing = speakerService.selectSpeakerById(id);
        if (existing == null || !eventId.equals(existing.getEventId()))
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Speaker not found");
        }
        speaker.setId(id);
        speaker.setEventId(null); // 不允许通过此接口修改 event_id
        return toAjax(speakerService.updateSpeaker(speaker));
    }

    /**
     * 删除演讲者
     */
    @PreAuthorize("@ss.hasPermi('crm:event:edit')")
    @Log(title = "活动演讲者", businessType = BusinessType.DELETE)
    @DeleteMapping(value = "/{id}")
    public AjaxResult remove(@PathVariable("eventId") String eventId, @PathVariable("id") Long id)
    {
        CrmEventSpeaker existing = speakerService.selectSpeakerById(id);
        if (existing == null || !eventId.equals(existing.getEventId()))
        {
            return AjaxResult.error(HttpStatus.NOT_FOUND, "Speaker not found");
        }
        return toAjax(speakerService.deleteSpeakerById(id));
    }
}
