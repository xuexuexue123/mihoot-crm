package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.WecomLoginBody;
import com.ruoyi.crm.service.login.LoginWecomService;

/**
 * 企业微信登录控制器
 *
 * @author ruoyi
 */
@RestController
public class LoginWecomController
{
    @Autowired
    private LoginWecomService loginWecomService;

    /**
     * 企业微信扫码登录
     *
     * @param body 登录请求体 { code, state, tenant }
     * @return token
     */
    @PostMapping("/login/wecom")
    public AjaxResult loginByWecom(@RequestBody WecomLoginBody body)
    {
        String token = loginWecomService.login(body.getCode(), body.getState(), body.getTenant());
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }
}
