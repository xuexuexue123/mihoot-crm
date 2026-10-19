package com.ruoyi.crm.service.impl;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.common.utils.sign.AESUtil;
import com.ruoyi.crm.domain.CrmEmailAttachments;
import com.ruoyi.crm.domain.CrmEmailContent;
import com.ruoyi.crm.domain.CrmOauthConnections;
import com.ruoyi.crm.mapper.CrmEmailAttachmentsMapper;
import com.ruoyi.crm.mapper.CrmOauthConnectionsMapper;
import com.ruoyi.crm.utils.MultipartFileAdapter;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.checkerframework.checker.units.qual.C;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmEmailMessagesMapper;
import com.ruoyi.crm.domain.CrmEmailMessages;
import com.ruoyi.crm.service.ICrmEmailMessagesService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.util.ByteArrayDataSource;

import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 邮件Service业务层处理
 * 
 * @author luohd
 * @date 2025-05-14
 */
@Service
public class CrmEmailMessagesServiceImpl implements ICrmEmailMessagesService 
{
    @Autowired
    private CrmOauthConnectionsMapper crmOauthConnectionsMapper;

    @Autowired
    private CrmEmailMessagesMapper crmEmailMessagesMapper;

    @Autowired
    private CrmEmailAttachmentsMapper crmEmailAttachmentsMapper;


    private static final String GMAIL_API_BASE = "https://www.googleapis.com/gmail/v1";
    private static final String OUTLOOK_GRAPH_API_BASE = "https://graph.microsoft.com/v1.0/me/messages";
    private static final String GMAIL_GET_MESSAGE_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/{messageId}?format=raw";
    private static final String GMAIL_SEND_MESSAGE_URL = "https://gmail.googleapis.com/gmail/v1/users/me/messages/send";

    private static final ObjectMapper mapper = new ObjectMapper();

    List<String> PRIORITY_FOLDERS = Arrays.asList("INBOX", "SENT", "DRAFT", "SPAM", "TRASH");

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders buildHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * ✅ 获取邮件详情
     */
    public CrmEmailMessages getMessageDetail(String provider, String hostName,
                                             String tenant, String accessToken,
                                             String messageId, String userId,
                                             String externalName) {
//        String url = GMAIL_API_BASE + "/messages/" + messageId + "?format=full";
//        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(accessToken));
//        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        try {
            if ("Gmail".equalsIgnoreCase(provider)) {

                CrmEmailMessages response = updateMessageDetails(hostName, tenant, accessToken, messageId, userId);
                return response;

            } else if ("Outlook".equalsIgnoreCase(provider)) {
                CrmEmailMessages response = updateOutlookMessageDetails(hostName, tenant, accessToken, messageId, userId);
                return response;
            } else {
                CrmEmailMessages response = updateImapMessageDetails(
                        hostName, tenant,
                        accessToken, messageId,
                        userId, provider,
                        externalName);
                return response;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ✅ 发送邮件（Base64 编码）
     */

    public int sendEmail(String provider, String accessToken, String userId, String tenant, String externalName,
                         String from, String to, String subject, String msgBody, List<MultipartFile> fileList) {
        ResponseEntity<JsonNode> response;
        try {
            if ("Gmail".equalsIgnoreCase(provider)) {
                String base64RawEmail = buildRawEmail(from, to, subject, msgBody, fileList);
                String url = GMAIL_API_BASE + "/users/me/messages/send";
                Map<String, String> body = new HashMap<>();
                body.put("raw", base64RawEmail);

                HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, buildHeaders(accessToken));
                response = restTemplate.postForEntity(url, entity, JsonNode.class);

            } else if ("Outlook".equalsIgnoreCase(provider)) {
                ObjectNode requestBody = buildOutlookEmailJson(from, to, subject, msgBody, fileList);

                RestTemplate restTemplate = new RestTemplate();
                String url = "https://graph.microsoft.com/v1.0/me/sendMail";

                HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), buildHeaders(accessToken));
                response = restTemplate.postForEntity(url, request, JsonNode.class);

            } else {
                response = null;
            }

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                String externalMessageId = null;
                String threadId = null;
                JsonNode responseBody = response.getBody();
                if (responseBody != null && !responseBody.isMissingNode()) {
                    externalMessageId = responseBody.path("id").asText(null);
                    threadId = responseBody.path("threadId").asText(null);
                }
                persistSentEmailWithAttachments(userId, tenant, externalName, provider, from, to, subject, msgBody,
                        fileList, externalMessageId, threadId);
                return 1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int persistSentEmailWithAttachments(String userId, String tenant, String externalName, String provider,
                                               String from, String to, String subject, String msgBody,
                                               List<MultipartFile> fileList, String externalMessageId, String threadId) {
        try {
            CrmEmailMessages email = null;
            if (StringUtils.isNotEmpty(externalMessageId)) {
                email = crmEmailMessagesMapper.existsByMessageId(externalMessageId);
            }

            boolean hasAttachments = fileList != null && !fileList.isEmpty();
            if (email == null) {
                email = new CrmEmailMessages();
                email.setId(UUID.randomUUID().toString());
                email.setUserId(userId);
                email.setProvider(provider);
                email.setMessageExternalId(externalMessageId);
                email.setThreadId(threadId);
                email.setSubject(subject);
                email.setSender(from);
                email.setRecipients(to);
                email.setSnippet(buildSnippet(msgBody));
                email.setBodyHtml(msgBody);
                email.setHasAttachment(hasAttachments ? 1 : 0);
                email.setFolder("SENT");
                email.setIsRead(1);
                email.setIsDownloaded(1);
                email.setReceivedAt(new Date());
                email.setSyncedAt(new Date());
                email.setExternalName(externalName);
                email.setCreateTime(new Date());
                crmEmailMessagesMapper.insertCrmEmailMessages(email);
            } else {
                email.setBodyHtml(msgBody);
                email.setHasAttachment(hasAttachments ? 1 : email.getHasAttachment());
                email.setIsDownloaded(1);
                email.setSyncedAt(new Date());
                crmEmailMessagesMapper.updateCrmEmailMessages(email);
            }

            if (hasAttachments) {
                List<CrmEmailAttachments> attachments = saveUploadedAttachments(tenant, email.getId(), fileList);
                email.setCrmEmailAttachmentsList(attachments);
            }
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String buildSnippet(String html) {
        if (html == null) {
            return "";
        }
        String plain = Jsoup.parse(html).text();
        return plain.length() > 120 ? plain.substring(0, 120) : plain;
    }

    private List<CrmEmailAttachments> saveUploadedAttachments(String tenant, String localMessageId,
                                                              List<MultipartFile> fileList) throws IOException {
        List<CrmEmailAttachments> attachments = new ArrayList<>();
        if (fileList == null || fileList.isEmpty()) {
            return attachments;
        }

        String filePath = RuoYiConfig.getUploadPath();
        for (MultipartFile file : fileList) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String filename = file.getOriginalFilename();
            byte[] fileBytes = file.getBytes();
            String storageUrl = FileUtils.writeBytesWithFilename(tenant, fileBytes, filePath, filename);

            CrmEmailAttachments att = new CrmEmailAttachments();
            att.setMessageId(localMessageId);
            att.setAttachmentId(UUID.randomUUID().toString());
            att.setFilename(filename);
            att.setMimeType(file.getContentType());
            att.setSize(fileBytes.length);
            att.setIsInline(0);
            att.setDownloaded(1);
            att.setStorageUrl(storageUrl);
            attachments.add(att);
        }

        replaceAttachmentRecords(localMessageId, attachments);
        return attachments;
    }

    private void replaceAttachmentRecords(String localMessageId, List<CrmEmailAttachments> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        for (CrmEmailAttachments attachment : attachments) {
            attachment.setMessageId(localMessageId);
        }
        crmEmailAttachmentsMapper.deleteCrmEmailAttachmentsByMessageId(localMessageId);
        crmEmailAttachmentsMapper.batchInsertCrmEmailAttachments(attachments);
    }

    private List<CrmEmailAttachments> loadAttachmentsForMessage(String localMessageId) {
        CrmEmailAttachments query = new CrmEmailAttachments();
        query.setMessageId(localMessageId);
        return crmEmailAttachmentsMapper.selectCrmEmailAttachmentsList(query);
    }

    /**
     * ✅ 转发送邮件（Base64 编码）
     */

    public int forwardEmail(String provider, String accessToken, String messageId, String from, String to, String subject,
                            String msgBody, List<MultipartFile> fileList) {
        try {
            if ("Gmail".equalsIgnoreCase(provider)) {
                return forwardGmailEmail(accessToken, messageId, from, to, subject, msgBody, fileList);
            } else if ("Outlook".equalsIgnoreCase(provider)) {
                return forwardOutlookEmail(accessToken, messageId, to, msgBody, fileList);
            } else {
                return 0;
            }
        } catch (IOException ioe) {
            return 0;
        }
    }


    public int forwardGmailEmail(String accessToken, String messageId, String from, String to, String subject,
                            String msgBody, List<MultipartFile> fileList) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Step 1: Get original message in raw format
            HttpHeaders getHeaders = new HttpHeaders();
            getHeaders.setBearerAuth(accessToken);
            HttpEntity<Void> getRequest = new HttpEntity<>(getHeaders);

            ResponseEntity<Map> response = restTemplate.exchange(
                    GMAIL_GET_MESSAGE_URL,
                    HttpMethod.GET,
                    getRequest,
                    Map.class,
                    messageId
            );

            String raw = (String) response.getBody().get("raw");
            byte[] originalBytes = Base64.getUrlDecoder().decode(raw);

            // Step 2: Parse original MimeMessage
            Session session = Session.getDefaultInstance(new Properties(), null);
            MimeMessage originalMessage = new MimeMessage(session, new ByteArrayInputStream(originalBytes));

            // Step 3: Build forward message
            MimeMessage forwardMessage = new MimeMessage(session);
            forwardMessage.setFrom(new InternetAddress(from));
            forwardMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            forwardMessage.setSubject("Fwd: " + (subject != null ? subject : originalMessage.getSubject()));
            forwardMessage.setSentDate(new Date());

            // Create multipart
            Multipart multipart = new MimeMultipart();

            // 3.1 Add plain text or HTML body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(msgBody != null ? msgBody : "Please see forwarded email.", "text/html; charset=utf-8");
            multipart.addBodyPart(textPart);

            // 3.2 Attach original email as .eml
            ByteArrayOutputStream emlStream = new ByteArrayOutputStream();
            originalMessage.writeTo(emlStream);
            byte[] emlBytes = emlStream.toByteArray();

            DataSource emlDataSource = new ByteArrayDataSource(emlBytes, "message/rfc822");
            MimeBodyPart emlAttachment = new MimeBodyPart();
            emlAttachment.setDataHandler(new DataHandler(emlDataSource));
            emlAttachment.setFileName("forwarded.eml");
            emlAttachment.setDisposition(Part.ATTACHMENT);
            multipart.addBodyPart(emlAttachment);

            // 3.3 Add any additional attachments
            if (fileList != null && !fileList.isEmpty()) {
                for (MultipartFile file : fileList) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.setFileName(file.getOriginalFilename());
                    attachmentPart.setContent(file.getBytes(), file.getContentType());
                    attachmentPart.setDisposition(Part.ATTACHMENT);
                    multipart.addBodyPart(attachmentPart);
                }
            }

            forwardMessage.setContent(multipart);

            // Step 4: Encode forwardMessage as raw
            ByteArrayOutputStream forwardStream = new ByteArrayOutputStream();
            forwardMessage.writeTo(forwardStream);
            String encodedEmail = Base64.getUrlEncoder().withoutPadding().encodeToString(forwardStream.toByteArray());

            // Step 5: Send via Gmail API
            Map<String, String> payload = new HashMap<>();
            payload.put("raw", encodedEmail);
//
//            HttpHeaders postHeaders = new HttpHeaders();
//            postHeaders.setBearerAuth(accessToken);
//            postHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> postRequest = new HttpEntity<>(payload, buildHeaders(accessToken));
            ResponseEntity<String> sendResponse = restTemplate.postForEntity(GMAIL_SEND_MESSAGE_URL, postRequest, String.class);

            if (sendResponse.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Email forwarded successfully.");
                return 1;
            } else {
                System.err.println("❌ Failed to forward email: " + sendResponse.getBody());
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private int forwardOutlookEmail(String accessToken, String messageId, String to, String comment, List<MultipartFile> attachments) throws IOException {
        String url = String.format("https://graph.microsoft.com/v1.0/me/messages/%s/forward", messageId);

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBody = objectMapper.createObjectNode();

        // 收件人
        ArrayNode toRecipients = objectMapper.createArrayNode();
        ObjectNode email = objectMapper.createObjectNode();
        email.put("address", to);
        ObjectNode recipient = objectMapper.createObjectNode();
        recipient.set("emailAddress", email);
        toRecipients.add(recipient);

        requestBody.set("toRecipients", toRecipients);

        // 正文评论
        requestBody.put("comment", comment);

        // 附件处理
        if (attachments != null && !attachments.isEmpty()) {
            ArrayNode attachmentArray = objectMapper.createArrayNode();

            for (MultipartFile file : attachments) {
                ObjectNode attachment = objectMapper.createObjectNode();
                attachment.put("@odata.type", "#microsoft.graph.fileAttachment");
                attachment.put("name", file.getOriginalFilename());
                attachment.put("contentType", file.getContentType());
                attachment.put("contentBytes", Base64.getEncoder().encodeToString(file.getBytes()));
                attachmentArray.add(attachment);
            }

            requestBody.set("attachments", attachmentArray);
        }

        // 设置 HTTP 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        // 发送请求
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to forward Outlook email: " + response.getBody());
        }
        return 1;
    }

    /**
     * ✅ 获取所有标签
     */
    public JsonNode listLabels(String accessToken) {
        String url = GMAIL_API_BASE + "/labels";
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(accessToken));
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        return response.getBody();
    }

