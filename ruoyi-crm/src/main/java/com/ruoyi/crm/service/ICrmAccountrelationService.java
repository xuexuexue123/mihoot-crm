package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmAccountrelation;
import com.ruoyi.crm.domain.CrmAccountrelationEmail;

/**
 * 客户关系Service接口
 * 
 * @author luohd
 * @date 2024-05-19
 */
public interface ICrmAccountrelationService 
{
    /**
     * 查询客户关系
     * 
     * @param relateId 客户关系主键
     * @return 客户关系
     */
    public CrmAccountrelation selectCrmAccountrelationByRelateId(String relateId);

    /**
     * 查询客户关系列表
     * 
     * @param crmAccountrelation 客户关系
     * @return 客户关系集合
     */
    public List<CrmAccountrelation> selectCrmAccountrelationList(CrmAccountrelation crmAccountrelation);

    /**
     * 新增客户关系
     * 
     * @param crmAccountrelation 客户关系
     * @return 结果
     */
    public int insertCrmAccountrelation(CrmAccountrelation crmAccountrelation);

    /**
     * 修改客户关系
     * 
     * @param crmAccountrelation 客户关系
     * @return 结果
     */
    public int updateCrmAccountrelation(CrmAccountrelationEmail crmAccountrelation) throws Exception;

    /**
     * 根据基金信息 删除关系
     *
     * @param crmAccountrelation 客户关系主键
     * @return 结果
     */
    public int deleteCrmAccountrelationByAccountId(CrmAccountrelation crmAccountrelation);

    /**
     * 批量删除客户关系
     * 
     * @param relateIds 需要删除的客户关系主键集合
     * @return 结果
     */
    public int deleteCrmAccountrelationByRelateIds(String[] relateIds);

    /**
     * 删除客户关系信息
     * 
     * @param relateId 客户关系主键
     * @return 结果
     */
    public int deleteCrmAccountrelationByRelateId(String relateId);
}
