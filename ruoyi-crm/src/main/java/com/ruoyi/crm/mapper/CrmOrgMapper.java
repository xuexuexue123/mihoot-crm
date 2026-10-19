package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmOrg;

/**
 * 机构Mapper接口
 * 
 * @author luohd
 * @date 2024-01-13
 */
public interface CrmOrgMapper 
{
    /**
     * 查询机构
     * 
     * @param orgId 机构主键
     * @return 机构
     */
    public CrmOrg selectCrmOrgByOrgId(String orgId);

    /**
     * 查询机构列表
     * 
     * @param crmOrg 机构
     * @return 机构集合
     */
    public List<CrmOrg> selectCrmOrgList(CrmOrg crmOrg);

    /**
     * 查询本周新建机构列表
     *
     * @param crmOrg 机构
     * @return 机构集合
     */
    public List<CrmOrg> selectCrmOrgListWeekly(CrmOrg crmOrg);

    /**
     * 新增机构
     * 
     * @param crmOrg 机构
     * @return 结果
     */
    public int insertCrmOrg(CrmOrg crmOrg);

    /**
     * 修改机构
     * 
     * @param crmOrg 机构
     * @return 结果
     */
    public int updateCrmOrg(CrmOrg crmOrg);

    /**
     * 删除机构
     * 
     * @param orgId 机构主键
     * @return 结果
     */
    public int deleteCrmOrgByOrgId(String orgId);

    /**
     * 批量删除机构
     * 
     * @param orgIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmOrgByOrgIds(String[] orgIds);
}
