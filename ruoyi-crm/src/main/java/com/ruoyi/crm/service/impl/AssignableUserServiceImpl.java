package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.crm.domain.dto.LeadAssignPatch;
import com.ruoyi.crm.domain.vo.AssignableResolveResult;
import com.ruoyi.crm.domain.vo.AssignableUserVo;
import com.ruoyi.crm.mapper.CrmLeadMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.support.AudienceLeadVisibility;
import com.ruoyi.system.domain.dto.AssignableUserQuery;
import com.ruoyi.system.mapper.SysUserMapper;

@Service
public class AssignableUserServiceImpl implements IAssignableUserService
{
    private static final Map<String, PurposePreset> PURPOSE_PRESETS = new HashMap<>();

    static
    {
        PURPOSE_PRESETS.put("lead_assign", new PurposePreset(true, "common,managers", "dept_and_child"));
        PURPOSE_PRESETS.put("lead_owner_dropdown", new PurposePreset(true, "common,managers", "tenant"));
        PURPOSE_PRESETS.put("lead_claim", new PurposePreset(true, "common", "self"));
        PURPOSE_PRESETS.put("wecom_livecode", new PurposePreset(true, "common,managers", "tenant"));
        PURPOSE_PRESETS.put("wecom_follow_resolve", new PurposePreset(true, "common,managers", "tenant"));
        PURPOSE_PRESETS.put("opty_owner_dropdown", new PurposePreset(false, "common,managers", "tenant"));
        PURPOSE_PRESETS.put("contact_owner_dropdown", new PurposePreset(false, "common,managers", "tenant"));
    }

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CrmLeadMapper crmLeadMapper;

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<AssignableUserVo> selectAssignableUserList(AssignableUserQuery query)
    {
        if (query == null)
        {
            query = new AssignableUserQuery();
        }
        applyPurposePresets(query);
        applyScopeFilters(query);
        query.setRoleKeyList(parseRoleKeys(query.getRoleKeys()));

        List<SysUser> users = sysUserMapper.selectAssignableUserList(query);
        Map<Long, AssignableUserVo> deduped = new LinkedHashMap<>();
        for (SysUser user : users)
        {
            if (user.getUserId() == null)
            {
                continue;
            }
            AssignableUserVo vo = deduped.computeIfAbsent(user.getUserId(), id -> toVo(user));
            mergeRoles(vo, user.getRoles());
        }
        List<AssignableUserVo> rows = new ArrayList<>(deduped.values());
        for (AssignableUserVo vo : rows)
        {
            vo.setOwnerSelectValue(buildOwnerSelectValue(vo));
            vo.setAssignable(true);
            vo.setAssignBlockReason(null);
            if (Boolean.TRUE.equals(query.getIncludeWorkload()) && vo.getUserId() != null)
            {
                vo.setActiveLeadCount(crmLeadMapper.countActiveLeadsByUserId(vo.getUserId()));
            }
        }
        return rows;
    }

    @Override
    public AssignableResolveResult resolve(String snUserid, Long userId, String purpose)
    {
        AssignableResolveResult result = new AssignableResolveResult();
        result.setResolved(false);

        String effectivePurpose = StringUtils.hasText(purpose) ? purpose.trim() : "wecom_follow_resolve";
        PurposePreset preset = PURPOSE_PRESETS.getOrDefault(effectivePurpose,
                PURPOSE_PRESETS.get("wecom_follow_resolve"));
        List<String> allowedRoles = parseRoleKeys(preset.roleKeys);

        SysUser user = null;
        if (StringUtils.hasText(snUserid))
        {
            int count = sysUserMapper.countUsersBySnUserid(snUserid.trim());
            if (count > 1)
            {
                result.setReason("multiple_matches");
                return result;
            }
            user = sysUserMapper.selectUserBySnUserid(snUserid.trim());
            if (user != null && userId != null && !userId.equals(user.getUserId()))
            {
                result.setReason("sn_userid_mismatch");
                return result;
            }
        }
        else if (userId != null)
        {
            user = sysUserMapper.selectUserById(userId);
        }
        else
        {
            result.setReason("user_not_found");
            return result;
        }

        if (user == null || !"0".equals(user.getDelFlag()))
        {
            result.setReason("user_not_found");
            return result;
        }
        if (!"0".equals(user.getStatus()))
        {
            result.setReason("user_disabled");
            return result;
        }

        AssignableUserVo vo = toVo(user);
        if (user.getRoles() != null)
        {
            vo.setRoles(user.getRoles());
        }
        vo.setOwnerSelectValue(buildOwnerSelectValue(vo));

        if (preset.wecomBound && !StringUtils.hasText(user.getSnUserid()))
        {
            result.setReason("wecom_not_bound");
            result.setUser(vo);
            return result;
        }

        if (!hasAllowedRole(user, allowedRoles))
        {
            result.setReason("role_not_allowed");
            result.setUser(vo);
            return result;
        }

        LeadAssignPatch patch = buildLeadAssignPatch(user);
        result.setResolved(true);
        result.setUser(vo);
        vo.setAssignable(true);
        result.setLeadAssignPatch(patch);
        return result;
    }

