package com.ruoyi.web.controller.crm.wechatmp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMassTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassPreviewRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMassSendRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpMassService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMassController extends BaseController
{
    @Autowired
    private IWechatMpMassService massService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @Log(title = "公众号官方群发预览", businessType = BusinessType.OTHER)
    @PostMapping("/mass/preview")
    public AjaxResult preview(@RequestBody WechatMpMassPreviewRequest req)
    {
        try
        {
            return AjaxResult.success(massService.preview(req));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @Log(title = "公众号官方群发", businessType = BusinessType.INSERT)
    @PostMapping("/mass/send")
    public AjaxResult send(@RequestBody WechatMpMassSendRequest req)
    {
        try
        {
            return AjaxResult.success(massService.send(req));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @GetMapping("/mass/task/list")
    public TableDataInfo list(CrmWechatMpMassTask query)
    {
        startPage();
        List<CrmWechatMpMassTask> list = massService.selectTaskList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @GetMapping("/mass/task/{id}")
    public AjaxResult detail(@PathVariable Long id)
    {
        try
        {
            return AjaxResult.success(massService.getTask(id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @Log(title = "公众号群发刷新状态", businessType = BusinessType.UPDATE)
    @PostMapping("/mass/task/{id}/refresh")
    public AjaxResult refresh(@PathVariable Long id)
    {
        try
        {
            return AjaxResult.success(massService.refreshTask(id));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @Log(title = "公众号群发删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/mass/task/{id}")
    public AjaxResult delete(@PathVariable Long id,
            @RequestParam(value = "articleIdx", required = false) Integer articleIdx)
    {
        try
        {
            return AjaxResult.success(massService.deleteTask(id, articleIdx));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:mass')")
    @GetMapping("/mass/quota")
    public AjaxResult quota(@RequestParam String appId)
    {
        try
        {
            return AjaxResult.success(massService.getQuota(appId));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }
}
