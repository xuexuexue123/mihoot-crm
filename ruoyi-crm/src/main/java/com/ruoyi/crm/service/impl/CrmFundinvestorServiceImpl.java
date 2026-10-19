package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmFundinvestorMapper;
import com.ruoyi.crm.domain.CrmFundinvestor;
import com.ruoyi.crm.service.ICrmFundinvestorService;

/**
 * 投资人Service业务层处理
 * 
 * @author luohd
 * @date 2024-06-17
 */
@Service
public class CrmFundinvestorServiceImpl implements ICrmFundinvestorService 
{
    @Autowired
    private CrmFundinvestorMapper crmFundinvestorMapper;

    /**
     * 查询投资人
     * 
     * @param investorId 投资人主键
     * @return 投资人
     */
    @Override
    public CrmFundinvestor selectCrmFundinvestorByInvestorId(String investorId)
    {
        return crmFundinvestorMapper.selectCrmFundinvestorByInvestorId(investorId);
    }

    /**
     * 查询投资人列表
     * 
     * @param crmFundinvestor 投资人
     * @return 投资人
     */
    @Override
    public List<CrmFundinvestor> selectCrmFundinvestorList(CrmFundinvestor crmFundinvestor)
    {
        return crmFundinvestorMapper.selectCrmFundinvestorList(crmFundinvestor);
    }

    /**
     * 新增投资人
     * 
     * @param crmFundinvestor 投资人
     * @return 结果
     */
    @Override
    public int insertCrmFundinvestor(CrmFundinvestor crmFundinvestor)
    {
        crmFundinvestor.setCreateTime(DateUtils.getNowDate());
        return crmFundinvestorMapper.insertCrmFundinvestor(crmFundinvestor);
    }

    /**
     * 修改投资人
     * 
     * @param crmFundinvestor 投资人
     * @return 结果
     */
    @Override
    public int updateCrmFundinvestor(CrmFundinvestor crmFundinvestor)
    {
        crmFundinvestor.setUpdateTime(DateUtils.getNowDate());
        return crmFundinvestorMapper.updateCrmFundinvestor(crmFundinvestor);
    }

    /**
     * 批量删除投资人
     * 
     * @param investorIds 需要删除的投资人主键
     * @return 结果
     */
    @Override
    public int deleteCrmFundinvestorByInvestorIds(String[] investorIds)
    {
        return crmFundinvestorMapper.deleteCrmFundinvestorByInvestorIds(investorIds);
    }

    /**
     * 删除投资人信息
     * 
     * @param investorId 投资人主键
     * @return 结果
     */
    @Override
    public int deleteCrmFundinvestorByInvestorId(String investorId)
    {
        return crmFundinvestorMapper.deleteCrmFundinvestorByInvestorId(investorId);
    }
}
