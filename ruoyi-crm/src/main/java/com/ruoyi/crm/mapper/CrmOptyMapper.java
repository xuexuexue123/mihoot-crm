package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmOpty;

/**
 * 销售机会Mapper接口
 * 
 * @author luohd
 * @date 2024-01-30
 */
public interface CrmOptyMapper 
{
    /**
     * 查询销售机会
     * 
     * @param optyId 销售机会主键
     * @return 销售机会
     */
    public CrmOpty selectCrmOptyByOptyId(String optyId);

    /**
     * 根据基金ID查询基金投资的项目
     *
     * @param accountId
     * @return
     */
    public List<CrmOpty> selectCrmOptyListByAccountId(String accountId);

    /**
     * 查询销售机会列表
     * 
     * @param crmOpty 销售机会
     * @return 销售机会集合
     */
    public List<CrmOpty> selectCrmOptyList(CrmOpty crmOpty);

    /**
     * 查询指定销售阶段的销售机会列表
     *
     * @param crmOpty 销售阶段
     * @return 销售机会集合
     */
    public List<CrmOpty> selectCrmOptyListByStages(CrmOpty crmOpty);


    /**
     * 查询本周销售机会列表
     *
     * @param crmOpty 销售机会
     * @return 销售机会集合
     */
    public List<CrmOpty> selectCrmOptyListWeekly(CrmOpty crmOpty);

    /**
     * 查询本周已赢单销售机会列表
     *
     * @param crmOpty 销售机会
     * @return 销售机会集合
     */
    public List<CrmOpty> selectWonCrmOptyListWeekly(CrmOpty crmOpty);

    /**
     * 新增销售机会
     * 
     * @param crmOpty 销售机会
     * @return 结果
     */
    public int insertCrmOpty(CrmOpty crmOpty);

    /**
     * 修改销售机会
     * 
     * @param crmOpty 销售机会
     * @return 结果
     */
    public int updateCrmOpty(CrmOpty crmOpty);

    /**
     * 删除销售机会
     * 
     * @param optyId 销售机会主键
     * @return 结果
     */
    public int deleteCrmOptyByOptyId(String optyId);

    /**
     * 批量删除销售机会
     * 
     * @param optyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmOptyByOptyIds(String[] optyIds);

    /**
     * 查询联系人下最新一条未关闭商机。
     */
    CrmOpty selectLatestActiveByContactId(
            @Param("contactId") String contactId,
            @Param("excludedStages") List<String> excludedStages);

    /**
     * 查询N天前赢单商机关联的所有联系人（去重）。
     * 通过 crm_optyrelation (target_type='contact') 关联 crm_opty 和 crm_contact。
     */
    List<CrmContact> selectContactsByWonOptyDaysAgo(@Param("daysAfter") Integer daysAfter);
}
