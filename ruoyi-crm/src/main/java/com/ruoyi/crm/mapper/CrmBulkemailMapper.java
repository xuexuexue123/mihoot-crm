package com.ruoyi.crm.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmBulkemail;

/**
 * 批量邮件Mapper接口
 * 
 * @author luohd
 * @date 2024-09-24
 */
public interface CrmBulkemailMapper 
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
     * 删除批量邮件
     * 
     * @param bulkId 批量邮件主键
     * @return 结果
     */
    public int deleteCrmBulkemailByBulkId(String bulkId);

    /**
     * 批量删除批量邮件
     * 
     * @param bulkIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmBulkemailByBulkIds(String[] bulkIds);

    List<String> selectScheduledDue(@Param("now") Date now, @Param("limit") int limit);

    int tryTransitionStatus(@Param("bulkId") String bulkId,
            @Param("fromStatuses") List<String> fromStatuses,
            @Param("toStatus") String toStatus,
            @Param("sendMode") String sendMode,
            @Param("scheduledAt") Date scheduledAt,
            @Param("cancelledAt") Date cancelledAt);

    int updateSendResult(@Param("bulkId") String bulkId,
            @Param("status") String status,
            @Param("total") long total,
            @Param("fails") long fails,
            @Param("sentAt") Date sentAt);
}
