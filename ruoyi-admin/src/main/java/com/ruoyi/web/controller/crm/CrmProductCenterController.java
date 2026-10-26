package com.ruoyi.web.controller.crm;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.crm.domain.CrmProduct;
import com.ruoyi.crm.domain.CrmProductCategory;
import com.ruoyi.crm.domain.CrmProductMedia;
import com.ruoyi.crm.domain.CrmWecomConfig;
import com.ruoyi.crm.domain.dto.ProductBatchStatusRequest;
import com.ruoyi.crm.domain.dto.ProductCategoryMoveRequest;
import com.ruoyi.crm.domain.dto.wecom.WecomProductAlbumResponse;
import com.ruoyi.crm.domain.vo.ProductBatchItemResultVo;
import com.ruoyi.crm.domain.vo.ProductCategoryTreeVo;
import com.ruoyi.crm.domain.vo.ProductReferenceSummaryVo;
import com.ruoyi.crm.domain.vo.ProductSellableVo;
import com.ruoyi.crm.mapper.CrmWecomConfigMapper;
import com.ruoyi.crm.service.ICrmProductCenterService;
import com.ruoyi.crm.service.ICrmWecomProductAlbumService;
import com.ruoyi.crm.wecom.WecomAccessTokenService;
import com.ruoyi.common.utils.StringUtils;

/**
 * 产品中心 Controller
 */
@RestController
@RequestMapping("/crm/product-center")
public class CrmProductCenterController extends BaseController
{
    @Autowired
    private ICrmProductCenterService productCenterService;

    @Autowired
    private ICrmWecomProductAlbumService wecomProductAlbumService;

    @Autowired
    private WecomAccessTokenService wecomAccessTokenService;

    @Autowired
    private CrmWecomConfigMapper wecomConfigMapper;

    // -------------------- 产品查询 --------------------

