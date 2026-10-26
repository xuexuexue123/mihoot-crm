package com.ruoyi.crm.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.crm.mapper.CrmOrgMapper;
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.service.ICrmOrgService;
import com.ruoyi.crm.service.support.RecordTriggerCrudHookSupport;

/**
 * 机构Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-13
 */
@Service
public class CrmOrgServiceImpl implements ICrmOrgService 
{
    @Autowired
    private CrmOrgMapper crmOrgMapper;

    @Autowired
    private RecordTriggerCrudHookSupport recordTriggerCrudHookSupport;

    /**
     * 查询机构
     * 
     * @param orgId 机构主键
     * @return 机构
     */
    @Override
    public CrmOrg selectCrmOrgByOrgId(String orgId)
    {
        return crmOrgMapper.selectCrmOrgByOrgId(orgId);
    }

    /**
     * 查询机构列表
     * 
     * @param crmOrg 机构
     * @return 机构
     */
    @Override
    public List<CrmOrg> selectCrmOrgList(CrmOrg crmOrg)
    {
        return crmOrgMapper.selectCrmOrgList(crmOrg);
    }

    /**
     * 查询本周新建机构列表
     *
     * @param crmOrg 机构
     * @return 机构
     */
    @Override
    public List<CrmOrg> selectCrmOrgListWeekly(CrmOrg crmOrg)
    {
        return crmOrgMapper.selectCrmOrgListWeekly(crmOrg);
    }

    /**
     * 新增机构
     * 
     * @param crmOrg 机构
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCrmOrg(CrmOrg crmOrg)
    {
        crmOrg.setCreateTime(DateUtils.getNowDate());
        int rows = crmOrgMapper.insertCrmOrg(crmOrg);
        if (rows > 0)
        {
            recordTriggerCrudHookSupport.afterCreate("organization", crmOrg.getOrgId(), crmOrg);
        }
        return rows;
    }

    /**
     * 修改机构
     * 
     * @param crmOrg 机构
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCrmOrg(CrmOrg crmOrg)
    {
        CrmOrg existing = crmOrgMapper.selectCrmOrgByOrgId(crmOrg.getOrgId());
        crmOrg.setUpdateTime(DateUtils.getNowDate());
        int rows = crmOrgMapper.updateCrmOrg(crmOrg);
        if (rows > 0)
        {
            CrmOrg updated = crmOrgMapper.selectCrmOrgByOrgId(crmOrg.getOrgId());
            recordTriggerCrudHookSupport.afterUpdate("organization", crmOrg.getOrgId(), updated, existing);
        }
        return rows;
    }

    /**
     * 批量删除机构
     * 
     * @param orgIds 需要删除的机构主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmOrgByOrgIds(String[] orgIds)
    {
        if (orgIds == null || orgIds.length == 0)
        {
            return 0;
        }
        java.util.List<CrmOrg> snapshots = new java.util.ArrayList<>();
        for (String orgId : orgIds)
        {
            CrmOrg existing = crmOrgMapper.selectCrmOrgByOrgId(orgId);
            if (existing != null)
            {
                snapshots.add(existing);
            }
        }
        int rows = crmOrgMapper.deleteCrmOrgByOrgIds(orgIds);
        if (rows > 0)
        {
            for (CrmOrg existing : snapshots)
            {
                recordTriggerCrudHookSupport.afterDelete("organization", existing.getOrgId(), existing);
            }
        }
        return rows;
    }

    /**
     * 删除机构信息
     * 
     * @param orgId 机构主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCrmOrgByOrgId(String orgId)
    {
        CrmOrg existing = crmOrgMapper.selectCrmOrgByOrgId(orgId);
        int rows = crmOrgMapper.deleteCrmOrgByOrgId(orgId);
        if (rows > 0 && existing != null)
        {
            recordTriggerCrudHookSupport.afterDelete("organization", orgId, existing);
        }
        return rows;
    }
}
