package com.ruoyi.crm.service;

import java.util.List;
import com.ruoyi.crm.domain.WWaitResume;

/**
 * WWaitResumeService接口
 * 
 * @author luohd
 * @date 2026-05-19
 */
public interface IWWaitResumeService
{
    /**
     * 查询WWaitResume
     * 
     * @param id WWaitResume主键
     * @return WWaitResume
     */
    public WWaitResume selectWWaitResumeById(String id);

    /**
     * 查询WWaitResume列表
     * 
     * @param wWaitResume WWaitResume
     * @return WWaitResume集合
     */
    public List<WWaitResume> selectWWaitResumeList(WWaitResume wWaitResume);

    /**
     * 新增WWaitResume
     * 
     * @param wWaitResume WWaitResume
     * @return 结果
     */
    public int insertWWaitResume(WWaitResume wWaitResume);

    /**
     * 修改WWaitResume
     * 
     * @param wWaitResume WWaitResume
     * @return 结果
     */
    public int updateWWaitResume(WWaitResume wWaitResume);

    /**
     * 批量删除WWaitResume
     * 
     * @param ids 需要删除的WWaitResume主键集合
     * @return 结果
     */
    public int deleteWWaitResumeByIds(String[] ids);

    /**
     * 删除WWaitResume信息
     * 
     * @param id WWaitResume主键
     * @return 结果
     */
    public int deleteWWaitResumeById(String id);
}
