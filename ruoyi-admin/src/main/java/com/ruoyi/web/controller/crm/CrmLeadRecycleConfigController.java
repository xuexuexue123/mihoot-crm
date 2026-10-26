package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.LeadRecycleConfigUpsertDto;
import com.ruoyi.crm.service.ICrmLeadRecycleConfigService;

@RestController
@RequestMapping("/crm/lead/recycle")
public class CrmLeadRecycleConfigController extends BaseController
{
    @Autowired
    private ICrmLeadRecycleConfigService recycleConfigService;

    @PreAuthorize("@ss.hasPermi('crm:lead:recycle:query') or @ss.hasPermi('crm:lead:recycle:edit')")
    @GetMapping("/config")
    public AjaxResult getConfig()
    {
        return success(recycleConfigService.getConfig());
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:recycle:edit')")
    @Log(title = "线索回收机制", businessType = BusinessType.UPDATE)
    @PutMapping("/config")
    public AjaxResult saveConfig(@RequestBody LeadRecycleConfigUpsertDto body)
    {
        return success(recycleConfigService.saveConfig(body));
    }
}
