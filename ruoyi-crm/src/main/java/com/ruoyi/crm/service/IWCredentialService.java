package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WCredential;

/**
 * WCredentialService接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface IWCredentialService
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
     * 批量删除WCredential
     * 
     * @param ids 需要删除的WCredential主键集合
     * @return 结果
     */
    public int deleteWCredentialByIds(String[] ids);

    /**
     * 删除WCredential信息
     * 
     * @param id WCredential主键
     * @return 结果
     */
    public int deleteWCredentialById(String id);
}
