package com.ruoyi.ems.mapper;

import java.util.List;
import com.ruoyi.ems.domain.SysReCorres;

/**
 * 文档Mapper接口
 * 
 * @author luohd
 * @date 2023-05-20
 */
public interface SysReCorresMapper 
{
    /**
     * 查询文档
     * 
     * @param corresId 文档主键
     * @return 文档
     */
    public SysReCorres selectSysReCorresByCorresId(String corresId);

    /**
     *
     * @param fileTitle
     * @return
     */
    public SysReCorres selectSysReCorresByFileTitle(String fileTitle);

    /**
     * 查询文档列表
     * 
     * @param sysReCorres 文档
     * @return 文档集合
     */
    public List<SysReCorres> selectSysReCorresList(SysReCorres sysReCorres);

    /**
     * 根据FolderID查询文档列表
     *
     * @param sysReCorres 文档
     * @return 文档集合
     */
    public List<SysReCorres> selectSysReCorresListByFolderId(SysReCorres sysReCorres);

    /**
     * 新增文档
     * 
     * @param sysReCorres 文档
     * @return 结果
     */
    public int insertSysReCorres(SysReCorres sysReCorres);

    /**
     * 修改文档
     * 
     * @param sysReCorres 文档
     * @return 结果
     */
    public int updateSysReCorres(SysReCorres sysReCorres);

    /**
     * 删除文档
     * 
     * @param corresId 文档主键
     * @return 结果
     */
    public int deleteSysReCorresByCorresId(String corresId);

    /**
     * 批量删除文档
     * 
     * @param corresIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysReCorresByCorresIds(String[] corresIds);
}
