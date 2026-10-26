package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmOptyacceptanceMapper;
import com.ruoyi.crm.domain.CrmOptyacceptance;
import com.ruoyi.crm.service.ICrmOptyacceptanceService;

/**
 * 销售任务Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-04
 */
@Service
public class CrmOptyacceptanceServiceImpl implements ICrmOptyacceptanceService 
{
    @Autowired
    private CrmOptyacceptanceMapper crmOptyacceptanceMapper;

    /**
     * 查询销售任务
     * 
     * @param acceptId 销售任务主键
     * @return 销售任务
     */
    @Override
    public CrmOptyacceptance selectCrmOptyacceptanceByAcceptId(String acceptId)
    {
        return crmOptyacceptanceMapper.selectCrmOptyacceptanceByAcceptId(acceptId);
    }

    /**
     * 查询销售任务列表
     * 
     * @param crmOptyacceptance 销售任务
     * @return 销售任务
     */
    @Override
    public List<CrmOptyacceptance> selectCrmOptyacceptanceList(CrmOptyacceptance crmOptyacceptance)
    {
        return crmOptyacceptanceMapper.selectCrmOptyacceptanceList(crmOptyacceptance);
    }

    /**
     * 新增销售任务
     * 
     * @param crmOptyacceptance 销售任务
     * @return 结果
     */
    @Override
    public int insertCrmOptyacceptance(CrmOptyacceptance crmOptyacceptance)
    {
        crmOptyacceptance.setCreateTime(DateUtils.getNowDate());
        return crmOptyacceptanceMapper.insertCrmOptyacceptance(crmOptyacceptance);
    }

    /**
     * 修改销售任务
     * 
     * @param crmOptyacceptance 销售任务
     * @return 结果
     */
    @Override
    public int updateCrmOptyacceptance(CrmOptyacceptance crmOptyacceptance)
    {
        crmOptyacceptance.setUpdateTime(DateUtils.getNowDate());
        return crmOptyacceptanceMapper.updateCrmOptyacceptance(crmOptyacceptance);
    }

    /**
     * 批量删除销售任务
     * 
     * @param acceptIds 需要删除的销售任务主键
     * @return 结果
     */
    @Override
    public int deleteCrmOptyacceptanceByAcceptIds(String[] acceptIds)
    {
        return crmOptyacceptanceMapper.deleteCrmOptyacceptanceByAcceptIds(acceptIds);
    }

    /**
     * 删除销售任务信息
     * 
     * @param acceptId 销售任务主键
     * @return 结果
     */
    @Override
    public int deleteCrmOptyacceptanceByAcceptId(String acceptId)
    {
        return crmOptyacceptanceMapper.deleteCrmOptyacceptanceByAcceptId(acceptId);
    }
}
