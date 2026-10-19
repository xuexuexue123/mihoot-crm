package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmEmailMessages;

/**
 * 邮件Mapper接口
 * 
 * @author luohd
 * @date 2025-05-14
 */
public interface CrmEmailMessagesMapper 
{
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
     * @param id 邮件主键
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
     * 删除邮件
     * 
     * @param id 邮件主键
     * @return 结果
     */
    public int deleteCrmEmailMessagesById(String id);

    /**
     * 批量删除邮件
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmEmailMessagesByIds(String[] ids);
}
