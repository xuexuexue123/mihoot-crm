package com.ruoyi.crm.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

/**
 * 顺丰路由推送回调（RoutePushService）。
 */
public interface ICrmSfExpressCallbackService
{
    ResponseEntity<Object> handleRoutePush(HttpServletRequest request);
}
