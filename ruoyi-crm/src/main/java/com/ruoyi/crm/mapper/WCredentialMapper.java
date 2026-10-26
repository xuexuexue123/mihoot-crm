package com.ruoyi.crm.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.crm.domain.WCredential;

/**
 * WCredentialMapper接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface WCredentialMapper
{
    /**
     * 查询WCredential
     * 
     * @param id WCredential主键
     * @return WCredential
     */
    public WCredential selectWCredentialById(String id);

    /**
     * 查询WCredential列表
     * 
     * @param wCredential WCredential
     * @return WCredential集合
     */
    public List<WCredential> selectWCredentialList(WCredential wCredential);

    /**
     * 新增WCredential
     * 
     * @param wCredential WCredential
     * @return 结果
     */
    public int insertWCredential(WCredential wCredential);

    /**
     * 修改WCredential
     * 
     * @param wCredential WCredential
     * @return 结果
     */
    public int updateWCredential(WCredential wCredential);

    /**
     * 删除WCredential
     * 
     * @param id WCredential主键
     * @return 结果
     */
    public int deleteWCredentialById(String id);

    /**
     * 批量删除WCredential
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWCredentialByIds(String[] ids);

    /**
     * 清除同一用户、同一类型下的主凭证标记
     */
    public int clearPrimaryByUserAndType(@Param("userId") String userId,
            @Param("type") String type, @Param("excludeId") String excludeId);
}
