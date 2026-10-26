package com.ruoyi.crm.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

/**
 * Wait 节点公开 webhook 回调（无需 JWT）
 */
public interface IWWaitResumePublicService
{
    ResponseEntity<Object> handleWebhookCallback(String resumeKey, HttpServletRequest request);
}
