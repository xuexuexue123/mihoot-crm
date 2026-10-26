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
import com.ruoyi.crm.domain.CrmSurveyresult;
import com.ruoyi.crm.service.ICrmSurveyresultService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 调查结论Controller
 * 
 * @author luohd
 * @date 2024-05-18
 */
@RestController
@RequestMapping("/crm/surveyresult")
public class CrmSurveyresultController extends BaseController
{
    @Autowired
    private ICrmSurveyresultService crmSurveyresultService;

    /**
     * 查询调查结论列表
     */
    @PreAuthorize("@ss.hasPermi('crm:surveyresult:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmSurveyresult crmSurveyresult)
    {
        startPage();
        List<CrmSurveyresult> list = crmSurveyresultService.selectCrmSurveyresultList(crmSurveyresult);
        return getDataTable(list);
    }

    /**
     * 导出调查结论列表
     */
    @PreAuthorize("@ss.hasPermi('crm:surveyresult:export')")
    @Log(title = "调查结论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmSurveyresult crmSurveyresult)
    {
        List<CrmSurveyresult> list = crmSurveyresultService.selectCrmSurveyresultList(crmSurveyresult);
        ExcelUtil<CrmSurveyresult> util = new ExcelUtil<CrmSurveyresult>(CrmSurveyresult.class);
        util.exportExcel(response, list, "调查结论数据");
    }

    /**
     * 获取调查结论详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:surveyresult:query')")
    @GetMapping(value = "/{resultId}")
    public AjaxResult getInfo(@PathVariable("resultId") String resultId)
    {
        return success(crmSurveyresultService.selectCrmSurveyresultByResultId(resultId));
    }

    /**
     * 新增调查结论
     */
    @PreAuthorize("@ss.hasPermi('crm:surveyresult:add')")
    @Log(title = "调查结论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmSurveyresult crmSurveyresult)
    {
        return toAjax(crmSurveyresultService.insertCrmSurveyresult(crmSurveyresult));
    }

    /**
     * 修改调查结论
     */
    @PreAuthorize("@ss.hasPermi('crm:surveyresult:edit')")
    @Log(title = "调查结论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmSurveyresult crmSurveyresult)
    {
        return toAjax(crmSurveyresultService.updateCrmSurveyresult(crmSurveyresult));
    }

    /**
     * 删除调查结论
     */
    @PreAuthorize("@ss.hasPermi('crm:surveyresult:remove')")
    @Log(title = "调查结论", businessType = BusinessType.DELETE)
	@DeleteMapping("/{resultIds}")
    public AjaxResult remove(@PathVariable String[] resultIds)
    {
        return toAjax(crmSurveyresultService.deleteCrmSurveyresultByResultIds(resultIds));
    }
}
