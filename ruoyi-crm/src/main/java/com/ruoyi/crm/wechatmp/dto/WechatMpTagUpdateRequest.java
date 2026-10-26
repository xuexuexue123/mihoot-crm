package com.ruoyi.crm.wechatmp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 编辑公众号标签请求
 *
 * @author luohd
 * @date 2026-07-17
 */
@Data
public class WechatMpTagUpdateRequest
{
    @NotNull(message = "标签ID不能为空")
    private Long id;

    @NotBlank(message = "标签名不能为空")
    private String name;
}
