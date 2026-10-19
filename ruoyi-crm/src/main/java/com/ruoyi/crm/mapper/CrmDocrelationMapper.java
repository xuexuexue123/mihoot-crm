package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmDocrelation;

/**
 * 文档关系Mapper接口
 * 
 * @author luohd
 * @date 2024-02-02
 */
public interface CrmDocrelationMapper 
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
     * 删除文档关系
     * 
     * @param relateId 文档关系主键
     * @return 结果
     */
    public int deleteCrmDocrelationByRelateId(String relateId);

    /**
     * 批量删除文档关系
     * 
     * @param relateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmDocrelationByRelateIds(String[] relateIds);
}
