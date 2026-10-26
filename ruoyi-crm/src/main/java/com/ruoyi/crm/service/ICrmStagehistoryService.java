package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmStagehistory;

/**
 * 阶段记录Service接口
 * 
 * @author luohd
 * @date 2024-02-07
 */
public interface ICrmStagehistoryService 
{
    /**
     * 查询阶段记录
     * 
     * @param shId 阶段记录主键
     * @return 阶段记录
     */
    public CrmStagehistory selectCrmStagehistoryByShId(String shId);

    /**
     * 查询阶段记录列表
     * 
     * @param crmStagehistory 阶段记录
     * @return 阶段记录集合
     */
    public List<CrmStagehistory> selectCrmStagehistoryList(CrmStagehistory crmStagehistory);

    /**
     * 新增阶段记录
     * 
     * @param crmStagehistory 阶段记录
     * @return 结果
     */
    public int insertCrmStagehistory(CrmStagehistory crmStagehistory);

    /**
     * 修改阶段记录
     * 
     * @param crmStagehistory 阶段记录
     * @return 结果
     */
    public int updateCrmStagehistory(CrmStagehistory crmStagehistory);

    /**
     * 批量删除阶段记录
     * 
     * @param shIds 需要删除的阶段记录主键集合
     * @return 结果
     */
    public int deleteCrmStagehistoryByShIds(String[] shIds);

    /**
     * 删除阶段记录信息
     * 
     * @param shId 阶段记录主键
     * @return 结果
     */
    public int deleteCrmStagehistoryByShId(String shId);
}