    private static void applyPurposePresets(AssignableUserQuery query)
    {
        String purpose = query.getPurpose();
        if (!StringUtils.hasText(purpose))
        {
            purpose = "lead_assign";
            query.setPurpose(purpose);
        }
        PurposePreset preset = PURPOSE_PRESETS.get(purpose.trim());
        if (preset == null)
        {
            return;
        }
        if (query.getWecomBound() == null)
        {
            query.setWecomBound(preset.wecomBound);
        }
        if (!StringUtils.hasText(query.getRoleKeys()))
        {
            query.setRoleKeys(preset.roleKeys);
        }
        if (!StringUtils.hasText(query.getScope()))
        {
            query.setScope(preset.scope);
        }
    }

    private static void applyScopeFilters(AssignableUserQuery query)
    {
        String scope = query.getScope();
        if (!StringUtils.hasText(scope))
        {
            return;
        }
        switch (scope.trim().toLowerCase())
        {
            case "self":
                query.setScopeUserId(SecurityUtils.getUserId());
                break;
            case "dept":
            case "dept_and_child":
                if (query.getScopeDeptId() == null)
                {
                    query.setScopeDeptId(SecurityUtils.getDeptId());
                }
                break;
            default:
                break;
        }
    }

    private static List<String> parseRoleKeys(String roleKeys)
    {
        if (!StringUtils.hasText(roleKeys))
        {
            return Arrays.asList("common", "managers");
        }
        return Arrays.stream(roleKeys.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    private static boolean hasAllowedRole(SysUser user, List<String> allowedRoles)
    {
        if (user.getRoles() == null || user.getRoles().isEmpty())
        {
            return false;
        }
        for (SysRole role : user.getRoles())
        {
            if (role != null && allowedRoles.contains(role.getRoleKey()))
            {
                return true;
            }
        }
        return false;
    }

    private static LeadAssignPatch buildLeadAssignPatch(SysUser user)
    {
        LeadAssignPatch patch = new LeadAssignPatch();
        patch.setUserId(user.getUserId());
        patch.setDeptId(user.getDeptId());
        patch.setOwner(displayName(user));
        if (StringUtils.hasText(user.getSnUserid()))
        {
            patch.setWecomFollowUserid(user.getSnUserid().trim());
        }
        patch.setStatus(AudienceLeadVisibility.STATUS_ASSIGNED);
        return patch;
    }

    private static AssignableUserVo toVo(SysUser user)
    {
        AssignableUserVo vo = new AssignableUserVo();
        vo.setUserId(user.getUserId());
        vo.setDeptId(user.getDeptId());
        vo.setUserName(user.getUserName());
        vo.setNickName(user.getNickName());
        vo.setSnUserid(user.getSnUserid());
        vo.setStatus(user.getStatus());
        vo.setDept(user.getDept());
        if (user.getRoles() != null)
        {
            vo.setRoles(new ArrayList<>(user.getRoles()));
        }
        return vo;
    }

    private static void mergeRoles(AssignableUserVo vo, List<SysRole> roles)
    {
        if (roles == null || roles.isEmpty())
        {
            return;
        }
        if (vo.getRoles() == null)
        {
            vo.setRoles(new ArrayList<>());
        }
        for (SysRole role : roles)
        {
            if (role == null || role.getRoleId() == null)
            {
                continue;
            }
            boolean exists = vo.getRoles().stream()
                    .anyMatch(r -> r != null && role.getRoleId().equals(r.getRoleId()));
            if (!exists)
            {
                vo.getRoles().add(role);
            }
        }
    }

    private static String buildOwnerSelectValue(AssignableUserVo vo)
    {
        String nick = StringUtils.hasText(vo.getNickName()) ? vo.getNickName() : vo.getUserName();
        Long deptId = vo.getDeptId() != null ? vo.getDeptId() : 0L;
        return vo.getUserId() + "|" + deptId + "|" + (nick != null ? nick : "");
    }

    private static String displayName(SysUser user)
    {
        if (StringUtils.hasText(user.getNickName()))
        {
            return user.getNickName();
        }
        return user.getUserName();
    }

    private static final class PurposePreset
    {
        private final boolean wecomBound;
        private final String roleKeys;
        private final String scope;

        private PurposePreset(boolean wecomBound, String roleKeys, String scope)
        {
            this.wecomBound = wecomBound;
            this.roleKeys = roleKeys;
            this.scope = scope;
        }
    }
}
