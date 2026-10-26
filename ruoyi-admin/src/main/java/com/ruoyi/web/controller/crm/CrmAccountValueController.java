package com.ruoyi.web.controller.crm;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.crm.domain.CrmAccountValue;
import com.ruoyi.crm.service.IPatientClvService;

@RestController
@RequestMapping("/crm/account-value")
public class CrmAccountValueController extends BaseController
{
    @Autowired
    private IPatientClvService patientClvService;

    @PreAuthorize("@ss.hasPermi('crm:serviceaccount:query')")
    @GetMapping("/list")
    public TableDataInfo list(CrmAccountValue query)
    {
        startPage();
        List<CrmAccountValue> list = patientClvService.listAccountValues(query);
        return getDataTable(list);
    }
}
