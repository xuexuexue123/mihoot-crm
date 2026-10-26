package com.ruoyi.web.controller.crm;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.crm.service.IWWaitResumePublicService;

/**
 * Wait 节点公开 webhook 回调（无需 JWT）
 *
 * 路径：/waitresume/{resumeKey}
 */
@Anonymous
@RestController
@RequestMapping("/waitresume")
public class WaitResumePublicController
{
    @Autowired
    private IWWaitResumePublicService wWaitResumePublicService;

    @GetMapping("/{resumeKey}")
    public ResponseEntity<Object> get(@PathVariable("resumeKey") String resumeKey, HttpServletRequest request)
    {
        return wWaitResumePublicService.handleWebhookCallback(resumeKey, request);
    }

    @PostMapping("/{resumeKey}")
    public ResponseEntity<Object> post(@PathVariable("resumeKey") String resumeKey, HttpServletRequest request)
    {
        return wWaitResumePublicService.handleWebhookCallback(resumeKey, request);
    }

    @PutMapping("/{resumeKey}")
    public ResponseEntity<Object> put(@PathVariable("resumeKey") String resumeKey, HttpServletRequest request)
    {
        return wWaitResumePublicService.handleWebhookCallback(resumeKey, request);
    }

    @PatchMapping("/{resumeKey}")
    public ResponseEntity<Object> patch(@PathVariable("resumeKey") String resumeKey, HttpServletRequest request)
    {
        return wWaitResumePublicService.handleWebhookCallback(resumeKey, request);
    }

    @DeleteMapping("/{resumeKey}")
    public ResponseEntity<Object> delete(@PathVariable("resumeKey") String resumeKey, HttpServletRequest request)
    {
        return wWaitResumePublicService.handleWebhookCallback(resumeKey, request);
    }
}
