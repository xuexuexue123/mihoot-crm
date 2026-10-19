package com.ruoyi.ems.service.impl;

import java.util.List;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReCorresMapper;
import com.ruoyi.ems.domain.SysReCorres;
import com.ruoyi.ems.service.ISysReCorresService;

/**
 * 文档Service业务层处理
 * 
 * @author luohd
 * @date 2023-05-20
 */
@Service
public class SysReCorresServiceImpl implements ISysReCorresService 
{
    @Autowired
    private SysReCorresMapper sysReCorresMapper;

    /**
     * 查询文档
     * 
     * @param corresId 文档主键
     * @return 文档
     */
    @Override
    public SysReCorres selectSysReCorresByCorresId(String corresId)
    {
        return sysReCorresMapper.selectSysReCorresByCorresId(corresId);
    }

    /**
     *
     * @param fileTitle
     * @return
     */
    @Override
    public SysReCorres selectSysReCorresByFileTitle(String fileTitle) {
        return sysReCorresMapper.selectSysReCorresByFileTitle(fileTitle);
    }

    /**
     * 查询文档列表
     * 
     * @param sysReCorres 文档
     * @return 文档
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysReCorres> selectSysReCorresList(SysReCorres sysReCorres)
    {
        return sysReCorresMapper.selectSysReCorresList(sysReCorres);
    }

    /**
     * 根据FolderID查询文档列表
     *
     * @param sysReCorres 文档
     * @return 文档集合
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysReCorres> selectSysReCorresListByFolderId(SysReCorres sysReCorres) {
        return sysReCorresMapper.selectSysReCorresListByFolderId(sysReCorres);
    }
    /**
     * 新增文档
     * 
     * @param sysReCorres 文档
     * @return 结果
     */
    @Override
    public int insertSysReCorres(SysReCorres sysReCorres)
    {
        sysReCorres.setCreateTime(DateUtils.getNowDate());
        LoginUser loginUser = SecurityUtils.getLoginUser();
        sysReCorres.setUserId(loginUser.getUserId());
        sysReCorres.setDeptId(loginUser.getDeptId());
        return sysReCorresMapper.insertSysReCorres(sysReCorres);
    }

    /**
     * 修改文档
     * 
     * @param sysReCorres 文档
     * @return 结果
     */
    @Override
    public int updateSysReCorres(SysReCorres sysReCorres)
    {
        sysReCorres.setUpdateTime(DateUtils.getNowDate());
        return sysReCorresMapper.updateSysReCorres(sysReCorres);
    }

    /**
     * 批量删除文档
     * 
     * @param corresIds 需要删除的文档主键
     * @return 结果
     */
    @Override
    public int deleteSysReCorresByCorresIds(String[] corresIds)
    {
        return sysReCorresMapper.deleteSysReCorresByCorresIds(corresIds);
    }

    /**
     * 删除文档信息
     * 
     * @param corresId 文档主键
     * @return 结果
     */
    @Override
    public int deleteSysReCorresByCorresId(String corresId)
    {
        return sysReCorresMapper.deleteSysReCorresByCorresId(corresId);
    }
}
