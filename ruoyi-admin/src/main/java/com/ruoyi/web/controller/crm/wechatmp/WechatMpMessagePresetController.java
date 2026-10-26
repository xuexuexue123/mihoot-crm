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
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessagePreset;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessagePresetRenderRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessagePresetService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMessagePresetController extends BaseController
{
    @Autowired
    private IWechatMpMessagePresetService presetService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/message/preset/list")
    public TableDataInfo list(CrmWechatMpMessagePreset query)
    {
        startPage();
        List<CrmWechatMpMessagePreset> list = presetService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/message/preset/{id}")
    public AjaxResult get(@PathVariable Long id)
    {
        return AjaxResult.success(presetService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号消息预设", businessType = BusinessType.INSERT)
    @PostMapping("/message/preset")
    public AjaxResult add(@RequestBody CrmWechatMpMessagePreset row)
    {
        presetService.add(row);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号消息预设", businessType = BusinessType.UPDATE)
    @PutMapping("/message/preset/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody CrmWechatMpMessagePreset row)
    {
        return AjaxResult.success(presetService.update(id, row));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号消息预设", businessType = BusinessType.DELETE)
    @DeleteMapping("/message/preset/{id}")
    public AjaxResult delete(@PathVariable Long id)
    {
        presetService.delete(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号消息预设渲染", businessType = BusinessType.OTHER)
    @PostMapping("/message/preset/{id}/render")
    public AjaxResult render(@PathVariable Long id, @RequestBody(required = false) WechatMpMessagePresetRenderRequest request)
    {
        return AjaxResult.success(presetService.render(id, request));
    }
}
