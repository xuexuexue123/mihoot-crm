package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmTerritoryMapper;
import com.ruoyi.crm.domain.CrmTerritory;
import com.ruoyi.crm.service.ICrmTerritoryService;

/**
 * 销售区域Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-19
 */
@Service
public class CrmTerritoryServiceImpl implements ICrmTerritoryService 
{
    @Autowired
    private CrmTerritoryMapper crmTerritoryMapper;

    /**
     * 查询销售区域
     * 
     * @param territoryId 销售区域主键
     * @return 销售区域
     */
    @Override
    public CrmTerritory selectCrmTerritoryByTerritoryId(String territoryId)
    {
        return crmTerritoryMapper.selectCrmTerritoryByTerritoryId(territoryId);
    }

    /**
     * 查询销售区域列表
     * 
     * @param crmTerritory 销售区域
     * @return 销售区域
     */
    @Override
    public List<CrmTerritory> selectCrmTerritoryList(CrmTerritory crmTerritory)
    {
        return crmTerritoryMapper.selectCrmTerritoryList(crmTerritory);
    }

    /**
     * 新增销售区域
     * 
     * @param crmTerritory 销售区域
     * @return 结果
     */
    @Override
    public int insertCrmTerritory(CrmTerritory crmTerritory)
    {
        crmTerritory.setCreateTime(DateUtils.getNowDate());
        return crmTerritoryMapper.insertCrmTerritory(crmTerritory);
    }

    /**
     * 修改销售区域
     * 
     * @param crmTerritory 销售区域
     * @return 结果
     */
    @Override
    public int updateCrmTerritory(CrmTerritory crmTerritory)
    {
        crmTerritory.setUpdateTime(DateUtils.getNowDate());
        return crmTerritoryMapper.updateCrmTerritory(crmTerritory);
    }

    /**
     * 批量删除销售区域
     * 
     * @param territoryIds 需要删除的销售区域主键
     * @return 结果
     */
    @Override
    public int deleteCrmTerritoryByTerritoryIds(String[] territoryIds)
    {
        return crmTerritoryMapper.deleteCrmTerritoryByTerritoryIds(territoryIds);
    }

    /**
     * 删除销售区域信息
     * 
     * @param territoryId 销售区域主键
     * @return 结果
     */
    @Override
    public int deleteCrmTerritoryByTerritoryId(String territoryId)
    {
        return crmTerritoryMapper.deleteCrmTerritoryByTerritoryId(territoryId);
    }
}
