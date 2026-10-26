package com.ruoyi.ems.service;

import java.util.List;
import com.ruoyi.ems.domain.SysEmsCorres;

/**
 * 文件信息Service接口
 * 
 * @author luohd
 * @date 2022-11-30
 */
public interface ISysEmsCorresService 
{
    /**
     * 查询文件信息
     * 
     * @param corresId 文件信息主键
     * @return 文件信息
     */
    public SysEmsCorres selectSysEmsCorresByCorresId(Long corresId);

    /**
     * 查询文件信息列表
     * 
     * @param sysEmsCorres 文件信息
     * @return 文件信息集合
     */
    public List<SysEmsCorres> selectSysEmsCorresList(SysEmsCorres sysEmsCorres);


    /**
     * 查询文件信息列表By Case Id
     *
     * @param caseId 案件id
     * @return 文件信息集合
     */
    public List<SysEmsCorres> selectSysEmsCorresListByCaseId(String caseId);


    /**
     * 新增文件信息
     * 
     * @param sysEmsCorres 文件信息
     * @return 结果
     */
    public int insertSysEmsCorres(SysEmsCorres sysEmsCorres);

    /**
     * 修改文件信息
     * 
     * @param sysEmsCorres 文件信息
     * @return 结果
     */
    public int updateSysEmsCorres(SysEmsCorres sysEmsCorres);

    /**
     * 批量删除文件信息
     * 
     * @param corresIds 需要删除的文件信息主键集合
     * @return 结果
     */
    public int deleteSysEmsCorresByCorresIds(Long[] corresIds);

    /**
     * 删除文件信息信息
     * 
     * @param corresId 文件信息主键
     * @return 结果
     */
    public int deleteSysEmsCorresByCorresId(Long corresId);
}
