package com.ruoyi.crm.mapper;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.crm.domain.CrmWecomKfSyncCursor;

public interface CrmWecomKfSyncCursorMapper
{
    CrmWecomKfSyncCursor selectByCorpOpenKfid(@Param("corpId") String corpId,
            @Param("openKfid") String openKfid);

    int upsertEnsure(CrmWecomKfSyncCursor row);

    int updateCursor(@Param("corpId") String corpId,
            @Param("openKfid") String openKfid,
            @Param("cursor") String cursor);
}
