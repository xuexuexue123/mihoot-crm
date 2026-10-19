package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.crm.domain.CrmAccountrelation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.ruoyi.crm.domain.CrmLead;
import com.ruoyi.crm.domain.dto.LeadConvertEvaluateResult;
import com.ruoyi.crm.domain.dto.MiniappLeadUpsertRequest;
import com.ruoyi.crm.domain.dto.MiniappMarketEventRegisterRequest;
import com.ruoyi.crm.domain.vo.LeadQualifyOutreachUrlVo;
import com.ruoyi.crm.domain.vo.SubjectRegistrationRowVo;
import com.ruoyi.crm.service.ICrmLeadConvertService;
import com.ruoyi.crm.service.ICrmLeadService;
import com.ruoyi.crm.service.ICrmMiniappLeadService;
import com.ruoyi.crm.service.ICrmMiniappMarketEventRegisterService;
import com.ruoyi.crm.service.support.LeadQualifyOutreachUrlService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 业务线索Controller
 * 
 * @author luohd
 * @date 2024-01-23
 */
@RestController
@RequestMapping("/crm/lead")
public class CrmLeadController extends BaseController
{
    private static final int REG_DEFAULT_PAGE_SIZE = 50;
    private static final int REG_MAX_PAGE_SIZE = 200;

    @Autowired
    private ICrmLeadService crmLeadService;

    @Autowired
    private ICrmLeadConvertService crmLeadConvertService;

    @Autowired
    private LeadQualifyOutreachUrlService leadQualifyOutreachUrlService;

    @Autowired
    private ICrmMiniappLeadService crmMiniappLeadService;

    @Autowired
    private ICrmMiniappMarketEventRegisterService marketEventRegisterService;

    @Autowired
    private com.ruoyi.crm.service.impl.LeadDedupeService leadDedupeService;

