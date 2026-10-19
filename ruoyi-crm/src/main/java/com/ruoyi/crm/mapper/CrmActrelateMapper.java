package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmActrelate;

/**
 * 关联的活动Mapper接口
 * 
 * @author luohd
 * @date 2024-01-22
 */
public interface CrmActrelateMapper 
{
    /**
     * 查询关联的活动
     * 
     * @param relateId 关联的活动主键
     * @return 关联的活动
     */
    public CrmActrelate selectCrmActrelateByRelateId(String relateId);

    /**
     * 查询关联的活动列表
     * 
     * @param crmActrelate 关联的活动
     * @return 关联的活动集合
     */
    public List<CrmActrelate> selectCrmActrelateList(CrmActrelate crmActrelate);

    /**
     * 新增关联的活动
     * 
     * @param crmActrelate 关联的活动
     * @return 结果
     */
    public int insertCrmActrelate(CrmActrelate crmActrelate);

    /**
     * 修改关联的活动
     * 
     * @param crmActrelate 关联的活动
     * @return 结果
     */
    public int updateCrmActrelate(CrmActrelate crmActrelate);

    /**
     * 删除关联的活动
     * 
     * @param relateId 关联的活动主键
     * @return 结果
     */
    public int deleteCrmActrelateByRelateId(String relateId);

    /**
     * 批量删除关联的活动
     * 
     * @param relateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmActrelateByRelateIds(String[] relateIds);
}
