package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmResearch;

/**
 * 研报Mapper接口
 * 
 * @author luohd
 * @date 2024-10-03
 */
public interface CrmResearchMapper 
{
    /**
     * 查询研报
     * 
     * @param researchId 研报主键
     * @return 研报
     */
    public CrmResearch selectCrmResearchByResearchId(String researchId);

    /**
     * 查询研报列表
     * 
     * @param crmResearch 研报
     * @return 研报集合
     */
    public List<CrmResearch> selectCrmResearchList(CrmResearch crmResearch);

    /**
     * 新增研报
     * 
     * @param crmResearch 研报
     * @return 结果
     */
    public int insertCrmResearch(CrmResearch crmResearch);

    /**
     * 修改研报
     * 
     * @param crmResearch 研报
     * @return 结果
     */
    public int updateCrmResearch(CrmResearch crmResearch);

    /**
     * 删除研报
     * 
     * @param researchId 研报主键
     * @return 结果
     */
    public int deleteCrmResearchByResearchId(String researchId);

    /**
     * 批量删除研报
     * 
     * @param researchIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmResearchByResearchIds(String[] researchIds);
}
