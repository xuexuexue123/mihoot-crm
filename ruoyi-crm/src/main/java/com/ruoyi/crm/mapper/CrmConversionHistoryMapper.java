package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmConversionHistory;

/**
 * 转化历史Mapper接口
 * 
 * @author luohd
 * @date 2025-04-29
 */
public interface CrmConversionHistoryMapper 
{
    /**
     * 查询转化历史
     * 
     * @param id 转化历史主键
     * @return 转化历史
     */
    public CrmConversionHistory selectCrmConversionHistoryById(String id);

    /**
     * 查询转化历史列表
     * 
     * @param crmConversionHistory 转化历史
     * @return 转化历史集合
     */
    public List<CrmConversionHistory> selectCrmConversionHistoryList(CrmConversionHistory crmConversionHistory);

    /**
     * 新增转化历史
     * 
     * @param crmConversionHistory 转化历史
     * @return 结果
     */
    public int insertCrmConversionHistory(CrmConversionHistory crmConversionHistory);

    /**
     * 修改转化历史
     * 
     * @param crmConversionHistory 转化历史
     * @return 结果
     */
    public int updateCrmConversionHistory(CrmConversionHistory crmConversionHistory);

    /**
     * 删除转化历史
     * 
     * @param id 转化历史主键
     * @return 结果
     */
    public int deleteCrmConversionHistoryById(String id);

    /**
     * 批量删除转化历史
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmConversionHistoryByIds(String[] ids);
}
