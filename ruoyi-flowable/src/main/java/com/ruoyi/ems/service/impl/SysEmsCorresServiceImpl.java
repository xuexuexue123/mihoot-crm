package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysEmsCorresMapper;
import com.ruoyi.ems.domain.SysEmsCorres;
import com.ruoyi.ems.service.ISysEmsCorresService;

/**
 * 文件信息Service业务层处理
 * 
 * @author luohd
 * @date 2022-11-30
 */
@Service
public class SysEmsCorresServiceImpl implements ISysEmsCorresService 
{
    @Autowired
    private SysEmsCorresMapper sysEmsCorresMapper;

    /**
     * 查询文件信息
     * 
     * @param corresId 文件信息主键
     * @return 文件信息
     */
    @Override
    public SysEmsCorres selectSysEmsCorresByCorresId(Long corresId)
    {
        return sysEmsCorresMapper.selectSysEmsCorresByCorresId(corresId);
    }

    /**
     * 查询文件信息列表
     * 
     * @param sysEmsCorres 文件信息
     * @return 文件信息
     */
    @Override
    public List<SysEmsCorres> selectSysEmsCorresList(SysEmsCorres sysEmsCorres)
    {
        return sysEmsCorresMapper.selectSysEmsCorresList(sysEmsCorres);
    }

    /**
     * 查询文件信息列表 By CaseId
     *
     * @param caseId 案件id
     * @return 文件信息
     */
    @Override
    public List<SysEmsCorres> selectSysEmsCorresListByCaseId(String caseId) {
        return sysEmsCorresMapper.selectSysEmsCorresListByCaseId(caseId);
    }

    /**
     * 新增文件信息
     * 
     * @param sysEmsCorres 文件信息
     * @return 结果
     */
    @Override
    public int insertSysEmsCorres(SysEmsCorres sysEmsCorres)
    {
        sysEmsCorres.setCreateTime(DateUtils.getNowDate());
        return sysEmsCorresMapper.insertSysEmsCorres(sysEmsCorres);
    }

    /**
     * 修改文件信息
     * 
     * @param sysEmsCorres 文件信息
     * @return 结果
     */
    @Override
    public int updateSysEmsCorres(SysEmsCorres sysEmsCorres)
    {
        sysEmsCorres.setUpdateTime(DateUtils.getNowDate());
        return sysEmsCorresMapper.updateSysEmsCorres(sysEmsCorres);
    }

    /**
     * 批量删除文件信息
     * 
     * @param corresIds 需要删除的文件信息主键
     * @return 结果
     */
    @Override
    public int deleteSysEmsCorresByCorresIds(Long[] corresIds)
    {
        return sysEmsCorresMapper.deleteSysEmsCorresByCorresIds(corresIds);
    }

    /**
     * 删除文件信息信息
     * 
     * @param corresId 文件信息主键
     * @return 结果
     */
    @Override
    public int deleteSysEmsCorresByCorresId(Long corresId)
    {
        return sysEmsCorresMapper.deleteSysEmsCorresByCorresId(corresId);
    }
}
