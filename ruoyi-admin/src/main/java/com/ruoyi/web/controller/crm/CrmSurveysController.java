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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.crm.domain.CrmSurveys;
import com.ruoyi.crm.domain.dto.SurveyOutreachSendRequest;
import com.ruoyi.crm.domain.dto.SurveyOutreachUrlRenderRequest;
import com.ruoyi.crm.domain.vo.SurveyOutreachStatsVo;
import com.ruoyi.crm.domain.vo.SurveyOutreachTaskVo;
import com.ruoyi.crm.domain.vo.SurveyOutreachUrlVo;
import com.ruoyi.crm.service.ICrmSurveysService;
import com.ruoyi.crm.service.support.SurveyOutreachSendService;
import com.ruoyi.crm.service.support.SurveyOutreachStatsService;
import com.ruoyi.crm.service.support.SurveyOutreachUrlService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 调查Controller
 * 
 * @author luohd
 * @date 2025-03-29
 */
@RestController
@RequestMapping("/crm/surveys")
public class CrmSurveysController extends BaseController
{
    @Autowired
    private ICrmSurveysService crmSurveysService;

    @Autowired
    private SurveyOutreachUrlService surveyOutreachUrlService;

    @Autowired
    private SurveyOutreachStatsService surveyOutreachStatsService;

    @Autowired
    private SurveyOutreachSendService surveyOutreachSendService;

    /**
     * 查询调查列表
     */
    @PreAuthorize("@ss.hasPermi('crm:surveys:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmSurveys crmSurveys)
    {
        startPage();
        List<CrmSurveys> list = crmSurveysService.selectCrmSurveysList(crmSurveys);
        return getDataTable(list);
    }

    /**
     * 导出调查列表
     */
    @PreAuthorize("@ss.hasPermi('crm:surveys:export')")
    @Log(title = "调查", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmSurveys crmSurveys)
    {
        List<CrmSurveys> list = crmSurveysService.selectCrmSurveysList(crmSurveys);
        ExcelUtil<CrmSurveys> util = new ExcelUtil<CrmSurveys>(CrmSurveys.class);
        util.exportExcel(response, list, "调查数据");
    }

    /**
     * 获取调查详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:surveys:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(crmSurveysService.selectCrmSurveysById(id));
    }

    @PreAuthorize("@ss.hasPermi('crm:surveys:query')")
    @GetMapping(value = "/{surveyId}/outreach-url")
    public AjaxResult getOutreachUrl(@PathVariable("surveyId") String surveyId)
    {
        SurveyOutreachUrlVo vo = surveyOutreachUrlService.issueGenericUrl(
                getLoginUser().getTenant(), surveyId);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:surveys:query')")
    @PostMapping(value = "/{surveyId}/outreach-url/render")
    public AjaxResult renderOutreachUrl(@PathVariable("surveyId") String surveyId,
            @RequestBody SurveyOutreachUrlRenderRequest request)
    {
        if (request == null)
        {
            request = new SurveyOutreachUrlRenderRequest();
        }
        SurveyOutreachUrlVo vo = surveyOutreachUrlService.issueRenderUrl(
                getLoginUser().getTenant(),
                surveyId,
                request);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('crm:surveys:query')")
    @GetMapping(value = "/{surveyId}/outreach/tasks")
    public TableDataInfo listOutreachTasks(@PathVariable("surveyId") String surveyId,
            @RequestParam(value = "recipientId", required = false) String recipientId,
            @RequestParam(value = "channel", required = false) String channel)
    {
        startPage();
        List<SurveyOutreachTaskVo> list = surveyOutreachStatsService.listTasks(
                surveyId, recipientId, channel);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:surveys:query')")
    @PostMapping(value = "/{surveyId}/outreach/send")
    public AjaxResult confirmOutreachSend(@PathVariable("surveyId") String surveyId,
            @RequestBody SurveyOutreachSendRequest request)
    {
        return success(surveyOutreachSendService.confirmSend(surveyId, request));
    }

    @PreAuthorize("@ss.hasPermi('crm:surveys:query')")
    @GetMapping(value = "/{surveyId}/outreach/stats")
    public AjaxResult getOutreachStats(@PathVariable("surveyId") String surveyId)
    {
        SurveyOutreachStatsVo stats = surveyOutreachStatsService.getStats(surveyId);
        return success(stats);
    }

    /**
     * 新增调查
     */
    @PreAuthorize("@ss.hasPermi('crm:surveys:add')")
    @Log(title = "调查", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmSurveys crmSurveys)
    {
        return toAjax(crmSurveysService.insertCrmSurveys(crmSurveys));
    }

    /**
     * 修改调查
     */
    @PreAuthorize("@ss.hasPermi('crm:surveys:edit')")
    @Log(title = "调查", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmSurveys crmSurveys)
    {
        return toAjax(crmSurveysService.updateCrmSurveys(crmSurveys));
    }

    /**
     * 删除调查
     */
    @PreAuthorize("@ss.hasPermi('crm:surveys:remove')")
    @Log(title = "调查", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(crmSurveysService.deleteCrmSurveysByIds(ids));
    }
}
