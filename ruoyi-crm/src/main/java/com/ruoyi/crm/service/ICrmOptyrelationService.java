package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmOptyrelation;
import com.ruoyi.crm.domain.CrmOptyrelationEmail;

/**
 * 机会关系Service接口
 * 
 * @author luohd
 * @date 2024-02-07
 */
public interface ICrmOptyrelationService 
{
    /**
     * 查询机会关系
     * 
     * @param relateId 机会关系主键
     * @return 机会关系
     */
    public CrmOptyrelation selectCrmOptyrelationByRelateId(String relateId);

    /**
     * 查询机会关系列表
     * 
     * @param crmOptyrelation 机会关系
     * @return 机会关系集合
     */
    public List<CrmOptyrelation> selectCrmOptyrelationList(CrmOptyrelation crmOptyrelation);

    /**
     * 新增机会关系
     * 
     * @param crmOptyrelation 机会关系
     * @return 结果
     */
    public int insertCrmOptyrelation(CrmOptyrelation crmOptyrelation);

    /**
     * 修改机会关系
     * 
     * @param crmOptyrelation 机会关系
     * @return 结果
     */
    public int updateCrmOptyrelation(CrmOptyrelationEmail crmOptyrelation) throws Exception;

    /**
     * 批量删除机会关系
     * 
     * @param relateIds 需要删除的机会关系主键集合
     * @return 结果
     */
    public int deleteCrmOptyrelationByRelateIds(String[] relateIds);

    /**
     * 删除机会关系信息
     * 
     * @param relateId 机会关系主键
     * @return 结果
     */
    public int deleteCrmOptyrelationByRelateId(String relateId);
}
