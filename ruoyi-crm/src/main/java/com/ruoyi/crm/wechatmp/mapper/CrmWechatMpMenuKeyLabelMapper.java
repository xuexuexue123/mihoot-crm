package com.ruoyi.crm.wechatmp.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CrmWechatMpMenuKeyLabelMapper
{
    @Select("SELECT menu_name FROM crm_wechat_mp_menu_key_label " +
            "WHERE app_id = #{appId} AND menu_key = #{menuKey} LIMIT 1")
    String selectNameByKey(@Param("appId") String appId, @Param("menuKey") String menuKey);
}
