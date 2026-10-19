package com.ruoyi.web.controller.crm;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.CrmWecomKfMessage;
import com.ruoyi.crm.domain.dto.wecom.KfMessageReplyRequest;
import com.ruoyi.crm.event.ActivityEventProducer;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.IWecomKfMessageService;
import com.ruoyi.crm.wecom.WecomKfMessageService;

@RestController
@RequestMapping("/crm/wecom/kf")
public class CrmWecomKfMessageController extends BaseController
{
    @Autowired
    private IWecomKfMessageService wecomKfMessageService;

    @Autowired
    private WecomKfMessageService wecomKfSyncService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    @Autowired
    private ActivityEventProducer activityEventProducer;

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/messages")
    public TableDataInfo listMessages(
            @RequestParam("externalUserid") String externalUserid,
            @RequestParam(value = "openKfid", required = false) String openKfid,
            @RequestParam(value = "direction", required = false) Integer direction)
    {
        String corpId = resolveCorpId();
        startPage();
        return getDataTable(wecomKfMessageService.selectMessages(corpId, externalUserid, openKfid, direction));
    }

    /**
     * 临时素材下载代理（二进制流）。须在 /messages/{msgid} 之前注册，避免路径冲突。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
    @GetMapping("/messages/media")
    public void media(
            @RequestParam String openKfid,
            @RequestParam String mediaId,
            @RequestParam(required = false) String externalUserid,
            HttpServletResponse response) throws IOException
    {
        wecomKfSyncService.streamTemporaryMedia(openKfid, mediaId, response);
    }

    /**
     * 上传临时素材（出站 image/file）。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom_kf:conversations')")
    @PostMapping("/messages/media/upload")
    public AjaxResult uploadMedia(
            @RequestParam String openKfid,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", required = false, defaultValue = "image") String type)
    {
        try
        {
            return AjaxResult.success(wecomKfSyncService.uploadTemporaryMedia(openKfid, type, file));
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(e.getCode() != null ? e.getCode() : HttpStatus.ERROR, e.getMessage());
        }
        catch (IOException e)
        {
            return AjaxResult.error(502, "上传失败: " + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/messages/{msgid}")
    public AjaxResult getMessage(@PathVariable("msgid") String msgid)
    {
        String corpId = resolveCorpId();
        CrmWecomKfMessage msg = wecomKfMessageService.selectByMsgid(corpId, msgid);
        if (msg == null)
        {
            return AjaxResult.error("消息不存在");
        }
        return AjaxResult.success(msg);
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @PostMapping("/message/reply")
    public AjaxResult recordReply(
            @RequestBody KfMessageReplyRequest body,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        if (body.getExternalUserid() == null || body.getExternalUserid().isEmpty())
        {
            return AjaxResult.error("externalUserid 不能为空");
        }
        if (body.getOpenKfid() == null || body.getOpenKfid().isEmpty())
        {
            return AjaxResult.error("openKfid 不能为空");
        }
        if (body.getMsgtype() == null || body.getMsgtype().isEmpty())
        {
            return AjaxResult.error("msgtype 不能为空");
        }
        if (body.getContent() == null || body.getContent().isEmpty())
        {
            return AjaxResult.error("content 不能为空");
        }

        String corpId = resolveCorpId();
        CrmWecomKfMessage record = new CrmWecomKfMessage();
        record.setCorpId(corpId);
        record.setOpenKfid(body.getOpenKfid());
        record.setExternalUserid(body.getExternalUserid());
        record.setMsgtype(body.getMsgtype());
        record.setContent(body.getContent());
        record.setMsgid(body.getMsgid());
        record.setSendTime(body.getSendTime());

        wecomKfMessageService.insertReply(record);

        try
        {
            long sendTimeSec = body.getSendTime() != null
                    ? body.getSendTime()
                    : System.currentTimeMillis() / 1000;
            activityEventProducer.produceWecomMessageSent(
                    body.getExternalUserid(), body.getMsgid(),
                    body.getMsgtype(), body.getContent(), sendTimeSec);
        }
        catch (Exception e)
        {
            logger.warn("Failed to produce message.sent Activity for externalUserid={}: {}",
                    body.getExternalUserid(), e.getMessage());
        }

        return AjaxResult.success(record.getId());
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/messages/by-contact/{contactId}")
    public TableDataInfo listMessagesByContact(
            @PathVariable("contactId") String contactId,
            @RequestParam(value = "openKfid", required = false) String openKfid)
    {
        String corpId = resolveCorpId();
        startPage();
        return getDataTable(wecomKfMessageService.selectByContactId(corpId, contactId, openKfid));
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:config')")
    @GetMapping("/messages/by-date")
    public TableDataInfo listMessagesByDate(
            @RequestParam(value = "openKfid", required = false) String openKfid,
            @RequestParam("startTime") Long startTime,
            @RequestParam("endTime") Long endTime)
    {
        String corpId = resolveCorpId();
        startPage();
        return getDataTable(wecomKfMessageService.selectMessagesByDate(corpId, openKfid, startTime, endTime));
    }

    private String resolveCorpId()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || cfg.getCorpId() == null || cfg.getCorpId().isEmpty())
        {
            return "";
        }
        return cfg.getCorpId();
    }
}
