package com.ruoyi.crm.mapper;

import com.ruoyi.crm.domain.CrmSubjectMap;

/**
 * Subject 继承映射 Mapper 接口
 *
 * @author luohd
 * @date 2026-06-10
 */
public interface CrmSubjectMapMapper
{
    /**
     * 插入映射关系（PostgreSQL ON CONFLICT DO UPDATE 保证幂等）
     */
    int upsert(CrmSubjectMap map);

    /**
     * 按子主体查询
     */
    CrmSubjectMap selectByChild(String childSubjectType, String childSubjectId);

    /**
     * 按父主体查询所有子主体
     */
    java.util.List<CrmSubjectMap> selectByParent(String parentSubjectType, String parentSubjectId);
}
