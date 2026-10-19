package com.ruoyi.web.controller.crm;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.crm.domain.CrmSubjectMap;
import com.ruoyi.crm.service.ICrmSubjectMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Subject 继承映射 Controller
 *
 * @author luohd
 * @date 2026-06-10
 */
@RestController
@RequestMapping("/crm/subject-map")
public class CrmSubjectMapController extends BaseController
{
    @Autowired
    private ICrmSubjectMapService subjectMapService;

    /**
     * 创建 Subject 继承关系（Lead → Contact 转换时调用）
     * 请求体示例：
     * {
     *   "parentSubjectType": "Contact",
     *   "parentSubjectId": "C1001",
     *   "childSubjectType": "Lead",
     *   "childSubjectId": "L1001"
     * }
     */
    @PreAuthorize("@ss.hasPermi('crm:activity:add')")
    @PostMapping
    public AjaxResult create(@RequestBody CrmSubjectMap map)
    {
        subjectMapService.upsert(map);
        return success();
    }
}
