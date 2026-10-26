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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmQuote;
import com.ruoyi.crm.service.ICrmQuoteService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ems.domain.SysSequenceId;
import com.ruoyi.ems.service.ISysSequenceIdService;

/**
 * 报价单Controller
 * 
 * @author luohd
 * @date 2024-02-10
 */
@RestController
@RequestMapping("/crm/quote")
public class CrmQuoteController extends BaseController
{
    @Autowired
    private ICrmQuoteService crmQuoteService;

    @Autowired
    private ISysSequenceIdService sysSequenceIdService;

    /**
     * 查询报价单列表
     */
    @PreAuthorize("@ss.hasPermi('crm:quote:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmQuote crmQuote)
    {
        startPage();
        List<CrmQuote> list = crmQuoteService.selectCrmQuoteList(crmQuote);
        return getDataTable(list);
    }

    /**
     * 导出报价单列表
     */
    @PreAuthorize("@ss.hasPermi('crm:quote:export')")
    @Log(title = "报价单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmQuote crmQuote)
    {
        List<CrmQuote> list = crmQuoteService.selectCrmQuoteList(crmQuote);
        ExcelUtil<CrmQuote> util = new ExcelUtil<CrmQuote>(CrmQuote.class);
        util.exportExcel(response, list, "报价单数据");
    }

    /**
     * 获取报价单详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:quote:query')")
    @GetMapping(value = "/{quoteId}")
    public AjaxResult getInfo(@PathVariable("quoteId") String quoteId)
    {
        return success(crmQuoteService.selectCrmQuoteByQuoteId(quoteId));
    }

    /**
     * 新增报价单
     */
    @PreAuthorize("@ss.hasPermi('crm:quote:add')")
    @Log(title = "报价单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmQuote crmQuote)
    {
        return toAjax(crmQuoteService.insertCrmQuote(crmQuote));
    }

    /**
     * 修改报价单
     */
    @PreAuthorize("@ss.hasPermi('crm:quote:edit')")
    @Log(title = "报价单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmQuote crmQuote)
    {
        return toAjax(crmQuoteService.updateCrmQuote(crmQuote));
    }

    /**
     * 删除报价单
     */
    @PreAuthorize("@ss.hasPermi('crm:quote:remove')")
    @Log(title = "报价单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{quoteIds}")
    public AjaxResult remove(@PathVariable String[] quoteIds)
    {
        return toAjax(crmQuoteService.deleteCrmQuoteByQuoteIds(quoteIds));
    }

    /**
     * 获取报价单编号序列号
     * 查询并递增指定前缀的序列号，返回当前序列号数值
     */
    @GetMapping("/sequence/{prefix}")
    public AjaxResult getSequenceId(@PathVariable("prefix") String prefix)
    {
        SysSequenceId seq = sysSequenceIdService.selectSysSequenceIdByPrefix(prefix);
        if (seq == null)
        {
            seq = new SysSequenceId();
            seq.setPrefix(prefix);
            seq.setCurrentNumber(1L);
            sysSequenceIdService.insertSysSequenceId(seq);
            return success(1);
        }
        Long currentNumber = seq.getCurrentNumber();
        seq.setCurrentNumber(currentNumber + 1);
        sysSequenceIdService.updateSysSequenceId(seq);
        return success(currentNumber);
    }

    /**
     * 上传报价单文件
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(@RequestParam("file") MultipartFile file)
    {
        return AjaxResult.success("ok");
    }
}
