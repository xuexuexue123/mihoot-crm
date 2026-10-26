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
import com.ruoyi.crm.domain.CrmHouseholdmember;
import com.ruoyi.crm.service.ICrmHouseholdmemberService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 群组成员Controller
 * 
 * @author luohd
 * @date 2024-02-02
 */
@RestController
@RequestMapping("/crm/householdmember")
public class CrmHouseholdmemberController extends BaseController
{
    @Autowired
    private ICrmHouseholdmemberService crmHouseholdmemberService;

    /**
     * 查询群组成员列表
     */
    @PreAuthorize("@ss.hasPermi('crm:householdmember:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmHouseholdmember crmHouseholdmember)
    {
        startPage();
        List<CrmHouseholdmember> list = crmHouseholdmemberService.selectCrmHouseholdmemberList(crmHouseholdmember);
        return getDataTable(list);
    }

    /**
     * 导出群组成员列表
     */
    @PreAuthorize("@ss.hasPermi('crm:householdmember:export')")
    @Log(title = "群组成员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmHouseholdmember crmHouseholdmember)
    {
        List<CrmHouseholdmember> list = crmHouseholdmemberService.selectCrmHouseholdmemberList(crmHouseholdmember);
        ExcelUtil<CrmHouseholdmember> util = new ExcelUtil<CrmHouseholdmember>(CrmHouseholdmember.class);
        util.exportExcel(response, list, "群组成员数据");
    }

    /**
     * 获取群组成员详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:householdmember:query')")
    @GetMapping(value = "/{memberId}")
    public AjaxResult getInfo(@PathVariable("memberId") String memberId)
    {
        return success(crmHouseholdmemberService.selectCrmHouseholdmemberByMemberId(memberId));
    }

    /**
     * 新增群组成员
     */
    @PreAuthorize("@ss.hasPermi('crm:householdmember:add')")
    @Log(title = "群组成员", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmHouseholdmember crmHouseholdmember)
    {
        return toAjax(crmHouseholdmemberService.insertCrmHouseholdmember(crmHouseholdmember));
    }

    /**
     * 修改群组成员
     */
    @PreAuthorize("@ss.hasPermi('crm:householdmember:edit')")
    @Log(title = "群组成员", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmHouseholdmember crmHouseholdmember)
    {
        return toAjax(crmHouseholdmemberService.updateCrmHouseholdmember(crmHouseholdmember));
    }

    /**
     * 删除群组成员
     */
    @PreAuthorize("@ss.hasPermi('crm:householdmember:remove')")
    @Log(title = "群组成员", businessType = BusinessType.DELETE)
	@DeleteMapping("/{memberIds}")
    public AjaxResult remove(@PathVariable String[] memberIds)
    {
        return toAjax(crmHouseholdmemberService.deleteCrmHouseholdmemberByMemberIds(memberIds));
    }
}
