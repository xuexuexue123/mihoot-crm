package com.ruoyi.web.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.crm.domain.CrmWecomExternalContact;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactImportRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactImportResult;
import com.ruoyi.crm.domain.dto.wecom.WecomExternalContactUpsertRequest;
import com.ruoyi.crm.service.ICrmWecomExternalContactService;

@RestController
@RequestMapping("/crm/wecom/external-contact")
public class CrmWecomExternalContactController extends BaseController
{
    @Autowired
    private ICrmWecomExternalContactService crmWecomExternalContactService;

    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/list")
    public TableDataInfo list(CrmWecomExternalContact query)
    {
        startPage();
        List<CrmWecomExternalContact> list = crmWecomExternalContactService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @GetMapping("/{id:\\d+}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        CrmWecomExternalContact row = crmWecomExternalContactService.selectById(id);
        return row == null ? AjaxResult.error("记录不存在") : AjaxResult.success(row);
    }

    /**
     * 从企业微信批量同步外部联系人（客户）并创建/更新本地映射与线索。
     */
    @PreAuthorize("@ss.hasPermi('crm:wecom:contact')")
    @Log(title = "企业微信外部联系人同步", businessType = BusinessType.UPDATE)
    @PostMapping("/import")
    public AjaxResult importFromWecom(@RequestBody(required = false) WecomExternalContactImportRequest req)
    {
        try
        {
            List<String> userids = req != null ? req.getUserids() : null;
            WecomExternalContactImportResult r = crmWecomExternalContactService.importFromWecom(userids);
            return AjaxResult.success(r);
        }
        catch (ServiceException e)
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 从 KF get_customer_info 响应持久化外部联系人（不创建 Lead，纯数据写入）。
     * 由 Next.js 服务端调用，无需 @PreAuthorize。
     */
    @PostMapping("/upsert")
    public AjaxResult upsert(@RequestBody WecomExternalContactUpsertRequest request)
    {
        Long id = crmWecomExternalContactService.upsertFromKfGetInfo(request);
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        return AjaxResult.success(data);
    }
}
