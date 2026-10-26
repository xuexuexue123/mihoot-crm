package com.ruoyi.crm.wechatmp.mapper;

import com.ruoyi.crm.wechatmp.domain.MpCallbackLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * 微信公众号回调日志 Mapper
 *
 * @author luohd
 * @date 2026-06-18
 */
@Mapper
public interface MpCallbackLogMapper
{
    @Insert("INSERT INTO crm_wechat_mp_callback_log " +
            "(msg_type, event, from_user, to_user, raw_xml, parsed, error_msg) " +
            "VALUES (#{msgType}, #{event}, #{fromUser}, #{toUser}, #{rawXml}, #{parsed}, #{errorMsg})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MpCallbackLog log);
}
