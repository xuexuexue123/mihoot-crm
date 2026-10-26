package com.ruoyi.crm.wechatmp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.wechatmp.domain.CrmWechatMpQrcode;

public interface CrmWechatMpQrcodeMapper
{
    CrmWechatMpQrcode selectById(@Param("id") Long id);

    CrmWechatMpQrcode selectByScene(@Param("appId") String appId, @Param("sceneStr") String sceneStr);

    List<CrmWechatMpQrcode> selectList(CrmWechatMpQrcode query);

    int insert(CrmWechatMpQrcode row);

    int update(CrmWechatMpQrcode row);

    int deleteById(@Param("id") Long id);

    @Insert("INSERT INTO crm_wechat_mp_menu_key_label (app_id, menu_key, menu_name) " +
            "VALUES (#{appId}, #{menuKey}, #{menuName}) " +
            "ON CONFLICT (app_id, menu_key) DO UPDATE SET menu_name = EXCLUDED.menu_name")
    int upsertMenuKeyLabel(@Param("appId") String appId, @Param("menuKey") String menuKey,
            @Param("menuName") String menuName);

    String selectFirstAppIdByEventId(@Param("crmEventId") String crmEventId);

    List<String> selectDistinctAppIdsByEventId(@Param("crmEventId") String crmEventId);
}
