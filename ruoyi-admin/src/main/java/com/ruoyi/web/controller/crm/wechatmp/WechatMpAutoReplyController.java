package com.ruoyi.web.controller.crm.wechatmp;

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
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpAutoReply;
import com.ruoyi.crm.wechatmp.service.IWechatMpAutoReplyService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpAutoReplyController extends BaseController
{
    @Autowired
    private IWechatMpAutoReplyService autoReplyService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/auto-reply/list")
    public TableDataInfo list(CrmWechatMpAutoReply query)
    {
        startPage();
        List<CrmWechatMpAutoReply> list = autoReplyService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/auto-reply/{id}")
    public AjaxResult get(@PathVariable Long id)
    {
        return AjaxResult.success(autoReplyService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号自动回复", businessType = BusinessType.INSERT)
    @PostMapping("/auto-reply")
    public AjaxResult add(@RequestBody CrmWechatMpAutoReply row)
    {
        autoReplyService.add(row);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号自动回复", businessType = BusinessType.UPDATE)
    @PutMapping("/auto-reply")
    public AjaxResult update(@RequestBody CrmWechatMpAutoReply row)
    {
        autoReplyService.update(row);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号自动回复", businessType = BusinessType.DELETE)
    @DeleteMapping("/auto-reply/{id}")
    public AjaxResult delete(@PathVariable Long id)
    {
        autoReplyService.delete(id);
        return AjaxResult.success();
    }
}
