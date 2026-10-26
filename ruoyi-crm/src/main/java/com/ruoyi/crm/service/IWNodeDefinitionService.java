package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WNodeDefinition;

/**
 * WNodeDefinitionService接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface IWNodeDefinitionService
{
    /**
     * 查询WNodeDefinition
     * 
     * @param id WNodeDefinition主键
     * @return WNodeDefinition
     */
    public WNodeDefinition selectWNodeDefinitionById(String id);

    /**
     * 查询WNodeDefinition列表
     * 
     * @param wNodeDefinition WNodeDefinition
     * @return WNodeDefinition集合
     */
    public List<WNodeDefinition> selectWNodeDefinitionList(WNodeDefinition wNodeDefinition);

    /**
     * 新增WNodeDefinition
     * 
     * @param wNodeDefinition WNodeDefinition
     * @return 结果
     */
    public int insertWNodeDefinition(WNodeDefinition wNodeDefinition);

    /**
     * 修改WNodeDefinition
     * 
     * @param wNodeDefinition WNodeDefinition
     * @return 结果
     */
    public int updateWNodeDefinition(WNodeDefinition wNodeDefinition);

    /**
     * 批量删除WNodeDefinition
     * 
     * @param ids 需要删除的WNodeDefinition主键集合
     * @return 结果
     */
    public int deleteWNodeDefinitionByIds(String[] ids);

    /**
     * 删除WNodeDefinition信息
     * 
     * @param id WNodeDefinition主键
     * @return 结果
     */
    public int deleteWNodeDefinitionById(String id);
}
