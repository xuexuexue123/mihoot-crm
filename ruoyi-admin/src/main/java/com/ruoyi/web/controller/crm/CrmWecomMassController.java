package com.ruoyi.web.controller.crm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomMassTask;
import com.ruoyi.crm.domain.dto.wecom.WecomMassSendCreateRequest;
import com.ruoyi.crm.service.ICrmWecomMassTaskService;

@RestController
@RequestMapping("/crm/wecom/mass")
public class CrmWecomMassController extends BaseController
{
    @Autowired
    private ICrmWecomMassTaskService crmWecomMassTaskService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:mass')")
    @Log(title = "企业微信群发", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult send(@RequestBody WecomMassSendCreateRequest req)
    {
        try
        {
            return AjaxResult.success(crmWecomMassTaskService.createMassSend(req));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:mass')")
    @GetMapping("/task/list")
    public TableDataInfo taskList(CrmWecomMassTask query)
    {
        startPage();
        List<CrmWecomMassTask> list = crmWecomMassTaskService.selectTaskList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:mass')")
    @GetMapping("/task/{id}")
    public AjaxResult taskDetail(@PathVariable Long id)
    {
        try
        {
            return AjaxResult.success(crmWecomMassTaskService.loadTaskDetailSync(id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
