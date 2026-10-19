package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmCollect;
import com.ruoyi.crm.domain.CrmCollectEmail;

/**
 * 数据收集Service接口
 * 
 * @author luohd
 * @date 2024-07-04
 */
public interface ICrmCollectService 
{
    /**
     * 查询数据收集
     * 
     * @param collectId 数据收集主键
     * @return 数据收集
     */
    public CrmCollect selectCrmCollectByCollectId(String collectId);

    /**
     * 查询数据收集列表
     * 
     * @param crmCollect 数据收集
     * @return 数据收集集合
     */
    public List<CrmCollect> selectCrmCollectList(CrmCollect crmCollect);

    /**
     * 新增数据收集
     * 
     * @param crmCollect 数据收集
     * @return 结果
     */
    public int insertCrmCollect(CrmCollectEmail crmCollect) throws Exception;

    /**
     * 修改数据收集
     * 
     * @param crmCollect 数据收集
     * @return 结果
     */
    public int updateCrmCollect(CrmCollect crmCollect);

    /**
     *  by validation code
     * @param crmCollect
     * @return
     */
    public int updateCrmCollectByValidationCode(CrmCollect crmCollect);

    /**
     * 批量删除数据收集
     * 
     * @param collectIds 需要删除的数据收集主键集合
     * @return 结果
     */
    public int deleteCrmCollectByCollectIds(String[] collectIds);

    /**
     * 删除数据收集信息
     * 
     * @param collectId 数据收集主键
     * @return 结果
     */
    public int deleteCrmCollectByCollectId(String collectId);
}
