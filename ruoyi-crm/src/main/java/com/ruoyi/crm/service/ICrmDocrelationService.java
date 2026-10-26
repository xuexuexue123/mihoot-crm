package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmDocrelation;

/**
 * 文档关系Service接口
 * 
 * @author luohd
 * @date 2024-02-02
 */
public interface ICrmDocrelationService 
{
    /**
     * 查询文档关系
     * 
     * @param relateId 文档关系主键
     * @return 文档关系
     */
    public CrmDocrelation selectCrmDocrelationByRelateId(String relateId);

    /**
     * 查询文档关系列表
     * 
     * @param crmDocrelation 文档关系
     * @return 文档关系集合
     */
    public List<CrmDocrelation> selectCrmDocrelationList(CrmDocrelation crmDocrelation);

    /**
     * 新增文档关系
     * 
     * @param crmDocrelation 文档关系
     * @return 结果
     */
    public int insertCrmDocrelation(CrmDocrelation crmDocrelation);

    /**
     * 修改文档关系
     * 
     * @param crmDocrelation 文档关系
     * @return 结果
     */
    public int updateCrmDocrelation(CrmDocrelation crmDocrelation);

    /**
     * 批量删除文档关系
     * 
     * @param relateIds 需要删除的文档关系主键集合
     * @return 结果
     */
    public int deleteCrmDocrelationByRelateIds(String[] relateIds);

    /**
     * 删除文档关系信息
     * 
     * @param relateId 文档关系主键
     * @return 结果
     */
    public int deleteCrmDocrelationByRelateId(String relateId);
}
