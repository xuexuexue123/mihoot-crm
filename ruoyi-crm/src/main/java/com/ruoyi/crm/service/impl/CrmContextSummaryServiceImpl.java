package com.ruoyi.crm.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.crm.domain.ContextSummaryScope;
import com.ruoyi.crm.domain.CrmAccount;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.CrmOrg;
import com.ruoyi.crm.domain.CrmOpty;
import com.ruoyi.crm.domain.vo.ContextSummaryVo;
import com.ruoyi.crm.domain.vo.ContextSummaryVo.RecentActivityItem;
import com.ruoyi.crm.mapper.CrmContextSummaryMapper;
import com.ruoyi.crm.service.ICrmAccountService;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmContextSummaryScopeService;
import com.ruoyi.crm.service.ICrmContextSummaryService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmOrgService;
import com.ruoyi.crm.service.ICrmOptyService;

@Service
public class CrmContextSummaryServiceImpl implements ICrmContextSummaryService
{
    private static final Set<String> SUPPORTED_TYPES = new HashSet<>();
    private static final int RECENT_PULSE_LIMIT = 5;

    static
    {
        SUPPORTED_TYPES.add("organization");
        SUPPORTED_TYPES.add("account");
        SUPPORTED_TYPES.add("contact");
        SUPPORTED_TYPES.add("opportunity");
        SUPPORTED_TYPES.add("lead");
    }

    @Autowired
    private ICrmOrgService crmOrgService;

    @Autowired
    private ICrmAccountService crmAccountService;

    @Autowired
    private ICrmContactService crmContactService;

    @Autowired
    private ICrmOptyService crmOptyService;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ICrmContextSummaryScopeService crmContextSummaryScopeService;

    @Autowired
    private CrmContextSummaryMapper crmContextSummaryMapper;

    @Override
    public ContextSummaryVo getSummary(String tenantId, String entityType, String entityId)
    {
        requireTenantId(tenantId);
        entityType = normalizeEntityType(entityType);
        if (StringUtils.isBlank(entityId))
        {
            throw new ServiceException("entityId 不能为空", HttpStatus.BAD_REQUEST);
        }
        entityId = entityId.trim();
        assertAccessible(entityType, entityId);

        switch (entityType)
        {
            case "organization":
                return buildOrganizationSummary(entityId);
            case "account":
                return buildAccountSummary(entityId);
            case "contact":
                return buildContactSummary(entityId);
            case "opportunity":
                return buildOpportunitySummary(entityId);
            case "lead":
                return buildLeadSummary(entityId);
            default:
                throw new ServiceException("不支持的 entityType: " + entityType, HttpStatus.BAD_REQUEST);
        }
    }

    private ContextSummaryVo buildOrganizationSummary(String orgId)
    {
        CrmOrg org = crmOrgService.selectCrmOrgByOrgId(orgId);
        if (org == null)
        {
            throw new ServiceException("机构不存在", HttpStatus.NOT_FOUND);
        }
        ContextSummaryVo vo = baseVo("organization", orgId, org.getFullName());
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("owner", org.getOwner());
        fields.put("industry", org.getIndustry());
        vo.setFields(fields);

        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setOrgId(orgId);
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("opportunity", nz(crmContextSummaryMapper.countOptyByOrgId(scope)));
        counts.put("openTask", nz(crmContextSummaryMapper.countOpenTaskByOrgId(scope)));
        counts.put("contact", nz(crmContextSummaryMapper.countContactByOrgId(scope)));
        counts.put("contract", nz(crmContextSummaryMapper.countContractByOrgId(scope)));
        vo.setRelatedCounts(counts);
        vo.setRecentActivity(loadRecentActivity(orgId, "机构"));
        return vo;
    }

    private ContextSummaryVo buildAccountSummary(String accountId)
    {
        CrmAccount account = crmAccountService.selectCrmAccountByAccountId(accountId);
        if (account == null)
        {
            throw new ServiceException("客户不存在", HttpStatus.NOT_FOUND);
        }
        ContextSummaryVo vo = baseVo("account", accountId, account.getName());
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("owner", account.getOwner());
        fields.put("industry", account.getIndustry());
        vo.setFields(fields);

        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setAccountId(accountId);
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("opportunity", nz(crmContextSummaryMapper.countOptyByAccountId(scope)));
        counts.put("openTask", nz(crmContextSummaryMapper.countOpenTaskByAccountId(scope)));
        counts.put("contact", nz(crmContextSummaryMapper.countContactByAccountId(scope)));
        counts.put("contract", nz(crmContextSummaryMapper.countContractByAccountId(scope)));
        vo.setRelatedCounts(counts);
        vo.setRecentActivity(loadRecentActivity(accountId, "客户"));
        return vo;
    }

