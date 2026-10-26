package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmEmailAttachments;

/**
 * 邮件附件Mapper接口
 * 
 * @author luohd
 * @date 2025-05-16
 */
public interface CrmEmailAttachmentsMapper 
{
    /**
     * 查询邮件附件
     * 
     * @param id 邮件附件主键
     * @return 邮件附件
     */
    public CrmEmailAttachments selectCrmEmailAttachmentsById(String id);

    /**
     * 查询邮件附件列表
     * 
     * @param crmEmailAttachments 邮件附件
     * @return 邮件附件集合
     */
    public List<CrmEmailAttachments> selectCrmEmailAttachmentsList(CrmEmailAttachments crmEmailAttachments);
//
//    /**
//     * 下载附件并更新CrmEmailAttachments
//     * @param messageId
//     * @param attachmentId
//     * @param accessToken
//     * @return
//     */
//    public byte[] downloadAttachment(String messageId, String attachmentId, String accessToken);

    /**
     * 新增邮件附件
     * 
     * @param crmEmailAttachments 邮件附件
     * @return 结果
     */
    public int insertCrmEmailAttachments(CrmEmailAttachments crmEmailAttachments);

    /**
     * batch insert
     * @param crmEmailAttachments
     * @return
     */
    public int batchInsertCrmEmailAttachments(List<CrmEmailAttachments> crmEmailAttachments);

    /**
     * 修改邮件附件
     * 
     * @param crmEmailAttachments 邮件附件
     * @return 结果
     */
    public int updateCrmEmailAttachments(CrmEmailAttachments crmEmailAttachments);

    /**
     * 删除邮件附件
     * 
     * @param id 邮件附件主键
     * @return 结果
     */
    public int deleteCrmEmailAttachmentsById(String id);

    /**
     * 批量删除邮件附件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmEmailAttachmentsByIds(String[] ids);

    /**
     * 按本地邮件 ID 删除附件（重新拉取详情前清理旧记录）
     */
    public int deleteCrmEmailAttachmentsByMessageId(String messageId);
}
