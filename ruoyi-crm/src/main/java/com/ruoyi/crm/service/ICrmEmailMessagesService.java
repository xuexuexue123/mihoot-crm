package com.ruoyi.crm.service;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.crm.domain.CrmEmailMessages;
import com.ruoyi.crm.domain.CrmOauthConnections;
import org.springframework.web.multipart.MultipartFile;

/**
 * 邮件Service接口
 * 
 * @author luohd
 * @date 2025-05-14
 */
public interface ICrmEmailMessagesService 
{

    /**
     * ✅ 获取邮件详情
     */
    public CrmEmailMessages getMessageDetail(String provider, String hostName, String tenant, String accessToken, String messageId, String userId, String externalName);

    /**
     * ✅ 发送邮件（Base64 编码），发送成功后写入已发邮件及附件记录
     */
    public int sendEmail(String provider, String accessToken, String userId, String tenant, String externalName,
                         String from, String to, String subject, String msgBody, List<MultipartFile> fileList);

    /**
     * SMTP/IMAP 发送成功后写入已发邮件及附件记录
     */
    public int persistSentEmailWithAttachments(String userId, String tenant, String externalName, String provider,
                                               String from, String to, String subject, String msgBody,
                                               List<MultipartFile> fileList, String externalMessageId, String threadId);

    /**
     * ✅ 发送邮件（Base64 编码）
     */
    public int forwardEmail(String provider, String accessToken, String messageId, String from, String to, String subject, String msgBody, List<MultipartFile> fileList);

    /**
     * ✅ 获取所有标签
     */
    public JsonNode listLabels(String accessToken);

    /**
     * ✅ 修改邮件标签（添加/移除标签，如 UNREAD, INBOX）
     */
    public JsonNode modifyMessageLabels(String accessToken, String messageId,
                                        List<String> addLabels, List<String> removeLabels);

    /**
     * 设置邮件为已读 / 未读
     * @param accessToken
     * @param messageId
     * @param markAsRead
     */
    public int modifyMessageLabel(String provider, String accessToken, String messageId, boolean markAsRead);

    /**
     * 移动邮件到回收站
     * @param accessToken
     * @param messageId
     */
    public int moveToTrash(String provider, String accessToken, String messageId, String userId, String externalName);

    /**
     * 彻底删除邮件
     * @param accessToken
     * @param messageId
     */
    public int deleteMessage(String provider, String accessToken, String messageId);

    /**
     * Sync email messages
     * @param accessToken
     * @param userId
     */
    public int syncMessages(String accessToken, String userId, String externalName);

    /**
     *
     * @param accessToken
     * @param userId
     * @param days  需要同步的天数
     * @return
     */
    public int listRecentMessages(String accessToken, String userId, int days, String externalName);

    /**
     * 获取Outlook的邮件
     * @param accessToken
     * @param userId
     * @param days
     * @param externalName
     * @return
     */
    public int listRecentOutlookEmails(String accessToken, String userId, int days, String externalName);


    /**
     * 获取指定邮箱目录最近14天的邮件metadata
     * @param conn
     * @throws Exception
     */
    public int listRecentIMAPEmails(String userId, String provider, int days, String externalName);

    /**
     * 查询邮件
     *
     * @param id 邮件主键
     * @return 邮件
     */
    public CrmEmailMessages selectCrmEmailMessagesById(String id);

    /**
     * 查询邮件是否已经被同步
     *
     * @param messageExternalId 邮件主键
     * @return 邮件
     */
    public CrmEmailMessages existsByMessageId(String messageExternalId);

    /**
     * 查询邮件列表
     * 
     * @param crmEmailMessages 邮件
     * @return 邮件集合
     */
    public List<CrmEmailMessages> selectCrmEmailMessagesList(CrmEmailMessages crmEmailMessages);

    /**
     * 新增邮件
     * 
     * @param crmEmailMessages 邮件
     * @return 结果
     */
    public int insertCrmEmailMessages(CrmEmailMessages crmEmailMessages);

    /**
     * 修改邮件
     * 
     * @param crmEmailMessages 邮件
     * @return 结果
     */
    public int updateCrmEmailMessages(CrmEmailMessages crmEmailMessages);

    /**
     * 批量删除邮件
     * 
     * @param ids 需要删除的邮件主键集合
     * @return 结果
     */
    public int deleteCrmEmailMessagesByIds(String[] ids);

    /**
     * 删除邮件信息
     * 
     * @param id 邮件主键
     * @return 结果
     */
    public int deleteCrmEmailMessagesById(String id);
}
