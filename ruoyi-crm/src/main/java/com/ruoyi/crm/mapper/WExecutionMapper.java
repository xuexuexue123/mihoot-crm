package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.WExecution;

/**
 * WExecutionMapper接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface WExecutionMapper
{
    /**
     * 查询WExecution
     * 
     * @param id WExecution主键
     * @return WExecution
     */
    public WExecution selectWExecutionById(String id);

    /**
     * 查询WExecution列表
     * 
     * @param wExecution WExecution
     * @return WExecution集合
     */
    public List<WExecution> selectWExecutionList(WExecution wExecution);

    /**
     * 新增WExecution
     * 
     * @param wExecution WExecution
     * @return 结果
     */
    public int insertWExecution(WExecution wExecution);

    /**
     * 修改WExecution
     * 
     * @param wExecution WExecution
     * @return 结果
     */
    public int updateWExecution(WExecution wExecution);

    /**
     * 删除WExecution
     * 
     * @param id WExecution主键
     * @return 结果
     */
    public int deleteWExecutionById(String id);

    /**
     * 批量删除WExecution
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWExecutionByIds(String[] ids);
}
