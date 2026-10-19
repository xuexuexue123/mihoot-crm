package com.ruoyi.crm.wechatmp.dto;

import java.util.List;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 批量打标 / 取消标签请求
 *
 * @author luohd
 * @date 2026-07-17
 */
@Data
public class WechatMpTagBatchRequest
{
    @NotEmpty(message = "openid列表不能为空")
    private List<String> openidList;

    @NotNull(message = "标签ID不能为空")
    private Long tagId;
}
