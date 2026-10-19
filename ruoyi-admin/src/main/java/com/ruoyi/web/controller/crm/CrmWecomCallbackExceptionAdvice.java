package com.ruoyi.web.controller.crm;

import java.nio.charset.StandardCharsets;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 企业微信回调须始终返回 {@code text/plain}，不可走全局 {@code AjaxResult}（JSON），否则 URL 验证失败。
 */
@Slf4j
@RestControllerAdvice(assignableTypes = CrmWecomCallbackController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CrmWecomCallbackExceptionAdvice
{
    private static final MediaType TEXT_PLAIN_UTF8 =
            new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handleUncaught(Throwable ex)
    {
        log.error("WeCom callback uncaught", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(TEXT_PLAIN_UTF8)
                .body("Internal Server Error");
    }
}
