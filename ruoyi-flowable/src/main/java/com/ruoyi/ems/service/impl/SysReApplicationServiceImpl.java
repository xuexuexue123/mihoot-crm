package com.ruoyi.ems.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.ems.mapper.SysReApplicationMapper;
import com.ruoyi.ems.domain.SysReApplication;
import com.ruoyi.ems.service.ISysReApplicationService;

/**
 * 应用Service业务层处理
 * 
 * @author luohd
 * @date 2023-03-28
 */
@Service
public class SysReApplicationServiceImpl implements ISysReApplicationService 
{
    @Autowired
    private SysReApplicationMapper sysReApplicationMapper;

    /**
     * 查询应用
     * 
     * @param pyId 应用主键
     * @return 应用
     */
    @Override
    public SysReApplication selectSysReApplicationByPyId(String pyId)
    {
        return sysReApplicationMapper.selectSysReApplicationByPyId(pyId);
    }

    /**
     * 查询应用列表
     * 
     * @param sysReApplication 应用
     * @return 应用
     */
    @Override
    public List<SysReApplication> selectSysReApplicationList(SysReApplication sysReApplication)
    {
        return sysReApplicationMapper.selectSysReApplicationList(sysReApplication);
    }

    /**
     * 新增应用
     * 
     * @param sysReApplication 应用
     * @return 结果
     */
    @Override
    public int insertSysReApplication(SysReApplication sysReApplication)
    {
        sysReApplication.setCreateTime(DateUtils.getNowDate());
        return sysReApplicationMapper.insertSysReApplication(sysReApplication);
    }

    /**
     * 修改应用
     * 
     * @param sysReApplication 应用
     * @return 结果
     */
    @Override
    public int updateSysReApplication(SysReApplication sysReApplication)
    {
        sysReApplication.setUpdateTime(DateUtils.getNowDate());
        return sysReApplicationMapper.updateSysReApplication(sysReApplication);
    }

    /**
     * 批量删除应用
     * 
     * @param pyIds 需要删除的应用主键
     * @return 结果
     */
    @Override
    public int deleteSysReApplicationByPyIds(String[] pyIds)
    {
        return sysReApplicationMapper.deleteSysReApplicationByPyIds(pyIds);
    }

    /**
     * 删除应用信息
     * 
     * @param pyId 应用主键
     * @return 结果
     */
    @Override
    public int deleteSysReApplicationByPyId(String pyId)
    {
        return sysReApplicationMapper.deleteSysReApplicationByPyId(pyId);
    }
}
