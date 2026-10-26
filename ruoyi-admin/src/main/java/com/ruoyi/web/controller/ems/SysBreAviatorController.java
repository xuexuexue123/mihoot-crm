package com.ruoyi.web.controller.ems;

import com.github.pagehelper.PageInfo;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.parser.ExpressionParser;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.ems.domain.SysAviatorExpression;
import com.ruoyi.ems.domain.SysCasetype;
import com.ruoyi.flowable.service.ISysBreAviatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * casetypeController
 *
 * @author luohd
 * @date 2023-01-18
 */

@RestController
@RequestMapping("/bre/aviator")
public class SysBreAviatorController  extends BaseController {

    @Autowired
    ISysBreAviatorService breAviatorService;

    /**
     * 获取给定的表达式的所有变量
     */
//    @PreAuthorize("@ss.hasPermi('bre:casetype:list')")
    @PostMapping("/getvars")
    public TableDataInfo getvars(@RequestBody SysAviatorExpression expression)
    {

        List<SysAviatorExpression> vars = breAviatorService.getVariableNames(expression);

        return getDataTable(vars);
    }


    /**
     * 获取给定的表达式的所有变量
     */
//    @PreAuthorize("@ss.hasPermi('bre:casetype:list')")
    @GetMapping("/exec")
    public AjaxResult getexeresult(String expression, Map<String, Object> variables)
    {

        Boolean result = breAviatorService.getExpressionExecutedResult(expression, variables);

        return toAjax(result);
    }




    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> vars)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(vars);
        rspData.setTotal(new PageInfo(vars).getTotal());
        return rspData;
    }
}
