package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmDd;

/**
 * 尽职调查Mapper接口
 * 
 * @author luohd
 * @date 2024-05-01
 */
public interface CrmDdMapper 
{
    /**
     * 查询尽职调查
     * 
     * @param ddId 尽职调查主键
     * @return 尽职调查
     */
    public CrmDd selectCrmDdByDdId(String ddId);

    /**
     * 查询尽职调查列表
     * 
     * @param crmDd 尽职调查
     * @return 尽职调查集合
     */
    public List<CrmDd> selectCrmDdList(CrmDd crmDd);

    /**
     * 新增尽职调查
     * 
     * @param crmDd 尽职调查
     * @return 结果
     */
    public int insertCrmDd(CrmDd crmDd);

    /**
     * 修改尽职调查
     * 
     * @param crmDd 尽职调查
     * @return 结果
     */
    public int updateCrmDd(CrmDd crmDd);

    /**
     * 删除尽职调查
     * 
     * @param ddId 尽职调查主键
     * @return 结果
     */
    public int deleteCrmDdByDdId(String ddId);

    /**
     * 批量删除尽职调查
     * 
     * @param ddIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmDdByDdIds(String[] ddIds);
}
