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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpCommentAutoReplyRule;
import com.ruoyi.crm.wechatmp.service.IWechatMpCommentAutoReplyRuleService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpCommentAutoReplyRuleController extends BaseController
{
    @Autowired
    private IWechatMpCommentAutoReplyRuleService ruleService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @GetMapping("/comment-auto-reply/list")
    public TableDataInfo list(CrmWechatMpCommentAutoReplyRule query)
    {
        startPage();
        List<CrmWechatMpCommentAutoReplyRule> list = ruleService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @GetMapping("/comment-auto-reply/{id}")
    public AjaxResult get(@PathVariable Long id)
    {
        return AjaxResult.success(ruleService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comment-auto-reply")
    public AjaxResult add(@RequestBody CrmWechatMpCommentAutoReplyRule row)
    {
        ruleService.add(row);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PutMapping("/comment-auto-reply")
    public AjaxResult update(@RequestBody CrmWechatMpCommentAutoReplyRule row)
    {
        ruleService.update(row);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @DeleteMapping("/comment-auto-reply/{id}")
    public AjaxResult delete(@PathVariable Long id)
    {
        ruleService.delete(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:comments')")
    @PostMapping("/comment-auto-reply/process")
    public AjaxResult process(@RequestParam(defaultValue = "50") int limit)
    {
        return AjaxResult.success(ruleService.processPending(limit));
    }
}
