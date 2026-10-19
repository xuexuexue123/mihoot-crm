package com.ruoyi.web.controller.crm;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmIdentityConflict;
import com.ruoyi.crm.mapper.CrmIdentityConflictMapper;
import com.ruoyi.crm.service.ICrmCustomerIdentityService;

/**
 * 身份冲突待人工合并 Controller。
 * 列出同一身份值被解析到不同 Contact 的冲突，供运营确认后合并（复用 mergeIdentity）。
 */
@RestController
@RequestMapping("/crm/identity/conflict")
public class CrmIdentityConflictController extends BaseController
{
    @Autowired
    private CrmIdentityConflictMapper identityConflictMapper;

    @Autowired
    private ICrmCustomerIdentityService identityService;

    /**
     * 分页查询冲突（默认 pending）。
     */
    @PreAuthorize("@ss.hasPermi('crm:identity:conflict:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmIdentityConflict query)
    {
        if (query.getStatus() == null || query.getStatus().isEmpty())
        {
            query.setStatus("pending");
        }
        startPage();
        List<CrmIdentityConflict> list = identityConflictMapper.selectList(query);
        return getDataTable(list);
    }

    /**
     * 合并：将 sourceContactId 的身份并入 targetContactId，并将冲突标记为已解决。
     */
    @PreAuthorize("@ss.hasPermi('crm:identity:conflict:merge')")
    @Log(title = "身份冲突合并", businessType = BusinessType.UPDATE)
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody Map<String, Object> body)
    {
        String sourceContactId = asString(body.get("sourceContactId"));
        String targetContactId = asString(body.get("targetContactId"));
        Long conflictId = asLong(body.get("conflictId"));

        if (sourceContactId == null || sourceContactId.isEmpty()
                || targetContactId == null || targetContactId.isEmpty())
        {
            return AjaxResult.error("sourceContactId 与 targetContactId 不能为空");
        }
        if (sourceContactId.equals(targetContactId))
        {
            return AjaxResult.error("源与目标联系人不能相同");
        }

        Map<String, Integer> stats = identityService.mergeIdentity(sourceContactId, targetContactId);
        if (conflictId != null)
        {
            identityConflictMapper.markResolved(conflictId, currentUsername());
        }
        return AjaxResult.success(stats);
    }

    /**
     * 忽略一条冲突。
     */
    @PreAuthorize("@ss.hasPermi('crm:identity:conflict:merge')")
    @Log(title = "身份冲突忽略", businessType = BusinessType.UPDATE)
    @PostMapping("/ignore")
    public AjaxResult ignore(@RequestBody Map<String, Object> body)
    {
        Long conflictId = asLong(body.get("conflictId"));
        if (conflictId == null)
        {
            return AjaxResult.error("conflictId 不能为空");
        }
        int rows = identityConflictMapper.markIgnored(conflictId, currentUsername());
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("冲突不存在或已处理");
    }

    private String currentUsername()
    {
        try
        {
            return getUsername();
        }
        catch (Exception e)
        {
            return "system";
        }
    }

    private static String asString(Object v)
    {
        return v == null ? null : v.toString();
    }

    private static Long asLong(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof Number)
        {
            return ((Number) v).longValue();
        }
        try
        {
            return Long.parseLong(v.toString());
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}
