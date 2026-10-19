package com.ruoyi.web.controller.system;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DictUtils;

/**
 * 字典 Redis 缓存（按 tenant 隔离）
 */
@RestController
@RequestMapping("/system/dict/cache")
public class SysDictCacheController extends BaseController
{
    /**
     * 清除当前租户下指定 dictType 的字典缓存
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典缓存", businessType = BusinessType.CLEAN)
    @DeleteMapping("/{dictType}")
    public AjaxResult clearDictTypeCache(@PathVariable String dictType)
    {
        DictUtils.removeDictCache(dictType);
        return success();
    }
}
