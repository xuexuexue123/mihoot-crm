package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmStagehistoryMapper;
import com.ruoyi.crm.domain.CrmStagehistory;
import com.ruoyi.crm.service.ICrmStagehistoryService;

/**
 * 阶段记录Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-07
 */
@Service
public class CrmStagehistoryServiceImpl implements ICrmStagehistoryService 
{
    @Autowired
    private CrmStagehistoryMapper crmStagehistoryMapper;

    /**
     * 查询阶段记录
     * 
     * @param shId 阶段记录主键
     * @return 阶段记录
     */
    @Override
    public CrmStagehistory selectCrmStagehistoryByShId(String shId)
    {
        return crmStagehistoryMapper.selectCrmStagehistoryByShId(shId);
    }

    /**
     * 查询阶段记录列表
     * 
     * @param crmStagehistory 阶段记录
     * @return 阶段记录
     */
    @Override
    public List<CrmStagehistory> selectCrmStagehistoryList(CrmStagehistory crmStagehistory)
    {
        return crmStagehistoryMapper.selectCrmStagehistoryList(crmStagehistory);
    }

    /**
     * 新增阶段记录
     * 
     * @param crmStagehistory 阶段记录
     * @return 结果
     */
    @Override
    public int insertCrmStagehistory(CrmStagehistory crmStagehistory)
    {
        crmStagehistory.setCreateTime(DateUtils.getNowDate());
        return crmStagehistoryMapper.insertCrmStagehistory(crmStagehistory);
    }

    /**
     * 修改阶段记录
     * 
     * @param crmStagehistory 阶段记录
     * @return 结果
     */
    @Override
    public int updateCrmStagehistory(CrmStagehistory crmStagehistory)
    {
        crmStagehistory.setUpdateTime(DateUtils.getNowDate());
        return crmStagehistoryMapper.updateCrmStagehistory(crmStagehistory);
    }

    /**
     * 批量删除阶段记录
     * 
     * @param shIds 需要删除的阶段记录主键
     * @return 结果
     */
    @Override
    public int deleteCrmStagehistoryByShIds(String[] shIds)
    {
        return crmStagehistoryMapper.deleteCrmStagehistoryByShIds(shIds);
    }

    /**
     * 删除阶段记录信息
     * 
     * @param shId 阶段记录主键
     * @return 结果
     */
    @Override
    public int deleteCrmStagehistoryByShId(String shId)
    {
        return crmStagehistoryMapper.deleteCrmStagehistoryByShId(shId);
    }
}
