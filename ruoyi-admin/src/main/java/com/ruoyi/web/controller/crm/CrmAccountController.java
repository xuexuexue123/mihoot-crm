package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmAccount;
import com.ruoyi.crm.service.ICrmAccountService;
import com.ruoyi.crm.service.IPatientClvService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 客户Controller
 * 
 * @author luohd
 * @date 2024-01-28
 */
@RestController
@RequestMapping({ "/crm/account", "/crm/serviceaccount" })
public class CrmAccountController extends BaseController
{
    @Autowired
    private ICrmAccountService crmAccountService;

    @Autowired
    private IPatientClvService patientClvService;

    /**
     * 查询客户列表
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:account:list,crm:serviceaccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmAccount crmAccount)
    {
        startPage();
        List<CrmAccount> list = crmAccountService.selectCrmAccountList(crmAccount);
        return getDataTable(list);
    }

    /**
     * 根据LP查询客户列表
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:account:list,crm:serviceaccount:list')")
    @GetMapping("/getbylp/{accountId}")
    public AjaxResult getbylp(@PathVariable("accountId") String accountId)
    {
        return success(crmAccountService.selectCrmAccountByLp(accountId));
    }


    /**
     * 根据LP查询客户列表
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:account:list,crm:serviceaccount:list')")
    @GetMapping("/getbypart/{partName}")
    public TableDataInfo getbypart(@PathVariable("partName") String partName)
    {
        startPage();
        List<CrmAccount> list = crmAccountService.selectCrmAccountByParticipant(partName);
        return getDataTable(list);
    }

    /**
     * 导出客户列表
     */
    @PreAuthorize("@ss.hasPermi('crm:account:export')")
    @Log(title = "客户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmAccount crmAccount)
    {
        List<CrmAccount> list = crmAccountService.selectCrmAccountList(crmAccount);
        ExcelUtil<CrmAccount> util = new ExcelUtil<CrmAccount>(CrmAccount.class);
        util.exportExcel(response, list, "客户数据");
    }

    @PreAuthorize("@ss.hasPermi('crm:account:query')")
    @GetMapping("/family-ltv/{accountId}")
    public AjaxResult familyLtv(@PathVariable("accountId") String accountId)
    {
        return success(crmAccountService.selectFamilyLtvByAccountId(accountId));
    }

    @PreAuthorize("@ss.hasPermi('crm:serviceaccount:query')")
    @GetMapping("/family-clv/{accountId}")
    public AjaxResult familyClv(@PathVariable("accountId") String accountId)
    {
        return success(patientClvService.getFamilyClv(accountId));
    }

    /**
     * 获取客户详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:account:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") String accountId)
    {
        return success(crmAccountService.selectCrmAccountByAccountId(accountId));
    }

    /**
     * 新增客户
     */
    @PreAuthorize("@ss.hasPermi('crm:account:add')")
    @Log(title = "客户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmAccount crmAccount)
    {
        return toAjax(crmAccountService.insertCrmAccount(crmAccount));
    }

    /**
     * 修改客户
     */
    @PreAuthorize("@ss.hasPermi('crm:account:edit')")
    @Log(title = "客户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmAccount crmAccount)
    {
        return toAjax(crmAccountService.updateCrmAccount(crmAccount));
    }

    /**
     * 修改客户（路径中带 accountId，兼容 PUT /crm/account/{id}、PUT /crm/serviceaccount/{id}）
     */
    @PreAuthorize("@ss.hasPermi('crm:account:edit')")
    @Log(title = "客户", businessType = BusinessType.UPDATE)
    @PutMapping("/{accountId}")
    public AjaxResult editByAccountId(@PathVariable("accountId") String accountId, @RequestBody CrmAccount crmAccount)
    {
        crmAccount.setAccountId(accountId);
        return toAjax(crmAccountService.updateCrmAccount(crmAccount));
    }

    /**
     * 删除客户
     */
    @PreAuthorize("@ss.hasPermi('crm:account:remove')")
    @Log(title = "客户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable String[] accountIds)
    {
        return toAjax(crmAccountService.deleteCrmAccountByAccountIds(accountIds));
    }
}
