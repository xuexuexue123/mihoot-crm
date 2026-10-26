package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.dto.FollowUpCadenceSaveRequest;
import com.ruoyi.crm.domain.vo.FollowUpCadenceVo;
import com.ruoyi.crm.service.ICrmFollowUpCadenceService;

/**
 * 跟进序列配置
 */
@RestController
@RequestMapping("/crm/followupcadence")
public class CrmFollowUpCadenceController extends BaseController
{
    @Autowired
    private ICrmFollowUpCadenceService crmFollowUpCadenceService;

    @PreAuthorize("@ss.hasPermi('crm:followupcadence:query')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestHeader(value = "tenant", required = false) String tenant)
    {
        startPage();
        List<FollowUpCadenceVo> list = crmFollowUpCadenceService.selectList(tenant);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:followupcadence:query')")
    @GetMapping("/{cadenceId}")
    public AjaxResult getById(@PathVariable("cadenceId") String cadenceId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        FollowUpCadenceVo data = crmFollowUpCadenceService.getById(tenant, cadenceId);
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('crm:followupcadence:add')")
    @Log(title = "跟进序列配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FollowUpCadenceSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmFollowUpCadenceService.add(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:followupcadence:edit')")
    @Log(title = "跟进序列配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody FollowUpCadenceSaveRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmFollowUpCadenceService.update(tenant, body);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:followupcadence:remove')")
    @Log(title = "跟进序列配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{cadenceId}")
    public AjaxResult remove(@PathVariable("cadenceId") String cadenceId,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        crmFollowUpCadenceService.remove(tenant, cadenceId);
        return success();
    }
}
