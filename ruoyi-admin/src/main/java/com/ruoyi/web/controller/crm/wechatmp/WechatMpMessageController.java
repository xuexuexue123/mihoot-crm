package com.ruoyi.web.controller.crm.wechatmp;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessage;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageReplyRequest;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageTemplateRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageService;
import com.ruoyi.crm.wechatmp.service.WechatMpTokenService;
import com.ruoyi.crm.wechatmp.WechatMpTencentApiClient;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMessageController extends BaseController
{
    @Autowired
    private IWechatMpMessageService messageService;

    @Autowired
    private WechatMpTokenService tokenService;

    @Autowired
    private WechatMpTencentApiClient tencentApiClient;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message') or @ss.hasPermi('crm:wechat_mp:conversations')")
    @GetMapping("/conversations/{openId}/messages")
    public TableDataInfo messages(@RequestParam String appId, @PathVariable String openId)
    {
        startPage();
        List<CrmWechatMpMessage> list = messageService.listMessages(appId, openId);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message') or @ss.hasPermi('crm:wechat_mp:conversations')")
    @Log(title = "公众号客服回复", businessType = BusinessType.INSERT)
    @PostMapping("/messages/reply")
    public AjaxResult reply(@RequestBody WechatMpMessageReplyRequest req)
    {
        messageService.reply(req);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message') or @ss.hasPermi('crm:wechat_mp:conversations')")
    @Log(title = "公众号模板消息", businessType = BusinessType.INSERT)
    @PostMapping("/messages/template")
    public AjaxResult template(@RequestBody WechatMpMessageTemplateRequest req)
    {
        messageService.sendTemplate(req);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message') or @ss.hasPermi('crm:wechat_mp:conversations')")
    @GetMapping("/messages/templates")
    public AjaxResult templateList(@RequestParam String appId)
    {
        String token = tokenService.getAccessToken(appId);
        return AjaxResult.success(tencentApiClient.getTemplateList(token));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message') or @ss.hasPermi('crm:wechat_mp:conversations')")
    @GetMapping("/messages/media")
    public void media(
            @RequestParam String appId,
            @RequestParam String mediaId,
            HttpServletResponse response) throws IOException
    {
        messageService.streamTemporaryMedia(appId, mediaId, response);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message') or @ss.hasPermi('crm:wechat_mp:conversations')")
    @PostMapping(value = "/messages/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadMedia(
            @RequestParam String appId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "image") String type) throws IOException
    {
        return AjaxResult.success(messageService.uploadTemporaryMedia(appId, type, file));
    }
}
