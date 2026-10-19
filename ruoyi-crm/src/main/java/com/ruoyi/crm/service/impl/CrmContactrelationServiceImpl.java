package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmContactrelationMapper;
import com.ruoyi.crm.domain.CrmContactrelation;
import com.ruoyi.crm.service.ICrmContactrelationService;

/**
 * 关联关系Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-14
 */
@Service
public class CrmContactrelationServiceImpl implements ICrmContactrelationService 
{
    @Autowired
    private CrmContactrelationMapper crmContactrelationMapper;

    /**
     * 查询关联关系
     * 
     * @param relateId 关联关系主键
     * @return 关联关系
     */
    @Override
    public CrmContactrelation selectCrmContactrelationByRelateId(String relateId)
    {
        return crmContactrelationMapper.selectCrmContactrelationByRelateId(relateId);
    }

    /**
     * 查询关联关系列表
     * 
     * @param crmContactrelation 关联关系
     * @return 关联关系
     */
    @Override
    public List<CrmContactrelation> selectCrmContactrelationList(CrmContactrelation crmContactrelation)
    {
        return crmContactrelationMapper.selectCrmContactrelationList(crmContactrelation);
    }

    /**
     * 新增关联关系
     * 
     * @param crmContactrelation 关联关系
     * @return 结果
     */
    @Override
    public int insertCrmContactrelation(CrmContactrelation crmContactrelation)
    {
        crmContactrelation.setCreateTime(DateUtils.getNowDate());
        return crmContactrelationMapper.insertCrmContactrelation(crmContactrelation);
    }

    /**
     * 修改关联关系
     * 
     * @param crmContactrelation 关联关系
     * @return 结果
     */
    @Override
    public int updateCrmContactrelation(CrmContactrelation crmContactrelation)
    {
        crmContactrelation.setUpdateTime(DateUtils.getNowDate());
        return crmContactrelationMapper.updateCrmContactrelation(crmContactrelation);
    }

    /**
     * 批量删除关联关系
     * 
     * @param relateIds 需要删除的关联关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmContactrelationByRelateIds(String[] relateIds)
    {
        return crmContactrelationMapper.deleteCrmContactrelationByRelateIds(relateIds);
    }

    /**
     * 删除关联关系By Contact Id
     *
     * @param crmContactrelation 关联关系
     * @return 结果
     */
    @Override
    public int deleteCrmContactrelationByContactId(CrmContactrelation crmContactrelation) {
        return crmContactrelationMapper.deleteCrmContactrelationByContactId(crmContactrelation);
    }

    /**
     * 删除关联关系信息
     * 
     * @param relateId 关联关系主键
     * @return 结果
     */
    @Override
    public int deleteCrmContactrelationByRelateId(String relateId)
    {
        return crmContactrelationMapper.deleteCrmContactrelationByRelateId(relateId);
    }
}
