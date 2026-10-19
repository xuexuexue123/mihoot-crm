package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmDdMapper;
import com.ruoyi.crm.domain.CrmDd;
import com.ruoyi.crm.service.ICrmDdService;

/**
 * 尽职调查Service业务层处理
 * 
 * @author luohd
 * @date 2024-05-01
 */
@Service
public class CrmDdServiceImpl implements ICrmDdService 
{
    @Autowired
    private CrmDdMapper crmDdMapper;

    /**
     * 查询尽职调查
     * 
     * @param ddId 尽职调查主键
     * @return 尽职调查
     */
    @Override
    public CrmDd selectCrmDdByDdId(String ddId)
    {
        return crmDdMapper.selectCrmDdByDdId(ddId);
    }

    /**
     * 查询尽职调查列表
     * 
     * @param crmDd 尽职调查
     * @return 尽职调查
     */
    @Override
    public List<CrmDd> selectCrmDdList(CrmDd crmDd)
    {
        return crmDdMapper.selectCrmDdList(crmDd);
    }

    /**
     * 新增尽职调查
     * 
     * @param crmDd 尽职调查
     * @return 结果
     */
    @Override
    public int insertCrmDd(CrmDd crmDd)
    {
        crmDd.setCreateTime(DateUtils.getNowDate());
        return crmDdMapper.insertCrmDd(crmDd);
    }

    /**
     * 修改尽职调查
     * 
     * @param crmDd 尽职调查
     * @return 结果
     */
    @Override
    public int updateCrmDd(CrmDd crmDd)
    {
        crmDd.setUpdateTime(DateUtils.getNowDate());
        return crmDdMapper.updateCrmDd(crmDd);
    }

    /**
     * 批量删除尽职调查
     * 
     * @param ddIds 需要删除的尽职调查主键
     * @return 结果
     */
    @Override
    public int deleteCrmDdByDdIds(String[] ddIds)
    {
        return crmDdMapper.deleteCrmDdByDdIds(ddIds);
    }

    /**
     * 删除尽职调查信息
     * 
     * @param ddId 尽职调查主键
     * @return 结果
     */
    @Override
    public int deleteCrmDdByDdId(String ddId)
    {
        return crmDdMapper.deleteCrmDdByDdId(ddId);
    }
}
