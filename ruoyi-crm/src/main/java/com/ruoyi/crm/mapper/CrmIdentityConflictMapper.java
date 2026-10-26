package com.ruoyi.crm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmIdentityConflict;

/**
 * 身份冲突 Mapper 接口
 */
public interface CrmIdentityConflictMapper
{
    /**
     * 插入冲突记录；同一 pending 冲突（类型+值+两侧 Contact）已存在时忽略。
     */
    int insertIgnoreOnConflict(CrmIdentityConflict conflict);

    /**
     * 按条件查询（默认按 create_time 倒序）。
     */
    List<CrmIdentityConflict> selectList(CrmIdentityConflict query);

    CrmIdentityConflict selectById(@Param("id") Long id);

    /**
     * 标记已解决（合并完成）。
     */
    int markResolved(@Param("id") Long id, @Param("resolvedBy") String resolvedBy);

    /**
     * 标记已忽略。
     */
    int markIgnored(@Param("id") Long id, @Param("resolvedBy") String resolvedBy);
}
