package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmDocrelationMapper;
import com.ruoyi.crm.domain.CrmDocrelation;
import com.ruoyi.crm.service.ICrmDocrelationService;

/**
 * 文档关系Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-02
 */
@Service
public class CrmDocrelationServiceImpl implements ICrmDocrelationService 
{
    @Autowired
    private CrmDocrelationMapper crmDocrelationMapper;

    /**
     * 查询文档关系
     * 
     * @param relateId 文档关系主键
     * @return 文档关系
     */
    @Override
    public CrmDocrelation selectCrmDocrelationByRelateId(String relateId)
    {
        return crmDocrelationMapper.selectCrmDocrelationByRelateId(relateId);
    }

    /**
     * 查询文档关系列表
     * 
     * @param crmDocrelation 文档关系
     * @return 文档关系
     */
    @Override
    public List<CrmDocrelation> selectCrmDocrelationList(CrmDocrelation crmDocrelation)
    {
        return crmDocrelationMapper.selectCrmDocrelationList(crmDocrelation);
    }

    /**
     * 新增文档关系
     * 
     * @param crmDocrelation 文档关系
     * @return 结果
     */
    @Override
    public int insertCrmDocrelation(CrmDocrelation crmDocrelation)
    {
        crmDocrelation.setCreateTime(DateUtils.getNowDate());
        return crmDocrelationMapper.insertCrmDocrelation(crmDocrelation);
    }

    /**
     * 修改文档关系
     * 
     * @param crmDocrelation 文档关系
     * @return 结果
     */
    @Override
    public int updateCrmDocrelation(CrmDocrelation crmDocrelation)
    {
        crmDocrelation.setUpdateTime(DateUtils.getNowDate());
        return crmDocrelationMapper.updateCrmDocrelation(crmDocrelation);
    }

    /**
     * 批量删除文档关系
     * 
     * @param relateIds 需要删除的文档关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmDocrelationByRelateIds(String[] relateIds)
    {
        return crmDocrelationMapper.deleteCrmDocrelationByRelateIds(relateIds);
    }

    /**
     * 删除文档关系信息
     * 
     * @param relateId 文档关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmDocrelationByRelateId(String relateId)
    {
        return crmDocrelationMapper.deleteCrmDocrelationByRelateId(relateId);
    }
}