    /**
     * 产品列表。前端按若依惯例请求 /products/list；同时保留 /products。
     * 注意：必须显式注册 /list，否则会被 /products/{productId} 把 "list" 当成 ID。
     */
    @PreAuthorize("@ss.hasPermi('crm:product:center:list')")
    @GetMapping({"/products/list", "/products"})
    public TableDataInfo listProducts(CrmProduct query)
    {
        startPage();
        List<CrmProduct> list = productCenterService.listProducts(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyPermi('crm:product:sellable:list,crm:product:center:list,crm:product:list')")
    @GetMapping({"/products/sellable", "/products/sellable/list"})
    public TableDataInfo listSellable(CrmProduct query)
    {
        startPage();
        List<ProductSellableVo> list = productCenterService.listSellable(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:query')")
    @GetMapping("/products/{productId:^(?!list$|sellable$|import-template$|export$|batch-status$).+}")
    public AjaxResult getProduct(@PathVariable String productId)
    {
        return success(productCenterService.getProductDetail(productId));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:query')")
    @GetMapping("/products/{productId:^(?!list$|sellable$).+}/references")
    public AjaxResult getReferences(@PathVariable String productId)
    {
        ProductReferenceSummaryVo vo = productCenterService.getReferences(productId);
        return success(vo);
    }

    // -------------------- 产品命令 --------------------

    @PreAuthorize("@ss.hasPermi('crm:product:center:add')")
    @Log(title = "产品中心", businessType = BusinessType.INSERT)
    @PostMapping("/products")
    public AjaxResult create(@RequestBody CrmProduct body)
    {
        return success(productCenterService.createDraft(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:edit')")
    @Log(title = "产品中心", businessType = BusinessType.UPDATE)
    @PutMapping({"/products/{productId}", "/products"})
    public AjaxResult update(@PathVariable(value = "productId", required = false) String productId,
                             @RequestBody CrmProduct body)
    {
        String id = StringUtils.isNotBlank(productId)
            ? productId
            : (body == null ? null : body.getProductId());
        if (StringUtils.isBlank(id))
        {
            return error("productId 不能为空");
        }
        return success(productCenterService.updateProduct(id, body));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:publish')")
    @Log(title = "产品中心-发布", businessType = BusinessType.UPDATE)
    @PostMapping("/products/{productId}/publish")
    public AjaxResult publish(@PathVariable String productId)
    {
        return success(productCenterService.publish(productId));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:disable')")
    @Log(title = "产品中心-停售", businessType = BusinessType.UPDATE)
    @PostMapping({"/products/{productId}/disable", "/products/{productId}/deactivate"})
    public AjaxResult disable(@PathVariable String productId)
    {
        return success(productCenterService.disable(productId));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:publish')")
    @Log(title = "产品中心-恢复在售", businessType = BusinessType.UPDATE)
    @PostMapping("/products/{productId}/reactivate")
    public AjaxResult reactivate(@PathVariable String productId)
    {
        return success(productCenterService.reactivate(productId));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:remove')")
    @Log(title = "产品中心-归档", businessType = BusinessType.DELETE)
    @DeleteMapping("/products/{productId}")
    public AjaxResult softDelete(@PathVariable String productId)
    {
        productCenterService.softDelete(productId);
        return success();
    }

    @PreAuthorize("@ss.hasAnyPermi('crm:product:center:publish,crm:product:center:disable,crm:product:center:remove')")
    @Log(title = "产品中心-批量状态", businessType = BusinessType.UPDATE)
    @PostMapping("/products/batch-status")
    public AjaxResult batchStatus(@RequestBody ProductBatchStatusRequest request)
    {
        List<ProductBatchItemResultVo> results = productCenterService.batchStatus(request);
        return success(results);
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:edit')")
    @Log(title = "产品中心-媒体", businessType = BusinessType.UPDATE)
    @PutMapping("/products/{productId}/media")
    public AjaxResult replaceMedia(@PathVariable String productId, @RequestBody List<CrmProductMedia> mediaList)
    {
        productCenterService.replaceMedia(productId, mediaList);
        return success(productCenterService.getProductDetail(productId));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:export')")
    @Log(title = "产品中心", businessType = BusinessType.EXPORT)
    @PostMapping("/products/export")
    public void export(HttpServletResponse response, CrmProduct query)
    {
        List<CrmProduct> list = productCenterService.listProducts(query);
        ExcelUtil<CrmProduct> util = new ExcelUtil<CrmProduct>(CrmProduct.class);
        util.exportExcel(response, list, "产品数据");
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:import')")
    @GetMapping("/products/import-template")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<CrmProduct> util = new ExcelUtil<CrmProduct>(CrmProduct.class);
        util.importTemplateExcel(response, "产品数据");
    }

    @PreAuthorize("@ss.hasPermi('crm:product:center:import')")
    @Log(title = "产品中心-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/products/import")
    public AjaxResult importData(MultipartFile file, @RequestParam(defaultValue = "create") String mode) throws Exception
    {
        ExcelUtil<CrmProduct> util = new ExcelUtil<CrmProduct>(CrmProduct.class);
        List<CrmProduct> list = util.importExcel(file.getInputStream());
        int success = 0;
        int failure = 0;
        StringBuilder errors = new StringBuilder();
        boolean upsert = "upsert".equalsIgnoreCase(mode);
        for (int i = 0; i < list.size(); i++)
        {
            CrmProduct row = list.get(i);
            int line = i + 2;
            try
            {
                if (upsert && StringUtils.isNotBlank(row.getProductCode()))
                {
                    CrmProduct query = new CrmProduct();
                    query.setProductCode(row.getProductCode().trim());
                    List<CrmProduct> existing = productCenterService.listProducts(query);
                    CrmProduct matched = null;
                    for (CrmProduct p : existing)
                    {
                        if (row.getProductCode().trim().equalsIgnoreCase(p.getProductCode()))
                        {
                            matched = p;
                            break;
                        }
                    }
                    if (matched != null)
                    {
                        row.setVersion(matched.getVersion());
                        productCenterService.updateProduct(matched.getProductId(), row);
                        success++;
                        continue;
                    }
                }
                productCenterService.createDraft(row);
                success++;
            }
            catch (Exception e)
            {
                failure++;
                errors.append("第").append(line).append("行: ").append(e.getMessage()).append("; ");
            }
        }
        AjaxResult result = AjaxResult.success();
        result.put("total", list.size());
        result.put("success", success);
        result.put("failure", failure);
        result.put("errors", errors.toString());
        return result;
    }

    // -------------------- 分类 --------------------

    @PreAuthorize("@ss.hasAnyPermi('crm:product:category:manage,crm:product:center:list,crm:product:sellable:list')")
    @GetMapping("/categories/tree")
    public AjaxResult categoryTree()
    {
        List<ProductCategoryTreeVo> tree = productCenterService.categoryTree();
        return success(tree);
    }

    @PreAuthorize("@ss.hasPermi('crm:product:category:manage')")
    @Log(title = "产品分类", businessType = BusinessType.INSERT)
    @PostMapping("/categories")
    public AjaxResult createCategory(@RequestBody CrmProductCategory body)
    {
        return success(productCenterService.createCategory(body));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:category:manage')")
    @Log(title = "产品分类", businessType = BusinessType.UPDATE)
    @PutMapping("/categories/{categoryId}")
    public AjaxResult updateCategory(@PathVariable String categoryId, @RequestBody CrmProductCategory body)
    {
        return success(productCenterService.updateCategory(categoryId, body));
    }

    @PreAuthorize("@ss.hasPermi('crm:product:category:manage')")
    @Log(title = "产品分类-移动", businessType = BusinessType.UPDATE)
    @PostMapping("/categories/{categoryId}/move")
    public AjaxResult moveCategory(@PathVariable String categoryId, @RequestBody ProductCategoryMoveRequest request)
    {
        productCenterService.moveCategory(categoryId, request);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:product:category:manage')")
    @Log(title = "产品分类-启用", businessType = BusinessType.UPDATE)
    @PostMapping("/categories/{categoryId}/enable")
    public AjaxResult enableCategory(@PathVariable String categoryId)
    {
        productCenterService.enableCategory(categoryId);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:product:category:manage')")
    @Log(title = "产品分类-停用", businessType = BusinessType.UPDATE)
    @PostMapping("/categories/{categoryId}/disable")
    public AjaxResult disableCategory(@PathVariable String categoryId)
    {
        productCenterService.disableCategory(categoryId);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('crm:product:category:manage')")
    @Log(title = "产品分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/categories/{categoryId}")
    public AjaxResult deleteCategory(@PathVariable String categoryId)
    {
        productCenterService.deleteCategory(categoryId);
        return success();
    }

    // -------------------- 企微人工同步 --------------------

    /**
     * 同步预览（前端路径 /wecom/preview）。不调用企微，仅返回主档将提交的内容。
     */
    @PreAuthorize("@ss.hasAnyPermi('crm:product:wecom:sync,crm:wecom:contact,crm:product:center:query')")
    @GetMapping({
        "/products/{productId}/wecom/preview",
        "/products/{productId}/channels/wecom/preview"
    })
    public AjaxResult previewWecom(@PathVariable String productId)
    {
        return success(wecomProductAlbumService.previewProductSync(productId));
    }

    @PreAuthorize("@ss.hasAnyPermi('crm:product:wecom:sync,crm:wecom:contact')")
    @Log(title = "产品中心-企微同步", businessType = BusinessType.UPDATE)
    @PostMapping({
        "/products/{productId}/channels/wecom/sync",
        "/products/{productId}/wecom/sync"
    })
    public AjaxResult syncWecom(@PathVariable String productId)
    {
        String accessToken = requireWecomAccessToken();
        WecomProductAlbumResponse resp = wecomProductAlbumService.syncProductFromMaster(accessToken, productId);
        if (resp == null)
        {
            return error("企业微信未返回响应");
        }
        if (resp.getErrcode() != null && resp.getErrcode() != 0)
        {
            return error("企业微信返回错误: " + resp.getErrmsg());
        }
        return success(resp);
    }

    @PreAuthorize("@ss.hasAnyPermi('crm:product:wecom:sync,crm:wecom:contact')")
    @Log(title = "产品中心-企微删除", businessType = BusinessType.DELETE)
    @DeleteMapping({
        "/products/{productId}/channels/wecom",
        "/products/{productId}/wecom"
    })
    public AjaxResult deleteWecom(@PathVariable String productId)
    {
        String accessToken = requireWecomAccessToken();
        wecomProductAlbumService.deleteProductChannel(accessToken, productId);
        return success();
    }

    private String requireWecomAccessToken()
    {
        CrmWecomConfig cfg = wecomConfigMapper.selectFirst();
        if (cfg == null || StringUtils.isEmpty(cfg.getCorpId()))
        {
            throw new com.ruoyi.common.exception.ServiceException("未找到企业微信配置，请先完成企微自建应用配置",
                com.ruoyi.common.constant.HttpStatus.BAD_REQUEST);
        }
        try
        {
            return wecomAccessTokenService.getAgentAccessToken(cfg);
        }
        catch (Exception e)
        {
            throw new com.ruoyi.common.exception.ServiceException("获取 access_token 失败: " + e.getMessage(),
                com.ruoyi.common.constant.HttpStatus.BAD_REQUEST);
        }
    }
}
