package com.ruoyi.web.controller.crm;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmEmailMessageRequest;
import com.ruoyi.crm.domain.CrmOauthConnections;
import com.ruoyi.crm.service.ICrmOauthConnectionsService;
import com.ruoyi.framework.datasource.DynamicDataSourceContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmEmailMessages;
import com.ruoyi.crm.service.ICrmEmailMessagesService;
import com.ruoyi.crm.support.CrmMailUserScopeSupport;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.web.multipart.MultipartFile;

/**
 * 邮件Controller
 * 
 * @author luohd
 * @date 2025-05-14
 */
@RestController
@RequestMapping("/crm/emailmessages")
public class CrmEmailMessagesController extends BaseController
{
    @Autowired
    private ICrmEmailMessagesService crmEmailMessagesService;

    @Autowired
    private ICrmOauthConnectionsService crmOauthConnectionsService;

    @Autowired
    private RedisCache redisCache;

    private static final String PREFIX = "email_sync_status:";


    @PostMapping("/imap/connectimap")
    public AjaxResult connectImapServer(@RequestBody CrmOauthConnections conn) {
        CrmMailUserScopeSupport.applyCurrentUserId(conn);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> meta;
        try {
            meta = objectMapper.readValue(conn.getMetadata(), Map.class);
            int port = 993;
            String host = (String) meta.get("imapHost");
            if (meta.get("imapPort").toString() != null) {
                port = Integer.parseInt(meta.get("imapPort").toString());
            }
            String username = conn.getExternalName();
            String password = AESUtil.decrypt(conn.getAccessToken());
            if (password == null) {
                return AjaxResult.error("password is wrong");
            }
            boolean useSSL = true;
            String useSslString = meta.get("useSsl").toString();
            if (useSslString != null) {
                useSSL = Boolean.parseBoolean(useSslString);
            } else {
                useSSL = false;
            }

            Properties props = new Properties();
            props.put("mail.store.protocol", "imap");
            props.put("mail.imap.host", host);
            props.put("mail.imap.port", String.valueOf(port));
            if (useSSL) {
                props.put("mail.imap.ssl.enable", "true");
            } else {
                props.put("mail.imap.ssl.enable", "false");
            }

            try {
                Session session = Session.getInstance(props);
                Store store = session.getStore("imap");
                store.connect(host, port, username, password);
                store.close();
                return AjaxResult.success("IMAP 连接成功");
            } catch (MessagingException e) {
                return AjaxResult.error(e.getMessage());
            }

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    @PostMapping("/imap/sendemail")
    public AjaxResult sendSmtpEmail(
            @RequestPart("conn") CrmOauthConnections conn,
            @RequestPart("crmEmailMessageRequest") CrmEmailMessageRequest crmEmailMessageRequest,
            @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList) {

        CrmMailUserScopeSupport.applyCurrentUserId(conn);

        try {
            // 解析conn.metadata JSON，提取smtp配置
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> metadata = mapper.readValue(conn.getMetadata(), new TypeReference<Map<String, Object>>() {});
            String smtpHost = (String) metadata.get("smtpHost");

            int port = 465;

            if (metadata.get("smtpPort").toString() != null) {
                port = Integer.parseInt(metadata.get("smtpPort").toString());
            }

            String username = conn.getExternalName();

            boolean useSSL = true;
            String useSslString = metadata.get("useSsl").toString();
            if (useSslString != null) {
                useSSL = Boolean.parseBoolean(useSslString);
            } else {
                useSSL = false;
            }

            // 解密密码（假设你已有decrypt方法）
            String password = AESUtil.decrypt(conn.getAccessToken());

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", String.valueOf(port));
            props.put("mail.smtp.ssl.enable", useSSL);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(crmEmailMessageRequest.getFrom()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(crmEmailMessageRequest.getTo()));
            message.setSubject(crmEmailMessageRequest.getSubject(), "UTF-8");

            // 处理正文和附件
            Multipart multipart = new MimeMultipart();

            // 邮件正文
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setContent(crmEmailMessageRequest.getMessageBody(), "text/html; charset=UTF-8");
            multipart.addBodyPart(textBodyPart);

            // 附件
            if (fileList != null) {
                for (MultipartFile file : fileList) {
                    if (file != null && !file.isEmpty()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        attachmentPart.setFileName(file.getOriginalFilename());
                        attachmentPart.setDataHandler(new DataHandler(new ByteArrayDataSource(file.getBytes(), file.getContentType())));
                        multipart.addBodyPart(attachmentPart);
                    }
                }
            }

            message.setContent(multipart);
            message.setSentDate(new Date());

            Transport.send(message);

            crmEmailMessagesService.persistSentEmailWithAttachments(
                    conn.getUserId(),
                    getLoginUser().getTenant(),
                    conn.getExternalName(),
                    conn.getProvider(),
                    crmEmailMessageRequest.getFrom(),
                    crmEmailMessageRequest.getTo(),
                    crmEmailMessageRequest.getSubject(),
                    crmEmailMessageRequest.getMessageBody(),
                    fileList,
                    null,
                    null);

            return AjaxResult.success("测试邮件发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("发送测试邮件失败: " + e.getMessage());
        }
    }

    /**
     * Sync 邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/syncnow")
    public AjaxResult syncnow(@RequestBody CrmEmailMessageRequest crmEmailMessageRequest)
    {
        CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessageRequest);
        String userId = crmEmailMessageRequest.getUserId();
        String externalName = crmEmailMessageRequest.getExternalName();
        String provider = crmEmailMessageRequest.getProvider();
        if (provider == null || provider.isEmpty())
        {
            provider = "Gmail";
        }
        String accessToken = crmOauthConnectionsService.resolveValidAccessToken(userId, provider, externalName);

        return toAjax(crmEmailMessagesService.syncMessages(accessToken, userId, externalName));
    }

    /**
     * 标记 邮件 为已读 / 未读
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/modifylabel")
    public AjaxResult modifyMessageLabel(@RequestBody CrmEmailMessageRequest crmEmailMessageRequest)
    {
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String messageId = crmEmailMessageRequest.getMessageId();
        String provider = crmEmailMessageRequest.getProvider();

        boolean markAsRead = crmEmailMessageRequest.getIsRead() == 1 ? true : false;

        return toAjax(crmEmailMessagesService.modifyMessageLabel(provider, accessToken, messageId, markAsRead));
    }

    /**
     * 移到trash
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/movetotrash")
    public AjaxResult moveToTrash(@RequestBody CrmEmailMessageRequest crmEmailMessageRequest)
    {
        CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessageRequest);
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String messageId = crmEmailMessageRequest.getMessageId();
        String provider = crmEmailMessageRequest.getProvider();
        String userId = crmEmailMessageRequest.getUserId();
        String externalName = crmEmailMessageRequest.getExternalName();

        if ((accessToken == null || accessToken.isEmpty())
                && provider != null
                && (provider.equalsIgnoreCase("gmail") || provider.equalsIgnoreCase("outlook"))) {
            accessToken = crmOauthConnectionsService.resolveValidAccessToken(userId, provider, externalName);
        }

        return toAjax(crmEmailMessagesService.moveToTrash(provider, accessToken, messageId, userId, externalName));
    }

    /**
     * 发送邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult sendmessage(
            @RequestPart("crmEmailMessageRequest") CrmEmailMessageRequest crmEmailMessageRequest,
            @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList)
    {
        CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessageRequest);
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String userId = crmEmailMessageRequest.getUserId();
        String tenant = crmEmailMessageRequest.getTenantId();
        String externalName = crmEmailMessageRequest.getExternalName();
        String from = crmEmailMessageRequest.getFrom();
        String to = crmEmailMessageRequest.getTo();
        String subject = crmEmailMessageRequest.getSubject();
        String messageBody = crmEmailMessageRequest.getMessageBody();
        String provider = crmEmailMessageRequest.getProvider();

        return toAjax(crmEmailMessagesService.sendEmail(provider, accessToken, userId, tenant, externalName,
                from, to, subject, messageBody, fileList));
    }

    /**
     * Forward邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/forward")
    public AjaxResult forwardmessage(
            @RequestPart("crmEmailMessageRequest") CrmEmailMessageRequest crmEmailMessageRequest,
            @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList)
    {
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String messageId = crmEmailMessageRequest.getMessageId();
        String from = crmEmailMessageRequest.getFrom();
        String to = crmEmailMessageRequest.getTo();
        String subject = crmEmailMessageRequest.getSubject();
        String provider = crmEmailMessageRequest.getProvider();
        String messageBody = crmEmailMessageRequest.getMessageBody();


        return toAjax(crmEmailMessagesService.forwardEmail(provider, accessToken, messageId, from, to, subject, messageBody, fileList));
    }
    /**
     * 彻底删除
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/deletemessage")
    public AjaxResult deletemessage(@RequestBody CrmEmailMessageRequest crmEmailMessageRequest)
    {
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String messageId = crmEmailMessageRequest.getMessageId();
        String provider = crmEmailMessageRequest.getProvider();

        return toAjax(crmEmailMessagesService.deleteMessage(provider, accessToken, messageId));
    }
    /**
     * 获取 邮件 详细内容
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/getdetail")
    public AjaxResult getdetail(HttpServletRequest request, @RequestBody CrmEmailMessageRequest crmEmailMessageRequest) {
        CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessageRequest);

        // 获取服务器URL和端口
        String serverName = request.getServerName(); // 服务器名称/域名/IP
        int serverPort = request.getServerPort();    // 服务器端口

        String hostName = request.getScheme() + "://" + serverName + ":" + serverPort;

        String tenant = crmEmailMessageRequest.getTenantId();
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String userId = crmEmailMessageRequest.getUserId();
        String messageId = crmEmailMessageRequest.getMessageId();
        String provider = crmEmailMessageRequest.getProvider();
        String externalName = crmEmailMessageRequest.getExternalName();

        if ((accessToken == null || accessToken.isEmpty())
                && provider != null
                && (provider.equalsIgnoreCase("gmail") || provider.equalsIgnoreCase("outlook"))) {
            accessToken = crmOauthConnectionsService.resolveValidAccessToken(userId, provider, externalName);
        }

        try {
            CrmEmailMessages res = crmEmailMessagesService.getMessageDetail(provider, hostName, tenant, accessToken, messageId, userId, externalName);

            return AjaxResult.success(res);

        } catch (Exception e) {
            System.out.println(e);

        }
        return AjaxResult.error();
    }

    /**
     * ASync 邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/startsync")
    public AjaxResult startsync(@RequestBody CrmEmailMessageRequest crmEmailMessageRequest)
    {
        CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessageRequest);
        String tenant = crmEmailMessageRequest.getTenantId();
        String userId = crmEmailMessageRequest.getUserId();
        String provider = crmEmailMessageRequest.getProvider();
        String externalName = crmEmailMessageRequest.getExternalName();

        final String accessToken;
        if (provider != null && (provider.equalsIgnoreCase("gmail") || provider.equalsIgnoreCase("outlook")))
        {
            accessToken = crmOauthConnectionsService.resolveValidAccessToken(userId, provider, externalName);
        }
        else
        {
            accessToken = null;
        }

        String cacheKey = PREFIX+tenant+externalName;

        String status = redisCache.getCacheObject(cacheKey);
        if ("running".equals(status)) {
            return AjaxResult.error("Email sync already in progress.");
        }

        redisCache.setCacheObject(cacheKey, "running");
        // 异步执行
        CompletableFuture.runAsync(() -> {
            try {
                DynamicDataSourceContextHolder.setDataSourceKey(tenant);

                if (provider.equalsIgnoreCase("gmail")) {

//                crmEmailMessagesService.syncMessages(accessToken, userId, externalName);
                    crmEmailMessagesService.listRecentMessages(accessToken, userId, 14, externalName);

                } else if (provider.equalsIgnoreCase("outlook")) {
                    crmEmailMessagesService.listRecentOutlookEmails(accessToken, userId, 14, externalName);
                } else {
                    crmEmailMessagesService.listRecentIMAPEmails(userId, provider, 14, externalName);
                }

                redisCache.setCacheObject(cacheKey, "completed");

            } catch (Exception e) {
                System.out.println(e);

                redisCache.setCacheObject(cacheKey, "failed");
            }
        });

        return AjaxResult.success("Email sync started.");
    }


    /**
     * 获取同步状态
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "Email", businessType = BusinessType.INSERT)
    @PostMapping("/status")
    public AjaxResult getSyncStatus(@RequestBody CrmEmailMessageRequest crmEmailMessageRequest)
    {
        CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessageRequest);
        String tenant = crmEmailMessageRequest.getTenantId();
        String accessToken = crmEmailMessageRequest.getAccessToken();
        String userId = crmEmailMessageRequest.getUserId();
        String provider = crmEmailMessageRequest.getProvider();
        String externalName = crmEmailMessageRequest.getExternalName();

        String cacheKey = PREFIX+tenant+externalName;

        String status = redisCache.getCacheObject(cacheKey);
        return AjaxResult.success(status);

    }


    private static final int EMAIL_LIST_MAX_PAGE_SIZE = 500;

    /**
     * 查询邮件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmEmailMessages crmEmailMessages)
    {
        crmEmailMessages.setUserId(CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessages.getUserId()));
        startPageWithMaxSize(EMAIL_LIST_MAX_PAGE_SIZE);
        List<CrmEmailMessages> list = crmEmailMessagesService.selectCrmEmailMessagesList(crmEmailMessages);
        return getDataTable(list);
    }

    private void startPageWithMaxSize(int maxPageSize)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageSize != null && pageSize > maxPageSize)
        {
            pageSize = maxPageSize;
        }
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(pageDomain.getReasonable());
    }

    /**
     * 导出邮件列表
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:export')")
    @Log(title = "邮件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmEmailMessages crmEmailMessages)
    {
        crmEmailMessages.setUserId(CrmMailUserScopeSupport.applyCurrentUserId(crmEmailMessages.getUserId()));
        List<CrmEmailMessages> list = crmEmailMessagesService.selectCrmEmailMessagesList(crmEmailMessages);
        ExcelUtil<CrmEmailMessages> util = new ExcelUtil<CrmEmailMessages>(CrmEmailMessages.class);
        util.exportExcel(response, list, "邮件数据");
    }

    /**
     * 获取邮件详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        CrmEmailMessages message = crmEmailMessagesService.selectCrmEmailMessagesById(id);
        CrmMailUserScopeSupport.assertOwnedByCurrentUser(message);
        return success(message);
    }

    /**
     * 新增邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:add')")
    @Log(title = "邮件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmEmailMessages crmEmailMessages)
    {
        crmEmailMessages.setUserId(CrmMailUserScopeSupport.currentUserId());
        return toAjax(crmEmailMessagesService.insertCrmEmailMessages(crmEmailMessages));
    }

    /**
     * 修改邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:edit')")
    @Log(title = "邮件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmEmailMessages crmEmailMessages)
    {
        CrmEmailMessages existing = crmEmailMessagesService.selectCrmEmailMessagesById(crmEmailMessages.getId());
        CrmMailUserScopeSupport.assertOwnedByCurrentUser(existing);
        crmEmailMessages.setUserId(CrmMailUserScopeSupport.currentUserId());
        return toAjax(crmEmailMessagesService.updateCrmEmailMessages(crmEmailMessages));
    }

    /**
     * 删除邮件
     */
    @PreAuthorize("@ss.hasPermi('crm:emailmessages:remove')")
    @Log(title = "邮件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        for (String id : ids)
        {
            CrmMailUserScopeSupport.assertOwnedByCurrentUser(crmEmailMessagesService.selectCrmEmailMessagesById(id));
        }
        return toAjax(crmEmailMessagesService.deleteCrmEmailMessagesByIds(ids));
    }
}
