package com.ruoyi.crm.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.crm.domain.CrmSalesgoal;
import com.ruoyi.crm.domain.CrmTimeperiod;
import com.ruoyi.crm.domain.dto.SalesgoalAssessBatchRequest;
import com.ruoyi.crm.domain.enums.SalesKpiCode;
import com.ruoyi.crm.domain.vo.AssignableUserVo;
import com.ruoyi.crm.domain.vo.CrmSalesgoalListVo;
import com.ruoyi.crm.mapper.CrmSalesgoalMapper;
import com.ruoyi.crm.service.IAssignableUserService;
import com.ruoyi.crm.service.ICrmSalesgoalService;
import com.ruoyi.crm.service.ICrmTimeperiodService;
import com.ruoyi.crm.service.ISalesKpiActualService;
import com.ruoyi.system.domain.dto.AssignableUserQuery;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.common.core.domain.entity.SysUser;

/**
 * 销售目标 Service（含 KPI 月度目标、考核、实际值聚合）。
 *
 * @author luohd
 * @date 2024-01-19
 */
@Service
public class CrmSalesgoalServiceImpl implements ICrmSalesgoalService
{
    private static final String PERIOD_TYPE_MONTHLY = "Monthly";
    private static final String GOAL_TYPE_KPI = "kpi";
    /** 考核中 */
    private static final long IS_ACTIVE_ASSESSING = 0L;
    /** 未考核（创建默认） */
    private static final long IS_ACTIVE_IDLE = 1L;

    @Autowired
    private CrmSalesgoalMapper crmSalesgoalMapper;

    @Autowired
    private ICrmTimeperiodService crmTimeperiodService;

    @Autowired
    private ISalesKpiActualService salesKpiActualService;

    @Autowired
    private IAssignableUserService assignableUserService;

    @Autowired
    private ISysUserService sysUserService;

    /** ensure-rows 空 ownerIds 时使用的角色 key，逗号分隔 */
    @Value("${crm.salesgoal.ensure-role-keys:common,managers}")
    private String ensureRoleKeys;

