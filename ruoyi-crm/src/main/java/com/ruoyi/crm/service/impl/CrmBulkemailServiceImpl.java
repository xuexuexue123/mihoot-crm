package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmBulkemailMapper;
import com.ruoyi.crm.domain.CrmBulkemail;
import com.ruoyi.crm.service.ICrmBulkemailService;

/**
 * 批量邮件Service业务层处理
 * 
 * @author luohd
 * @date 2024-09-24
 */
@Service
public class CrmBulkemailServiceImpl implements ICrmBulkemailService 
{
    @Autowired
    private CrmBulkemailMapper crmBulkemailMapper;

    /**
     * 查询批量邮件
     * 
     * @param bulkId 批量邮件主键
     * @return 批量邮件
     */
    @Override
    public CrmBulkemail selectCrmBulkemailByBulkId(String bulkId)
    {
        return crmBulkemailMapper.selectCrmBulkemailByBulkId(bulkId);
    }

    /**
     * 查询批量邮件列表
     * 
     * @param crmBulkemail 批量邮件
     * @return 批量邮件
     */
    @Override
    public List<CrmBulkemail> selectCrmBulkemailList(CrmBulkemail crmBulkemail)
    {
        return crmBulkemailMapper.selectCrmBulkemailList(crmBulkemail);
    }

    /**
     * 新增批量邮件
     * 
     * @param crmBulkemail 批量邮件
     * @return 结果
     */
    @Override
    public int insertCrmBulkemail(CrmBulkemail crmBulkemail)
    {
        crmBulkemail.setCreateTime(DateUtils.getNowDate());
        return crmBulkemailMapper.insertCrmBulkemail(crmBulkemail);
    }

    /**
     * 修改批量邮件
     * 
     * @param crmBulkemail 批量邮件
     * @return 结果
     */
    @Override
    public int updateCrmBulkemail(CrmBulkemail crmBulkemail)
    {
        crmBulkemail.setUpdateTime(DateUtils.getNowDate());
        return crmBulkemailMapper.updateCrmBulkemail(crmBulkemail);
    }

    /**
     * 批量删除批量邮件
     * 
     * @param bulkIds 需要删除的批量邮件主键
     * @return 结果
     */
    @Override
    public int deleteCrmBulkemailByBulkIds(String[] bulkIds)
    {
        return crmBulkemailMapper.deleteCrmBulkemailByBulkIds(bulkIds);
    }

    /**
     * 删除批量邮件信息
     * 
     * @param bulkId 批量邮件主键
     * @return 结果
     */
    @Override
    public int deleteCrmBulkemailByBulkId(String bulkId)
    {
        return crmBulkemailMapper.deleteCrmBulkemailByBulkId(bulkId);
    }
}