    /**
     * ✅ 修改邮件标签（添加/移除标签，如 UNREAD, INBOX）
     */
    public JsonNode modifyMessageLabels(String accessToken, String messageId,
                                        List<String> addLabels, List<String> removeLabels) {
        String url = GMAIL_API_BASE + "/messages/" + messageId + "/modify";

        Map<String, Object> payload = new HashMap<>();
        if (addLabels != null && !addLabels.isEmpty()) payload.put("addLabelIds", addLabels);
        if (removeLabels != null && !removeLabels.isEmpty()) payload.put("removeLabelIds", removeLabels);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, buildHeaders(accessToken));
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, entity, JsonNode.class);
        return response.getBody();

    }
    /**
     * Sync email messages
     * @param accessToken
     * @param userId
     */
    @Transactional
    public int syncMessages(String accessToken, String userId, String externalName){
        String pageToken = null;
        int counter = 0;

        // 构建查询字符串，只同步最近 days 天，例如 30 天
        String queryDate = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String queryParam = "&q=after:" + queryDate;

        do {
            // 构造 URL
            String url = GMAIL_API_BASE + "/users/me/messages?maxResults=100" + queryParam;
            if (pageToken != null) {
                url += "&pageToken=" + pageToken;
            }

            // 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 请求 Gmail API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();

            // 解析邮件 messageId
            for (JsonNode message : body.get("messages")) {
                String messageId = message.get("id").asText();
                try {
                    fetchAndStoreMessageMetadata(accessToken, messageId, userId, externalName);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            // 获取下一页 pageToken
            pageToken = body.has("nextPageToken") ? body.get("nextPageToken").asText() : null;
            counter += 1;
        } while (pageToken != null);
        return counter;
    }

    /**
     *
     * @param accessToken
     * @param userId
     * @param days  需要同步的天数
     * @return
     */
    @Override
    public int listRecentMessages(String accessToken, String userId, int days, String externalName) {
        String pageToken = null;
        int counter = 0;

        // 构建查询字符串，只同步最近 days 天，例如 30 天
        String queryDate = LocalDate.now().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String queryParam = "&q=after:" + queryDate;

        do {
            // 构造 URL
            String url = GMAIL_API_BASE + "/users/me/messages?maxResults=100" + queryParam;
            if (pageToken != null) {
                url += "&pageToken=" + pageToken;
            }

            // 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 请求 Gmail API
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode body = response.getBody();

            // 解析邮件 messageId
            if (body.has("messages")) {
                for (JsonNode message : body.get("messages")) {
                    String messageId = message.get("id").asText();
                    try {
                        fetchAndStoreMessageMetadata(accessToken, messageId, userId, externalName);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }

            // 获取下一页 pageToken
            pageToken = body.has("nextPageToken") ? body.get("nextPageToken").asText() : null;
            counter += 1;
        } while (pageToken != null);

        return counter;
    }

    /**
     * 获取Outlook的邮件
     * @param accessToken
     * @param userId
     * @param days
     * @param externalName
     * @return
     */
    @Override
    public int listRecentOutlookEmails(String accessToken, String userId, int days, String externalName) {
        try{

        List<CrmEmailMessages> results = new ArrayList<>();

        for (String folder : PRIORITY_FOLDERS) {
            List<CrmEmailMessages> folderResults = getOutlookEmailsByFolder(folder, accessToken, userId, days, externalName);
            if (folderResults != null && !folderResults.isEmpty()) {
                results.addAll(folderResults);
            }
        }

        for (CrmEmailMessages email: results) {
            crmEmailMessagesMapper.insertCrmEmailMessages(email);
        }

        return results.size();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取指定邮箱目录最近14天的邮件metadata
     * @param userId
     * @param provider
     * @param days
     * @param externalName
     * @return
     */
    @Override
    public int listRecentIMAPEmails(String userId, String provider, int days, String externalName) {
        try{

            Map<String, Object> connParams = new HashMap<>();
            connParams.put("userId", userId);
            connParams.put("provider", provider);
            connParams.put("externalName", externalName);

            CrmOauthConnections conn = crmOauthConnectionsMapper.selectCrmOauthConnectionsByUserId(connParams);

            if(conn == null) {
                throw new Exception("Failed to get connection");
            }

            List<CrmEmailMessages> results = new ArrayList<>();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> meta;

            meta = objectMapper.readValue(conn.getMetadata(), Map.class);
            int port = 993;
            String host = (String) meta.get("imapHost");
            if (meta.get("imapPort").toString() != null) {
                port = Integer.parseInt(meta.get("imapPort").toString());
            }
            String username = conn.getExternalName();
            String password = AESUtil.decrypt(conn.getAccessToken());
            if (password == null) {
                throw new Exception("Failed to get password");
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

            Session session = Session.getInstance(props);
            Store store = session.getStore("imap");
            store.connect(host, port, username, password);

            Folder defaultFolder = store.getDefaultFolder();
            Folder[] folders = defaultFolder.list("*"); // *表示所有子目录
//            for (Folder f : folders) {
//                System.out.println("Folder name: " + f.getFullName());
//            }


            for (String folder : PRIORITY_FOLDERS) {

                List<CrmEmailMessages> folderResults = getImapEmailsByFolder(store, folders, folder, userId, days, externalName);
                if (folderResults != null && !folderResults.isEmpty()) {
                    results.addAll(folderResults);
                }
            }

            for (CrmEmailMessages email: results) {
                crmEmailMessagesMapper.insertCrmEmailMessages(email);
            }

            store.close();
            return results.size();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("IMAP sync failed: " + e.getMessage(), e);
        }
    }
    /**
     * 查询邮件
     * 
     * @param id 邮件主键
     * @return 邮件
     */
    @Override
    public CrmEmailMessages selectCrmEmailMessagesById(String id)
    {
        return crmEmailMessagesMapper.selectCrmEmailMessagesById(id);
    }

    /**
     * 查询邮件是否已经被同步
     *
     * @param messageExternalId 邮件主键
     * @return 邮件
     */
    public CrmEmailMessages existsByMessageId(String messageExternalId) {
        return crmEmailMessagesMapper.existsByMessageId(messageExternalId);

    }
    /**
     * 查询邮件列表
     * 
     * @param crmEmailMessages 邮件
     * @return 邮件
     */
    @Override
    public List<CrmEmailMessages> selectCrmEmailMessagesList(CrmEmailMessages crmEmailMessages)
    {
        return crmEmailMessagesMapper.selectCrmEmailMessagesList(crmEmailMessages);
    }

    /**
     * 新增邮件
     * 
     * @param crmEmailMessages 邮件
     * @return 结果
     */
    @Override
    public int insertCrmEmailMessages(CrmEmailMessages crmEmailMessages)
    {
        return crmEmailMessagesMapper.insertCrmEmailMessages(crmEmailMessages);
    }

    /**
     * 修改邮件
     * 
     * @param crmEmailMessages 邮件
     * @return 结果
     */
    @Override
    public int updateCrmEmailMessages(CrmEmailMessages crmEmailMessages)
    {
        return crmEmailMessagesMapper.updateCrmEmailMessages(crmEmailMessages);
    }

    /**
     * 批量删除邮件
     * 
     * @param ids 需要删除的邮件主键
     * @return 结果
     */
    @Override
    public int deleteCrmEmailMessagesByIds(String[] ids)
    {
        return crmEmailMessagesMapper.deleteCrmEmailMessagesByIds(ids);
    }

    /**
     * 删除邮件信息
     * 
     * @param id 邮件主键
     * @return 结果
     */
    @Override
    public int deleteCrmEmailMessagesById(String id)
    {
        return crmEmailMessagesMapper.deleteCrmEmailMessagesById(id);
    }

    @Override
    public int modifyMessageLabel(String provider, String accessToken, String messageId, boolean markAsRead) {
        if ("Gmail".equalsIgnoreCase(provider)) {
            setGmailReadStatus(accessToken, messageId, markAsRead);
        } else if ("Outlook".equalsIgnoreCase(provider)) {
            setOutlookReadStatus(accessToken, messageId, markAsRead);
        } else {
            //TODO IMAP
        }
        int isRead = markAsRead ? 1 : 0;

        CrmEmailMessages crmEmailMessages = new CrmEmailMessages();
        crmEmailMessages.setMessageExternalId(messageId);
        crmEmailMessages.setIsRead(isRead);

        return crmEmailMessagesMapper.updateCrmEmailMessages(crmEmailMessages);
    }

    private List<CrmEmailMessages> getOutlookEmailsByFolder(String folder, String accessToken, String userId, int days, String externalName) throws UnsupportedEncodingException {
        String outlookFolder = mapFolderName(folder);
        List<CrmEmailMessages> result = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        OffsetDateTime fourteenDaysAgo = OffsetDateTime.now(ZoneOffset.UTC).minusDays(days);
        String fromDate = fourteenDaysAgo.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);  // ISO-8601 带 Z 时区的格式

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = "https://graph.microsoft.com/v1.0/me/mailFolders/" + outlookFolder + "/messages" +
                "?$select=id,conversationId,subject,bodyPreview,receivedDateTime,from,toRecipients,ccRecipients,bccRecipients,isRead,categories,hasAttachments" +
                "&$filter=receivedDateTime ge " + fromDate +
                "&$top=50" +
                "&$orderby=receivedDateTime desc";

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch Outlook messages metadata");
        }

        for (JsonNode item : response.getBody().get("value")) {
            CrmEmailMessages email = new CrmEmailMessages();
            email.setUserId(userId);

            String messageId = item.path("id").asText();

            CrmEmailMessages crmEmailMessages = crmEmailMessagesMapper.existsByMessageId(messageId);
            if (crmEmailMessages != null) {
                continue; // 已同步，跳过
            }

            email.setMessageExternalId(messageId);
            email.setThreadId(item.path("conversationId").asText(null));
            email.setSubject(item.path("subject").asText(null));

            JsonNode from = item.path("from").path("emailAddress");
            email.setSender(from.path("address").asText(null));

            List<String> recipients = new ArrayList<>();
            Stream.of("toRecipients", "ccRecipients", "bccRecipients").forEach(field -> {
                if (item.has(field)) {
                    for (JsonNode recipient : item.get(field)) {
                        String address = recipient.path("emailAddress").path("address").asText(null);
                        if (address != null) recipients.add(address);
                    }
                }
            });
            email.setRecipients(String.join(",", recipients));

            email.setSnippet(item.path("bodyPreview").asText(null));
            email.setHasAttachment(item.path("hasAttachments").asInt(0));
            email.setIsRead(item.path("isRead").asInt(0));

            if (item.has("receivedDateTime")) {
                email.setReceivedAt(Timestamp.valueOf(
                        LocalDateTime.parse(item.get("receivedDateTime").asText(), DateTimeFormatter.ISO_DATE_TIME)));
            }

            email.setFolder(folder);

            // 标签/分类
            if (item.has("categories")) {
                JsonNode labelIdsNode = item.get("categories");

                // 设置是否已读


                // 序列化 labels 为 JSON 字符串存入数据库
                String labelIdsJson = labelIdsNode != null ? labelIdsNode.toString() : "[]";

                email.setLabels(labelIdsJson); // 保存为：["UNREAD","CATEGORY_PERSONAL","INBOX"]


            }
            email.setIsDownloaded(0); // 初始设为 false，等后续下载邮件详情再设置
            email.setSyncedAt(new Date());

            email.setProvider("Outlook");
            email.setCreateTime(new Date());
            email.setExternalName(externalName);

            // 可选：保存原始 JSON
//            email.setRawJson(item.toPrettyString());

            result.add(email);
        }
        return result;
    }
    private List<CrmEmailMessages> getImapEmailsByFolder(Store store, Folder[] folders, String realFolder, String userId, int days, String externalName) throws UnsupportedEncodingException {

        try {
            String folderName = getRealFolderName(realFolder, folders);

            List<CrmEmailMessages> result = new ArrayList<>();

            IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
            if (!folder.exists()) {
                System.out.println("指定文件夹不存在: " + folderName);
                store.close();
                return null;
            }

            folder.open(Folder.READ_ONLY);

            // 计算14天前的日期
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -14);
            Date fourteenDaysAgo = cal.getTime();

            // 用ReceivedDateTerm找最近14天邮件
            ReceivedDateTerm recentTerm = new ReceivedDateTerm(ComparisonTerm.GE, fourteenDaysAgo);

            Message[] messages = folder.search(recentTerm);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            System.out.println("在目录 " + folderName + " 中找到 " + messages.length + " 封最近14天的邮件:");

            for (Message msg : messages) {

                long uid = folder.getUID(msg);
                CrmEmailMessages email = new CrmEmailMessages();

                CrmEmailMessages crmEmailMessages = crmEmailMessagesMapper.existsByMessageId(String.valueOf(uid));
                if (crmEmailMessages != null) {
                    continue; // 已同步，跳过
                }
                Address[] froms = msg.getFrom();
                String from = froms == null ? "未知" : ((InternetAddress) froms[0]).getAddress();

                String subject = msg.getSubject();
                Date receivedDate = msg.getReceivedDate();
                String receivedDateStr = receivedDate == null ? "未知" : sdf.format(receivedDate);

                Address[] tos = msg.getRecipients(MimeMessage.RecipientType.TO);
                String to = tos == null ? "未知" : ((InternetAddress) tos[0]).getAddress();
                String recipients = formatAddresses(tos);

                Object content = msg.getContent();

                if (content instanceof String) {
                    // 简单邮件，只有一个正文部分
                    String body = (String) content;
                    if (msg.isMimeType("text/html")) {
                        email.setBodyHtml(body);
                    } else if (msg.isMimeType("text/plain")) {
                        email.setBodyPlain(body);
                    }
                } else if (content instanceof Multipart) {
                    // 多部分邮件（常见于 HTML + 附件 或 HTML + Text）


                    handleMultipart((Multipart) content, email);


                }

                email.setSender(from);
                email.setRecipients(recipients);
                email.setFolder(realFolder);
                email.setUserId(userId);
                email.setMessageExternalId(String.valueOf(uid));
                email.setSubject(msg.getSubject());
                email.setSnippet(extractSnippet(msg));
                email.setReceivedAt(DateUtils.parseDate(receivedDateStr));
                email.setIsRead(0);
                email.setIsDownloaded(0);
                email.setExternalName(externalName);
                email.setSyncedAt(DateUtils.getNowDate());
                email.setProvider("IMAP");

                result.add(email);
            }

            folder.close(false);

            return result;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }

    private void setGmailReadStatus(String accessToken, String messageId, boolean markAsRead) {
        RestTemplate restTemplate = createAuthorizedRestTemplate(accessToken);
        String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/" + messageId + "/modify";

        Map<String, List<String>> body = new HashMap<>();
        if (markAsRead) {
            body.put("removeLabelIds", Arrays.asList("UNREAD")); // 设置为已读
        } else {
            body.put("addLabelIds", Arrays.asList("UNREAD"));    // 设置为未读
        }

        HttpEntity<Map<String, List<String>>> requestEntity = new HttpEntity<>(body);
        restTemplate.postForEntity(url, requestEntity, Void.class);

    }

    private int setOutlookReadStatus(String accessToken, String messageId, boolean markAsRead) {

        RestTemplate restTemplate = createRestTemplateSupportingPatch(); // 使用上面的方法
        String url = "https://graph.microsoft.com/v1.0/me/messages/" + messageId;

        HttpHeaders headers = createAuthHeaders(accessToken);
        Map<String, Object> payload = new HashMap<>();
        payload.put("isRead", markAsRead);
        int isRead = markAsRead ? 1 : 0;

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);

        CrmEmailMessages crmEmailMessages = new CrmEmailMessages();
        crmEmailMessages.setMessageExternalId(messageId);
        crmEmailMessages.setIsRead(isRead);

        return crmEmailMessagesMapper.updateCrmEmailMessages(crmEmailMessages);
    }
    /**
     * 移动邮件到回收站
     */
    public int moveToTrash(String provider, String accessToken, String messageId, String userId, String externalName) {
        try {
            ResponseEntity<String> response = null;

            if ("Gmail".equalsIgnoreCase(provider)) {
                RestTemplate restTemplate = createAuthorizedRestTemplate(accessToken);
                String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/" + messageId + "/trash";
                response = restTemplate.postForEntity(url, null, String.class);
            } else if ("Outlook".equalsIgnoreCase(provider)) {
                String url = "https://graph.microsoft.com/v1.0/me/messages/" + messageId + "/move";

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> body = new HashMap<>();
                body.put("destinationId", "deleteditems");

                HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
                RestTemplate restTemplate = new RestTemplate();
                response = restTemplate.postForEntity(url, request, String.class);
            } else if (isImapProvider(provider)) {
                return moveImapMessageToTrash(messageId, userId, provider, externalName);
            } else {
                return 0;
            }

            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                CrmEmailMessages crmEmailMessages = new CrmEmailMessages();
                crmEmailMessages.setMessageExternalId(messageId);
                crmEmailMessages.setFolder("TRASH");
                return crmEmailMessagesMapper.updateCrmEmailMessages(crmEmailMessages);
            }
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to move email to trash: " + e.getMessage(), e);
        }
    }

    private boolean isImapProvider(String provider) {
        return provider != null
                && !"Gmail".equalsIgnoreCase(provider)
                && !"Outlook".equalsIgnoreCase(provider);
    }

    private int moveImapMessageToTrash(String messageId, String userId, String provider, String externalName) throws Exception {
        Map<String, Object> connParams = new HashMap<>();
        connParams.put("userId", userId);
        connParams.put("provider", provider);
        connParams.put("externalName", externalName);

        CrmOauthConnections conn = crmOauthConnectionsMapper.selectCrmOauthConnectionsByUserId(connParams);
        if (conn == null) {
            throw new Exception("Failed to get IMAP connection");
        }

        CrmEmailMessages email = crmEmailMessagesMapper.existsByMessageId(messageId);
        if (email == null) {
            throw new Exception("Email not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> meta = objectMapper.readValue(conn.getMetadata(), Map.class);
        int port = 993;
        String host = (String) meta.get("imapHost");
        if (meta.get("imapPort") != null && meta.get("imapPort").toString() != null) {
            port = Integer.parseInt(meta.get("imapPort").toString());
        }
        String username = conn.getExternalName();
        String password = AESUtil.decrypt(conn.getAccessToken());
        if (password == null) {
            throw new Exception("Failed to get password");
        }
        boolean useSSL = true;
        if (meta.get("useSsl") != null && meta.get("useSsl").toString() != null) {
            useSSL = Boolean.parseBoolean(meta.get("useSsl").toString());
        }

        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", String.valueOf(port));
        props.put("mail.imap.ssl.enable", useSSL ? "true" : "false");

        Session session = Session.getInstance(props);
        IMAPStore store = (IMAPStore) session.getStore("imap");
        store.connect(host, port, username, password);

        try {
            String standardFolder = email.getFolder() != null ? email.getFolder() : "INBOX";
            String sourceFolderName = resolveImapFolderName(store, standardFolder, meta);
            if (sourceFolderName == null) {
                sourceFolderName = "INBOX";
            }

            long uid = Long.parseLong(messageId);
            IMAPFolder sourceFolder = (IMAPFolder) store.getFolder(sourceFolderName);
            sourceFolder.open(Folder.READ_WRITE);

            Message message = sourceFolder.getMessageByUID(uid);
            if (message == null) {
                // 邮件已不在源文件夹，视为已删除
                sourceFolder.close(false);
                CrmEmailMessages alreadyGone = new CrmEmailMessages();
                alreadyGone.setMessageExternalId(messageId);
                alreadyGone.setFolder("TRASH");
                return crmEmailMessagesMapper.updateCrmEmailMessages(alreadyGone);
            }

            String trashFolderName = resolveImapFolderName(store, "TRASH", meta);
            if (trashFolderName != null && !trashFolderName.equals(sourceFolderName)) {
                IMAPFolder trashFolder = (IMAPFolder) store.getFolder(trashFolderName);
                trashFolder.open(Folder.READ_WRITE);
                sourceFolder.copyMessages(new Message[]{message}, trashFolder);
                trashFolder.close(false);
            }

            // 部分服务器在 COPY 后会让原消息对象失效（等同于移动），重新按 UID 获取后再标记删除
            try {
                Message toDelete = sourceFolder.getMessageByUID(uid);
                if (toDelete != null) {
                    toDelete.setFlag(Flags.Flag.DELETED, true);
                    sourceFolder.expunge();
                }
            } catch (MessageRemovedException ignored) {
                // 消息已被服务器移除（移动已完成），无需额外处理
            }

            sourceFolder.close(false);
        } finally {
            store.close();
        }

        CrmEmailMessages crmEmailMessages = new CrmEmailMessages();
        crmEmailMessages.setMessageExternalId(messageId);
        crmEmailMessages.setFolder("TRASH");
        return crmEmailMessagesMapper.updateCrmEmailMessages(crmEmailMessages);
    }

    /**
     *
     * @param accessToken
     * @param messageId
     */
    public int deleteMessage(String provider, String accessToken, String messageId) {
        if ("IMAP".equalsIgnoreCase(provider)) {

        } else {

            RestTemplate restTemplate = createAuthorizedRestTemplate(accessToken);
            String url = "https://gmail.googleapis.com/gmail/v1/users/me/messages/" + messageId;

            restTemplate.delete(url);
        }

        CrmEmailMessages crmEmailMessages = new CrmEmailMessages();
        crmEmailMessages.setMessageExternalId(messageId);
        crmEmailMessages.setIsDeleted(1);

        return crmEmailMessagesMapper.updateCrmEmailMessages(crmEmailMessages);
    }

    private void fetchAndStoreMessageMetadata(String accessToken, String messageId, String userId, String externalName) throws JsonProcessingException {
        String url = GMAIL_API_BASE + "/users/me/messages/" + messageId + "?format=metadata&metadataHeaders=Subject&metadataHeaders=From&metadataHeaders=To";

        CrmEmailMessages crmEmailMessages = crmEmailMessagesMapper.existsByMessageId(messageId);
        if (crmEmailMessages != null) {
            return; // 已同步，跳过
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        JsonNode message = response.getBody();

        // 提取字段
        String subject = "";
        String from = "";
        String to = "";
        String threadId = "";
        for (JsonNode header : message.get("payload").get("headers")) {
            String name = header.get("name").asText();
            String value = header.get("value").asText();
            if ("Subject".equalsIgnoreCase(name)) subject = value;
            if ("From".equalsIgnoreCase(name)) from = value;
            if ("To".equalsIgnoreCase(name)) to = value;
        }

        threadId = message.get("threadId").asText();

        JsonNode labelIdsNode = message.get("labelIds");

        String folder = "OTHER";

        // 设置是否已读
        int isRead = 1;

        if (labelIdsNode != null && labelIdsNode.isArray()) {
            Iterator<JsonNode> iterator = labelIdsNode.elements();
            while (iterator.hasNext()) {
                String label = iterator.next().asText();

                // 如果有 UNREAD 标签，则设置 isRead = 0
                if ("UNREAD".equals(label)) {
                    isRead = 0;
                }

                // 查找优先文件夹标签
                if (PRIORITY_FOLDERS.contains(label) && "OTHER".equals(folder)) {
                    folder = label;
                }
            }
        }

// 序列化 labels 为 JSON 字符串存入数据库
        String labelIdsJson = labelIdsNode != null ? labelIdsNode.toString() : "[]";


        String snippet = message.get("snippet").asText();
        long internalDate = message.get("internalDate").asLong();

        CrmEmailMessages msg = new CrmEmailMessages();
        msg.setId(UUID.randomUUID().toString());
        msg.setUserId(userId);
        msg.setMessageExternalId(messageId);
        msg.setSubject(subject);
        msg.setSender(from);
        msg.setRecipients(to);
        msg.setSnippet(snippet);
        msg.setReceivedAt(new Date(internalDate));
        msg.setProvider("Gmail");
        msg.setIsDownloaded(0);
        msg.setLabels(labelIdsJson); // 保存为：["UNREAD","CATEGORY_PERSONAL","INBOX"]
        msg.setThreadId(threadId);
        msg.setIsRead(isRead);
        msg.setFolder(folder);
        msg.setCreateTime(new Date());
        msg.setExternalName(externalName);

        crmEmailMessagesMapper.insertCrmEmailMessages(msg);
    }

    private CrmEmailContent extractBodyParts(JsonNode payload) {
        CrmEmailContent content = new CrmEmailContent();
        extractRecursive(payload, content);
        return content;
    }

    private void extractRecursive(JsonNode part, CrmEmailContent content) {
        if (part == null) return;

        String mimeType = part.path("mimeType").asText();
        JsonNode body = part.path("body");
        String data = body.path("data").asText();

        if ("text/plain".equalsIgnoreCase(mimeType) && content.plainText == null) {
            content.plainText = decodeBase64(data);
        } else if ("text/html".equalsIgnoreCase(mimeType) && content.htmlText == null) {
            content.htmlText = decodeBase64(data);
        }

        if (part.has("parts")) {
            for (JsonNode subPart : part.get("parts")) {
                extractRecursive(subPart, content);
            }
        }
    }

    private List<CrmEmailAttachments> extractOutlookAttachments(String tenant, String externalMessageId,
                                                                String localMessageId, String accessToken) {
        List<CrmEmailAttachments> attachments = new ArrayList<>();

        // 上传文件保存路径
        String filePath = RuoYiConfig.getUploadPath();

        // 获取附件列表
        String url = String.format("https://graph.microsoft.com/v1.0/me/messages/%s/attachments", externalMessageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

        JsonNode attachmentArray = response.getBody().path("value");

        for (JsonNode item : attachmentArray) {
            String type = item.path("@odata.type").asText();

            // 只处理文件型附件
            if (!"#microsoft.graph.fileAttachment".equals(type)) continue;

            CrmEmailAttachments att = new CrmEmailAttachments();

            String attachmentId = item.path("id").asText();
            String name = item.path("name").asText();
            String contentType = item.path("contentType").asText();
            int size = item.path("size").asInt();
            String contentId = item.path("contentId").asText(null);
            boolean isInlineBool = item.path("isInline").asBoolean(false); // ✅ 使用 isInline 字段判断
            int isInline = isInlineBool ? 1 : 0;

            String contentBytes = item.path("contentBytes").asText();
            byte[] fileBytes = Base64.getDecoder().decode(contentBytes);

            try {
                String storageUrl = FileUtils.writeBytesWithFilename(tenant, fileBytes, filePath, name);

                att.setMessageId(localMessageId);
                att.setAttachmentId(attachmentId);
                att.setFilename(name);
                att.setMimeType(contentType);
                att.setSize(size);
                att.setContentId(contentId);
                att.setIsInline(isInline);
                att.setDownloaded(1);
                att.setStorageUrl(storageUrl);

                attachments.add(att);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return attachments;
    }

    private List<CrmEmailAttachments> extractAttachments(String tenant, JsonNode payload, String externalMessageId,
                                                         String localMessageId, String accessToken) {
        List<CrmEmailAttachments> attachments = new ArrayList<>();

        String filePath = RuoYiConfig.getUploadPath();
        extractAttachmentRecursive(tenant, payload, externalMessageId, localMessageId, attachments, accessToken, filePath);
        return attachments;
    }

    private void extractAttachmentRecursive(String tenant, JsonNode part, String externalMessageId, String localMessageId,
                                            List<CrmEmailAttachments> list, String accessToken, String filePath) {
        String mimeType = part.path("mimeType").asText();
        boolean isAttachment = mimeType != null && !mimeType.startsWith("multipart/") && part.path("filename").asText().length() > 0;

        if (isAttachment) {
            String contentDisposition = getHeader(part, "Content-Disposition").toLowerCase();
            int isInline = contentDisposition.startsWith("inline") ? 1 : 0;
            String attachmentId = part.path("body").path("attachmentId").asText();
            String filename = part.path("filename").asText();


            CrmEmailAttachments att = new CrmEmailAttachments();
            att.setMessageId(localMessageId);
            att.setAttachmentId(attachmentId);
            att.setFilename(filename);
            att.setMimeType(mimeType);
            att.setSize(part.path("body").path("size").asInt());
            att.setContentId(getHeader(part, "Content-ID"));
            att.setIsInline(isInline);
            att.setDownloaded(0);


            byte[] fileBytes = downloadAttachment(externalMessageId, attachmentId, accessToken);

                try {
                    String storageUrl = FileUtils.writeBytesWithFilename(tenant, fileBytes, filePath, filename);

                    att.setStorageUrl(storageUrl);
                    att.setDownloaded(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            list.add(att);
        }

        if (part.has("parts")) {
            for (JsonNode subPart : part.get("parts")) {
                extractAttachmentRecursive(tenant, subPart, externalMessageId, localMessageId, list, accessToken, filePath);
            }
        }
    }

    /**
     * 获取邮件详细内容，并且更新CrmEmailMessage表
     * @param hostName
     * @param tenant
     * @param accessToken
     * @param messageId
     * @param userId
     * @return
     * @throws JsonProcessingException
     */
    private CrmEmailMessages updateMessageDetails(String hostName, String tenant, String accessToken, String messageId, String userId) throws JsonProcessingException {
        String url = GMAIL_API_BASE + "/users/me/messages/" + messageId + "?format=full";

        CrmEmailMessages email = crmEmailMessagesMapper.existsByMessageId(messageId);
        List<CrmEmailAttachments> attachments;
        if (email == null) {
            return null; // 已同步，跳过
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
        JsonNode messageJson = response.getBody();

        String threadId = messageJson.get("threadId").asText();
        String snippet = messageJson.has("snippet") ? messageJson.get("snippet").asText() : null;
        long internalDate = messageJson.has("internalDate") ? messageJson.get("internalDate").asLong() : 0L;

        JsonNode labelIdsNode = messageJson.get("labelIds");
        List<String> labelList = new ArrayList<>();
        if (labelIdsNode != null && labelIdsNode.isArray()) {
            for (JsonNode label : labelIdsNode) {
                labelList.add(label.asText());
            }
        }

        boolean isRead = !labelList.contains("UNREAD");

        JsonNode payload = messageJson.get("payload");
        String subject = null, from = null, to = null;
        if (payload.has("headers")) {
            for (JsonNode header : payload.get("headers")) {
                String name = header.get("name").asText();
                String value = header.get("value").asText();
                if ("Subject".equalsIgnoreCase(name)) subject = value;
                if ("From".equalsIgnoreCase(name)) from = value;
                if ("To".equalsIgnoreCase(name)) to = value;
            }
        }

        CrmEmailContent content = extractBodyParts(payload);
        // 正文解析（plain / html）
//        String bodyPlain = null;
//        String bodyHtml = null;
        List<JsonNode> parts = new ArrayList<>();
        if (payload.has("parts")) {
            for (JsonNode part : payload.get("parts")) {
                parts.add(part);
            }
        } else {
            parts.add(payload); // 单体邮件（无多 part）
        }


        boolean hasAttachment = parts.stream().anyMatch(p -> p.has("filename") && !p.get("filename").asText().isEmpty());
        int hasAttachmentInt = hasAttachment ? 1 : 0;
        int isReadInt = isRead ? 1 : 0;

        // 设置主要 folder，用于直观展示
        List<String> mainFolders = Arrays.asList("INBOX", "SENT", "SPAM", "TRASH", "DRAFT");
        for (String label : labelList) {
            if (mainFolders.contains(label)) {
                email.setFolder(label);
                break;
            }
        }

        if (hasAttachment) {

            attachments = extractAttachments(tenant, payload, messageId, email.getId(), accessToken);
            replaceAttachmentRecords(email.getId(), attachments);
            content.htmlText = replaceInlineCidImages(hostName, content.htmlText, attachments);

        } else {
            attachments = Collections.emptyList();
        }

        email.setUserId(userId);
        email.setProvider(email.getProvider());
        email.setMessageExternalId(messageId);
        email.setThreadId(threadId);
        email.setSubject(subject);
        email.setSender(from);
        email.setRecipients(to);
        email.setSnippet(snippet);
        email.setHasAttachment(hasAttachmentInt);
        email.setBodyPlain(content.plainText);
        email.setBodyHtml(content.htmlText);
        email.setLabels(new ObjectMapper().valueToTree(labelList).toString());
        email.setIsRead(1);
        email.setIsDownloaded(1);
        email.setReceivedAt(new Date(internalDate));
        email.setSyncedAt(new Date());
//        email.setRawJson(messageJson.toPrettyString());

        crmEmailMessagesMapper.updateCrmEmailMessages(email);
        email.setCrmEmailAttachmentsList(hasAttachment ? attachments : loadAttachmentsForMessage(email.getId()));

        return email;
    }

    private CrmEmailMessages updateImapMessageDetails(String hostName, String tenant,
                                                      String accessToken, String messageId,
                                                      String userId, String provider,
                                                      String externalName) throws JsonProcessingException {

        Map<String, Object> connParams = new HashMap<>();
        connParams.put("userId", userId);
        connParams.put("provider", provider);
        connParams.put("externalName", externalName);

        CrmOauthConnections conn = crmOauthConnectionsMapper.selectCrmOauthConnectionsByUserId(connParams);
        if (conn == null) return null;

        CrmEmailMessages email = crmEmailMessagesMapper.existsByMessageId(messageId);
        if (email == null) {
            return null; // 已同步，跳过
        }

        List<CrmEmailAttachments> attachments;


        try {

            Long uid = Long.parseLong(messageId);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> meta;

            meta = objectMapper.readValue(conn.getMetadata(), Map.class);
            int port = 993;
            String host = (String) meta.get("imapHost");
            if (meta.get("imapPort").toString() != null) {
                port = Integer.parseInt(meta.get("imapPort").toString());
            }
            String username = conn.getExternalName();
            String password = AESUtil.decrypt(conn.getAccessToken());
            if (password == null) {
                throw new Exception("Failed to get password");
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

            Session session = Session.getInstance(props);
            IMAPStore store = (IMAPStore) session.getStore("imap");
            store.connect(username, password);

            String standardFolder = email.getFolder() != null ? email.getFolder() : "INBOX";
            Folder defaultFolder = store.getDefaultFolder();
            Folder[] folders = defaultFolder.list("*");
            String folderName = getRealFolderName(standardFolder, folders);
            if (folderName == null) {
                folderName = "INBOX";
            }

            IMAPFolder folder = (IMAPFolder) store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);


            Message message = folder.getMessageByUID(uid);

            if (message != null) {

                // 如果需要解析内容：
                Object content = message.getContent();
                if (content instanceof Multipart) {
                    attachments = extractAttachmentsFromMessage(tenant, content, email.getId(), RuoYiConfig.getUploadPath());

                    if (!attachments.isEmpty()) {
                        replaceAttachmentRecords(email.getId(), attachments);
                        email.setBodyHtml(replaceInlineCidImages(hostName, email.getBodyHtml(), attachments));
                    }
                }
            } else {
                System.out.println("Message not found for UID: " + uid);
            }
            folder.close(false);
            store.close();

            email.setIsRead(1);
            email.setIsDownloaded(1);

            crmEmailMessagesMapper.updateCrmEmailMessages(email);
            email.setCrmEmailAttachmentsList(loadAttachmentsForMessage(email.getId()));
            return email;

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private CrmEmailMessages updateOutlookMessageDetails(String hostName, String tenant, String accessToken, String messageId, String userId) throws JsonProcessingException {
        String url = String.format("https://graph.microsoft.com/v1.0/me/messages/%s?$expand=attachments", messageId);

        CrmEmailMessages email = crmEmailMessagesMapper.existsByMessageId(messageId);

        List<CrmEmailAttachments> attachments = Collections.emptyList();
        if (email == null) {
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch message: " + response.getBody());
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(response.getBody());

        JsonNode bodyNode = json.path("body");
        String contentType = bodyNode.path("contentType").asText();
        String content = bodyNode.path("content").asText();

        boolean hasAttachment = json.path("hasAttachments").asBoolean();
        boolean containsCid = content != null && content.contains("cid:");

        if (hasAttachment || containsCid) {
            attachments = extractOutlookAttachments(tenant, messageId, email.getId(), accessToken);
            replaceAttachmentRecords(email.getId(), attachments);
            content = replaceInlineCidImages(hostName, content, attachments);
        }

        if ("html".equalsIgnoreCase(contentType)) {
            email.setBodyHtml(content);
        } else {
            email.setBodyPlain(content);
        }

        email.setUserId(userId);
        email.setProvider("Outlook");
        email.setMessageExternalId(json.path("id").asText());
        email.setThreadId(json.path("conversationId").asText(null));
        email.setSubject(json.path("subject").asText(null));
        email.setSender(extractSender(json.path("from")));
        email.setRecipients(extractRecipients(json));
        email.setSnippet(json.path("bodyPreview").asText(null));
        email.setHasAttachment(json.path("hasAttachments").asBoolean(false) ? 1 : 0);
        email.setIsRead(json.path("isRead").asBoolean(false) ? 1 : 0);
        email.setLabels(extractCategories(json.path("categories")));
        email.setReceivedAt(parseReceivedTime(json.path("receivedDateTime").asText(null)));
        email.setIsDownloaded(1);
        email.setSyncedAt(new Date());

        crmEmailMessagesMapper.updateCrmEmailMessages(email);
        email.setCrmEmailAttachmentsList(hasAttachment || containsCid ? attachments : loadAttachmentsForMessage(email.getId()));

        return email;
    }
    /**
     * 使用 RestTemplate 获取附件内容
     *
     * @param messageId    Gmail 邮件 ID
     * @param attachmentId Gmail 附件 ID
     * @param accessToken  OAuth2 访问令牌
     * @return 解码后的附件内容
     */
    private byte[] downloadAttachment(String messageId, String attachmentId, String accessToken) {
        String url = String.format(
                "https://gmail.googleapis.com/gmail/v1/users/me/messages/%s/attachments/%s",
                messageId, attachmentId
        );

        // 设置 Authorization 头
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 发起 GET 请求

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, JsonNode.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("Failed to download attachment");
        }

        // 解析 base64url 编码的 data 字段
        String base64Data = response.getBody().get("data").asText();
        return Base64.getUrlDecoder().decode(base64Data);
    }

    private RestTemplate createAuthorizedRestTemplate(String accessToken) {
        return new RestTemplateBuilder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }
    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
    private static String replaceInlineCidImages(String hostName, String htmlText, List<CrmEmailAttachments> attachments) {
        if (htmlText == null || htmlText.isEmpty() || attachments == null || attachments.isEmpty()) {
            return htmlText;
        }

        // 正则匹配所有 <img src="cid:..."> 或 <img src='cid:...'>
        Pattern cidPattern = Pattern.compile("<img\\s+[^>]*src=[\"']cid:([^\"'>]+)[\"'][^>]*>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cidPattern.matcher(htmlText);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String cid = matcher.group(1);
            String replacementUrl = null;

            // 查找是否有匹配的 contentId
            for (CrmEmailAttachments attachment : attachments) {
                if (attachment.getIsInline() == 1 && cid.equals(stripAngleBrackets(attachment.getContentId()))) {
                    replacementUrl = hostName + attachment.getStorageUrl();
                    break;
                }
            }

            if (replacementUrl != null) {
                // 构造新的 img 标签
                String newImgTag = matcher.group(0).replaceFirst("src=[\"']cid:[^\"'>]+[\"']", "src=\"" + replacementUrl + "\"");
                matcher.appendReplacement(sb, Matcher.quoteReplacement(newImgTag));
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private String getHeader(JsonNode part, String name) {
        if (part.has("headers")) {
            for (JsonNode header : part.get("headers")) {
                if (name.equalsIgnoreCase(header.path("name").asText())) {
                    return header.path("value").asText().replaceAll("[<>]", "");
                }
            }
        }
        return null;
    }
    private String decodeBase64(String base64Data) {
        if (base64Data == null || base64Data.isEmpty()) return null;
        byte[] bytes = Base64.getUrlDecoder().decode(base64Data);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // 去除 Content-ID 的尖括号，如 <abc@xyz> -> abc@xyz
    private static String stripAngleBrackets(String cid) {
        if (cid == null) return null;
        return cid.replaceAll("^<|>$", "");
    }
    /**
     * 构建用于 Microsoft Graph API 的邮件 JSON 内容（支持 HTML + 附件）
     *
     * @param from       发件人邮箱（可为空，Graph API 自动使用登录用户）
     * @param to         收件人邮箱
     * @param subject    邮件主题
     * @param body       HTML 正文内容
     * @param attachments 附件列表（MultipartFile）
     * @return 可直接发送的 JSON 对象
     */
    public static ObjectNode buildOutlookEmailJson(String from, String to, String subject, String body,
                                                   List<MultipartFile> attachments) throws Exception {
        ObjectNode message = mapper.createObjectNode();

        // Subject
        message.put("subject", subject);

        // Body
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.put("contentType", "HTML");
        bodyNode.put("content", body);
        message.set("body", bodyNode);

        // Recipients
        ArrayNode toRecipients = mapper.createArrayNode();
        ObjectNode recipient = mapper.createObjectNode();
        ObjectNode emailAddress = mapper.createObjectNode();
        emailAddress.put("address", to);
        recipient.set("emailAddress", emailAddress);
        toRecipients.add(recipient);
        message.set("toRecipients", toRecipients);

        // Attachments
        if (attachments != null && !attachments.isEmpty()) {
            ArrayNode attachmentArray = mapper.createArrayNode();

            for (MultipartFile file : attachments) {
                ObjectNode att = mapper.createObjectNode();
                att.put("@odata.type", "#microsoft.graph.fileAttachment");
                att.put("name", file.getOriginalFilename());
                att.put("contentType", file.getContentType());
                att.put("contentBytes", Base64.getEncoder().encodeToString(file.getBytes()));
                attachmentArray.add(att);
            }

            message.set("attachments", attachmentArray);
        }

        // Final structure
        ObjectNode root = mapper.createObjectNode();
        root.set("message", message);
        root.put("saveToSentItems", true);

        return root;
    }
    private String buildRawEmail(String from, String to, String subject, String body,
            List<MultipartFile> attachments) throws Exception {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
//        message.setText(body);

        // HTML 部分
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(body, "text/html; charset=utf-8");

        // 构造 multipart 邮件
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart); // 先加正文

        if (attachments != null) {
            // 添加所有附件
            for (MultipartFile file : attachments) {
                MimeBodyPart attachmentPart = new MimeBodyPart();

                attachmentPart.setFileName(file.getOriginalFilename());
                attachmentPart.setContent(file.getBytes(), file.getContentType());
                attachmentPart.setDisposition(Part.ATTACHMENT);

                multipart.addBodyPart(attachmentPart);
            }
        }

        message.setContent(multipart);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        message.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String extractSender(JsonNode fromNode) {
        if (fromNode == null || fromNode.isMissingNode()) return null;
        JsonNode emailAddress = fromNode.path("emailAddress");
        return emailAddress.path("address").asText(null);
    }

    private String extractRecipients(JsonNode json) throws JsonProcessingException {
        List<String> all = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (String field : Arrays.asList("toRecipients", "ccRecipients", "bccRecipients")) {
            JsonNode arrayNode = json.path(field);
            if (arrayNode.isArray()) {
                for (JsonNode node : arrayNode) {
                    String email = node.path("emailAddress").path("address").asText(null);
                    if (email != null) {
                        all.add(email);
                    }
                }
            }
        }

        return mapper.writeValueAsString(all); // 存成 JSON 字符串
    }

    private String extractCategories(JsonNode node) throws JsonProcessingException {
        if (node == null || !node.isArray()) return null;
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(node);
    }

    private Date parseReceivedTime(String receivedDateTimeStr) {
        if (receivedDateTimeStr == null) return null;
        return Date.from(java.time.OffsetDateTime.parse(receivedDateTimeStr).toInstant());
    }


    private RestTemplate createRestTemplateSupportingPatch() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    /**
     * 获取服务器所有文件夹名，并映射为标准文件夹名，返回 Map<服务器文件夹名, 标准文件夹名>
     */
    private Map<String, String> getFolderMapping(Store store) throws MessagingException {
        Map<String, String> folderMap = new LinkedHashMap<>();

        Folder defaultFolder = store.getDefaultFolder();
        // 递归获取所有文件夹
        List<Folder> allFolders = new ArrayList<>();
        fetchAllFolders(defaultFolder, allFolders);

        for (Folder folder : allFolders) {
            String fullName = folder.getFullName();
            // 取最后一部分目录名（避免多级目录）
            String[] parts = fullName.split(folder.getSeparator() + "");
            String lastPart = parts[parts.length - 1];

            // 如果是标准文件夹，则做映射，否则返回原名
            String mappedName = PRIORITY_FOLDERS.contains(lastPart.toUpperCase()) ? mapFolderName(lastPart) : lastPart;

            folderMap.put(fullName, mappedName);
        }

        return folderMap;
    }

    /**
     * 递归获取所有文件夹（包括子文件夹）
     */
    private static void fetchAllFolders(Folder folder, List<Folder> collector) throws MessagingException {
        Folder[] folders = folder.list();
        for (Folder f : folders) {
            collector.add(f);
            if ((f.getType() & Folder.HOLDS_FOLDERS) != 0) {
                fetchAllFolders(f, collector);
            }
        }
    }
    /**
     * 根据标准文件夹名，返回真实服务器文件夹名称
     * @param standardFolder 标准文件夹名，如 "INBOX", "SENT", "DRAFT", "SPAM", "TRASH"
     * @param folders 真实服务器文件夹数组
     * @return 真实文件夹名，如果没找到返回 null
     */
    private static String getRealFolderName(String standardFolder, Folder[] folders) throws MessagingException {
        if (standardFolder == null || folders == null) return null;

        String stdUpper = standardFolder.toUpperCase();

        // 先定义标准名对应的别名匹配列表（大小写不敏感）
        Map<String, List<String>> standardToAliases = new HashMap<>();
        standardToAliases.put("INBOX", Collections.singletonList("INBOX"));
        standardToAliases.put("SENT", Arrays.asList("SENT", "SENT ITEMS", "Sent Items", "SENT MAIL", "已发送", "sent"));
        standardToAliases.put("DRAFT", Arrays.asList("DRAFT", "DRAFTS", "草稿", "draft", "Drafts"));
        standardToAliases.put("SPAM", Arrays.asList("SPAM", "JUNK", "JUNK EMAIL", "垃圾邮件", "junk", "Junk E-mail"));
        standardToAliases.put("TRASH", Arrays.asList("TRASH", "DELETED ITEMS", "DELETED MESSAGES", "已删除", "已删除邮件", "垃圾箱", "deleted", "Trash", "Bin", "Rubbish"));

        List<String> aliases = standardToAliases.getOrDefault(stdUpper, Collections.singletonList(standardFolder));

        // 遍历服务器文件夹，尝试匹配
        for (Folder folder : folders) {
            String fullName = folder.getFullName();
            String[] parts = fullName.split(folder.getSeparator() + "");
            String lastPart = parts[parts.length - 1].toUpperCase();

            for (String alias : aliases) {
                if (lastPart.equalsIgnoreCase(alias)) {
                    return fullName; // 返回服务器文件夹的完整名称
                }
            }
        }

        // 没找到匹配的，返回null或者标准名
        return null;
    }

    /**
     * 解析 IMAP 服务器上的真实文件夹路径：metadata 配置 → RFC6154 特殊用途属性 → 递归别名匹配
     */
    private String resolveImapFolderName(Store store, String standardFolder, Map<String, Object> meta) throws MessagingException {
        if (standardFolder == null) {
            return null;
        }

        String metaKey = standardFolder.toLowerCase() + "Folder";
        if (meta != null && meta.get(metaKey) != null) {
            String configured = meta.get(metaKey).toString().trim();
            if (!configured.isEmpty()) {
                Folder configuredFolder = store.getFolder(configured);
                if (configuredFolder.exists()) {
                    return configuredFolder.getFullName();
                }
            }
        }

        List<Folder> allFolders = new ArrayList<>();
        fetchAllFolders(store.getDefaultFolder(), allFolders);

        String imapSpecialUse = mapStandardFolderToImapAttribute(standardFolder);
        if (imapSpecialUse != null) {
            for (Folder folder : allFolders) {
                if (!(folder instanceof IMAPFolder)) {
                    continue;
                }
                String[] attrs = ((IMAPFolder) folder).getAttributes();
                if (attrs == null) {
                    continue;
                }
                for (String attr : attrs) {
                    if (imapSpecialUse.equalsIgnoreCase(attr)) {
                        return folder.getFullName();
                    }
                }
            }
        }

        Folder[] folderArray = allFolders.toArray(new Folder[0]);
        String matched = getRealFolderName(standardFolder, folderArray);
        if (matched != null) {
            return matched;
        }

        return findFolderByKeyword(allFolders, standardFolder);
    }

    private String mapStandardFolderToImapAttribute(String standardFolder) {
        if (standardFolder == null) {
            return null;
        }
        switch (standardFolder.toUpperCase()) {
            case "INBOX":
                return "\\Inbox";
            case "SENT":
                return "\\Sent";
            case "DRAFT":
                return "\\Drafts";
            case "TRASH":
                return "\\Trash";
            case "SPAM":
                return "\\Junk";
            default:
                return null;
        }
    }

    private String findFolderByKeyword(List<Folder> folders, String standardFolder) throws MessagingException {
        if ("TRASH".equalsIgnoreCase(standardFolder)) {
            String[] keywords = {"trash", "deleted", "bin", "rubbish", "垃圾箱", "已删除"};
            for (Folder folder : folders) {
                String fullName = folder.getFullName().toLowerCase();
                for (String keyword : keywords) {
                    if (fullName.contains(keyword)) {
                        return folder.getFullName();
                    }
                }
            }
        }
        return null;
    }

    private String extractSnippet(Message message) throws Exception {
        Object content = message.getContent();

        if (content instanceof String) {
            String text = (String) content;
            return summarize(text);
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType("text/plain")) {
                    return summarize((String) part.getContent());
                } else if (part.isMimeType("text/html")) {
                    // Optionally strip HTML tags if no plain text
                    String html = (String) part.getContent();
                    String plainText = Jsoup.parse(html).text();
                    return summarize(plainText);
                }
            }
        }
        return "";
    }

    private String summarize(String text) {
        return text.length() > 100 ? text.substring(0, 100) + "..." : text;
    }

    private static String formatAddresses(Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return "";
        }
        List<String> formatted = new ArrayList<>();
        for (Address address : addresses) {
            if (address instanceof InternetAddress) {
                formatted.add(((InternetAddress) address).getAddress());
            } else if (address != null) {
                formatted.add(address.toString());
            }
        }
        return String.join(",", formatted);
    }
    private static String mapFolderName(String folder) {
        switch (folder.toUpperCase()) {
            case "INBOX":
                return "Inbox";
            case "SENT":
                return "SentItems";
            case "DRAFT":
                return "Drafts";
            case "SPAM":
                return "JunkEmail";
            case "TRASH":
                return "DeletedItems";
            default:
                return folder;
        }
    }
    private List<CrmEmailAttachments> extractAttachmentsFromMessage(String tenant, Object content, String localMessageId, String filePath) {
        List<CrmEmailAttachments> attachments = new ArrayList<>();

        try {

            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                extractMultipart(tenant, multipart, localMessageId, attachments, filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attachments;
    }

    private void extractMultipart(String tenant, Multipart multipart, String localMessageId, List<CrmEmailAttachments> attachments, String filePath) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);

            if (bodyPart.getContent() instanceof Multipart) {
                extractMultipart(tenant, (Multipart) bodyPart.getContent(), localMessageId, attachments, filePath);
            } else {
                String disposition = bodyPart.getDisposition();
                String filename = bodyPart.getFileName();

                String contentType = bodyPart.getContentType(); // e.g., "application/vnd.ms-excel; name=..."
                String mimeType = extractMimeType(contentType); // --> "application/vnd.ms-excel"

                String contentId = getHeaderValue(bodyPart, "Content-ID");


                boolean isAttachment = Part.ATTACHMENT.equalsIgnoreCase(disposition)
                        || (filename != null && !filename.trim().isEmpty());

                boolean isInlineImage = (Part.INLINE.equalsIgnoreCase(disposition) || disposition == null)
                        && contentId != null
                        && mimeType.startsWith("image/");

                if (isAttachment || isInlineImage) {
                    // 文件名可能是 MIME 编码的
                    String decodedFilename = (filename != null) ? MimeUtility.decodeText(filename) : "inline-" + UUID.randomUUID();

                    byte[] fileBytes = getBytesFromPart(bodyPart);

                    CrmEmailAttachments att = new CrmEmailAttachments();
                    att.setMessageId(localMessageId);
                    att.setAttachmentId(decodedFilename); // 可选
                    att.setFilename(decodedFilename);
                    att.setSize(fileBytes.length);
                    att.setContentId(getHeaderValue(bodyPart, "Content-ID"));
                    att.setIsInline(Part.INLINE.equalsIgnoreCase(disposition) ? 1 : 0);
                    att.setDownloaded(1);
                    att.setIsInline(isInlineImage ? 1 : 0);

                    att.setMimeType(mimeType);
                    try {
                        String storageUrl = FileUtils.writeBytesWithFilename(tenant, fileBytes, filePath, decodedFilename);

                        att.setStorageUrl(storageUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    attachments.add(att);
                }
            }
        }
    }

    private List<CrmEmailAttachments> extractAttachmentsFromNestedPart(String tenant, Multipart multipart, String localMessageId, String filePath) throws Exception {
        List<CrmEmailAttachments> list = new ArrayList<>();

        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);
            String disposition = part.getDisposition();
            String filename = part.getFileName();
            boolean isAttachment = Part.ATTACHMENT.equalsIgnoreCase(disposition) || (filename != null && !filename.isEmpty());

            if (isAttachment) {
                MimeBodyPart mimeBodyPart = (MimeBodyPart) part;
                File file = new File(filePath, filename);
                mimeBodyPart.saveFile(file);

                CrmEmailAttachments att = new CrmEmailAttachments();
                att.setMessageId(localMessageId);
                att.setAttachmentId(null);
                att.setFilename(filename);
                att.setMimeType(part.getContentType());
                att.setSize((int) file.length());
                att.setContentId(getHeaderValue(part, "Content-ID"));
                att.setIsInline(Part.INLINE.equalsIgnoreCase(disposition) ? 1 : 0);
                att.setDownloaded(1);
                att.setStorageUrl(file.getAbsolutePath());

                list.add(att);
            }

            if (part.getContent() instanceof Multipart) {
                list.addAll(extractAttachmentsFromNestedPart(tenant, (Multipart) part.getContent(), localMessageId, filePath));
            }
        }

        return list;
    }

    private String getHeaderValue(Part part, String headerName) throws MessagingException {
        String[] headers = part.getHeader(headerName);
        return (headers != null && headers.length > 0) ? headers[0] : null;
    }
    private String extractMimeType(String contentType) {
        if (contentType == null) return null;
        int semicolonIndex = contentType.indexOf(';');
        return semicolonIndex > 0 ? contentType.substring(0, semicolonIndex).trim() : contentType.trim();
    }
    private byte[] getBytesFromPart(Part part) throws Exception {
        try (InputStream is = part.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[8192];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
    private void handleMultipart(Multipart multipart, CrmEmailMessages email) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart part = multipart.getBodyPart(i);

            if (part.getContent() instanceof Multipart) {
                handleMultipart((Multipart) part.getContent(), email);
            } else {
                String disposition = part.getDisposition();
                String contentType = part.getContentType().toLowerCase();
                String contentId = getHeaderValue(part, "Content-ID");

                // 提取 HTML/Plain 文本
                if (part.isMimeType("text/html") && email.getBodyHtml() == null) {
                    email.setBodyHtml((String) part.getContent());
                } else if (part.isMimeType("text/plain") && email.getBodyPlain() == null) {
                    email.setBodyPlain((String) part.getContent());
                }

                // 标记附件（inline 图或真实附件）
                if ((Part.ATTACHMENT.equalsIgnoreCase(disposition)) ||
                        (Part.INLINE.equalsIgnoreCase(disposition) && contentId != null && contentType.startsWith("image/"))) {
                    email.setHasAttachment(1);
                }
            }
        }
    }

}
