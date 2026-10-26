package com.ruoyi.crm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.crm.mapper.CrmPartnerMapper;
import com.ruoyi.crm.domain.CrmPartner;
import com.ruoyi.crm.service.ICrmPartnerService;

/**
 * 合作伙伴Service业务层处理
 * 
 * @author luohd
 * @date 2024-03-28
 */
@Service
public class CrmPartnerServiceImpl implements ICrmPartnerService 
{
    @Autowired
    private CrmPartnerMapper crmPartnerMapper;

    /**
     * 查询合作伙伴
     * 
     * @param partnerId 合作伙伴主键
     * @return 合作伙伴
     */
    @Override
    public CrmPartner selectCrmPartnerByPartnerId(String partnerId)
    {
        return crmPartnerMapper.selectCrmPartnerByPartnerId(partnerId);
    }

    /**
     * 查询合作伙伴列表
     * 
     * @param crmPartner 合作伙伴
     * @return 合作伙伴
     */
    @Override
    public List<CrmPartner> selectCrmPartnerList(CrmPartner crmPartner)
    {
        return crmPartnerMapper.selectCrmPartnerList(crmPartner);
    }

    /**
     * 新增合作伙伴
     * 
     * @param crmPartner 合作伙伴
     * @return 结果
     */
    @Override
    public int insertCrmPartner(CrmPartner crmPartner)
    {
        crmPartner.setCreateTime(DateUtils.getNowDate());
        return crmPartnerMapper.insertCrmPartner(crmPartner);
    }

    /**
     * 修改合作伙伴
     * 
     * @param crmPartner 合作伙伴
     * @return 结果
     */
    @Override
    public int updateCrmPartner(CrmPartner crmPartner)
    {
        crmPartner.setUpdateTime(DateUtils.getNowDate());
        return crmPartnerMapper.updateCrmPartner(crmPartner);
    }

    /**
     * 批量删除合作伙伴
     * 
     * @param partnerIds 需要删除的合作伙伴主键
     * @return 结果
     */
    @Override
    public int deleteCrmPartnerByPartnerIds(String[] partnerIds)
    {
        return crmPartnerMapper.deleteCrmPartnerByPartnerIds(partnerIds);
    }

    /**
     * 删除合作伙伴信息
     * 
     * @param partnerId 合作伙伴主键
     * @return 结果
     */
    @Override
    public int deleteCrmPartnerByPartnerId(String partnerId)
    {
        return crmPartnerMapper.deleteCrmPartnerByPartnerId(partnerId);
    }
}
