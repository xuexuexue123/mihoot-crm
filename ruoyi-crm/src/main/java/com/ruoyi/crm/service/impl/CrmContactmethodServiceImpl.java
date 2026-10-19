package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmContactmethodMapper;
import com.ruoyi.crm.domain.CrmContactmethod;
import com.ruoyi.crm.service.ICrmContactmethodService;

/**
 * 联系方式Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-16
 */
@Service
public class CrmContactmethodServiceImpl implements ICrmContactmethodService 
{
    @Autowired
    private CrmContactmethodMapper crmContactmethodMapper;

    /**
     * 查询联系方式
     * 
     * @param methodId 联系方式主键
     * @return 联系方式
     */
    @Override
    public CrmContactmethod selectCrmContactmethodByMethodId(String methodId)
    {
        return crmContactmethodMapper.selectCrmContactmethodByMethodId(methodId);
    }

    /**
     * 查询联系方式列表
     * 
     * @param crmContactmethod 联系方式
     * @return 联系方式
     */
    @Override
    public List<CrmContactmethod> selectCrmContactmethodList(CrmContactmethod crmContactmethod)
    {
        return crmContactmethodMapper.selectCrmContactmethodList(crmContactmethod);
    }

    /**
     * 新增联系方式
     * 
     * @param crmContactmethod 联系方式
     * @return 结果
     */
    @Override
    public int insertCrmContactmethod(CrmContactmethod crmContactmethod)
    {
        crmContactmethod.setCreateTime(DateUtils.getNowDate());
        return crmContactmethodMapper.insertCrmContactmethod(crmContactmethod);
    }

    /**
     * 修改联系方式
     * 
     * @param crmContactmethod 联系方式
     * @return 结果
     */
    @Override
    public int updateCrmContactmethod(CrmContactmethod crmContactmethod)
    {
        crmContactmethod.setUpdateTime(DateUtils.getNowDate());
        return crmContactmethodMapper.updateCrmContactmethod(crmContactmethod);
    }

    /**
     * 批量删除联系方式
     * 
     * @param methodIds 需要删除的联系方式主键
     * @return 结果
     */
    @Override
    public int deleteCrmContactmethodByMethodIds(String[] methodIds)
    {
        return crmContactmethodMapper.deleteCrmContactmethodByMethodIds(methodIds);
    }

    /**
     * 删除联系方式信息
     * 
     * @param methodId 联系方式主键
     * @return 结果
     */
    @Override
    public int deleteCrmContactmethodByMethodId(String methodId)
    {
        return crmContactmethodMapper.deleteCrmContactmethodByMethodId(methodId);
    }
}
