package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmFundpaidMapper;
import com.ruoyi.crm.domain.CrmFundpaid;
import com.ruoyi.crm.service.ICrmFundpaidService;

/**
 * 基金投资Service业务层处理
 * 
 * @author luohd
 * @date 2024-07-09
 */
@Service
public class CrmFundpaidServiceImpl implements ICrmFundpaidService 
{
    @Autowired
    private CrmFundpaidMapper crmFundpaidMapper;

    /**
     * 查询基金投资
     * 
     * @param paidId 基金投资主键
     * @return 基金投资
     */
    @Override
    public CrmFundpaid selectCrmFundpaidByPaidId(String paidId)
    {
        return crmFundpaidMapper.selectCrmFundpaidByPaidId(paidId);
    }

    /**
     * 根据权益ID查询流水
     *
     * @param interestId
     * @return
     */
    @Override
    public List<CrmFundpaid> selectCrmFundpaidByInterestId(String interestId)
    {
        return crmFundpaidMapper.selectCrmFundpaidByInterestId(interestId);
    }
    /**
     * 查询基金投资列表
     * 
     * @param crmFundpaid 基金投资
     * @return 基金投资
     */
    @Override
    public List<CrmFundpaid> selectCrmFundpaidList(CrmFundpaid crmFundpaid)
    {
        return crmFundpaidMapper.selectCrmFundpaidList(crmFundpaid);
    }

    /**
     * 新增基金投资
     * 
     * @param crmFundpaid 基金投资
     * @return 结果
     */
    @Override
    public int insertCrmFundpaid(CrmFundpaid crmFundpaid)
    {
        crmFundpaid.setCreateTime(DateUtils.getNowDate());
        return crmFundpaidMapper.insertCrmFundpaid(crmFundpaid);
    }

    /**
     * 修改基金投资
     * 
     * @param crmFundpaid 基金投资
     * @return 结果
     */
    @Override
    public int updateCrmFundpaid(CrmFundpaid crmFundpaid)
    {
        crmFundpaid.setUpdateTime(DateUtils.getNowDate());
        return crmFundpaidMapper.updateCrmFundpaid(crmFundpaid);
    }

    /**
     * 批量删除基金投资
     * 
     * @param paidIds 需要删除的基金投资主键
     * @return 结果
     */
    @Override
    public int deleteCrmFundpaidByPaidIds(String[] paidIds)
    {
        return crmFundpaidMapper.deleteCrmFundpaidByPaidIds(paidIds);
    }

    /**
     * 删除基金投资信息
     * 
     * @param paidId 基金投资主键
     * @return 结果
     */
    @Override
    public int deleteCrmFundpaidByPaidId(String paidId)
    {
        return crmFundpaidMapper.deleteCrmFundpaidByPaidId(paidId);
    }
}
