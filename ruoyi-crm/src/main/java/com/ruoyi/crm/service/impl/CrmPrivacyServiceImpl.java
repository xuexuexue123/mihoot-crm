package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmPrivacyMapper;
import com.ruoyi.crm.domain.CrmPrivacy;
import com.ruoyi.crm.service.ICrmPrivacyService;

/**
 * 专属信息Service业务层处理
 * 
 * @author luohd
 * @date 2024-02-22
 */
@Service
public class CrmPrivacyServiceImpl implements ICrmPrivacyService 
{
    @Autowired
    private CrmPrivacyMapper crmPrivacyMapper;

    /**
     * 查询专属信息
     * 
     * @param privacyId 专属信息主键
     * @return 专属信息
     */
    @Override
    public CrmPrivacy selectCrmPrivacyByPrivacyId(String privacyId)
    {
        return crmPrivacyMapper.selectCrmPrivacyByPrivacyId(privacyId);
    }

    /**
     * 查询专属信息列表
     * 
     * @param crmPrivacy 专属信息
     * @return 专属信息
     */
    @Override
    public List<CrmPrivacy> selectCrmPrivacyList(CrmPrivacy crmPrivacy)
    {
        return crmPrivacyMapper.selectCrmPrivacyList(crmPrivacy);
    }

    /**
     * 新增专属信息
     * 
     * @param crmPrivacy 专属信息
     * @return 结果
     */
    @Override
    public int insertCrmPrivacy(CrmPrivacy crmPrivacy)
    {
        crmPrivacy.setCreateTime(DateUtils.getNowDate());
        return crmPrivacyMapper.insertCrmPrivacy(crmPrivacy);
    }

    /**
     * 修改专属信息
     * 
     * @param crmPrivacy 专属信息
     * @return 结果
     */
    @Override
    public int updateCrmPrivacy(CrmPrivacy crmPrivacy)
    {
        crmPrivacy.setUpdateTime(DateUtils.getNowDate());
        return crmPrivacyMapper.updateCrmPrivacy(crmPrivacy);
    }

    /**
     * 批量删除专属信息
     * 
     * @param privacyIds 需要删除的专属信息主键
     * @return 结果
     */
    @Override
    public int deleteCrmPrivacyByPrivacyIds(String[] privacyIds)
    {
        return crmPrivacyMapper.deleteCrmPrivacyByPrivacyIds(privacyIds);
    }

    /**
     * 删除专属信息信息
     * 
     * @param privacyId 专属信息主键
     * @return 结果
     */
    @Override
    public int deleteCrmPrivacyByPrivacyId(String privacyId)
    {
        return crmPrivacyMapper.deleteCrmPrivacyByPrivacyId(privacyId);
    }
}
