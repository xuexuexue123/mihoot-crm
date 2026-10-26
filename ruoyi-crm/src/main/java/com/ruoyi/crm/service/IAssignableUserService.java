package com.ruoyi.crm.service;

import java.util.List;

import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.domain.vo.AssignableUserVo;
import com.ruoyi.system.domain.dto.AssignableUserQuery;

/**
 * 可分配用户查询（Lead 指派、企微归属）
 */
public interface IAssignableUserService
{
    List<AssignableUserVo> selectAssignableUserList(AssignableUserQuery query);

    AssignableResolveResult resolve(String snUserid, Long userId, String purpose);
}
