package com.ruoyi.crm.wechatmp.support;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;

/**
 * 微信图文留言 API 错误码 → 业务文案
 */
public final class WechatMpCommentApiErrors
{
    private WechatMpCommentApiErrors()
    {
    }

    public static boolean isSuccess(JSONObject res)
    {
        if (res == null)
        {
            return false;
        }
        int err = res.getIntValue("errcode", 0);
        return err == 0;
    }

    public static int errcode(JSONObject res)
    {
        return res == null ? -1 : res.getIntValue("errcode", -1);
    }

    public static String message(JSONObject res)
    {
        if (res == null)
        {
            return "微信接口无响应";
        }
        int code = res.getIntValue("errcode", 0);
        String errmsg = res.getString("errmsg");
        switch (code)
        {
            case 88000:
                return "该公众号无留言权限，请确认已认证并开通留言功能";
            case 88001:
                return "图文不存在";
            case 88005:
                return "该留言已回复，无法再次回复";
            case 88007:
                return "回复内容超长";
            case 88008:
                return "评论不存在";
            case 45009:
                return "微信接口调用达到日限额，请稍后重试";
            default:
                return errmsg != null ? ("微信错误 " + code + ": " + errmsg) : ("微信错误 " + code);
        }
    }

    public static ServiceException toException(JSONObject res)
    {
        int code = errcode(res);
        int http = code == 88005 ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        if (code == 88000)
        {
            http = HttpStatus.FORBIDDEN;
        }
        return new ServiceException(message(res), http);
    }
}
