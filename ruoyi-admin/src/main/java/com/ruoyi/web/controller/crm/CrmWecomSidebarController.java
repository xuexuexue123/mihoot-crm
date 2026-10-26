package com.ruoyi.web.controller.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarGroupResolveResultDto;
import com.ruoyi.crm.domain.dto.wecom.WecomSidebarResolveResultDto;
import com.ruoyi.crm.service.ICrmWecomSidebarResolveService;

/**
 * 企微聊天侧边栏 resolve。
 */
@RestController
@RequestMapping({ "/crm/wecom", "/wecom" })
public class CrmWecomSidebarController extends BaseController
{
    @Autowired
    private ICrmWecomSidebarResolveService sidebarResolveService;

    @GetMapping("/sidebar/resolve")
    public AjaxResult resolve(@RequestParam(value = "externalUserid", required = false) String externalUserid)
    {
        if (StringUtils.isEmpty(externalUserid))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "externalUserid 不能为空");
        }
        try
        {
            WecomSidebarResolveResultDto data = sidebarResolveService.resolve(externalUserid.trim());
            return AjaxResult.success(data);
        }
        catch (ServiceException e)
        {
            Integer code = e.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, e.getMessage());
            }
            logger.error("sidebar resolve 失败: externalUserid={}", externalUserid, e);
            return AjaxResult.error(HttpStatus.ERROR, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("sidebar resolve 失败: externalUserid={}", externalUserid, e);
            return AjaxResult.error(HttpStatus.ERROR, "sidebar resolve 失败");
        }
    }

    @GetMapping("/sidebar/resolve-group")
    public AjaxResult resolveGroup(@RequestParam(value = "chatId", required = false) String chatId)
    {
        if (StringUtils.isEmpty(chatId))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "chatId 不能为空");
        }
        try
        {
            WecomSidebarGroupResolveResultDto data = sidebarResolveService.resolveGroup(chatId.trim());
            if (WecomSidebarGroupResolveResultDto.STATUS_DUPLICATE_BINDING.equals(data.getCrmStatus()))
            {
                return AjaxResult.success(WecomSidebarGroupResolveResultDto.MSG_DUPLICATE_BINDING, data);
            }
            return AjaxResult.success(data);
        }
        catch (ServiceException e)
        {
            Integer code = e.getCode();
            if (code != null)
            {
                return AjaxResult.error(code, e.getMessage());
            }
            logger.error("sidebar resolve-group 失败: chatId={}", chatId, e);
            return AjaxResult.error(HttpStatus.ERROR, e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("sidebar resolve-group 失败: chatId={}", chatId, e);
            return AjaxResult.error(HttpStatus.ERROR, "sidebar resolve-group 失败");
        }
    }
}