    @Override
    public CrmSalesgoal selectCrmSalesgoalByGoalId(String goalId)
    {
        return crmSalesgoalMapper.selectCrmSalesgoalByGoalId(goalId);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmSalesgoal> selectCrmSalesgoalList(CrmSalesgoal crmSalesgoal)
    {
        applyDefaultMonthlyFilter(crmSalesgoal);
        return crmSalesgoalMapper.selectCrmSalesgoalList(crmSalesgoal);
    }

    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<CrmSalesgoalListVo> selectCrmSalesgoalListWithActual(CrmSalesgoal crmSalesgoal)
    {
        applyDefaultMonthlyFilter(crmSalesgoal);
        List<CrmSalesgoal> rows = crmSalesgoalMapper.selectCrmSalesgoalList(crmSalesgoal);
        if (CollectionUtils.isEmpty(rows))
        {
            return Collections.emptyList();
        }
        List<CrmSalesgoalListVo> vos = rows.stream().map(CrmSalesgoalListVo::from).collect(Collectors.toList());
        enrichActualValues(vos, crmSalesgoal);
        return vos;
    }

    @Override
    public List<CrmSalesgoal> selectMyCrmSalesgoal(CrmSalesgoal crmSalesgoal)
    {
        applyDefaultMonthlyFilter(crmSalesgoal);
        return crmSalesgoalMapper.selectMyCrmSalesgoal(crmSalesgoal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCrmSalesgoal(CrmSalesgoal crmSalesgoal)
    {
        if (crmSalesgoal == null)
        {
            throw new ServiceException("请求体不能为空");
        }
        // 有 kpiCode：KPI 路径；无 kpiCode：兼容旧 Vue 季度目标创建
        if (StringUtils.hasText(crmSalesgoal.getKpiCode()))
        {
            return insertKpiSalesgoal(crmSalesgoal);
        }
        if (!StringUtils.hasText(crmSalesgoal.getGoalId()))
        {
            crmSalesgoal.setGoalId(IdUtils.fastUUID());
        }
        crmSalesgoal.setCreateTime(DateUtils.getNowDate());
        fillAuditUserDept(crmSalesgoal, true);
        return crmSalesgoalMapper.insertCrmSalesgoal(crmSalesgoal);
    }

    private int insertKpiSalesgoal(CrmSalesgoal crmSalesgoal)
    {
        if (!StringUtils.hasText(crmSalesgoal.getOwnerId()))
        {
            throw new ServiceException("ownerId 不能为空");
        }
        if (!StringUtils.hasText(crmSalesgoal.getPeriodId()))
        {
            throw new ServiceException("periodId 不能为空");
        }
        SalesKpiCode kpi = SalesKpiCode.fromCode(crmSalesgoal.getKpiCode());
        CrmTimeperiod period = requireMonthlyPeriod(crmSalesgoal.getPeriodId());
        assertUnique(crmSalesgoal.getOwnerId(), period.getPeriodId(), kpi.getCode(), null);

        fillPeriodFields(crmSalesgoal, period);
        crmSalesgoal.setKpiCode(kpi.getCode());
        crmSalesgoal.setGoalType(GOAL_TYPE_KPI);
        crmSalesgoal.setGoalName(kpi.getCode());
        if (crmSalesgoal.getGoalValue() == null)
        {
            crmSalesgoal.setGoalValue(BigDecimal.ZERO);
        }
        if (crmSalesgoal.getIsActive() == null)
        {
            crmSalesgoal.setIsActive(IS_ACTIVE_IDLE);
        }
        if (!StringUtils.hasText(crmSalesgoal.getGoalId()))
        {
            crmSalesgoal.setGoalId(IdUtils.fastUUID());
        }
        fillOwnerNameIfBlank(crmSalesgoal);
        fillAuditUserDept(crmSalesgoal, true);
        crmSalesgoal.setCreateTime(DateUtils.getNowDate());
        try
        {
            return crmSalesgoalMapper.insertCrmSalesgoal(crmSalesgoal);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("销售目标重复：同一人同一月同一 KPI 已存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCrmSalesgoal(CrmSalesgoal crmSalesgoal)
    {
        if (crmSalesgoal == null || !StringUtils.hasText(crmSalesgoal.getGoalId()))
        {
            throw new ServiceException("goalId 不能为空");
        }
        CrmSalesgoal existing = crmSalesgoalMapper.selectCrmSalesgoalByGoalId(crmSalesgoal.getGoalId());
        if (existing == null)
        {
            throw new ServiceException("销售目标不存在");
        }

        String ownerId = StringUtils.hasText(crmSalesgoal.getOwnerId()) ? crmSalesgoal.getOwnerId() : existing.getOwnerId();
        String periodId = StringUtils.hasText(crmSalesgoal.getPeriodId()) ? crmSalesgoal.getPeriodId() : existing.getPeriodId();
        String kpiCode = StringUtils.hasText(crmSalesgoal.getKpiCode()) ? crmSalesgoal.getKpiCode() : existing.getKpiCode();

        if (StringUtils.hasText(kpiCode))
        {
            SalesKpiCode.fromCode(kpiCode);
            crmSalesgoal.setKpiCode(kpiCode);
        }
        if (StringUtils.hasText(periodId) && !periodId.equals(existing.getPeriodId()))
        {
            CrmTimeperiod period = requireMonthlyPeriod(periodId);
            fillPeriodFields(crmSalesgoal, period);
        }
        if (StringUtils.hasText(kpiCode) && StringUtils.hasText(ownerId) && StringUtils.hasText(periodId))
        {
            assertUnique(ownerId, periodId, kpiCode, existing.getGoalId());
        }
        if (StringUtils.hasText(crmSalesgoal.getOwnerId()) && !StringUtils.hasText(crmSalesgoal.getOwner()))
        {
            fillOwnerNameIfBlank(crmSalesgoal);
        }
        crmSalesgoal.setUpdateTime(DateUtils.getNowDate());
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                crmSalesgoal.setUpdateBy(loginUser.getUsername());
            }
        }
        catch (Exception ignored)
        {
        }
        try
        {
            return crmSalesgoalMapper.updateCrmSalesgoal(crmSalesgoal);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("销售目标重复：同一人同一月同一 KPI 已存在");
        }
    }

    @Override
    public int resetCrmSalesgoal(CrmSalesgoal crmSalesgoal)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        crmSalesgoal.setUpdateTime(DateUtils.getNowDate());
        crmSalesgoal.setUpdateBy(loginUser.getUsername());
        return crmSalesgoalMapper.resetCrmSalesgoal(crmSalesgoal);
    }

    @Override
    public int deleteCrmSalesgoalByGoalIds(String[] goalIds)
    {
        return crmSalesgoalMapper.deleteCrmSalesgoalByGoalIds(goalIds);
    }

    @Override
    public int deleteCrmSalesgoalByGoalId(String goalId)
    {
        return crmSalesgoalMapper.deleteCrmSalesgoalByGoalId(goalId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assessBatch(SalesgoalAssessBatchRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getPeriodId()) || !StringUtils.hasText(request.getKpiCode()))
        {
            throw new ServiceException("periodId 与 kpiCode 不能为空");
        }
        SalesKpiCode.fromCode(request.getKpiCode());
        requireMonthlyPeriod(request.getPeriodId());
        Long isActive = parseIsActive(request.getIsActive());
        if (isActive == null)
        {
            throw new ServiceException("isActive 必须为 0（考核中）或 1（未考核）");
        }
        String updateBy = null;
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                updateBy = loginUser.getUsername();
            }
        }
        catch (Exception ignored)
        {
        }
        return crmSalesgoalMapper.updateAssessBatch(request.getPeriodId(), request.getKpiCode(), isActive,
                updateBy, DateUtils.getNowDate());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> ensureRows(SalesgoalAssessBatchRequest request)
    {
        if (request == null || !StringUtils.hasText(request.getPeriodId()) || !StringUtils.hasText(request.getKpiCode()))
        {
            throw new ServiceException("periodId 与 kpiCode 不能为空");
        }
        SalesKpiCode kpi = SalesKpiCode.fromCode(request.getKpiCode());
        CrmTimeperiod period = requireMonthlyPeriod(request.getPeriodId());

        List<String> ownerIds = request.getOwnerIds();
        Map<String, String> ownerNames = new HashMap<>();
        if (CollectionUtils.isEmpty(ownerIds))
        {
            ownerIds = resolveDefaultOwnerIds(ownerNames);
        }
        else
        {
            for (String oid : ownerIds)
            {
                ownerNames.put(oid, resolveUserNick(oid));
            }
        }
        if (CollectionUtils.isEmpty(ownerIds))
        {
            Map<String, Integer> empty = new HashMap<>();
            empty.put("created", 0);
            empty.put("existing", 0);
            return empty;
        }

        // 去重
        List<String> distinct = new ArrayList<>(new LinkedHashSet<>(ownerIds));
        List<String> existing = crmSalesgoalMapper.selectExistingOwnerIds(period.getPeriodId(), kpi.getCode(), distinct);
        Set<String> existingSet = existing == null ? Collections.emptySet() : new HashSet<>(existing);

        List<CrmSalesgoal> toInsert = new ArrayList<>();
        String createBy = null;
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser != null)
            {
                createBy = loginUser.getUsername();
            }
        }
        catch (Exception ignored)
        {
        }
        for (String oid : distinct)
        {
            if (existingSet.contains(oid))
            {
                continue;
            }
            CrmSalesgoal row = new CrmSalesgoal();
            row.setGoalId(IdUtils.fastUUID());
            row.setKpiCode(kpi.getCode());
            row.setGoalType(GOAL_TYPE_KPI);
            row.setGoalName(kpi.getCode());
            row.setGoalValue(BigDecimal.ZERO);
            row.setIsActive(IS_ACTIVE_IDLE);
            row.setOwnerId(oid);
            row.setOwner(ownerNames.getOrDefault(oid, oid));
            fillPeriodFields(row, period);
            row.setCreateBy(createBy);
            row.setCreateTime(DateUtils.getNowDate());
            try
            {
                Long uid = Long.valueOf(oid);
                row.setUserId(uid);
            }
            catch (NumberFormatException ignored)
            {
            }
            toInsert.add(row);
        }
        int created = 0;
        if (!toInsert.isEmpty())
        {
            created = crmSalesgoalMapper.batchInsertCrmSalesgoal(toInsert);
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("created", created);
        result.put("existing", existingSet.size());
        return result;
    }

    private void enrichActualValues(List<CrmSalesgoalListVo> vos, CrmSalesgoal query)
    {
        String periodId = query != null ? query.getPeriodId() : null;
        String kpiCode = query != null ? query.getKpiCode() : null;
        if (!StringUtils.hasText(periodId) || !StringUtils.hasText(kpiCode))
        {
            return;
        }
        SalesKpiCode kpi;
        try
        {
            kpi = SalesKpiCode.fromCode(kpiCode);
        }
        catch (ServiceException ex)
        {
            return;
        }
        CrmTimeperiod period = crmTimeperiodService.selectCrmTimeperiodByPeriodId(periodId);
        if (period == null || period.getStartDate() == null || period.getEndDate() == null)
        {
            return;
        }
        List<String> ownerIds = vos.stream()
                .map(CrmSalesgoalListVo::getOwnerId)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        if (ownerIds.isEmpty())
        {
            return;
        }
        Map<String, BigDecimal> actuals = salesKpiActualService.aggregate(ownerIds, period.getStartDate(),
                period.getEndDate(), kpi);
        for (CrmSalesgoalListVo vo : vos)
        {
            BigDecimal actual = actuals.get(vo.getOwnerId());
            // 非赢单率：无事件时按 0；赢单率：map 无 key 表示分母为 0 → null
            if (actual == null && kpi != SalesKpiCode.WIN_RATE)
            {
                actual = BigDecimal.ZERO;
            }
            vo.setActualValue(actual);
            vo.setCompletionRate(computeCompletionRate(vo.getGoalValue(), actual));
        }
    }

    static BigDecimal computeCompletionRate(BigDecimal goalValue, BigDecimal actualValue)
    {
        if (goalValue == null || actualValue == null || goalValue.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }
        return actualValue.divide(goalValue, 4, RoundingMode.HALF_UP);
    }

    private void applyDefaultMonthlyFilter(CrmSalesgoal query)
    {
        if (query == null)
        {
            return;
        }
        // KPI 查询（带 kpiCode）默认仅 Monthly；旧 Vue 无 kpiCode 时不强制
        if (StringUtils.hasText(query.getKpiCode()) && !StringUtils.hasText(query.getPeriodType()))
        {
            query.setPeriodType(PERIOD_TYPE_MONTHLY);
        }
    }

    private CrmTimeperiod requireMonthlyPeriod(String periodId)
    {
        CrmTimeperiod period = crmTimeperiodService.selectCrmTimeperiodByPeriodId(periodId);
        if (period == null)
        {
            throw new ServiceException("periodId 不存在: " + periodId);
        }
        if (!PERIOD_TYPE_MONTHLY.equalsIgnoreCase(period.getPeriodType()))
        {
            throw new ServiceException("periodId 必须为 Monthly 销售周期");
        }
        return period;
    }

    private void fillPeriodFields(CrmSalesgoal goal, CrmTimeperiod period)
    {
        goal.setPeriodId(period.getPeriodId());
        goal.setPeriodName(period.getPeriodName());
        goal.setPeriodType(PERIOD_TYPE_MONTHLY);
        goal.setMonthId(period.getPeriodId());
        goal.setMonthName(period.getPeriodName());
        goal.setQuarterId(period.getQuarterId());
        goal.setQuarterName(period.getQuarterName());
        goal.setFiscalId(period.getFiscalId());
        goal.setFiscalName(period.getFiscalName());
    }

    private void assertUnique(String ownerId, String periodId, String kpiCode, String excludeGoalId)
    {
        int count = crmSalesgoalMapper.countByOwnerPeriodKpi(ownerId, periodId, kpiCode, excludeGoalId);
        if (count > 0)
        {
            throw new ServiceException("销售目标重复：同一人同一月同一 KPI 已存在");
        }
    }

    private void fillOwnerNameIfBlank(CrmSalesgoal goal)
    {
        if (StringUtils.hasText(goal.getOwner()) || !StringUtils.hasText(goal.getOwnerId()))
        {
            return;
        }
        goal.setOwner(resolveUserNick(goal.getOwnerId()));
    }

    private String resolveUserNick(String ownerId)
    {
        try
        {
            Long uid = Long.valueOf(ownerId);
            SysUser user = sysUserService.selectUserById(uid);
            if (user != null)
            {
                return StringUtils.hasText(user.getNickName()) ? user.getNickName() : user.getUserName();
            }
        }
        catch (Exception ignored)
        {
        }
        return ownerId;
    }

    private void fillAuditUserDept(CrmSalesgoal goal, boolean create)
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null)
            {
                return;
            }
            if (create)
            {
                goal.setCreateBy(loginUser.getUsername());
            }
            if (goal.getUserId() == null && StringUtils.hasText(goal.getOwnerId()))
            {
                try
                {
                    goal.setUserId(Long.valueOf(goal.getOwnerId()));
                }
                catch (NumberFormatException ignored)
                {
                    goal.setUserId(loginUser.getUserId());
                }
            }
            if (goal.getDeptId() == null && loginUser.getUser() != null)
            {
                goal.setDeptId(loginUser.getDeptId());
            }
        }
        catch (Exception ignored)
        {
        }
    }

    private List<String> resolveDefaultOwnerIds(Map<String, String> ownerNames)
    {
        AssignableUserQuery query = new AssignableUserQuery();
        query.setPurpose("opty_owner_dropdown");
        query.setRoleKeys(ensureRoleKeys);
        query.setScope("tenant");
        query.setWecomBound(false);
        query.setPageSize(500);
        query.setPageNum(1);
        List<AssignableUserVo> users = assignableUserService.selectAssignableUserList(query);
        List<String> ids = new ArrayList<>();
        if (users == null)
        {
            return ids;
        }
        for (AssignableUserVo u : users)
        {
            if (u.getUserId() == null)
            {
                continue;
            }
            String id = String.valueOf(u.getUserId());
            ids.add(id);
            String name = StringUtils.hasText(u.getNickName()) ? u.getNickName() : u.getUserName();
            ownerNames.put(id, name != null ? name : id);
        }
        return ids;
    }

    static Long parseIsActive(String raw)
    {
        if (!StringUtils.hasText(raw))
        {
            return null;
        }
        String v = raw.trim();
        if ("0".equals(v))
        {
            return IS_ACTIVE_ASSESSING;
        }
        if ("1".equals(v))
        {
            return IS_ACTIVE_IDLE;
        }
        try
        {
            long n = Long.parseLong(v);
            if (n == 0L || n == 1L)
            {
                return n;
            }
        }
        catch (NumberFormatException ignored)
        {
        }
        return null;
    }
}
