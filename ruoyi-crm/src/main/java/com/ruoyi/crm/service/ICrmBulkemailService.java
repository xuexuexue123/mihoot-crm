package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmBulkemail;

/**
 * 批量邮件Service接口
 * 
 * @author luohd
 * @date 2024-09-24
 */
public interface ICrmBulkemailService 
{
    /**
     * 查询批量邮件
     * 
     * @param bulkId 批量邮件主键
     * @return 批量邮件
     */
    public CrmBulkemail selectCrmBulkemailByBulkId(String bulkId);

    /**
     * 查询批量邮件列表
     * 
     * @param crmBulkemail 批量邮件
     * @return 批量邮件集合
     */
    public List<CrmBulkemail> selectCrmBulkemailList(CrmBulkemail crmBulkemail);

    /**
     * 新增批量邮件
     * 
     * @param crmBulkemail 批量邮件
     * @return 结果
     */
    public int insertCrmBulkemail(CrmBulkemail crmBulkemail);

    /**
     * 修改批量邮件
     * 
     * @param crmBulkemail 批量邮件
     * @return 结果
     */
    public int updateCrmBulkemail(CrmBulkemail crmBulkemail);

    /**
     * 批量删除批量邮件
     * 
     * @param bulkIds 需要删除的批量邮件主键集合
     * @return 结果
     */
    public int deleteCrmBulkemailByBulkIds(String[] bulkIds);

    /**
     * 删除批量邮件信息
     * 
     * @param bulkId 批量邮件主键
     * @return 结果
     */
    public int deleteCrmBulkemailByBulkId(String bulkId);
}
