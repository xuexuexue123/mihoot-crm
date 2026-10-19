package com.ruoyi.web.controller.crm;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.vo.CampaignRoiVo;
import com.ruoyi.crm.service.IPatientClvService;

@RestController
@RequestMapping("/crm/campaign-roi")
public class CrmCampaignRoiController extends BaseController
{
    @Autowired
    private IPatientClvService patientClvService;

    @PreAuthorize("@ss.hasPermi('crm:marketing:stats')")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(value = "campaignType", defaultValue = "all") String campaignType,
            @RequestParam(value = "campaignId", required = false) String campaignId,
            @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo)
    {
        startPage();
        List<CampaignRoiVo> list = patientClvService.listCampaignRoi(campaignType, campaignId, dateFrom, dateTo);
        return getDataTable(list);
    }
}
