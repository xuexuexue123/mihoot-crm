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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomContactWay;
import com.ruoyi.crm.domain.dto.wecom.WecomContactWayCreateRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomContactWayUpdateRequest;
import com.ruoyi.crm.service.ICrmWecomContactWayService;

@RestController
@RequestMapping("/crm/wecom/contact-way")
public class CrmWecomContactWayController extends BaseController
{
    @Autowired
    private ICrmWecomContactWayService crmWecomContactWayService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @Log(title = "企业微信联系我", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult create(@RequestBody WecomContactWayCreateRequest req)
    {
        try
        {
            return AjaxResult.success(crmWecomContactWayService.createContactWay(req));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/list")
    public TableDataInfo list(CrmWecomContactWay query)
    {
        startPage();
        List<CrmWecomContactWay> list = crmWecomContactWayService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @Log(title = "企业微信联系我", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody WecomContactWayUpdateRequest req)
    {
        try
        {
            return AjaxResult.success(crmWecomContactWayService.updateContactWay(id, req));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @Log(title = "企业微信联系我", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        try
        {
            crmWecomContactWayService.deleteContactWay(id);
            return AjaxResult.success();
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
