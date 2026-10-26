package com.ruoyi.web.controller.crm;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.crm.domain.CrmContactQuery;
import com.ruoyi.crm.domain.CrmInvestor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.sql.SqlUtil;
import com.github.pagehelper.PageHelper;
import com.ruoyi.crm.domain.CrmContact;
import com.ruoyi.crm.domain.vo.SubjectRegistrationRowVo;
import com.ruoyi.crm.service.ICrmContactService;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;
import com.ruoyi.crm.service.ICrmMiniappMarketEventRegisterService;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.crm.wecom.WecomContactConvertSyncSupport;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 联系人Controller
 * 
 * @author luohd
 * @date 2024-02-02
 */
@RestController
@RequestMapping("/crm/contact")
public class CrmContactController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(CrmContactController.class);

    private static final int REG_DEFAULT_PAGE_SIZE = 50;
    private static final int REG_MAX_PAGE_SIZE = 200;

    @Autowired
    private ICrmContactService crmContactService;

    @Autowired
    private IPatientClvService patientClvService;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    @Autowired
    private ICrmWecomExternalContactService wecomExternalContactService;

    @Autowired
    private WecomContactConvertSyncSupport wecomContactConvertSyncSupport;

    @Autowired
    private com.ruoyi.crm.service.ICrmLeadPoolService leadPoolService;

    @Autowired
    private ICrmMiniappMarketEventRegisterService marketEventRegisterService;

    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping("/{contactId}/source-lead")
    public AjaxResult sourceLead(@PathVariable String contactId)
    {
        return success(leadPoolService.findSourceLeadByContactId(contactId));
    }

    /**
     * 联系人详情：报名列表（含 H5 / market_event 等全部来源）
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping("/{contactId}/registrations")
    public TableDataInfo listRegistrations(@PathVariable("contactId") String contactId,
            @RequestParam(value = "registrationSource", required = false) String registrationSource,
            @RequestHeader(value = "tenant", required = false) String tenant)
    {
        String tenantId = tenant;
        if (tenantId == null || tenantId.isEmpty())
        {
            tenantId = getLoginUser().getTenant();
        }
        startPageCappedForRegistrations();
        List<SubjectRegistrationRowVo> list = marketEventRegisterService.listByContactId(
                tenantId, contactId, registrationSource);
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
     * 查询联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmContact crmContact)
    {
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactList(crmContact);
        return getDataTable(list);
    }

    /**
     * 查询联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping("/existing")
    public TableDataInfo checkexisting(CrmContact crmContact)
    {
        startPage();
        List<CrmContact> list = crmContactService.checkContactExisting(crmContact);
        return getDataTable(list);
    }
    /**
     * 查询联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping("/listjson")
    public TableDataInfo listjson(CrmInvestor crmInvestor)
    {
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactFromJSON(crmInvestor);
        return getDataTable(list);
    }

    /**
     * 根据基金id查询投资人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping("/listbyaccountid")
    public TableDataInfo listbyaccountid(CrmInvestor crmInvestor)
    {
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactListByAccountId(crmInvestor);
        return getDataTable(list);
    }

    /**
     * 根据household id查询联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping(value = "/getByHouseholdId/{householdId}")
    public TableDataInfo listbyhouseholdid(@PathVariable("householdId")String householdId) {
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactListByHouseholdId(householdId);
        return getDataTable(list);
    }

    /**
     * 查询联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping("/listweekly")
    public TableDataInfo listweekly(CrmContact crmContact)
    {
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactListWeekly(crmContact);
        return getDataTable(list);
    }
    /**
     * 查询今天过生日的联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @GetMapping("/listbirthday")
    public TableDataInfo listBirthday(CrmContact crmContact)
    {
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactBirthdayList(crmContact);
        return getDataTable(list);
    }
    /**
     * 查询联系人列表by tags
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:list')")
    @PostMapping("/listtag")
    public TableDataInfo listByTags(@RequestBody CrmContactQuery crmContactQuery) {
//        logger.info("query string = " + crmContactQuery.toString());
//
//        return null;
        startPage();
        List<CrmContact> list = crmContactService.selectCrmContactListByTags(crmContactQuery);
        return getDataTable(list);
    }

    /**
     * 导出联系人列表
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:export')")
    @Log(title = "联系人", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmContact crmContact)
    {
        List<CrmContact> list = crmContactService.selectCrmContactList(crmContact);
        ExcelUtil<CrmContact> util = new ExcelUtil<CrmContact>(CrmContact.class);
        util.exportExcel(response, list, "联系人数据");
    }

    /**
     * 影像门户 SSO：302 至已配置的 imagingPortalUrl（不落库长期 URL 给未鉴权方）。
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping("/imaging-sso/{contactId}")
    public void imagingSso(@PathVariable("contactId") String contactId, HttpServletResponse response) throws IOException
    {
        CrmContact c = crmContactService.selectCrmContactByContactId(contactId);
        if (c == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "联系人不存在");
            return;
        }
        if (c.getImagingPortalUrl() == null || c.getImagingPortalUrl().isEmpty())
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "未配置影像门户链接");
            return;
        }
        logger.info("imaging-sso contactId={} user={}", contactId, getUsername());
        response.sendRedirect(c.getImagingPortalUrl().trim());
    }

    /**
     * 患者 CLV 详情
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping("/{contactId}/clv")
    public AjaxResult contactClv(@PathVariable("contactId") String contactId)
    {
        return success(patientClvService.getContactClv(contactId));
    }

    /**
     * 获取联系人详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping(value = "/{contactId}")
    public AjaxResult getInfo(@PathVariable("contactId") String contactId)
    {
        return success(crmContactService.selectCrmContactByContactId(contactId));
    }

    /**
     * 新增联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:add')")
    @Log(title = "联系人", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmContact crmContact)
    {
        int rows = crmContactService.insertCrmContact(crmContact);
        if (rows > 0 && crmContact.getContactId() != null)
        {
            syncContactIdentities(crmContact, "contact_form");
            wecomContactConvertSyncSupport.syncAfterContactCreate(crmContact);
        }
        return toAjax(rows);
    }

    /**
     * 修改联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:edit')")
    @Log(title = "联系人", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmContact crmContact)
    {
        int rows = crmContactService.updateCrmContact(crmContact);
        if (rows > 0 && crmContact.getContactId() != null)
        {
            syncContactIdentities(crmContact, "contact_form");
        }
        return toAjax(rows);
    }

    /**
     * 同步 Contact 的身份字段到 crm_customer_identity 表。
     */
    private void syncContactIdentities(CrmContact c, String source)
    {
        String contactId = c.getContactId();
        try
        {
            if (c.getMobilePhone() != null && !c.getMobilePhone().isEmpty())
            {
                identityService.bindIdentity(contactId, "mobile_phone", c.getMobilePhone(), source);
            }
            if (c.getPersonalEmail() != null && !c.getPersonalEmail().isEmpty())
            {
                identityService.bindIdentity(contactId, "email", c.getPersonalEmail(), source);
            }
            if (c.getWorkEmail() != null && !c.getWorkEmail().isEmpty()
                    && !c.getWorkEmail().equals(c.getPersonalEmail()))
            {
                identityService.bindIdentity(contactId, "email", c.getWorkEmail(), source);
            }
            if (c.getWecomExternalUserid() != null && !c.getWecomExternalUserid().isEmpty())
            {
                identityService.bindIdentity(contactId, "wecom_external_userid", c.getWecomExternalUserid(), source);
                wecomExternalContactService.upsertFromCrmBinding(
                        c.getWecomExternalUserid(), null, contactId, null);
            }
            if (c.getWechat() != null && !c.getWechat().isEmpty())
            {
                identityService.bindIdentity(contactId, "wechat_id", c.getWechat(), source);
            }
        }
        catch (Exception e)
        {
            log.warn("Failed to sync identity for contactId={}: {}", contactId, e.getMessage());
        }
    }

    /**
     * 删除联系人
     */
    @PreAuthorize("@ss.hasPermi('crm:contact:remove')")
    @Log(title = "联系人", businessType = BusinessType.DELETE)
	@DeleteMapping("/{contactIds}")
    public AjaxResult remove(@PathVariable String[] contactIds)
    {
        return toAjax(crmContactService.deleteCrmContactByContactIds(contactIds));
    }
}
