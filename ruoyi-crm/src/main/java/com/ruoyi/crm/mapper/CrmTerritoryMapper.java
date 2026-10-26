package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmTerritory;

/**
 * 销售区域Mapper接口
 * 
 * @author luohd
 * @date 2024-01-19
 */
public interface CrmTerritoryMapper 
{
    /**
     * 查询销售区域
     * 
     * @param territoryId 销售区域主键
     * @return 销售区域
     */
    public CrmTerritory selectCrmTerritoryByTerritoryId(String territoryId);

    /**
     * 查询销售区域列表
     * 
     * @param crmTerritory 销售区域
     * @return 销售区域集合
     */
    public List<CrmTerritory> selectCrmTerritoryList(CrmTerritory crmTerritory);

    /**
     * 新增销售区域
     * 
     * @param crmTerritory 销售区域
     * @return 结果
     */
    public int insertCrmTerritory(CrmTerritory crmTerritory);

    /**
     * 修改销售区域
     * 
     * @param crmTerritory 销售区域
     * @return 结果
     */
    public int updateCrmTerritory(CrmTerritory crmTerritory);

    /**
     * 删除销售区域
     * 
     * @param territoryId 销售区域主键
     * @return 结果
     */
    public int deleteCrmTerritoryByTerritoryId(String territoryId);

    /**
     * 批量删除销售区域
     * 
     * @param territoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmTerritoryByTerritoryIds(String[] territoryIds);
}
