package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.CrmWecomWelcomeLog;
import com.ruoyi.crm.service.ICrmWecomWelcomeLogService;

@RestController
@RequestMapping("/crm/wecom/welcome-log")
public class CrmWecomWelcomeLogController extends BaseController
{
    @Autowired
    private ICrmWecomWelcomeLogService crmWecomWelcomeLogService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/list")
    public TableDataInfo list(CrmWecomWelcomeLog query,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime)
    {
        if (beginTime != null && !beginTime.isEmpty())
        {
            query.getParams().put("beginTime", beginTime);
        }
        if (endTime != null && !endTime.isEmpty())
        {
            query.getParams().put("endTime", endTime);
        }
        startPage();
        List<CrmWecomWelcomeLog> list = crmWecomWelcomeLogService.selectList(query);
        return getDataTable(list);
    }
}