    private ContextSummaryVo buildContactSummary(String contactId)
    {
        CrmContact contact = crmContactService.selectCrmContactByContactId(contactId);
        if (contact == null)
        {
            throw new ServiceException("联系人不存在", HttpStatus.NOT_FOUND);
        }
        ContextSummaryVo vo = baseVo("contact", contactId, contact.getName());
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("owner", contact.getOwner());
        fields.put("email", contact.getWorkEmail());
        vo.setFields(fields);

        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setContactId(contactId);
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("opportunity", nz(crmContextSummaryMapper.countOptyByContactId(scope)));
        counts.put("openTask", nz(crmContextSummaryMapper.countOpenTaskByContactId(scope)));
        vo.setRelatedCounts(counts);
        vo.setRecentActivity(loadRecentActivity(contactId, "联系人"));
        return vo;
    }

    private ContextSummaryVo buildOpportunitySummary(String optyId)
    {
        CrmOpty opty = crmOptyService.selectCrmOptyByOptyId(optyId);
        if (opty == null)
        {
            throw new ServiceException("商机不存在", HttpStatus.NOT_FOUND);
        }
        ContextSummaryVo vo = baseVo("opportunity", optyId, opty.getName());
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("owner", opty.getOwner());
        fields.put("stage", opty.getStage());
        vo.setFields(fields);

        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setOptyId(optyId);
        Map<String, Long> counts = new HashMap<>();
        counts.put("openTask", nz(crmContextSummaryMapper.countOpenTaskByOptyId(scope)));
        vo.setRelatedCounts(counts);
        vo.setRecentActivity(loadRecentActivity(optyId, "销售机会"));
        return vo;
    }

    private ContextSummaryVo buildLeadSummary(String leadId)
    {
        CrmLead lead = crmLeadService.selectCrmLeadByLeadId(leadId);
        if (lead == null)
        {
            throw new ServiceException("线索不存在", HttpStatus.NOT_FOUND);
        }
        ContextSummaryVo vo = baseVo("lead", leadId, lead.getLeadName());
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("owner", lead.getOwner());
        fields.put("stage", lead.getStage());
        vo.setFields(fields);

        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setLeadId(leadId);
        Map<String, Long> counts = new HashMap<>();
        counts.put("opportunity", nz(crmContextSummaryMapper.countOptyByLeadId(scope)));
        vo.setRelatedCounts(counts);
        vo.setRecentActivity(loadRecentActivity(leadId, "业务线索"));
        return vo;
    }

    private List<RecentActivityItem> loadRecentActivity(String entityId, String pulseRelateType)
    {
        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setEntityId(entityId);
        scope.setPulseRelateType(pulseRelateType);
        scope.setPulseLimit(RECENT_PULSE_LIMIT);
        List<Map<String, Object>> rows = crmContextSummaryScopeService.selectRecentPulse(scope);
        List<RecentActivityItem> items = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (Map<String, Object> row : rows)
        {
            RecentActivityItem item = new RecentActivityItem();
            item.setPulseType(stringVal(row.get("pulse_type")));
            item.setMessage(stringVal(row.get("message_data")));
            if (row.get("create_time") != null)
            {
                item.setCreateTime(fmt.format(row.get("create_time")));
            }
            items.add(item);
        }
        return items;
    }

    private void assertAccessible(String entityType, String entityId)
    {
        ContextSummaryScope scope = new ContextSummaryScope();
        scope.setEntityId(entityId);
        Long count;
        switch (entityType)
        {
            case "organization":
                count = crmContextSummaryScopeService.countAccessibleOrg(scope);
                break;
            case "account":
                count = crmContextSummaryScopeService.countAccessibleAccount(scope);
                break;
            case "contact":
                count = crmContextSummaryScopeService.countAccessibleContact(scope);
                break;
            case "opportunity":
                count = crmContextSummaryScopeService.countAccessibleOpty(scope);
                break;
            case "lead":
                count = crmContextSummaryScopeService.countAccessibleLead(scope);
                break;
            default:
                throw new ServiceException("不支持的 entityType: " + entityType, HttpStatus.BAD_REQUEST);
        }
        if (count == null || count <= 0)
        {
            throw new ServiceException("无权访问该实体", HttpStatus.FORBIDDEN);
        }
    }

    private ContextSummaryVo baseVo(String entityType, String entityId, String displayName)
    {
        ContextSummaryVo vo = new ContextSummaryVo();
        vo.setEntityType(entityType);
        vo.setEntityId(entityId);
        vo.setDisplayName(displayName);
        return vo;
    }

    private String normalizeEntityType(String entityType)
    {
        if (StringUtils.isBlank(entityType))
        {
            throw new ServiceException("entityType 不能为空", HttpStatus.BAD_REQUEST);
        }
        entityType = entityType.trim().toLowerCase();
        if (!SUPPORTED_TYPES.contains(entityType))
        {
            throw new ServiceException("不支持的 entityType: " + entityType, HttpStatus.BAD_REQUEST);
        }
        return entityType;
    }

    private void requireTenantId(String tenantId)
    {
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("缺少租户头 tenant", HttpStatus.BAD_REQUEST);
        }
    }

    private long nz(Long value)
    {
        return value != null ? value : 0L;
    }

    private String stringVal(Object value)
    {
        return value != null ? value.toString() : "";
    }
}
