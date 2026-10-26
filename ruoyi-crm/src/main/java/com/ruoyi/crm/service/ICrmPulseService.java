package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmPulse;

/**
 * 动态Service接口
 * 
 * @author luohd
 * @date 2024-02-24
 */
public interface ICrmPulseService 
{
    /**
     * 查询动态
     * 
     * @param pulseId 动态主键
     * @return 动态
     */
    public CrmPulse selectCrmPulseByPulseId(String pulseId);

    /**
     *
     * @param pulseIds
     * @return
     */
    public List<CrmPulse> selectCrmPulseByPulseIds(String[] pulseIds);

    public List<CrmPulse> selectNoteByPulseIds(String[] pulseIds);
    /**
     * 查询动态列表
     * 
     * @param crmPulse 动态
     * @return 动态集合
     */
    public List<CrmPulse> selectCrmPulseList(CrmPulse crmPulse);

    /**
     * 查询本周动态列表
     *
     * @param crmPulse 动态
     * @return 动态集合
     */
    public List<CrmPulse> selectCrmPulseListWeekly(CrmPulse crmPulse);

    /**
     * 新增动态
     * 
     * @param crmPulse 动态
     * @return 结果
     */
    public int insertCrmPulse(CrmPulse crmPulse);

    /**
     * 修改动态
     * 
     * @param crmPulse 动态
     * @return 结果
     */
    public int updateCrmPulse(CrmPulse crmPulse);

    /**
     * 批量删除动态
     * 
     * @param pulseIds 需要删除的动态主键集合
     * @return 结果
     */
    public int deleteCrmPulseByPulseIds(String[] pulseIds);

    /**
     * 删除动态信息
     * 
     * @param pulseId 动态主键
     * @return 结果
     */
    public int deleteCrmPulseByPulseId(String pulseId);
}
