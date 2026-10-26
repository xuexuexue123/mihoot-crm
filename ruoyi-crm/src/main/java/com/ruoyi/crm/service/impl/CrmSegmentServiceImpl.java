package com.ruoyi.crm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.CrmSegment;
import com.ruoyi.crm.domain.dto.SegmentPreviewRequest;
import com.ruoyi.crm.domain.dto.SegmentSaveRequest;
import com.ruoyi.crm.domain.vo.SegmentEnumValueVo;
import com.ruoyi.crm.domain.vo.SegmentFieldMetaVo;
import com.ruoyi.crm.domain.vo.SegmentMemberVo;
import com.ruoyi.crm.domain.vo.SegmentPreviewVo;
import com.ruoyi.crm.domain.vo.SegmentVo;
import com.ruoyi.crm.mapper.CrmSegmentMapper;
import com.ruoyi.crm.service.ICrmSegmentService;
import com.ruoyi.crm.service.ICrmTagService;
import com.ruoyi.crm.service.support.CompiledSegmentQuery;
import com.ruoyi.crm.service.support.SegmentFieldRegistry;
import com.ruoyi.crm.service.support.SegmentRuleCompiler;
import com.ruoyi.crm.service.support.SegmentRuleValidator;

@Service
public class CrmSegmentServiceImpl implements ICrmSegmentService
{
    @Autowired
    private CrmSegmentMapper crmSegmentMapper;

    @Autowired
    private SegmentRuleCompiler segmentRuleCompiler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Lazy
    private ICrmTagService crmTagService;

    @Override
    public List<SegmentVo> selectList(String tenantId)
    {
        List<SegmentVo> list = crmSegmentMapper.selectList(requireTenantId(tenantId));
        list.forEach(this::normalizeRuleJson);
        return list;
    }

    @Override
    public SegmentVo getById(String tenantId, Long id)
    {
        tenantId = requireTenantId(tenantId);
        if (id == null)
        {
            return null;
        }
        SegmentVo vo = crmSegmentMapper.selectById(tenantId, id);
        normalizeRuleJson(vo);
        return vo;
    }

    @Override
    public void add(String tenantId, SegmentSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        validateSaveRequest(request, false);
        String ruleJsonText = toRuleJsonText(request.getRuleJson());
        long count = countByRule(request.getRuleJson());
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();

        CrmSegment row = new CrmSegment();
        row.setTenantId(tenantId);
        row.setName(request.getName().trim());
        row.setDescription(StringUtils.trimToNull(request.getDescription()));
        row.setRuleJson(ruleJsonText);
        row.setType("dynamic");
        row.setEstSize(Math.toIntExact(count));
        row.setComputedAt(now);
        row.setStatus(normalizeStatus(request.getStatus()));
        row.setCreateBy(username);
        row.setCreateTime(now);
        row.setUpdateBy(username);
        row.setUpdateTime(now);
        crmSegmentMapper.insertCrmSegment(row);
    }

