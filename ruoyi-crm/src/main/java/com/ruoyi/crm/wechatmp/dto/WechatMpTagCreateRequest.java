package com.ruoyi.crm.wechatmp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 创建公众号标签请求
 *
 * @author luohd
 * @date 2026-07-17
 */
@Data
public class WechatMpTagCreateRequest
{
    @NotBlank(message = "标签名不能为空")
    private String name;
}
