package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmContactrelation;

/**
 * 关联关系Mapper接口
 * 
 * @author luohd
 * @date 2024-02-14
 */
public interface CrmContactrelationMapper 
{
    /**
     * 查询关联关系
     * 
     * @param relateId 关联关系主键
     * @return 关联关系
     */
    public CrmContactrelation selectCrmContactrelationByRelateId(String relateId);

    /**
     * 查询关联关系列表
     * 
     * @param crmContactrelation 关联关系
     * @return 关联关系集合
     */
    public List<CrmContactrelation> selectCrmContactrelationList(CrmContactrelation crmContactrelation);

    /**
     * 新增关联关系
     * 
     * @param crmContactrelation 关联关系
     * @return 结果
     */
    public int insertCrmContactrelation(CrmContactrelation crmContactrelation);

    /**
     * 修改关联关系
     * 
     * @param crmContactrelation 关联关系
     * @return 结果
     */
    public int updateCrmContactrelation(CrmContactrelation crmContactrelation);

    /**
     * 删除关联关系
     * 
     * @param relateId 关联关系主键
     * @return 结果
     */
    public int deleteCrmContactrelationByRelateId(String relateId);

    /**
     * 删除关联关系By Contact Id
     *
     * @param crmContactrelation 关联关系
     * @return 结果
     */
    public int deleteCrmContactrelationByContactId(CrmContactrelation crmContactrelation);

    /**
     * 批量删除关联关系
     * 
     * @param relateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmContactrelationByRelateIds(String[] relateIds);
}
