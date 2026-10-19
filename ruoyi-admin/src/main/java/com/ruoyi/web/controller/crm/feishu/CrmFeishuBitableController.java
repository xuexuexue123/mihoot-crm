package com.ruoyi.web.controller.crm.feishu;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.crm.feishu.dto.FeishuBitableExportResult;
import com.ruoyi.crm.feishu.dto.FeishuBitableRequest;
import com.ruoyi.crm.feishu.service.ICrmFeishuBitableService;

/**
 * 飞书多维表格代理。不加 @PreAuthorize（对齐 WecomTokenController），供 BFF / 工作流调用。
 */
@RestController
@RequestMapping("/crm/feishu/bitable")
public class CrmFeishuBitableController extends BaseController
{
    private static final Pattern FEISHU_CODE = Pattern.compile("feishuCode=(\\d+)");

    @Autowired
    private ICrmFeishuBitableService feishuBitableService;

    @PostMapping("/apps/create")
    public AjaxResult createApp(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.createApp(body));
    }

    @PostMapping("/tables/create")
    public AjaxResult createTable(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.createTable(body));
    }

    @PostMapping("/tables/list")
    public AjaxResult listTables(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.listTables(body));
    }

    @PostMapping("/fields/list")
    public AjaxResult listFields(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.listFields(body));
    }

    @PostMapping("/records/list")
    public AjaxResult listRecords(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.listRecords(body));
    }

    @PostMapping("/records/create")
    public AjaxResult createRecord(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.createRecord(body));
    }

    @PostMapping("/records/batch_create")
    public AjaxResult batchCreateRecords(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.batchCreateRecords(body));
    }

    @PostMapping("/records/update")
    public AjaxResult updateRecord(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.updateRecord(body));
    }

    @PostMapping("/records/search")
    public AjaxResult searchRecords(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.searchRecords(body));
    }

    @PostMapping("/records/delete")
    public AjaxResult deleteRecord(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.deleteRecord(body));
    }

    @PostMapping("/records/batch_delete")
    public AjaxResult batchDeleteRecords(@RequestBody FeishuBitableRequest body)
    {
        return invoke(() -> feishuBitableService.batchDeleteRecords(body));
    }

    /**
     * 分页拉取记录并下载 CSV / Excel（format=csv|xlsx，默认 xlsx）。
     */
    @PostMapping("/records/export")
    public void exportRecords(@RequestBody FeishuBitableRequest body, HttpServletResponse response)
    {
        try
        {
            FeishuBitableExportResult file = feishuBitableService.exportRecords(body);
            response.setContentType(file.getContentType());
            response.setCharacterEncoding("utf-8");
            FileUtils.setAttachmentResponseHeader(response, file.getFilename());
            response.getOutputStream().write(file.getContent());
            response.getOutputStream().flush();
        }
        catch (ServiceException e)
        {
            writeJsonError(response, e);
        }
        catch (Exception e)
        {
            logger.error("飞书多维表格导出失败", e);
            writeJsonError(response, new ServiceException("导出失败"));
        }
    }

    private void writeJsonError(HttpServletResponse response, ServiceException e)
    {
        try
        {
            response.reset();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            AjaxResult err = AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
            Integer feishuCode = extractFeishuCode(e.getDetailMessage());
            if (feishuCode != null)
            {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("feishuCode", feishuCode);
                err.put(AjaxResult.DATA_TAG, data);
            }
            response.getWriter().write(com.alibaba.fastjson2.JSON.toJSONString(err));
        }
        catch (Exception ignored)
        {
            // ignore secondary write failure
        }
    }

    private AjaxResult invoke(SupplierMap call)
    {
        try
        {
            return AjaxResult.success(call.get());
        }
        catch (ServiceException e)
        {
            AjaxResult err = AjaxResult.error(HttpStatus.BAD_REQUEST, e.getMessage());
            Integer feishuCode = extractFeishuCode(e.getDetailMessage());
            if (feishuCode != null)
            {
                Map<String, Object> data = new LinkedHashMap<>();
                data.put("feishuCode", feishuCode);
                err.put(AjaxResult.DATA_TAG, data);
            }
            return err;
        }
    }

    private static Integer extractFeishuCode(String detail)
    {
        if (detail == null || detail.isEmpty())
        {
            return null;
        }
        Matcher m = FEISHU_CODE.matcher(detail);
        if (m.find())
        {
            try
            {
                return Integer.parseInt(m.group(1));
            }
            catch (NumberFormatException ignored)
            {
                return null;
            }
        }
        return null;
    }

    @FunctionalInterface
    private interface SupplierMap
    {
        Map<String, Object> get();
    }
}
