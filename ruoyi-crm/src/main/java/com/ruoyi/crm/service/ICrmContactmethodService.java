package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.CrmContactmethod;

/**
 * 联系方式Service接口
 * 
 * @author luohd
 * @date 2024-03-16
 */
public interface ICrmContactmethodService 
{
    /**
     * 查询联系方式
     * 
     * @param methodId 联系方式主键
     * @return 联系方式
     */
    public CrmContactmethod selectCrmContactmethodByMethodId(String methodId);

    /**
     * 查询联系方式列表
     * 
     * @param crmContactmethod 联系方式
     * @return 联系方式集合
     */
    public List<CrmContactmethod> selectCrmContactmethodList(CrmContactmethod crmContactmethod);

    /**
     * 新增联系方式
     * 
     * @param crmContactmethod 联系方式
     * @return 结果
     */
    public int insertCrmContactmethod(CrmContactmethod crmContactmethod);

    /**
     * 修改联系方式
     * 
     * @param crmContactmethod 联系方式
     * @return 结果
     */
    public int updateCrmContactmethod(CrmContactmethod crmContactmethod);

    /**
     * 批量删除联系方式
     * 
     * @param methodIds 需要删除的联系方式主键集合
     * @return 结果
     */
    public int deleteCrmContactmethodByMethodIds(String[] methodIds);

    /**
     * 删除联系方式信息
     * 
     * @param methodId 联系方式主键
     * @return 结果
     */
    public int deleteCrmContactmethodByMethodId(String methodId);
}
