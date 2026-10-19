package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmRespondents;

/**
 * 受访者Mapper接口
 * 
 * @author luohd
 * @date 2025-03-29
 */
public interface CrmRespondentsMapper 
{
    /**
     * 查询受访者
     * 
     * @param id 受访者主键
     * @return 受访者
     */
    public CrmRespondents selectCrmRespondentsById(String id);

    /**
     * 查询受访者列表
     * 
     * @param crmRespondents 受访者
     * @return 受访者集合
     */
    public List<CrmRespondents> selectCrmRespondentsList(CrmRespondents crmRespondents);

    /**
     * 新增受访者
     * 
     * @param crmRespondents 受访者
     * @return 结果
     */
    public int insertCrmRespondents(CrmRespondents crmRespondents);

    /**
     * 修改受访者
     * 
     * @param crmRespondents 受访者
     * @return 结果
     */
    public int updateCrmRespondents(CrmRespondents crmRespondents);

    /**
     * 删除受访者
     * 
     * @param id 受访者主键
     * @return 结果
     */
    public int deleteCrmRespondentsById(String id);

    /**
     * 批量删除受访者
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmRespondentsByIds(String[] ids);

    public long countByRelateId(String relateId);
}
