package com.ruoyi.web.controller.crm.wechatmp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.wechatmp.domain.CrmWechatMpMessageTask;
import com.ruoyi.crm.wechatmp.dto.WechatMpMessageBatchRequest;
import com.ruoyi.crm.wechatmp.service.IWechatMpMessageTaskService;

@RestController
@RequestMapping({ "/crm/wechat_mp", "/wechat_mp" })
public class WechatMpMessageTaskController extends BaseController
{
    @Autowired
    private IWechatMpMessageTaskService messageTaskService;

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @Log(title = "公众号批量发送", businessType = BusinessType.INSERT)
    @PostMapping("/message/batch")
    public AjaxResult batch(@RequestBody WechatMpMessageBatchRequest req, HttpServletRequest request)
    {
        String tenant = request.getHeader("tenant");
        return AjaxResult.success(messageTaskService.createBatch(tenant, req));
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/message/task/list")
    public TableDataInfo list(CrmWechatMpMessageTask query)
    {
        startPage();
        List<CrmWechatMpMessageTask> list = messageTaskService.selectTaskList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:wechat_mp:message')")
    @GetMapping("/message/task/{id}")
    public AjaxResult detail(@PathVariable Long id)
    {
        return AjaxResult.success(messageTaskService.getTaskDetail(id));
    }
}
