package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.EventTriggerMatchRequest;
import com.ruoyi.crm.service.IEventTriggerMatchService;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;

/**
 * CRM_EVENT_TRIGGER BFF 专用 API
 */
@RestController
@RequestMapping("/crm/event-trigger")
public class EventTriggerController extends BaseController
{
    @Autowired
    private IEventTriggerMatchService eventTriggerMatchService;

    @Autowired
    private RecordTriggerBffSecretValidator recordTriggerBffSecretValidator;

    /**
     * 根据 Activity eventType 匹配 CRM_EVENT_TRIGGER 节点
     */
    @PostMapping("/match")
    public AjaxResult match(@RequestBody EventTriggerMatchRequest request, HttpServletRequest httpRequest)
    {
        recordTriggerBffSecretValidator.validate(httpRequest);
        return success(eventTriggerMatchService.match(request));
    }
}
