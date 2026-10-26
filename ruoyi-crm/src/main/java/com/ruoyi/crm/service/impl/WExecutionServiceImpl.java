package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.WExecutionMapper;
import com.ruoyi.crm.domain.WExecution;
import com.ruoyi.crm.service.IWExecutionService;

/**
 * WExecutionService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WExecutionServiceImpl implements IWExecutionService
{
    @Autowired
    private WExecutionMapper wExecutionMapper;

    /**
     * 查询WExecution
     * 
     * @param id WExecution主键
     * @return WExecution
     */
    @Override
    public WExecution selectWExecutionById(String id)
    {
        return wExecutionMapper.selectWExecutionById(id);
    }

    /**
     * 查询WExecution列表
     * 
     * @param wExecution WExecution
     * @return WExecution
     */
    @Override
    public List<WExecution> selectWExecutionList(WExecution wExecution)
    {
        return wExecutionMapper.selectWExecutionList(wExecution);
    }

    /**
     * 新增WExecution
     * 
     * @param wExecution WExecution
     * @return 结果
     */
    @Override
    public int insertWExecution(WExecution wExecution)
    {
        return wExecutionMapper.insertWExecution(wExecution);
    }

    /**
     * 修改WExecution
     * 
     * @param wExecution WExecution
     * @return 结果
     */
    @Override
    public int updateWExecution(WExecution wExecution)
    {
        return wExecutionMapper.updateWExecution(wExecution);
    }

    /**
     * 批量删除WExecution
     * 
     * @param ids 需要删除的WExecution主键
     * @return 结果
     */
    @Override
    public int deleteWExecutionByIds(String[] ids)
    {
        return wExecutionMapper.deleteWExecutionByIds(ids);
    }

    /**
     * 删除WExecution信息
     * 
     * @param id WExecution主键
     * @return 结果
     */
    @Override
    public int deleteWExecutionById(String id)
    {
        return wExecutionMapper.deleteWExecutionById(id);
    }
}
