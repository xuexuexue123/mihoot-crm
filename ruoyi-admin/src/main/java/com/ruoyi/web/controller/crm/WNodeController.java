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
import com.ruoyi.crm.domain.WNode;
import com.ruoyi.crm.service.IWNodeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * WNodeController
 * 
 * @author luohd
 * @date 2026-05-19
 */
@RestController
@RequestMapping("/crm/wnode")
public class WNodeController extends BaseController
{
    @Autowired
    private IWNodeService wNodeService;

    /**
     * 查询WNode列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wnode:list')")
    @GetMapping("/list")
    public TableDataInfo list(WNode wNode)
    {
        startPage();
        List<WNode> list = wNodeService.selectWNodeList(wNode);
        return getDataTable(list);
    }

    /**
     * 导出WNode列表
     */
    @PreAuthorize("@ss.hasPermi('crm:wnode:export')")
    @Log(title = "WNode", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WNode wNode)
    {
        List<WNode> list = wNodeService.selectWNodeList(wNode);
        ExcelUtil<WNode> util = new ExcelUtil<WNode>(WNode.class);
        util.exportExcel(response, list, "WNode数据");
    }

    /**
     * 获取WNode详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:wnode:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wNodeService.selectWNodeById(id));
    }

    /**
     * 新增WNode
     */
    @PreAuthorize("@ss.hasPermi('crm:wnode:add')")
    @Log(title = "WNode", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WNode wNode)
    {
        return toAjax(wNodeService.insertWNode(wNode));
    }

    /**
     * 修改WNode
     */
    @PreAuthorize("@ss.hasPermi('crm:wnode:edit')")
    @Log(title = "WNode", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WNode wNode)
    {
        return toAjax(wNodeService.updateWNode(wNode));
    }

    /**
     * 删除WNode
     */
    @PreAuthorize("@ss.hasPermi('crm:wnode:remove')")
    @Log(title = "WNode", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wNodeService.deleteWNodeByIds(ids));
    }
}
