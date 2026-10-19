package com.ruoyi.web.controller.crm;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.vo.KfSummaryCompatVo;
import com.ruoyi.crm.service.IContactServiceSummaryService;

/**
 * 微信客服摘要兼容入口（薄代理 → Contact service-summary）
 */
@RestController
@RequestMapping("/crm/wecom/kf")
public class CrmWecomKfSummaryController extends BaseController
{
    @Autowired
    private IContactServiceSummaryService contactServiceSummaryService;

    @PreAuthorize("@ss.hasAnyPermi('crm:contact:edit,crm:contact:query')")
    @PostMapping("/summary")
    public AjaxResult summary(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        String externalUserid = body != null && body.get("externalUserid") != null
                ? String.valueOf(body.get("externalUserid")).trim() : null;
        String openKfid = body != null && body.get("openKfid") != null
                ? String.valueOf(body.get("openKfid")).trim() : null;
        if (StringUtils.isEmpty(externalUserid))
        {
            return error("Missing required field: externalUserid");
        }
        KfSummaryCompatVo data = contactServiceSummaryService.generateKfSummary(tenant, externalUserid, openKfid);
        return success(data);
    }
}
