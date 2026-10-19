package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmPulseMapper;
import com.ruoyi.crm.domain.CrmPulse;
import com.ruoyi.crm.service.ICrmPulseService;

/**
 * 动态Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-24
 */
@Service
public class CrmPulseServiceImpl implements ICrmPulseService 
{
    @Autowired
    private CrmPulseMapper crmPulseMapper;

    /**
     * 查询动态
     * 
     * @param pulseId 动态主键
     * @return 动态
     */
    @Override
    public CrmPulse selectCrmPulseByPulseId(String pulseId)
    {
        return crmPulseMapper.selectCrmPulseByPulseId(pulseId);
    }


    /**
     *
     * @param pulseIds
     * @return
     */
    public List<CrmPulse> selectCrmPulseByPulseIds(String[] pulseIds) {
        return crmPulseMapper.selectCrmPulseByPulseIds(pulseIds);
    }

    public List<CrmPulse> selectNoteByPulseIds(String[] pulseIds) {
        return crmPulseMapper.selectNoteByPulseIds(pulseIds);
    }


    /**
     * 查询动态列表
     * 
     * @param crmPulse 动态
     * @return 动态
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmPulse> selectCrmPulseList(CrmPulse crmPulse)
    {
        return crmPulseMapper.selectCrmPulseList(crmPulse);
    }

    /**
     * 查询本周动态列表
     *
     * @param crmPulse 动态
     * @return 动态
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmPulse> selectCrmPulseListWeekly(CrmPulse crmPulse)
    {
        return crmPulseMapper.selectCrmPulseListWeekly(crmPulse);
    }

    /**
     * 新增动态
     * 
     * @param crmPulse 动态
     * @return 结果
     */
    @Override
    public int insertCrmPulse(CrmPulse crmPulse)
    {
        crmPulse.setCreateTime(DateUtils.getNowDate());
        return crmPulseMapper.insertCrmPulse(crmPulse);
    }

    /**
     * 修改动态
     * 
     * @param crmPulse 动态
     * @return 结果
     */
    @Override
    public int updateCrmPulse(CrmPulse crmPulse)
    {
        crmPulse.setUpdateTime(DateUtils.getNowDate());
        return crmPulseMapper.updateCrmPulse(crmPulse);
    }

    /**
     * 批量删除动态
     * 
     * @param pulseIds 需要删除的动态主键
     * @return 结果
     */
    @Override
    public int deleteCrmPulseByPulseIds(String[] pulseIds)
    {
        return crmPulseMapper.deleteCrmPulseByPulseIds(pulseIds);
    }

    /**
     * 删除动态信息
     * 
     * @param pulseId 动态主键
     * @return 结果
     */
    @Override
    public int deleteCrmPulseByPulseId(String pulseId)
    {
        return crmPulseMapper.deleteCrmPulseByPulseId(pulseId);
    }
}
