package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmPartner;

/**
 * 合作伙伴Mapper接口
 * 
 * @author luohd
 * @date 2024-03-28
 */
public interface CrmPartnerMapper 
{
    /**
     * 查询合作伙伴
     * 
     * @param partnerId 合作伙伴主键
     * @return 合作伙伴
     */
    public CrmPartner selectCrmPartnerByPartnerId(String partnerId);

    /**
     * 查询合作伙伴列表
     * 
     * @param crmPartner 合作伙伴
     * @return 合作伙伴集合
     */
    public List<CrmPartner> selectCrmPartnerList(CrmPartner crmPartner);

    /**
     * 新增合作伙伴
     * 
     * @param crmPartner 合作伙伴
     * @return 结果
     */
    public int insertCrmPartner(CrmPartner crmPartner);

    /**
     * 修改合作伙伴
     * 
     * @param crmPartner 合作伙伴
     * @return 结果
     */
    public int updateCrmPartner(CrmPartner crmPartner);

    /**
     * 删除合作伙伴
     * 
     * @param partnerId 合作伙伴主键
     * @return 结果
     */
    public int deleteCrmPartnerByPartnerId(String partnerId);

    /**
     * 批量删除合作伙伴
     * 
     * @param partnerIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmPartnerByPartnerIds(String[] partnerIds);
}
