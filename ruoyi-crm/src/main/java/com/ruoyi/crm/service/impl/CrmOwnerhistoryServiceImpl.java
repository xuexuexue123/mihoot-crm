package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmOwnerhistoryMapper;
import com.ruoyi.crm.domain.CrmOwnerhistory;
import com.ruoyi.crm.service.ICrmOwnerhistoryService;

/**
 * owner记录Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-20
 */
@Service
public class CrmOwnerhistoryServiceImpl implements ICrmOwnerhistoryService 
{
    @Autowired
    private CrmOwnerhistoryMapper crmOwnerhistoryMapper;

    /**
     * 查询owner记录
     * 
     * @param ohId owner记录主键
     * @return owner记录
     */
    @Override
    public CrmOwnerhistory selectCrmOwnerhistoryByOhId(String ohId)
    {
        return crmOwnerhistoryMapper.selectCrmOwnerhistoryByOhId(ohId);
    }

    /**
     * 查询owner记录列表
     * 
     * @param crmOwnerhistory owner记录
     * @return owner记录
     */
    @Override
    public List<CrmOwnerhistory> selectCrmOwnerhistoryList(CrmOwnerhistory crmOwnerhistory)
    {
        return crmOwnerhistoryMapper.selectCrmOwnerhistoryList(crmOwnerhistory);
    }

    /**
     * 查询指定Target的历史记录
     * @param crmOwnerhistory
     * @return
     */
    @Override
    public List<CrmOwnerhistory> selectCrmOwnerhistoryListByTarget(CrmOwnerhistory crmOwnerhistory)
    {
        return crmOwnerhistoryMapper.selectCrmOwnerhistoryListByTarget(crmOwnerhistory);
    }
    /**
     * 新增owner记录
     * 
     * @param crmOwnerhistory owner记录
     * @return 结果
     */
    @Override
    public int insertCrmOwnerhistory(CrmOwnerhistory crmOwnerhistory)
    {
        crmOwnerhistory.setCreateTime(DateUtils.getNowDate());
        return crmOwnerhistoryMapper.insertCrmOwnerhistory(crmOwnerhistory);
    }

    /**
     * 修改owner记录
     * 
     * @param crmOwnerhistory owner记录
     * @return 结果
     */
    @Override
    public int updateCrmOwnerhistory(CrmOwnerhistory crmOwnerhistory)
    {
        crmOwnerhistory.setUpdateTime(DateUtils.getNowDate());
        return crmOwnerhistoryMapper.updateCrmOwnerhistory(crmOwnerhistory);
    }

    /**
     * 批量删除owner记录
     * 
     * @param ohIds 需要删除的owner记录主键
     * @return 结果
     */
    @Override
    public int deleteCrmOwnerhistoryByOhIds(String[] ohIds)
    {
        return crmOwnerhistoryMapper.deleteCrmOwnerhistoryByOhIds(ohIds);
    }

    /**
     * 删除owner记录信息
     * 
     * @param ohId owner记录主键
     * @return 结果
     */
    @Override
    public int deleteCrmOwnerhistoryByOhId(String ohId)
    {
        return crmOwnerhistoryMapper.deleteCrmOwnerhistoryByOhId(ohId);
    }
}
