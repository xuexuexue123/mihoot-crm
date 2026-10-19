package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmQuoteoptionMapper;
import com.ruoyi.crm.domain.CrmQuoteoption;
import com.ruoyi.crm.service.ICrmQuoteoptionService;

/**
 * 报价产品Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-14
 */
@Service
public class CrmQuoteoptionServiceImpl implements ICrmQuoteoptionService 
{
    @Autowired
    private CrmQuoteoptionMapper crmQuoteoptionMapper;

    /**
     * 查询报价产品
     * 
     * @param optionId 报价产品主键
     * @return 报价产品
     */
    @Override
    public CrmQuoteoption selectCrmQuoteoptionByOptionId(String optionId)
    {
        return crmQuoteoptionMapper.selectCrmQuoteoptionByOptionId(optionId);
    }

    /**
     * 查询报价产品列表
     * 
     * @param crmQuoteoption 报价产品
     * @return 报价产品
     */
    @Override
    public List<CrmQuoteoption> selectCrmQuoteoptionList(CrmQuoteoption crmQuoteoption)
    {
        return crmQuoteoptionMapper.selectCrmQuoteoptionList(crmQuoteoption);
    }

    /**
     * 新增报价产品
     * 
     * @param crmQuoteoption 报价产品
     * @return 结果
     */
    @Override
    public int insertCrmQuoteoption(CrmQuoteoption crmQuoteoption)
    {
        crmQuoteoption.setCreateTime(DateUtils.getNowDate());
        return crmQuoteoptionMapper.insertCrmQuoteoption(crmQuoteoption);
    }

    /**
     * 修改报价产品
     * 
     * @param crmQuoteoption 报价产品
     * @return 结果
     */
    @Override
    public int updateCrmQuoteoption(CrmQuoteoption crmQuoteoption)
    {
        crmQuoteoption.setUpdateTime(DateUtils.getNowDate());
        return crmQuoteoptionMapper.updateCrmQuoteoption(crmQuoteoption);
    }

    /**
     * 批量删除报价产品
     * 
     * @param optionIds 需要删除的报价产品主键
     * @return 结果
     */
    @Override
    public int deleteCrmQuoteoptionByOptionIds(String[] optionIds)
    {
        return crmQuoteoptionMapper.deleteCrmQuoteoptionByOptionIds(optionIds);
    }

    /**
     * 删除报价产品信息
     * 
     * @param optionId 报价产品主键
     * @return 结果
     */
    @Override
    public int deleteCrmQuoteoptionByOptionId(String optionId)
    {
        return crmQuoteoptionMapper.deleteCrmQuoteoptionByOptionId(optionId);
    }
}
