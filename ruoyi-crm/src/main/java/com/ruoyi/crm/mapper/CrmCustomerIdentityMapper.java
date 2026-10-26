package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.CrmCustomerIdentity;

/**
 * 客户身份Mapper接口
 *
 * @author luohd
 * @date 2026-06-16
 */
public interface CrmCustomerIdentityMapper
{
    /**
     * 按身份类型和值查询唯一记录
     */
    CrmCustomerIdentity selectByTypeValue(
        @Param("identityType") String identityType,
        @Param("identityValue") String identityValue);

    /**
     * 按联系人ID查询所有身份
     */
    List<CrmCustomerIdentity> selectByContactId(@Param("contactId") String contactId);

    /**
     * 批量按身份标识查询
     */
    List<CrmCustomerIdentity> batchResolve(
        @Param("queries") List<CrmCustomerIdentity> queries);

    /**
     * 插入身份记录（普通 INSERT）
     */
    int insert(CrmCustomerIdentity identity);

    /**
     * 插入或忽略（ON CONFLICT DO NOTHING），用于幂等绑定
     */
    int insertOrIgnore(CrmCustomerIdentity identity);

    /**
     * 按联系人+类型+值删除一条身份
     */
    int deleteByContactTypeValue(
        @Param("contactId") String contactId,
        @Param("identityType") String identityType,
        @Param("identityValue") String identityValue);

    /**
     * 合并身份：将源联系人的所有身份记录迁移到目标联系人
     */
    int updateContactIdByContactId(
        @Param("sourceContactId") String sourceContactId,
        @Param("targetContactId") String targetContactId);

    /**
     * 清除联系人下所有主身份标记
     */
    int clearPrimaryByContactId(@Param("contactId") String contactId);

    /**
     * 将指定身份设为主身份
     */
    int setPrimaryByContactTypeValue(
        @Param("contactId") String contactId,
        @Param("identityType") String identityType,
        @Param("identityValue") String identityValue);
}
