package com.ruoyi.crm.mapper;

import java.util.List;
import com.ruoyi.crm.domain.CrmOwnerhistory;

/**
 * owner记录Mapper接口
 * 
 * @author luohd
 * @date 2024-01-20
 */
public interface CrmOwnerhistoryMapper 
{
    /**
     * 查询owner记录
     * 
     * @param ohId owner记录主键
     * @return owner记录
     */
    public CrmOwnerhistory selectCrmOwnerhistoryByOhId(String ohId);

    /**
     * 查询owner记录列表
     * 
     * @param crmOwnerhistory owner记录
     * @return owner记录集合
     */
    public List<CrmOwnerhistory> selectCrmOwnerhistoryList(CrmOwnerhistory crmOwnerhistory);

    /**
     * 查询指定目标的历史记录
     * @param crmOwnerhistory
     * @return
     */
    public List<CrmOwnerhistory> selectCrmOwnerhistoryListByTarget(CrmOwnerhistory crmOwnerhistory);

    /**
     * 新增owner记录
     * 
     * @param crmOwnerhistory owner记录
     * @return 结果
     */
    public int insertCrmOwnerhistory(CrmOwnerhistory crmOwnerhistory);

    /**
     * 修改owner记录
     * 
     * @param crmOwnerhistory owner记录
     * @return 结果
     */
    public int updateCrmOwnerhistory(CrmOwnerhistory crmOwnerhistory);

    /**
     * 删除owner记录
     * 
     * @param ohId owner记录主键
     * @return 结果
     */
    public int deleteCrmOwnerhistoryByOhId(String ohId);

    /**
     * 批量删除owner记录
     * 
     * @param ohIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCrmOwnerhistoryByOhIds(String[] ohIds);
}
