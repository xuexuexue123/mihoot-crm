package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmActrelateMapper;
import com.ruoyi.crm.domain.CrmActrelate;
import com.ruoyi.crm.service.ICrmActrelateService;

/**
 * 关联的活动Service业务层处理
 * 
 * @author luohd
 * @date 2024-01-22
 */
@Service
public class CrmActrelateServiceImpl implements ICrmActrelateService 
{
    @Autowired
    private CrmActrelateMapper crmActrelateMapper;

    /**
     * 查询关联的活动
     * 
     * @param relateId 关联的活动主键
     * @return 关联的活动
     */
    @Override
    public CrmActrelate selectCrmActrelateByRelateId(String relateId)
    {
        return crmActrelateMapper.selectCrmActrelateByRelateId(relateId);
    }

    /**
     * 查询关联的活动列表
     * 
     * @param crmActrelate 关联的活动
     * @return 关联的活动
     */
    @Override
    public List<CrmActrelate> selectCrmActrelateList(CrmActrelate crmActrelate)
    {
        return crmActrelateMapper.selectCrmActrelateList(crmActrelate);
    }

    /**
     * 新增关联的活动
     * 
     * @param crmActrelate 关联的活动
     * @return 结果
     */
    @Override
    public int insertCrmActrelate(CrmActrelate crmActrelate)
    {
        crmActrelate.setCreateTime(DateUtils.getNowDate());
        return crmActrelateMapper.insertCrmActrelate(crmActrelate);
    }

    /**
     * 修改关联的活动
     * 
     * @param crmActrelate 关联的活动
     * @return 结果
     */
    @Override
    public int updateCrmActrelate(CrmActrelate crmActrelate)
    {
        crmActrelate.setUpdateTime(DateUtils.getNowDate());
        return crmActrelateMapper.updateCrmActrelate(crmActrelate);
    }

    /**
     * 批量删除关联的活动
     * 
     * @param relateIds 需要删除的关联的活动主键
     * @return 结果
     */
    @Override
    public int deleteCrmActrelateByRelateIds(String[] relateIds)
    {
        return crmActrelateMapper.deleteCrmActrelateByRelateIds(relateIds);
    }

    /**
     * 删除关联的活动信息
     * 
     * @param relateId 关联的活动主键
     * @return 结果
     */
    @Override
    public int deleteCrmActrelateByRelateId(String relateId)
    {
        return crmActrelateMapper.deleteCrmActrelateByRelateId(relateId);
    }
}
