package com.ruoyi.framework.web.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.DemoModeException;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 全局异常处理器
 * 
 * @author ruoyi
 */
@RestControllerAdvice
public class GlobalExceptionHandler
{
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * SSE / 长轮询异步超时：客户端会重连。
     * 不可返回 AjaxResult，否则 Content-Type=text/event-stream 时再抛
     * {@code HttpMessageNotWritableException}。
     */
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleAsyncRequestTimeoutException(AsyncRequestTimeoutException e,
            HttpServletRequest request, HttpServletResponse response)
    {
        String requestURI = request.getRequestURI();
        if (isInboxSsePath(requestURI) || isEventStream(request, response))
        {
            log.debug("SSE async timeout (client should reconnect): {}", requestURI);
        }
        else
        {
            log.warn("Async request timed out: {}", requestURI);
        }
        // 不写 body；响应可能已 committed
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public AjaxResult handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
            HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 请求体缺失 / JSON 无法解析（如 POST 未带 body、日期格式非法）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public AjaxResult handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String detail = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        log.warn("请求地址'{}',请求体无法读取: {}", requestURI, detail);
        if (detail != null && detail.contains("Required request body is missing"))
        {
            return AjaxResult.error(HttpStatus.BAD_REQUEST,
                    "请求体不能为空，请提交 JSON（Content-Type: application/json）");
        }
        return AjaxResult.error(HttpStatus.BAD_REQUEST, "请求体格式错误: " + detail);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public AjaxResult handleServiceException(ServiceException e, HttpServletRequest request)
    {
        Integer code = e.getCode();
        String requestURI = request.getRequestURI();
        if (code != null && code == HttpStatus.NOT_FOUND)
        {
            log.debug("请求地址'{}',{}", requestURI, e.getMessage());
        }
        else if (code != null && (code == HttpStatus.FORBIDDEN || code == HttpStatus.BAD_REQUEST))
        {
            log.warn("请求地址'{}',{}", requestURI, e.getMessage());
        }
        else
        {
            log.error("请求地址'{}',{}", requestURI, e.getMessage(), e);
        }
        AjaxResult result = StringUtils.isNotNull(code)
                ? AjaxResult.error(code, e.getMessage())
                : AjaxResult.error(e.getMessage());
        if (StringUtils.isNotEmpty(e.getReason()))
        {
            result.put("reason", e.getReason());
        }
        return result;
    }

    /**
     * 用户异常（验证码错误/过期、密码错误等）
     */
    @ExceptionHandler(UserException.class)
    public AjaxResult handleUserException(UserException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        log.warn("请求地址'{}',{}", requestURI, e.getMessage());
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException e, HttpServletRequest request,
            HttpServletResponse response)
    {
        String requestURI = request.getRequestURI();
        if (isInboxSsePath(requestURI) || isEventStream(request, response))
        {
            log.warn("请求地址'{}',SSE/流式请求异常（不返回 JSON）: {}", requestURI, e.toString());
            return null;
        }
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
    {
        String requestURI = request.getRequestURI();
        if (isInboxSsePath(requestURI) || isEventStream(request, response))
        {
            log.warn("请求地址'{}',SSE/流式请求系统异常（不返回 JSON）: {}", requestURI, e.toString());
            return null;
        }
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return AjaxResult.error(e.getMessage());
    }

    private static boolean isInboxSsePath(String requestURI)
    {
        return requestURI != null && requestURI.contains("/inbox/events");
    }

    private static boolean isEventStream(HttpServletRequest request, HttpServletResponse response)
    {
        if (response != null)
        {
            String ct = response.getContentType();
            if (ct != null && ct.toLowerCase().startsWith(MediaType.TEXT_EVENT_STREAM_VALUE))
            {
                return true;
            }
        }
        if (request != null)
        {
            String accept = request.getHeader("Accept");
            return accept != null && accept.toLowerCase().contains(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        return false;
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult handleDemoModeException(DemoModeException e)
    {
        return AjaxResult.error("演示模式，不允许操作");
    }
}
