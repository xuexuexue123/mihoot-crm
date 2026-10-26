package com.ruoyi.web.controller.ems;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.ems.domain.SysReDatafield;
import com.ruoyi.ems.domain.SysReFieldPOJO;
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
import com.ruoyi.ems.domain.SysReDataobj;
import com.ruoyi.ems.service.ISysReDataobjService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 数据对象Controller
 * 
 * @author luohd
 * @date 2023-03-02
 */
@RestController
@RequestMapping("/ems/dataobj")
public class SysReDataobjController extends BaseController
{
    @Autowired
    private ISysReDataobjService sysReDataobjService;
    private JSONObject jsonObject;

    /**
     * 查询数据对象列表
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysReDataobj sysReDataobj)
    {
        startPage();
        List<SysReDataobj> list = sysReDataobjService.selectSysReDataobjList(sysReDataobj);
        return getDataTable(list);
    }

    /**
     * 导出数据对象列表
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:export')")
    @Log(title = "数据对象", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysReDataobj sysReDataobj)
    {
        List<SysReDataobj> list = sysReDataobjService.selectSysReDataobjList(sysReDataobj);
        ExcelUtil<SysReDataobj> util = new ExcelUtil<SysReDataobj>(SysReDataobj.class);
        util.exportExcel(response, list, "数据对象数据");
    }

    /**
     * 获取数据对象详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:query')")
    @GetMapping(value = "/{dataobjId}")
    public AjaxResult getInfo(@PathVariable("dataobjId") String dataobjId)
    {
        return success(sysReDataobjService.selectSysReDataobjByDataobjId(dataobjId));
    }

    /**
     * 获取数据对象详细信息
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:query')")
    @GetMapping(value = "/by/{dataobjName}")
    public AjaxResult getInfoByName(@PathVariable("dataobjName") String dataobjName)
    {
        return success(sysReDataobjService.selectSysReDataobjByDataobjName(dataobjName));
    }

    /**
     * 新增数据对象
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:add')")
    @Log(title = "数据对象", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysReDataobj sysReDataobj)
    {
        return toAjax(sysReDataobjService.insertSysReDataobj(sysReDataobj));
    }

    @PreAuthorize("@ss.hasPermi('ems:dataobj:add')")
    @Log(title = "数据对象", businessType = BusinessType.INSERT)
    @PostMapping("/dynamicallyTable")
    public AjaxResult createDynamicallyTable(@RequestBody SysReDataobj sysReDataobj){
        System.out.println("############ create table");
        Map<String, String> mymap = new HashMap<>(); //使用map的key,value进行存值,key:"存的是field" ,value:"存储的是type"

        System.out.println("sysReDataobj.getSysReDatafieldList() = " + sysReDataobj.getSysReDatafieldList().toString());

        for (SysReDatafield sysReDatafield : sysReDataobj.getSysReDatafieldList()) {
            String type = sysReDatafield.getFieldType();
            String field = sysReDatafield.getFieldName();
            mymap.put(field, type);//遍历template中的值将其分别存储到key,value中
        }

        sysReDataobjService.creatTemplateTable(sysReDataobj.getTableName(), mymap);//此处的name为表名

        return AjaxResult.success("成功生成数据表");
    }


    @PreAuthorize("@ss.hasPermi('ems:dataobj:query')")
    @PostMapping("/getTableDetails")
    public List<HashMap<String,Object>> getTableData(@RequestBody SysReDataobj sysReDataobj){
        String tableName = sysReDataobj.getTableName();
        String columns = "";
        List<HashMap<String, Object>> hashMaps = new ArrayList<>();

        for (SysReDatafield sysReDatafield : sysReDataobj.getSysReDatafieldList()) {
            String type = sysReDatafield.getFieldType();
            String field = sysReDatafield.getFieldName();
            columns += field + ",";
        }

        columns = columns.substring(0, columns.length()-1);
//        System.out.println("columns = " + columns);

        try{

            hashMaps = sysReDataobjService.selectTemplateTableData(columns, tableName);

        } catch (Exception e) {
            System.out.println("e = " + e);
        }
        return hashMaps;
    }

    /**
     * 修改数据对象
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:edit')")
    @Log(title = "数据对象", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysReDataobj sysReDataobj)
    {
        return toAjax(sysReDataobjService.updateSysReDataobj(sysReDataobj));
    }

    /**
     * 删除数据对象
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:remove')")
    @Log(title = "数据对象", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dataobjIds}")
    public AjaxResult remove(@PathVariable String[] dataobjIds)
    {
        return toAjax(sysReDataobjService.deleteSysReDataobjByDataobjIds(dataobjIds));
    }

    /**
     * 删除数据对象
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:remove')")
    @Log(title = "数据对象", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableName}/{id}")
    public AjaxResult removeTemplateTableItemById(@PathVariable String tableName, @PathVariable Long id)
    {
        return toAjax(sysReDataobjService.deleteTemplateTableById(tableName, id));
    }


    /**
     * 新增数据对象Item
     */
    @PreAuthorize("@ss.hasPermi('ems:dataobj:add')")
    @Log(title = "数据对象", businessType = BusinessType.INSERT)
    @PostMapping("/field")
    public AjaxResult addDataItem(@RequestBody String requestBody)
    {
//        System.out.println("request body = " + requestBody);

        jsonObject = JSONObject.parse(requestBody);

        return toAjax(sysReDataobjService.insertTemplateTableData(jsonObject.get("tableName").toString(),
                jsonObject.get("fields").toString(),
                jsonObject.get("values").toString()
        ));
    }

    @Log(title = "数据对象", businessType = BusinessType.UPDATE)
    @PostMapping("/truncateTable")
    public AjaxResult truncateDynamicallyTable(@RequestBody SysReDataobj sysReDataobj){

        sysReDataobjService.truncateTable(sysReDataobj.getTableName());

        return AjaxResult.success("成功删除数据表中所有数据");
    }


    @Log(title = "数据对象", businessType = BusinessType.UPDATE)
    @PostMapping("/deleteTable")
    public AjaxResult deleteDynamicallyTable(@RequestBody SysReDataobj sysReDataobj){
        System.out.println("############ delete table");

        sysReDataobjService.deleteTable(sysReDataobj.getTableName());

        return AjaxResult.success("成功删除数据表");
    }


}