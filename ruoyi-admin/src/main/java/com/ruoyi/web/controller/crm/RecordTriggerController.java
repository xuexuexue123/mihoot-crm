package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.dto.RecordTriggerMatchRequest;
import com.ruoyi.crm.service.IRecordTriggerMatchService;
import com.ruoyi.crm.service.support.RecordTriggerBffSecretValidator;

/**
 * CRM_RECORD_TRIGGER BFF 专用 API
 */
@RestController
@RequestMapping("/crm/record-trigger")
public class RecordTriggerController extends BaseController
{
    @Autowired
    private IRecordTriggerMatchService recordTriggerMatchService;

    @Autowired
    private RecordTriggerBffSecretValidator recordTriggerBffSecretValidator;

    /**
     * 根据 CRM CRUD 操作匹配 CRM_RECORD_TRIGGER 节点
     */
    @PostMapping("/match")
    public AjaxResult match(@RequestBody RecordTriggerMatchRequest request, HttpServletRequest httpRequest)
    {
        recordTriggerBffSecretValidator.validate(httpRequest);
        return success(recordTriggerMatchService.match(request));
    }
}