    /**
     * 小程序 BFF：按手机号查找活跃未转化线索（须在 /{leadId} 之前声明）
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:miniapp') or @ss.hasPermi('crm:lead:list')")
    @GetMapping("/by-mobile")
    public AjaxResult byMobile(@RequestParam("mobilePhone") String mobilePhone)
    {
        try
        {
            return success(crmMiniappLeadService.findByMobile(mobilePhone));
        }
        catch (ServiceException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 小程序 BFF：按手机号幂等 upsert 线索
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:miniapp') or (@ss.hasPermi('crm:lead:add') and @ss.hasPermi('crm:lead:edit'))")
    @Log(title = "小程序线索Upsert", businessType = BusinessType.UPDATE)
    @RateLimiter(time = 60, count = 30, limitType = LimitType.DEFAULT)
    @PostMapping("/miniapp/upsert")
    public AjaxResult miniappUpsert(@RequestBody MiniappLeadUpsertRequest request)
    {
        try
        {
            return success(crmMiniappLeadService.upsert(request));
        }
        catch (ServiceException e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 小程序 BFF：七库市场活动报名（Lead/Contact + registration + Activity）
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:miniapp') or (@ss.hasPermi('crm:lead:add') and @ss.hasPermi('crm:lead:edit'))")
    @Log(title = "小程序七库活动报名", businessType = BusinessType.INSERT)
    @RateLimiter(time = 60, count = 30, limitType = LimitType.DEFAULT)
    @PostMapping("/miniapp/market-event-register")
    public AjaxResult marketEventRegister(@RequestBody MiniappMarketEventRegisterRequest request,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        String tenantId = tenant;
        if (tenantId == null || tenantId.isEmpty())
        {
            tenantId = getLoginUser().getTenant();
        }
        return success(marketEventRegisterService.register(tenantId, request));
    }

    /**
     * 线索详情：报名列表（含 H5 / market_event 等全部来源）
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:query')")
    @GetMapping("/{leadId}/registrations")
    public TableDataInfo listRegistrations(@PathVariable("leadId") String leadId,
            @RequestParam(value = "registrationSource", required = false) String registrationSource,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        String tenantId = tenant;
        if (tenantId == null || tenantId.isEmpty())
        {
            tenantId = getLoginUser().getTenant();
        }
        startPageCappedForRegistrations();
        List<SubjectRegistrationRowVo> list = marketEventRegisterService.listByLeadId(
                tenantId, leadId, registrationSource);
        return getDataTable(list);
    }

    private void startPageCappedForRegistrations()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum == null || pageNum < 1)
        {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1)
        {
            pageSize = REG_DEFAULT_PAGE_SIZE;
        }
        else if (pageSize > REG_MAX_PAGE_SIZE)
        {
            pageSize = REG_MAX_PAGE_SIZE;
        }
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(pageDomain.getReasonable());
    }

    /**
     * 查询业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmLead crmLead)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadList(crmLead);
        return getDataTable(list);
    }

    /**
     * 根据基金，查询业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/listbyaccount")
    public TableDataInfo listbyaccount(CrmAccountrelation crmAccountrelation)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadListByRelation(crmAccountrelation);
        return getDataTable(list);
    }

    /**
     * 查询业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/listcriteria")
    public TableDataInfo listcriteria(CrmLead crmLead)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadListByCriteria(crmLead);
        return getDataTable(list);
    }

    /**
     * 查询本周业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmLead crmLead)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadListWeekly(crmLead);
        return getDataTable(list);
    }

    /**
     * 查询本周已转化的业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/listweeklyconvert")
    public TableDataInfo listweeklyconvert(CrmLead crmLead)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadListWeeklyConverted(crmLead);
        return getDataTable(list);
    }
    /**
     * 查询最近三月业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/listquarterly")
    public TableDataInfo listquarterly(CrmLead crmLead)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadListQuarterly(crmLead);
        return getDataTable(list);
    }
    /**
     * 查询业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:list')")
    @GetMapping("/pendinglist")
    public TableDataInfo pendinglist(CrmLead crmLead)
    {
        startPage();
        List<CrmLead> list = crmLeadService.selectCrmLeadPendingList(crmLead);
        return getDataTable(list);
    }

    /**
     * 导出业务线索列表
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:export')")
    @Log(title = "业务线索", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmLead crmLead)
    {
        List<CrmLead> list = crmLeadService.selectCrmLeadListExport(crmLead);
        ExcelUtil<CrmLead> util = new ExcelUtil<CrmLead>(CrmLead.class);
        util.exportExcel(response, list, "Leads", "", crmLead.getLang());
    }

    /**
     * 获取业务线索详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:query')")
    @GetMapping(value = "/{leadId}")
    public AjaxResult getInfo(@PathVariable("leadId") String leadId)
    {
        CrmLead lead = crmLeadService.selectCrmLeadByLeadId(leadId);
        if (lead != null && com.ruoyi.crm.service.support.AudienceLeadVisibility.isConvertedStatus(lead.getStatus()))
        {
            lead.setArchived(true);
        }
        return success(lead);
    }

    /**
     * 签发认定潜客问卷外投链接（QR / 复制）
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:query') or @ss.hasPermi('crm:lead:edit')")
    @PostMapping("/{leadId}/qualify-outreach-url")
    public AjaxResult issueQualifyOutreachUrl(@PathVariable("leadId") String leadId)
    {
        LeadQualifyOutreachUrlVo vo = leadQualifyOutreachUrlService.issueUrl(getLoginUser().getTenant(), leadId);
        return success(vo);
    }

    /**
     * 新增业务线索
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:add')")
    @Log(title = "业务线索", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmLead crmLead)
    {
        com.ruoyi.crm.domain.dto.LeadDedupeMatch match = leadDedupeService.findDuplicateLead(crmLead);
        if (match != null && match.isDuplicate())
        {
            AjaxResult result = AjaxResult.error(409, "线索已存在");
            result.put("duplicate", true);
            result.put("leadId", match.getId());
            result.put("matchedField", match.getMatchedField());
            return result;
        }
        return toAjax(crmLeadService.insertCrmLead(crmLead));
    }

    @PreAuthorize("@ss.hasPermi('crm:lead:add') or @ss.hasPermi('crm:lead:list')")
    @PostMapping("/dedupe/check")
    public AjaxResult checkDedupe(@RequestBody com.ruoyi.crm.domain.dto.LeadDedupeCheckRequest body)
    {
        com.ruoyi.crm.domain.CrmLead probe = new CrmLead();
        if (body != null)
        {
            probe.setLeadId(body.getLeadId());
            probe.setMobilePhone(body.getMobilePhone());
            probe.setWecomExternalUserid(body.getWecomExternalUserid());
            probe.setWorkPhone(body.getWorkPhone());
            probe.setWorkEmail(body.getWorkEmail());
            probe.setWechatMpOpenid(body.getWechatMpOpenid());
        }
        String entity = body == null || body.getEntity() == null ? "lead" : body.getEntity();
        com.ruoyi.crm.domain.dto.LeadDedupeMatch match;
        if ("contact".equalsIgnoreCase(entity))
        {
            match = leadDedupeService.findDuplicateContact(probe, null);
        }
        else
        {
            match = leadDedupeService.findDuplicateLead(probe);
        }
        if (match == null)
        {
            java.util.Map<String, Object> empty = new java.util.HashMap<>();
            empty.put("duplicate", false);
            return success(empty);
        }
        return success(match);
    }

    /**
     * 修改业务线索
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:edit')")
    @Log(title = "业务线索", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmLead crmLead)
    {
        CrmLead existing = crmLeadService.selectCrmLeadByLeadId(crmLead.getLeadId());
        int rows = crmLeadService.updateCrmLead(crmLead);
        AjaxResult result = toAjax(rows);
        if (rows > 0 && existing != null)
        {
            LeadConvertEvaluateResult convertEvaluate = crmLeadConvertService.evaluateStageArrived(
                    existing, crmLeadService.selectCrmLeadByLeadId(crmLead.getLeadId()));
            if (convertEvaluate != null)
            {
                result.put("convertEvaluate", convertEvaluate);
            }
        }
        return result;
    }

    /**
     * 删除业务线索
     */
    @PreAuthorize("@ss.hasPermi('crm:lead:remove')")
    @Log(title = "业务线索", businessType = BusinessType.DELETE)
	@DeleteMapping("/{leadIds}")
    public AjaxResult remove(@PathVariable String[] leadIds)
    {
        return toAjax(crmLeadService.deleteCrmLeadByLeadIds(leadIds));
    }
}
