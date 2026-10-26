package com.ruoyi.web.controller.crm;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmWecomContactGroup;
import com.ruoyi.crm.domain.dto.wecom.WecomContactGroupProvisionRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.service.wecom.contactgroup.WecomContactGroupService;

/**
 * 企微一客一群 / 建群任务
 */
@RestController
@RequestMapping({ "/crm/wecom", "/wecom" })
public class CrmWecomContactGroupController extends BaseController
{
    @Autowired
    private WecomContactGroupService contactGroupService;

    @PostMapping("/contact-group/provision")
    public AjaxResult provision(@RequestBody WecomContactGroupProvisionRequest body)
    {
        if (body == null || StringUtils.isBlank(body.getContactId()))
        {
            return AjaxResult.error("contactId 不能为空");
        }
        return AjaxResult.success(contactGroupService.provision(body.getContactId()));
    }

    @GetMapping("/contact-group/by-contact/{contactId}")
    public AjaxResult byContact(@PathVariable String contactId)
    {
        CrmWecomContactGroup group = contactGroupService.byContact(contactId);
        return AjaxResult.success(group);
    }

    @GetMapping("/contact-group/by-chat/{chatId}")
    public AjaxResult byChat(@PathVariable String chatId)
    {
        if (StringUtils.isBlank(chatId))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "chatId 不能为空");
        }
        try
        {
            CrmWecomContactGroup group = contactGroupService.byChat(chatId);
            return AjaxResult.success(group);
        }
        catch (ServiceException e)
        {
            Integer code = e.getCode();
            if (code != null && code == 409)
            {
                return AjaxResult.success(WecomSidebarGroupResolveResultDto.MSG_DUPLICATE_BINDING, null);
            }
            if (code != null)
            {
                return AjaxResult.error(code, e.getMessage());
            }
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/contact-group/bind")
    public AjaxResult bind(@RequestBody WecomContactGroupProvisionRequest body)
    {
        if (body == null)
        {
            return AjaxResult.error("body 不能为空");
        }
        return AjaxResult.success(contactGroupService.bind(body.getTaskId(), body.getChatId(), body.getGroupName()));
    }

    @PostMapping("/contact-group/activate")
    public AjaxResult activate(@RequestBody WecomContactGroupProvisionRequest body)
    {
        if (body == null)
        {
            return AjaxResult.error("body 不能为空");
        }
        return AjaxResult.success(
                contactGroupService.activate(body.getTaskId(), body.getContactId(), body.getChatId()));
    }

    @GetMapping("/provision-group/context")
    public AjaxResult provisionContext(@RequestParam(required = false) String contactId,
            @RequestParam(required = false) String taskId)
    {
        Map<String, Object> data = contactGroupService.provisionContext(contactId, taskId);
        return AjaxResult.success(data);
    }
}
