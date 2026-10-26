package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.crm.service.ICrmSfExpressCallbackService;

/**
 * 顺丰路由状态推送回调（无登录；URL 须携带 ?tenant=）。
 */
@Anonymous
@RestController
@RequestMapping("/crm/sf-express/callback")
public class CrmSfExpressCallbackController
{
    @Autowired
    private ICrmSfExpressCallbackService crmSfExpressCallbackService;

    @PostMapping("/route")
    public ResponseEntity<Object> routePush(HttpServletRequest request)
    {
        return crmSfExpressCallbackService.handleRoutePush(request);
    }
}
