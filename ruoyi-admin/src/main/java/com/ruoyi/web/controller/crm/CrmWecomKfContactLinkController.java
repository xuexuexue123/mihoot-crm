package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.service.IWecomKfConversationService;
import com.ruoyi.crm.wecom.WecomContactInfoSyncSupport;

/**
 * 微信客服 Contact 关联管理 API。
 * 身份类型：{@code wechat_kf_openid}（KF external_userid）。
 */
@RestController
@RequestMapping("/crm/wecom/kf/contact")
public class CrmWecomKfContactLinkController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmWecomKfContactLinkController.class);

    @Autowired
    private WecomContactInfoSyncSupport contactInfoSyncSupport;

    @Autowired
    private IWecomKfConversationService conversationService;

    /**
     * 绑定 external_userid（wechat_kf_openid）与 CRM Contact。
     * Body: externalUserid, contactId, openKfid?, setPrimary?
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations') or @ss.hasPermi('crm:wecom:config')")
    @PostMapping("/link")
    public AjaxResult linkContact(@RequestBody Map<String, Object> body)
    {
        if (body == null)
        {
            return AjaxResult.error(400, "body is required");
        }
        String externalUserid = body.get("externalUserid") != null
                ? String.valueOf(body.get("externalUserid")) : null;
        String contactId = body.get("contactId") != null
                ? String.valueOf(body.get("contactId")) : null;
        String openKfid = body.get("openKfid") != null
                ? String.valueOf(body.get("openKfid")) : null;
        boolean setPrimary = false;
        if (body.get("setPrimary") != null)
        {
            Object raw = body.get("setPrimary");
            if (raw instanceof Boolean)
            {
                setPrimary = (Boolean) raw;
            }
            else
            {
                setPrimary = Boolean.parseBoolean(String.valueOf(raw));
            }
        }

        try
        {
            Map<String, Object> result = conversationService.linkContact(
                    externalUserid, contactId, openKfid, setPrimary);
            // 最佳努力同步头像昵称（若有外部联系人行）
            try
            {
                contactInfoSyncSupport.syncFromExternalContactRow(externalUserid, contactId);
            }
            catch (Exception e)
            {
                log.debug("KF contact profile sync skipped: {}", e.getMessage());
            }
            return AjaxResult.success(result);
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations') or @ss.hasPermi('crm:wecom:config')")
    @DeleteMapping("/link")
    public AjaxResult unlinkContact(@RequestBody Map<String, String> body)
    {
        String externalUserid = body != null ? body.get("externalUserid") : null;
        String openKfid = body != null ? body.get("openKfid") : null;
        try
        {
            return AjaxResult.success(conversationService.unlinkContact(externalUserid, openKfid));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
    }

    /**
     * 同步微信客户信息到 Contact 表（保留兼容）。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations') or @ss.hasPermi('crm:wecom:config')")
    @PutMapping("/sync-info")
    public AjaxResult syncWecomContactInfo(@RequestBody Map<String, String> body)
    {
        String externalUserid = body != null ? body.get("externalUserid") : null;
        String contactId = body != null ? body.get("contactId") : null;

        if (externalUserid == null || externalUserid.isEmpty())
        {
            return AjaxResult.error("externalUserid 不能为空");
        }
        if (contactId == null || contactId.isEmpty())
        {
            return AjaxResult.error("contactId 不能为空");
        }

        boolean ok = contactInfoSyncSupport.syncFromExternalContactRow(externalUserid, contactId);

        Map<String, Object> result = new HashMap<>();
        result.put("externalUserid", externalUserid);
        result.put("contactId", contactId);
        result.put("synced", ok);
        return AjaxResult.success(result);
    }
}