    @Override
    public void update(String tenantId, SegmentSaveRequest request)
    {
        tenantId = requireTenantId(tenantId);
        validateSaveRequest(request, true);
        CrmSegment existing = crmSegmentMapper.selectEntityById(tenantId, request.getId());
        if (existing == null)
        {
            throw new ServiceException("分群不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        String ruleJsonText = toRuleJsonText(request.getRuleJson());
        long count = countByRule(request.getRuleJson());
        String username = SecurityUtils.getUsername();
        Date now = DateUtils.getNowDate();

        existing.setName(request.getName().trim());
        existing.setDescription(StringUtils.trimToNull(request.getDescription()));
        existing.setRuleJson(ruleJsonText);
        existing.setEstSize(Math.toIntExact(count));
        existing.setComputedAt(now);
        existing.setStatus(normalizeStatus(request.getStatus()));
        existing.setUpdateBy(username);
        existing.setUpdateTime(now);
        crmSegmentMapper.updateCrmSegment(existing);
    }

    @Override
    public void remove(String tenantId, Long id)
    {
        tenantId = requireTenantId(tenantId);
        if (id == null)
        {
            return;
        }
        CrmSegment existing = crmSegmentMapper.selectEntityById(tenantId, id);
        if (existing == null)
        {
            return;
        }
        crmSegmentMapper.deleteById(tenantId, id);
    }

    @Override
    public SegmentPreviewVo preview(String tenantId, SegmentPreviewRequest request)
    {
        requireTenantId(tenantId);
        if (request == null || request.getRuleJson() == null)
        {
            throw new ServiceException("缺少 ruleJson", HttpStatus.BAD_REQUEST);
        }
        return new SegmentPreviewVo(countByRule(request.getRuleJson()));
    }

    @Override
    public long countMembers(String tenantId, Long id)
    {
        tenantId = requireTenantId(tenantId);
        CrmSegment segment = crmSegmentMapper.selectEntityById(tenantId, id);
        if (segment == null)
        {
            throw new ServiceException("分群不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        Object ruleJson = parseRuleJsonObject(segment.getRuleJson());
        return countByRule(ruleJson);
    }

    @Override
    public List<SegmentMemberVo> selectMembers(String tenantId, Long id, int pageNum, int pageSize)
    {
        tenantId = requireTenantId(tenantId);
        CrmSegment segment = crmSegmentMapper.selectEntityById(tenantId, id);
        if (segment == null)
        {
            throw new ServiceException("分群不存在或不属于当前租户", HttpStatus.BAD_REQUEST);
        }
        Object ruleJson = parseRuleJsonObject(segment.getRuleJson());
        return queryMembersByRule(ruleJson, pageNum, pageSize);
    }

    @Override
    public List<SegmentMemberVo> previewMembers(String tenantId, Object ruleJson, int pageNum, int pageSize)
    {
        requireTenantId(tenantId);
        if (ruleJson == null)
        {
            throw new ServiceException("缺少 ruleJson", HttpStatus.BAD_REQUEST);
        }
        SegmentRuleValidator.validate(ruleJson);
        return queryMembersByRule(ruleJson, pageNum, pageSize);
    }

    @Override
    public long countByRuleJson(String tenantId, Object ruleJson)
    {
        requireTenantId(tenantId);
        if (ruleJson == null)
        {
            throw new ServiceException("缺少 ruleJson", HttpStatus.BAD_REQUEST);
        }
        return countByRule(ruleJson);
    }

    @Override
    public List<SegmentFieldMetaVo> listFields()
    {
        List<SegmentFieldMetaVo> fields = SegmentFieldRegistry.allFieldMeta();
        List<SegmentEnumValueVo> tagEnums = crmTagService.listEnabledEnumValues();
        for (SegmentFieldMetaVo field : fields)
        {
            if (SegmentFieldRegistry.SOURCE_TAG.equals(field.getSource())
                    && SegmentFieldRegistry.FIELD_CRM_TAG.equals(field.getField()))
            {
                field.setEnumValues(tagEnums);
            }
        }
        return fields;
    }

    private List<SegmentMemberVo> queryMembersByRule(Object ruleJson, int pageNum, int pageSize)
    {
        CompiledSegmentQuery query = segmentRuleCompiler.compile(ruleJson);
        List<Object> args = new ArrayList<>(query.getParams());
        String sql = query.membersSql() + " LIMIT ? OFFSET ?";
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.max(pageSize, 1);
        int offset = (safePageNum - 1) * safePageSize;
        args.add(safePageSize);
        args.add(offset);
        return jdbcTemplate.query(sql, args.toArray(), (rs, rowNum) -> {
            SegmentMemberVo member = new SegmentMemberVo();
            member.setContactId(rs.getString("contact_id"));
            member.setName(rs.getString("name"));
            member.setMobilePhone(rs.getString("mobile_phone"));
            return member;
        });
    }

    private long countByRule(Object ruleJson)
    {
        CompiledSegmentQuery query = segmentRuleCompiler.compile(ruleJson);
        Long count = jdbcTemplate.queryForObject(query.countSql(), query.getParams().toArray(), Long.class);
        return count == null ? 0L : count;
    }

    private void validateSaveRequest(SegmentSaveRequest request, boolean requireId)
    {
        if (request == null)
        {
            throw new ServiceException("请求体不能为空", HttpStatus.BAD_REQUEST);
        }
        if (requireId && request.getId() == null)
        {
            throw new ServiceException("缺少分群 ID", HttpStatus.BAD_REQUEST);
        }
        if (StringUtils.isBlank(request.getName()))
        {
            throw new ServiceException("分群名称不能为空", HttpStatus.BAD_REQUEST);
        }
        if (request.getRuleJson() == null)
        {
            throw new ServiceException("缺少 ruleJson", HttpStatus.BAD_REQUEST);
        }
        SegmentRuleValidator.validate(request.getRuleJson());
    }

    private String toRuleJsonText(Object ruleJson)
    {
        if (ruleJson instanceof String)
        {
            return (String) ruleJson;
        }
        return JSON.toJSONString(ruleJson);
    }

    private Object parseRuleJsonObject(String ruleJson)
    {
        if (StringUtils.isBlank(ruleJson))
        {
            return new JSONObject();
        }
        return JSON.parse(ruleJson);
    }

    private void normalizeRuleJson(SegmentVo vo)
    {
        if (vo == null || vo.getRuleJson() == null)
        {
            return;
        }
        if (vo.getRuleJson() instanceof String)
        {
            vo.setRuleJson(JSON.parse((String) vo.getRuleJson()));
        }
    }

    private String normalizeStatus(String status)
    {
        if (StringUtils.isBlank(status))
        {
            return "active";
        }
        String normalized = status.trim().toLowerCase();
        if ("active".equals(normalized) || "archived".equals(normalized))
        {
            return normalized;
        }
        throw new ServiceException("status 必须为 active 或 archived", HttpStatus.BAD_REQUEST);
    }

    private String requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
        return tenantId.trim();
    }
}
