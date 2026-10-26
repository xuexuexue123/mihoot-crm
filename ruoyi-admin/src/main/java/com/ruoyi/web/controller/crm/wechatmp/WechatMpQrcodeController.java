package com.ruoyi.web.controller.crm.wechatmp;

import java.util.List;
import java.util.Map;

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
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpQrcode;
import com.ruoyi.crm.wechatmp.dto.WechatMpQrcodeCreateRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpQrcodeService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpQrcodeController extends BaseController
{
    @Autowired
    private IWechatMpQrcodeService qrcodeService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:promotion')")
    @GetMapping("/qrcode/list")
    public TableDataInfo list(CrmWechatMpQrcode query)
    {
        startPage();
        List<CrmWechatMpQrcode> list = qrcodeService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:promotion')")
    @Log(title = "公众号推广码", businessType = BusinessType.INSERT)
    @PostMapping("/qrcode")
    public AjaxResult create(@RequestBody WechatMpQrcodeCreateRequest req)
    {
        return AjaxResult.success(qrcodeService.create(req));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:promotion')")
    @GetMapping("/qrcode/{id}")
    public AjaxResult get(@PathVariable Long id)
    {
        return AjaxResult.success(qrcodeService.getById(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:promotion')")
    @Log(title = "公众号推广码", businessType = BusinessType.UPDATE)
    @PutMapping("/qrcode/{id}")
    public AjaxResult update(@PathVariable Long id, @RequestBody CrmWechatMpQrcode patch)
    {
        qrcodeService.update(id, patch);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:promotion')")
    @Log(title = "公众号推广码", businessType = BusinessType.DELETE)
    @DeleteMapping("/qrcode/{id}")
    public AjaxResult delete(@PathVariable Long id)
    {
        qrcodeService.delete(id);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:promotion')")
    @GetMapping("/qrcode/{id}/stats")
    public AjaxResult stats(@PathVariable Long id)
    {
        Map<String, Object> stats = qrcodeService.getStats(id);
        return AjaxResult.success(stats);
    }
}
