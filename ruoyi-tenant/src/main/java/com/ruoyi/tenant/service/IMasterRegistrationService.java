package com.ruoyi.tenant.service;

import java.util.List;
import com.ruoyi.tenant.domain.MasterRegistration;

/**
 * 主注册Service接口
 * 
 * @author luohd
 * @date 2024-03-17
 */
public interface IMasterRegistrationService 
{
    /**
     * 检查公司名字是否唯一
     * @param companyName
     * @return
     */
    String checkCompanyNameUnique(String companyName);

    /**
     * 检查手机是否唯一
     * @param mobile
     * @return
     */
    String checkMobileUnique(String mobile);

    /**
     * 检查邮箱是否唯一
     * @param email
     * @return
     */
    String checkEmailUnique(String email);

    /**
     * 查询主注册
     * 
     * @param registId 主注册主键
     * @return 主注册
     */
    public MasterRegistration selectMasterRegistrationByRegistId(Long registId);

    /**
     * 查询主注册列表
     * 
     * @param masterRegistration 主注册
     * @return 主注册集合
     */
    public List<MasterRegistration> selectMasterRegistrationList(MasterRegistration masterRegistration);

    /**
     * 新增主注册
     * 
     * @param masterRegistration 主注册
     * @return 结果
     */
    public int insertMasterRegistration(MasterRegistration masterRegistration);

    /**
     * 修改主注册
     * 
     * @param masterRegistration 主注册
     * @return 结果
     */
    public int updateMasterRegistration(MasterRegistration masterRegistration);

    /**
     * 批量删除主注册
     * 
     * @param registIds 需要删除的主注册主键集合
     * @return 结果
     */
    public int deleteMasterRegistrationByRegistIds(Long[] registIds);

    /**
     * 删除主注册信息
     * 
     * @param registId 主注册主键
     * @return 结果
     */
    public int deleteMasterRegistrationByRegistId(Long registId);
}
