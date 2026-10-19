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
import com.ruoyi.crm.domain.WConnection;
import com.ruoyi.crm.service.IWConnectionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WConnectionController
 * 
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wconnection")
public class WConnectionController extends BaseController
{
    @Autowired
    private IWConnectionService wConnectionService;

    /**
     * 查询WConnection列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wconnection:list')")
    @GetMapping("/list")
    public TableDataInfo list(WConnection wConnection)
    {
        startPage();
        List<WConnection> list = wConnectionService.selectWConnectionList(wConnection);
        return getDataTable(list);
    }

    /**
     * 导出WConnection列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wconnection:export')")
    @Log(title = "WConnection", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WConnection wConnection)
    {
        List<WConnection> list = wConnectionService.selectWConnectionList(wConnection);
        ExcelUtil<WConnection> util = new ExcelUtil<WConnection>(WConnection.class);
        util.exportExcel(response, list, "WConnection数据");
    }

    /**
     * 获取WConnection详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wconnection:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wConnectionService.selectWConnectionById(id));
    }

    /**
     * 新增WConnection
     */
    @PreAuthorize("@ss.hasPermi('crm:wconnection:add')")
    @Log(title = "WConnection", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WConnection wConnection)
    {
        return toAjax(wConnectionService.insertWConnection(wConnection));
    }

    /**
     * 修改WConnection
     */
    @PreAuthorize("@ss.hasPermi('crm:wconnection:edit')")
    @Log(title = "WConnection", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WConnection wConnection)
    {
        return toAjax(wConnectionService.updateWConnection(wConnection));
    }

    /**
     * 删除WConnection
     */
    @PreAuthorize("@ss.hasPermi('crm:wconnection:remove')")
    @Log(title = "WConnection", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wConnectionService.deleteWConnectionByIds(ids));
    }
}
