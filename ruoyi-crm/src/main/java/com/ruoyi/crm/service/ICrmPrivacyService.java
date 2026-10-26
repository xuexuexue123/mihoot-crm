package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmPrivacy;

/**
 * 专属信息Service接口
 * 
 * @author luohd
 * @date 2024-02-22
 */
public interface ICrmPrivacyService 
{
    /**
     * 查询专属信息
     * 
     * @param privacyId 专属信息主键
     * @return 专属信息
     */
    public CrmPrivacy selectCrmPrivacyByPrivacyId(String privacyId);

    /**
     * 查询专属信息列表
     * 
     * @param crmPrivacy 专属信息
     * @return 专属信息集合
     */
    public List<CrmPrivacy> selectCrmPrivacyList(CrmPrivacy crmPrivacy);

    /**
     * 新增专属信息
     * 
     * @param crmPrivacy 专属信息
     * @return 结果
     */
    public int insertCrmPrivacy(CrmPrivacy crmPrivacy);

    /**
     * 修改专属信息
     * 
     * @param crmPrivacy 专属信息
     * @return 结果
     */
    public int updateCrmPrivacy(CrmPrivacy crmPrivacy);

    /**
     * 批量删除专属信息
     * 
     * @param privacyIds 需要删除的专属信息主键集合
     * @return 结果
     */
    public int deleteCrmPrivacyByPrivacyIds(String[] privacyIds);

    /**
     * 删除专属信息信息
     * 
     * @param privacyId 专属信息主键
     * @return 结果
     */
    public int deleteCrmPrivacyByPrivacyId(String privacyId);
}
