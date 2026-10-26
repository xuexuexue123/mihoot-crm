package com.ruoyi.web.controller.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmCustomerIdentity;
import com.ruoyi.crm.domain.dto.identity.IdentityResolveBatchItem;
import com.ruoyi.crm.domain.dto.identity.IdentityResolveBatchRequest;
import com.ruoyi.crm.domain.vo.IdentityResolveResultVo;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;

/**
 * 客户身份Controller
 *
 * @author luohd
 * @date 2026-06-16
 */
@RestController
@RequestMapping("/crm/identity")
public class CrmCustomerIdentityController extends BaseController
{
    @Autowired
    private ICrmCustomerIdentityService identityService;

    /**
     * 按身份标识查找 Contact
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:query,crm:contact:query')")
    @GetMapping("/resolve")
    public AjaxResult resolve(
            @RequestParam("identityType") String identityType,
            @RequestParam("identityValue") String identityValue)
    {
        IdentityResolveResultVo resolveResult = identityService.resolveIdentity(identityType, identityValue);
        String contactId = resolveResult.getContactId();
        String leadId = resolveResult.getLeadId();
        boolean matched = contactId != null || leadId != null;

        AjaxResult result = AjaxResult.success();
        result.put("identityType", identityType);
        result.put("identityValue", identityValue);
        result.put("contactId", contactId);
        result.put("leadId", leadId);
        result.put("matched", matched);
        if (resolveResult.getMatchedVia() != null)
        {
            result.put("matchedVia", resolveResult.getMatchedVia());
        }
        return result;
    }

    /**
     * 批量按身份标识查找 Contact
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:query,crm:contact:query')")
    @PostMapping("/resolve/batch")
    public AjaxResult resolveBatch(@RequestBody List<CrmCustomerIdentity> identities)
    {
        Map<String, String> resultMap = identityService.batchResolveIdentities(identities);
        return AjaxResult.success(resultMap);
    }

    /**
     * 批量 resolve（文档约定路径与数组响应）
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:query,crm:contact:query')")
    @PostMapping("/resolve-batch")
    public AjaxResult resolveBatchExtended(@RequestBody IdentityResolveBatchRequest request)
    {
        List<CrmCustomerIdentity> identities = new ArrayList<>();
        if (request != null && request.getItems() != null)
        {
            for (IdentityResolveBatchItem item : request.getItems())
            {
                if (item == null)
                {
                    continue;
                }
                CrmCustomerIdentity id = new CrmCustomerIdentity();
                id.setIdentityType(item.getIdentityType());
                id.setIdentityValue(item.getIdentityValue());
                identities.add(id);
            }
        }
        List<IdentityResolveResultVo> resolved = identityService.batchResolveIdentitiesExtended(identities);
        List<Map<String, Object>> data = new ArrayList<>();
        for (IdentityResolveResultVo vo : resolved)
        {
            Map<String, Object> row = new HashMap<>();
            row.put("identityType", vo.getIdentityType());
            row.put("identityValue", vo.getIdentityValue());
            row.put("contactId", vo.getContactId());
            row.put("leadId", vo.getLeadId());
            row.put("matched", vo.isMatched());
            if (vo.getMatchedVia() != null)
            {
                row.put("matchedVia", vo.getMatchedVia());
            }
            data.add(row);
        }
        return AjaxResult.success(data);
    }

    /**
     * 查询 Contact 的所有身份列表
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:query,crm:contact:query')")
    @GetMapping("/list/{contactId}")
    public TableDataInfo listByContact(@PathVariable("contactId") String contactId)
    {
        List<CrmCustomerIdentity> list = identityService.listIdentitiesByContact(contactId);
        return getDataTable(list);
    }

    /**
     * 绑定身份
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:edit,crm:contact:edit')")
    @Log(title = "客户身份", businessType = BusinessType.INSERT)
    @PostMapping("/bind")
    public AjaxResult bind(@RequestBody CrmCustomerIdentity identity)
    {
        identityService.bindIdentity(
                identity.getContactId(),
                identity.getIdentityType(),
                identity.getIdentityValue(),
                identity.getSource() != null ? identity.getSource() : "manual"
        );
        return AjaxResult.success();
    }

    /**
     * 解绑身份
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:edit,crm:contact:edit')")
    @Log(title = "客户身份", businessType = BusinessType.DELETE)
    @DeleteMapping("/unbind")
    public AjaxResult unbind(@RequestBody CrmCustomerIdentity identity)
    {
        identityService.unbindIdentity(
                identity.getContactId(),
                identity.getIdentityType(),
                identity.getIdentityValue()
        );
        return AjaxResult.success();
    }

    /**
     * 设为主身份
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:edit,crm:contact:edit')")
    @Log(title = "客户身份", businessType = BusinessType.UPDATE)
    @PutMapping("/primary")
    public AjaxResult setPrimary(@RequestBody CrmCustomerIdentity identity)
    {
        identityService.setPrimaryIdentity(
                identity.getContactId(),
                identity.getIdentityType(),
                identity.getIdentityValue()
        );
        return AjaxResult.success();
    }

    /**
     * 合并两个 Contact 的身份
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:identity:edit,crm:contact:edit')")
    @Log(title = "客户身份", businessType = BusinessType.UPDATE)
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody Map<String, String> params)
    {
        String sourceContactId = params.get("sourceContactId");
        String targetContactId = params.get("targetContactId");
        Map<String, Integer> stats = identityService.mergeIdentity(sourceContactId, targetContactId);
        return AjaxResult.success(stats);
    }
}
