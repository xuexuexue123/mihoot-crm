package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmOperatorMapper;
import com.ruoyi.crm.domain.CrmOperator;
import com.ruoyi.crm.service.ICrmOperatorService;

/**
 * 销售人员Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-29
 */
@Service
public class CrmOperatorServiceImpl implements ICrmOperatorService 
{
    @Autowired
    private CrmOperatorMapper crmOperatorMapper;

    /**
     * 查询销售人员
     * 
     * @param operatorId 销售人员主键
     * @return 销售人员
     */
    @Override
    public CrmOperator selectCrmOperatorByOperatorId(String operatorId)
    {
        return crmOperatorMapper.selectCrmOperatorByOperatorId(operatorId);
    }

    /**
     * 查询销售人员列表
     * 
     * @param crmOperator 销售人员
     * @return 销售人员
     */
    @Override
    public List<CrmOperator> selectCrmOperatorList(CrmOperator crmOperator)
    {
        return crmOperatorMapper.selectCrmOperatorList(crmOperator);
    }

    /**
     * 新增销售人员
     * 
     * @param crmOperator 销售人员
     * @return 结果
     */
    @Override
    public int insertCrmOperator(CrmOperator crmOperator)
    {
        crmOperator.setCreateTime(DateUtils.getNowDate());
        return crmOperatorMapper.insertCrmOperator(crmOperator);
    }

    /**
     * 修改销售人员
     * 
     * @param crmOperator 销售人员
     * @return 结果
     */
    @Override
    public int updateCrmOperator(CrmOperator crmOperator)
    {
        crmOperator.setUpdateTime(DateUtils.getNowDate());
        return crmOperatorMapper.updateCrmOperator(crmOperator);
    }

    /**
     * 批量删除销售人员
     * 
     * @param operatorIds 需要删除的销售人员主键
     * @return 结果
     */
    @Override
    public int deleteCrmOperatorByOperatorIds(String[] operatorIds)
    {
        return crmOperatorMapper.deleteCrmOperatorByOperatorIds(operatorIds);
    }

    /**
     * 删除销售人员信息
     * 
     * @param operatorId 销售人员主键
     * @return 结果
     */
    @Override
    public int deleteCrmOperatorByOperatorId(String operatorId)
    {
        return crmOperatorMapper.deleteCrmOperatorByOperatorId(operatorId);
    }
}
