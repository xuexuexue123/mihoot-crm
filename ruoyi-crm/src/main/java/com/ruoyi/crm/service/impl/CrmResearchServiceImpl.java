package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmResearchMapper;
import com.ruoyi.crm.domain.CrmResearch;
import com.ruoyi.crm.service.ICrmResearchService;

/**
 * 研报Service业务层处理
 * 
 * @author luohd
 * @date 2024-10-03
 */
@Service
public class CrmResearchServiceImpl implements ICrmResearchService 
{
    @Autowired
    private CrmResearchMapper crmResearchMapper;

    /**
     * 查询研报
     * 
     * @param researchId 研报主键
     * @return 研报
     */
    @Override
    public CrmResearch selectCrmResearchByResearchId(String researchId)
    {
        return crmResearchMapper.selectCrmResearchByResearchId(researchId);
    }

    /**
     * 查询研报列表
     * 
     * @param crmResearch 研报
     * @return 研报
     */
    @Override
    public List<CrmResearch> selectCrmResearchList(CrmResearch crmResearch)
    {
        return crmResearchMapper.selectCrmResearchList(crmResearch);
    }

    /**
     * 新增研报
     * 
     * @param crmResearch 研报
     * @return 结果
     */
    @Override
    public int insertCrmResearch(CrmResearch crmResearch)
    {
        crmResearch.setCreateTime(DateUtils.getNowDate());
        return crmResearchMapper.insertCrmResearch(crmResearch);
    }

    /**
     * 修改研报
     * 
     * @param crmResearch 研报
     * @return 结果
     */
    @Override
    public int updateCrmResearch(CrmResearch crmResearch)
    {
        crmResearch.setUpdateTime(DateUtils.getNowDate());
        return crmResearchMapper.updateCrmResearch(crmResearch);
    }

    /**
     * 批量删除研报
     * 
     * @param researchIds 需要删除的研报主键
     * @return 结果
     */
    @Override
    public int deleteCrmResearchByResearchIds(String[] researchIds)
    {
        return crmResearchMapper.deleteCrmResearchByResearchIds(researchIds);
    }

    /**
     * 删除研报信息
     * 
     * @param researchId 研报主键
     * @return 结果
     */
    @Override
    public int deleteCrmResearchByResearchId(String researchId)
    {
        return crmResearchMapper.deleteCrmResearchByResearchId(researchId);
    }
}
