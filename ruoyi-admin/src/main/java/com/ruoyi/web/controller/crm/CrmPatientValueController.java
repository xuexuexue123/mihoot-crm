package com.ruoyi.web.controller.crm;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.CrmPatientValue;
import com.ruoyi.crm.domain.dto.PatientClvRecomputeRequest;
import com.ruoyi.crm.service.IPatientClvService;

@RestController
@RequestMapping("/crm/patient-value")
public class CrmPatientValueController extends BaseController
{
    @Autowired
    private IPatientClvService patientClvService;

    @PreAuthorize("@ss.hasPermi('crm:contact:query')")
    @GetMapping("/list")
    public TableDataInfo list(CrmPatientValue query)
    {
        startPage();
        List<CrmPatientValue> list = patientClvService.listPatientValues(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:patient-value:recompute')")
    @PostMapping("/recompute")
    public AjaxResult recompute(@RequestBody PatientClvRecomputeRequest request)
    {
        return success(patientClvService.recompute(request));
    }
}
