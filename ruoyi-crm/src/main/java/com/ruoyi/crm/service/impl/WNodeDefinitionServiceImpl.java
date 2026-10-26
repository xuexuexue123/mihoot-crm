package com.ruoyi.crm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.WNodeDefinitionMapper;
import com.ruoyi.crm.domain.WNodeDefinition;
import com.ruoyi.crm.service.IWNodeDefinitionService;

/**
 * WNodeDefinitionService业务层处理
 * 
 * @author luohd
 * @date 2026-05-19
 */
@Service
public class WNodeDefinitionServiceImpl implements IWNodeDefinitionService
{
    @Autowired
    private WNodeDefinitionMapper wNodeDefinitionMapper;

    /**
     * 查询WNodeDefinition
     * 
     * @param id WNodeDefinition主键
     * @return WNodeDefinition
     */
    @Override
    public WNodeDefinition selectWNodeDefinitionById(String id)
    {
        return wNodeDefinitionMapper.selectWNodeDefinitionById(id);
    }

    /**
     * 查询WNodeDefinition列表
     * 
     * @param wNodeDefinition WNodeDefinition
     * @return WNodeDefinition
     */
    @Override
    public List<WNodeDefinition> selectWNodeDefinitionList(WNodeDefinition wNodeDefinition)
    {
        return wNodeDefinitionMapper.selectWNodeDefinitionList(wNodeDefinition);
    }

    /**
     * 新增WNodeDefinition
     * 
     * @param wNodeDefinition WNodeDefinition
     * @return 结果
     */
    @Override
    public int insertWNodeDefinition(WNodeDefinition wNodeDefinition)
    {
        return wNodeDefinitionMapper.insertWNodeDefinition(wNodeDefinition);
    }

    /**
     * 修改WNodeDefinition
     * 
     * @param wNodeDefinition WNodeDefinition
     * @return 结果
     */
    @Override
    public int updateWNodeDefinition(WNodeDefinition wNodeDefinition)
    {
        return wNodeDefinitionMapper.updateWNodeDefinition(wNodeDefinition);
    }

    /**
     * 批量删除WNodeDefinition
     * 
     * @param ids 需要删除的WNodeDefinition主键
     * @return 结果
     */
    @Override
    public int deleteWNodeDefinitionByIds(String[] ids)
    {
        return wNodeDefinitionMapper.deleteWNodeDefinitionByIds(ids);
    }

    /**
     * 删除WNodeDefinition信息
     * 
     * @param id WNodeDefinition主键
     * @return 结果
     */
    @Override
    public int deleteWNodeDefinitionById(String id)
    {
        return wNodeDefinitionMapper.deleteWNodeDefinitionById(id);
    }
}
