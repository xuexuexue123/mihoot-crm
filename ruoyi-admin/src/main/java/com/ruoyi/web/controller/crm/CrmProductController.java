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
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.service.ICrmProductService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 产品Controller
 * 
 * @author luohd
 * @date 2024-02-06
 */
@RestController
@RequestMapping("/crm/product")
public class CrmProductController extends BaseController
{
    @Autowired
    private ICrmProductService crmProductService;

    /**
     * 查询产品列表
     */
    @PreAuthorize("@ss.hasPermi('crm:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrmProduct crmProduct)
    {
        startPage();
        List<CrmProduct> list = crmProductService.selectCrmProductList(crmProduct);
        return getDataTable(list);
    }

    /**
     * 在售产品目录（兼容报价选品；推荐改用 /crm/product-center/products/sellable）
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:product:list,crm:product:sellable:list')")
    @GetMapping("/sellable")
    public TableDataInfo sellable(CrmProduct crmProduct)
    {
        startPage();
        crmProduct.setStatus(CrmProduct.STATUS_ACTIVE);
        List<CrmProduct> list = crmProductService.selectCrmProductList(crmProduct);
        return getDataTable(list);
    }

    /**
     * 导出产品列表
     */
    @PreAuthorize("@ss.hasPermi('crm:product:export')")
    @Log(title = "产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrmProduct crmProduct)
    {
        List<CrmProduct> list = crmProductService.selectCrmProductList(crmProduct);
        ExcelUtil<CrmProduct> util = new ExcelUtil<CrmProduct>(CrmProduct.class);
        util.exportExcel(response, list, "产品数据");
    }

    /**
     * 获取产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('crm:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") String productId)
    {
        return success(crmProductService.selectCrmProductByProductId(productId));
    }

    /**
     * 新增产品
     */
    @PreAuthorize("@ss.hasPermi('crm:product:add')")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrmProduct crmProduct)
    {
        return toAjax(crmProductService.insertCrmProduct(crmProduct));
    }

    /**
     * 修改产品
     */
    @PreAuthorize("@ss.hasPermi('crm:product:edit')")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrmProduct crmProduct)
    {
        return toAjax(crmProductService.updateCrmProduct(crmProduct));
    }

    /**
     * 删除产品
     */
    @PreAuthorize("@ss.hasPermi('crm:product:remove')")
    @Log(title = "产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable String[] productIds)
    {
        return toAjax(crmProductService.deleteCrmProductByProductIds(productIds));
    }
}
