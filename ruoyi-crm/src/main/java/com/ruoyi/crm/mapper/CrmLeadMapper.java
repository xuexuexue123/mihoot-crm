package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmAccountrelation;
import com.ruoyi.crm.domain.CrmLead;

/**
 * 业务线索Mapper接口
 * 
 * @author luohd
 * @date 2024-01-30
 */
public interface CrmLeadMapper 
{
    /**
     * 查询业务线索
     * 
     * @param leadId 业务线索主键
     * @return 业务线索
     */
    public CrmLead selectCrmLeadByLeadId(String leadId);

    /**
     * 行锁加载 Lead（转化幂等）
     */
    public CrmLead selectByLeadIdForUpdate(String leadId);

    /**
     * 查询业务线索列表
     * 
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadList(CrmLead crmLead);

    /**
     * 查询跟基金相关的查询项目列表
     *
     * @param crmAccountrelation 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListByRelation(CrmAccountrelation crmAccountrelation);


    /**
     * 根据业务条件，查询业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListByCriteria(CrmLead crmLead);

    /**
     * 查询本周业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListWeekly(CrmLead crmLead);

    /**
     * 查询本周已转化的业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListWeeklyConverted(CrmLead crmLead);

    /**
     * 查询最近三月业务线索列表
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListQuarterly(CrmLead crmLead);

    /**
     * 查询最所有业务线索列表导出
     *
     * @param crmLead 业务线索
     * @return 业务线索集合
     */
    public List<CrmLead> selectCrmLeadListExport(CrmLead crmLead);

    /**
     * 查找所有没有被转化，也没有被合并的线索
     * @param crmLead
     * @return
     */
    public List<CrmLead> selectCrmLeadPendingList(CrmLead crmLead);

    /**
     * 新增业务线索
     * 
     * @param crmLead 业务线索
     * @return 结果
     */
    public int insertCrmLead(CrmLead crmLead);

    /**
     * 修改业务线索
     * 
     * @param crmLead 业务线索
     * @return 结果
     */
    public int updateCrmLead(CrmLead crmLead);

    /**
     * 删除业务线索
     * 
     * @param leadId 业务线索主键
     * @return 结果
     */
    public int deleteCrmLeadByLeadId(String leadId);

    /**
     * 批量删除业务线索
     * 
     * @param leadIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmLeadByLeadIds(String[] leadIds);

    CrmLead selectBySidAndMobilePhone(@Param("sid") String sid, @Param("mobilePhone") String mobilePhone);

    CrmLead selectInitialByMobilePhone(@Param("mobilePhone") String mobilePhone);

    /**
     * 同号未转化活跃线索（status ∈ initial / assigned / open），按创建时间倒序取一条。
     */
    CrmLead selectActiveUnconvertedByMobilePhone(@Param("mobilePhone") String mobilePhone);

    CrmLead selectLatestByMobilePhone(@Param("mobilePhone") String mobilePhone);

    CrmLead selectBySidAndMpOpenId(@Param("sid") String sid, @Param("openId") String openId,
            @Param("appId") String appId);

    CrmLead selectByWecomExternalUserid(@Param("wecomExternalUserid") String wecomExternalUserid);

    List<CrmLead> selectByWecomExternalUserids(@Param("externalUserids") List<String> externalUserids);

    int countActiveLeadsByUserId(@Param("userId") Long userId);

    int countAssignedLeadsByUserId(@Param("userId") Long userId);

    int countAssignedLeadByMobileExcludeLead(@Param("mobilePhone") String mobilePhone, @Param("excludeLeadId") String excludeLeadId);

    List<CrmLead> selectCrmLeadPoolList(com.ruoyi.crm.domain.dto.CrmLeadPoolQuery query);

    List<CrmLead> selectCrmLeadConvertedList(com.ruoyi.crm.domain.dto.CrmLeadConvertedQuery query);

    List<CrmLead> selectLeadsForAutoRecycle(@Param("idleHours") int idleHours, @Param("limit") int limit);

    List<CrmLead> selectAssignedLeadsOverHold(@Param("userId") Long userId, @Param("excess") int excess);

    List<Long> selectUserIdsOverHold(@Param("maxHold") int maxHold);

    int updateLeadPoolAssignment(CrmLead lead);

    int touchLastFollowupAt(@Param("leadId") String leadId, @Param("lastFollowupAt") java.util.Date lastFollowupAt);

    CrmLead selectAssignedByContactId(@Param("contactId") String contactId);

    CrmLead selectAssignedByWecomExternalUserid(@Param("wecomExternalUserid") String wecomExternalUserid);

    CrmLead selectUnconvertedDuplicate(com.ruoyi.crm.domain.dto.LeadDedupeQuery query);

    CrmLead selectConvertedLeadByContactId(@Param("contactId") String contactId);

    /**
     * 查询N天前转化线索关联的联系人（去重）。
     * 线索状态='converted' 且 convert_time 的日期部分恰好为 CURRENT_DATE - N 天。
     */
    List<com.ruoyi.crm.domain.CrmContact> selectContactsByConvertedLeadDaysAgo(@Param("daysAfter") Integer daysAfter);

    /**
     * WhatsApp 自动化：创建时间落在 [createdFrom, createdTo) 的线索（需有 contact 或手机号）。
     */
    List<CrmLead> selectCreatedBetween(@Param("createdFrom") java.util.Date createdFrom,
                                       @Param("createdTo") java.util.Date createdTo,
                                       @Param("limit") int limit);
}
