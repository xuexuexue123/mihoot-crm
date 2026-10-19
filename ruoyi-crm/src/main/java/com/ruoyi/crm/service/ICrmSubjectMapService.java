package com.ruoyi.crm.service;

import com.ruoyi.crm.domain.CrmSubjectMap;
import java.util.List;

/**
 * Subject 继承映射服务
 *
 * @author luohd
 * @date 2026-06-10
 */
public interface ICrmSubjectMapService
{
    /**
     * 创建 Subject 继承关系（Lead → Contact 转换时调用）
     * 幂等操作：如果子主体已存在映射，则更新父主体指向
     */
    CrmSubjectMap upsert(CrmSubjectMap map);

    /**
     * 按子主体查询父主体
     */
    CrmSubjectMap findByChild(String childSubjectType, String childSubjectId);

    /**
     * 按父主体查询所有子主体
     */
    List<CrmSubjectMap> findByParent(String parentSubjectType, String parentSubjectId);
}
