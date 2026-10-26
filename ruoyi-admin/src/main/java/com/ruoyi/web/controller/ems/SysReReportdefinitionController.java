package com.ruoyi.web.controller.ems;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.domain.SysReDataobj;
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
import com.ruoyi.ems.domain.SysReReportdefinition;
import com.ruoyi.ems.service.ISysReReportdefinitionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报表定义Controller
 * 
 * @author luohd
 * @date 2023-03-06
 */
@RestController
@RequestMapping("/ems/reportdefinition")
public class SysReReportdefinitionController extends BaseController
{
    @Autowired
    private ISysReReportdefinitionService sysReReportdefinitionService;

    /**
     * 查询报表定义列表
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReReportdefinition sysReReportdefinition)
    {
        startPage();
        System.out.println("sysReReportdefinition = " + sysReReportdefinition.toString());
        List<SysReReportdefinition> list = sysReReportdefinitionService.selectSysReReportdefinitionList(sysReReportdefinition);
        return getDataTable(list);
    }

    /**
     * 查询报表定义列表
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:list')")
    @GetMapping("/list/{dataobjId}")
    public TableDataInfo listByDataobj(@PathVariable("dataobjId") String dataobjId)
    {
        startPage();
        System.out.println("sysReReportdefinition = " + dataobjId);
        List<SysReReportdefinition> list = sysReReportdefinitionService.selectSysReReportdefinitionListByDataobj(dataobjId);
        return getDataTable(list);
    }

    /**
     * 导出报表定义列表
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:export')")
    @Log(title = "报表定义", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReReportdefinition sysReReportdefinition)
    {
        List<SysReReportdefinition> list = sysReReportdefinitionService.selectSysReReportdefinitionList(sysReReportdefinition);
        ExcelUtil<SysReReportdefinition> util = new ExcelUtil<SysReReportdefinition>(SysReReportdefinition.class);
        util.exportExcel(response, list, "报表定义数据");
    }

    /**
     * 获取报表定义详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(sysReReportdefinitionService.selectSysReReportdefinitionById(id));
    }

    /**
     * 新增报表定义
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:add')")
    @Log(title = "报表定义", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReReportdefinition sysReReportdefinition)
    {
        return toAjax(sysReReportdefinitionService.insertSysReReportdefinition(sysReReportdefinition));
    }

    /**
     * 修改报表定义
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:edit')")
    @Log(title = "报表定义", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReReportdefinition sysReReportdefinition)
    {
        return toAjax(sysReReportdefinitionService.updateSysReReportdefinition(sysReReportdefinition));
    }

    /**
     * 删除报表定义
     */
    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:remove')")
    @Log(title = "报表定义", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(sysReReportdefinitionService.deleteSysReReportdefinitionByIds(ids));
    }


    @PreAuthorize("@ss.hasPermi('ems:reportdefinition:query')")
    @PostMapping("/getReportData")
    public List<HashMap<String,Object>> getTableData(@RequestBody SysReReportdefinition sysReReportdefinition){
//        String tableName = sysReReportdefinition.getTableName();
//        String columns = "name, gender, salary";
//        String tableName = "table_table_industry";
//        String filters = "name='章三'";

//        for (SysReDatafield sysReDatafield : sysReDataobj.getSysReDatafieldList()) {
//            String type = sysReDatafield.getFieldType();
//            String field = sysReDatafield.getFieldName();
//            columns += field + ",";
//        }

//        columns = columns.substring(0, columns.length()-1);
//        System.out.println("columns = " + columns);

        List<HashMap<String, Object>> hashMaps = sysReReportdefinitionService.selectReportDataByFilter(sysReReportdefinition.getColumns(),
                sysReReportdefinition.getParameters(), sysReReportdefinition.getFilters());
        return hashMaps;
    }


    @Anonymous
    @GetMapping(value = "/getReportDataByID/{id}")
    public List<HashMap<String,Object>> getReportDataByID(@PathVariable("id") String id){

        SysReReportdefinition sysReReportdefinition = sysReReportdefinitionService.selectSysReReportdefinitionById(id);

        JSONArray columns = JSON.parseArray(sysReReportdefinition.getColumns());
        JSONArray filters = JSON.parseArray(sysReReportdefinition.getFilters());

        String columnsStr = "";
        String filtersStr = "";
        String tableName = sysReReportdefinition.getParameters();

        for (int i = 0; i < columns.size(); i++) {
            JSONObject jb = columns.getJSONObject(i);
            columnsStr += jb.get("prop") + ",";

        }
        columnsStr = columnsStr.substring(0, columnsStr.length()-1);

        for (int i = 0; i < filters.size(); i++) {
            JSONObject jb = filters.getJSONObject(i);
            filtersStr = filtersStr + jb.get("source") + jb.get("relationship") + "'" + jb.get("value") + "'";

        }

        List<HashMap<String, Object>> hashMaps = sysReReportdefinitionService.selectReportDataByFilter(columnsStr,
                tableName, filtersStr);

        return hashMaps;
    }

}
